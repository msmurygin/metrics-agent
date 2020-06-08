package ru.ltm.agent.config;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    private String packageName;
    private String className;
    private List<String> methodsToInstrument;

    public Settings(String packageName, String className, List<String> methodsToInstrument) {
        this.packageName = packageName;
        this.className = className;
        this.methodsToInstrument = methodsToInstrument;
    }

    public Settings(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public Settings(String packageName, String className, JSONArray methods) {
        this.packageName = packageName;
        this.className = className;

        if (methodsToInstrument == null)
            methodsToInstrument = new ArrayList<>();

        for (Object obj : methods){
            methodsToInstrument.add((String)obj);
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getMethodsToInstrument() {
        return methodsToInstrument;
    }

    public void setMethodsToInstrument(List<String> methodsToInstrument) {
        this.methodsToInstrument = methodsToInstrument;
    }
}
