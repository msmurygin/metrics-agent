package ru.ltm.agent.config;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.ltm.agent.ClassFinder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JSONSettingsUtils {
    private static final String OS  = "os.name";
    private static final String BASE_OS_NAME = "Linux";
    private static final String DEFAULT_LINUX_FILE_LOCATION = "/opt/infor/sce/scprd/wm/app/conf/settings.json";
    private static final String DEFAULT_WIN_FILE_LOCATION = "C:\\Infor\\sce\\scprd\\wm\\app\\conf\\settings.json";
    private static final String METRICS_FILE_NAME = "metrics.registry.filename";
    private static final String PATCH_LIB_NAME = System.getProperty("patch.lib.file.jar");
    private static final String PATCH_FILE_JAR = System.getProperty("patch.lib.path")  + PATCH_LIB_NAME;
    private static final String JSON_ROOT = "settings";


    protected  static Map<String, Set<Settings>> getClasses()throws Exception {
        Map<String, Set<Settings>> CLASSES_TO_INSTRUMENT = new HashMap<>();
        JSONArray array = getSettings();
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();


            String packageName = next.getString("PackageName");
            String className = next.getString("className");
            String fullClassName = packageName +"."+ className;
            JSONArray methods = next.getJSONArray("Methods");


            if (className.equalsIgnoreCase("*")){
                List<String> searchClasses = ClassFinder.search(packageName, PATCH_FILE_JAR);
                for (String clazz : searchClasses){
                    Set<Settings> methodSet = new HashSet<>();
                    String finalClassName = clazz.replaceAll(packageName+".","");
                    methodSet.add(new Settings(packageName, finalClassName, Arrays.asList(new String[]{"*"})));
                    CLASSES_TO_INSTRUMENT.put(clazz, methodSet);
                }
            }else {
                Set<Settings> methodSet = new HashSet<>();
                methodSet.add(new Settings(packageName, className, methods));
                CLASSES_TO_INSTRUMENT.put(fullClassName, methodSet);
            }
        }

        return CLASSES_TO_INSTRUMENT;
    }


    private static JSONArray getSettings() throws Exception{
        InputStream inputStream =  new FileInputStream(new File(getFileName()));
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        JSONObject obj = new JSONObject(textBuilder.toString());
        return  obj.getJSONArray(JSON_ROOT);

    }

    private static String getFileName(){
        String os = System.getProperty(OS);
        String defaultFileName = os.equalsIgnoreCase(BASE_OS_NAME) ? DEFAULT_LINUX_FILE_LOCATION :
                DEFAULT_WIN_FILE_LOCATION;
        String FileName = System.getProperty(METRICS_FILE_NAME, defaultFileName);
        return FileName;
    }
}
