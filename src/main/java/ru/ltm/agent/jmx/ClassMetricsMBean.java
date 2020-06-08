package ru.ltm.agent.jmx;

public interface ClassMetricsMBean {

    long getDuration();

    void setDuration(long duration);

    long getTimeStamp();

    void setTimeStamp(long timeStamp);

    void setClassName(String className);

    String getClassName();
}
