package ru.ltm.agent.config;

import com.sun.tools.javac.util.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;


class JSONSettingsUtilsTest {

    @BeforeAll
    public static void beforeAll(){
        System.setProperty("metrics.registry.filename","/home/maxim/settings.json");
    }


    @Test
    public void testGetClasses(){
        try {
            Map<String, Set<Settings>> classes = JSONSettingsUtils.getClasses();
            Assert.checkNonNull(classes);
            Assert.check(!classes.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}