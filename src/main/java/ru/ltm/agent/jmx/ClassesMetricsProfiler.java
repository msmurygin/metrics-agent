package ru.ltm.agent.jmx;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;


public class ClassesMetricsProfiler {
    private static MBeanServer mbs = null;
    private static String CLASS_NAME = "ClassName";
    private static String DURATION = "Duration";
    private static String TIME_STAMP = "TimeStamp";
    private static String DOT = ".";
    private static String NAME_PREFIX = ":name=";


    public ClassesMetricsProfiler() {
        try {
            mbs = ManagementFactory.getPlatformMBeanServer();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void mark2(String classNameWithMethod, long duration){
        try{
            int idx = classNameWithMethod.lastIndexOf(DOT);
            String className = classNameWithMethod.substring(0, idx) ;
            String methodName = classNameWithMethod.substring(idx + 1);
            Class<?> clazz = Class.forName(className);

            ObjectName completeTaskObjectName = new ObjectName( clazz.getCanonicalName() + NAME_PREFIX + methodName);
            if (mbs.isRegistered(completeTaskObjectName)) {
                mbs.setAttribute(completeTaskObjectName, new Attribute(CLASS_NAME, className));
                mbs.setAttribute(completeTaskObjectName, new Attribute(DURATION, duration));
                mbs.setAttribute(completeTaskObjectName, new Attribute(TIME_STAMP, System.currentTimeMillis()));
            }else {
                ClassMetricsMBean metricsMBean = new ClassMetrics();
                metricsMBean.setClassName(className);
                metricsMBean.setDuration(duration);
                metricsMBean.setTimeStamp(System.currentTimeMillis());
                mbs.registerMBean(metricsMBean, completeTaskObjectName);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}