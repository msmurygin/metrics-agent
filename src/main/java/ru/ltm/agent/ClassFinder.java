package ru.ltm.agent;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {


    // for Infor usage

    /*   private static final String HOME = "/home/maxim/jboss/";
    private static final String EAR_FILE = HOME + "wmserver.ear";
    private static final String PATCH_LIB_NAME = "wmserver-patch.jar";
    private static final String PATCH_FILE_JAR = "lib/patch/" + PATCH_LIB_NAME;
    private static final String EXTRACTED_PATCH = HOME + PATCH_LIB_NAME;
    private static final String PACKAGE_TO_SEARCH = "com/ssaglobal/scm/wms/service/dcustomize/";

    private static final String HOME = "/home/maxim/jboss/";
    private static final String EAR_FILE = HOME + "voiceconsole-monitor-0.0.1-SNAPSHOT.jar";
    private static final String PATCH_LIB_NAME = "log4j-api-2.13.2.jar";
    private static final String PATCH_FILE_JAR = "BOOT-INF/lib/" + PATCH_LIB_NAME;
    private static final String EXTRACTED_PATCH = HOME + PATCH_LIB_NAME;
    private static final String PACKAGE_TO_SEARCH = "org/apache/logging/log4j/internal/";
    */
    private static final String TEMP_DIR = System.getProperty("temp.file.path");
    private static final String DEPLOYMENT_HOME = System.getProperty("jboss.deployment.home"); //"/home/maxim/jboss/";
    private static final String EAR_FILE = DEPLOYMENT_HOME + System.getProperty("jboss.deployment.ear"); //"voiceconsole-monitor-0.0.1-SNAPSHOT.jar";
    private static final String PATCH_LIB_NAME = System.getProperty("patch.lib.file.jar");  //"log4j-api-2.13.2.jar";
    private static final String EXTRACTED_PATCH = TEMP_DIR + PATCH_LIB_NAME;




    public static List<String> search(String packageToSearch, String patchFileJar) throws IOException{
        if (patchFileJar != null )
            return searchInEar(packageToSearch, patchFileJar);
        else
            return searchInJar(packageToSearch);
    }

    private static List<String> searchInJar(String packageToSearch) throws IOException{

        List<String> theSearchResult = new ArrayList<>();
        File f = new File (EAR_FILE);

        JarFile jar = new JarFile(f);
        Enumeration<JarEntry> entries =    jar.entries();
        while ( entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();
            String clazzName = jarEntry.getName()
                    .replaceAll("BOOT-INF/classes/","")
                    .replaceAll("META-INF/classes/","")
                    .replaceAll("/",".");

            if (clazzName.startsWith(packageToSearch.replaceAll("/",".")) && jarEntry.getName().endsWith(".class")){
                String finalClassName = clazzName.replace(".class","");
                theSearchResult.add(finalClassName);
            }
        }
        return theSearchResult;
    }


    private static List<String> searchInEar(String packageToSearch, String patchFileJar) throws IOException{

        List<String> theSearchResult = new ArrayList<>();
        File f = new File (EAR_FILE);

        JarFile jar = new JarFile(f);
        Enumeration<JarEntry> entries =    jar.entries();
        while ( entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.getName().equalsIgnoreCase(patchFileJar)){

                InputStream is = jar.getInputStream(jarEntry);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(new File(EXTRACTED_PATCH));
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                fos.close();
                is.close();

            }
        }
        File extractedFile = new File(EXTRACTED_PATCH);
        if (extractedFile.exists()) {
            JarFile extractedPatch = new JarFile(extractedFile);
            Enumeration<JarEntry> extractedPatchEntries = extractedPatch.entries();
            while (extractedPatchEntries.hasMoreElements()){
                JarEntry extractedPatchJarEntry =   extractedPatchEntries.nextElement();
                if (extractedPatchJarEntry.getName().replaceAll("/",".")
                        .startsWith(packageToSearch.replaceAll("/",".")) &&
                        extractedPatchJarEntry.getName().endsWith(".class")) {
                    System.out.println(extractedPatchJarEntry.getName());
                    theSearchResult.add(extractedPatchJarEntry.getName().replaceAll("/", ".").replaceAll(".class", ""));
                }
            }
        }
        extractedFile.delete();
        return theSearchResult;
    }


}
