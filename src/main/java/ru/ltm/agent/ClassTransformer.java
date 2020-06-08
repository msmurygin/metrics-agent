package ru.ltm.agent;

import javassist.*;
import ru.ltm.agent.config.ClassRegistry;
import ru.ltm.agent.config.Settings;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassTransformer implements ClassFileTransformer {

    private static final String METRICS_START_TIME_FIELD_SRC = "public static long __metricStartTime;";
    private static final String METRICS_START_TIME_FIELD_INITIALIZE_SRC = "System.currentTimeMillis()";

    private static final String CLASS_METRICS_FIELD_SRC = "public static final ru.ltm.agent.jmx.ClassesMetricsProfiler metricsProfiler;";
    private static final String CLASS_METRICS_FIELD_INITIALIZE_SRC = "new ru.ltm.agent.jmx.ClassesMetricsProfiler()";
    private static final String METRICS_START_TIME_VAR_SRC = "__metricStartTime";
    private static final String METRICS_START_TIME_VAR_INITIALIZE_SRC = "__metricStartTime = System.currentTimeMillis();";
    private static final String METRICS_END_TIME_VAR_SRC = "\", System.currentTimeMillis() - __metricStartTime);";
    private static final String JMX_METHOD = "metricsProfiler.mark2(\"";
    private static final String CATCH_BLOCK_SRC = "{ System.out.println($e); throw $e; }";
    private static final String EXCEPTION_TYPE_SRC = "java.lang.Exception";
    private static final String PACKAGE_DEV = "/";
    private static final String DOT = ".";
    private static final String INFOR_HOME = "c:\\Infor\\";
    private static final Map<String,Set<Settings>> CLASSES_TO_INSTRUMENT = ClassRegistry.getInstance().getClasses();


    @Override
    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
        String clazzName = className.replace(PACKAGE_DEV,DOT);
        if (CLASSES_TO_INSTRUMENT.containsKey(clazzName)) {
            Set<Settings> methodsToInstrument = CLASSES_TO_INSTRUMENT.get(clazzName);
            return transformClass(redefiningClass, bytes, methodsToInstrument);
        }

        return null;
    }

    private byte[] transformClass(Class classToTransform, byte[] b, Set<Settings> methodsToInstrument) {
        ClassPool pool = ClassPool.getDefault();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        pool.appendClassPath(new LoaderClassPath(classLoader));


        CtClass.debugDump = INFOR_HOME;
        CtClass cl = null;
        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));


            CtField metricsField = CtField.make(CLASS_METRICS_FIELD_SRC, cl);
            cl.addField(metricsField, CtField.Initializer.byExpr(CLASS_METRICS_FIELD_INITIALIZE_SRC));

            CtField starTimeField = CtField.make(METRICS_START_TIME_FIELD_SRC, cl);
            cl.addField(starTimeField, CtField.Initializer.byExpr(METRICS_START_TIME_FIELD_INITIALIZE_SRC));


            CtBehavior[] methods = cl.getDeclaredBehaviors();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].isEmpty() == false) {
                    changeMethod(methods[i], cl, methodsToInstrument);
                }
            }
            b = cl.toBytecode();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cl.debugWriteFile(INFOR_HOME);
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

    private void changeMethod(CtBehavior method, CtClass ctClass, Set<Settings> methodsToInstrument) throws Exception {
        for (Object settingObj : methodsToInstrument.toArray()){
            Settings settings  = (Settings) settingObj;
            List<String> methods = settings.getMethodsToInstrument();
            if (methods.contains(method.getName()) ||
                    ( settings.getMethodsToInstrument().get(0).equalsIgnoreCase("*") &&
                            !method.getName().equalsIgnoreCase("<clinit>") &&
                            !method.getName().startsWith("lambda$")) ){

                String metricName = ctClass.getPackageName() + DOT + ctClass.getSimpleName() + DOT + method.getName();
                method.insertBefore(METRICS_START_TIME_VAR_INITIALIZE_SRC);
                CtClass etype = ClassPool.getDefault().get(EXCEPTION_TYPE_SRC);
                method.addCatch("{ throw $e; }" , etype);
                method.insertAfter( JMX_METHOD + metricName + METRICS_END_TIME_VAR_SRC,true);


            }
        }
    }

}
