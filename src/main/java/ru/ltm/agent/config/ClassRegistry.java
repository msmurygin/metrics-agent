package ru.ltm.agent.config;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassRegistry {

    private static Map<String, String> packagesToInstrument;
    private static Map<String, Set<Settings>> classesToInstrument;
    private static ClassRegistry instance;


    private ClassRegistry(){
        classesToInstrument = new HashMap<>();
        packagesToInstrument = new HashMap<>();
    }

    public static ClassRegistry getInstance(){
        if (instance == null){
            instance = new ClassRegistry();
        }

        return instance;
    }
    public  Map<String, Set<Settings>> getClasses(){
        if (classesToInstrument.isEmpty()){
            try {
                classesToInstrument.putAll(JSONSettingsUtils.getClasses());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classesToInstrument;
    }

}
