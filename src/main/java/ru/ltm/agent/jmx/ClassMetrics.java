package ru.ltm.agent.jmx;

public class ClassMetrics implements ClassMetricsMBean {
   private long duration;
   private long timeStamp;
   private String className;
    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public void setClassName(String className) {
        this.className  = className;
    }

    @Override
    public String getClassName() {
        return  this.className ;
    }


}
