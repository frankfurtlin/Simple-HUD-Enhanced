package com.frankfurtlin.simpleHudEnhanced.utli;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * CPU 资源占用计算
 */
public class CPUMonitorCalc {
 
    private static final CPUMonitorCalc instance = new CPUMonitorCalc();

    private final ThreadMXBean threadBean;
    private long preTime = System.nanoTime();
    private long preUsedTime = 0;
    private double preUsedPercentageTime = 0;

    private CPUMonitorCalc() {
        threadBean = ManagementFactory.getThreadMXBean();
    }
 
    public static CPUMonitorCalc getInstance() {
        return instance;
    }
 
    public double getProcessCpu() {
        if(System.nanoTime() - preTime < 2000 * 1000 * 1000){
            return preUsedPercentageTime;
        }
        long totalTime = 0;
        for (long id : threadBean.getAllThreadIds()) {
            totalTime += threadBean.getThreadCpuTime(id);
        }
        long curTime = System.nanoTime();
        long usedTime = totalTime - preUsedTime;
        long totalPassedTime = curTime - preTime;
        preTime = curTime;
        preUsedTime = totalTime;
        preUsedPercentageTime = (((double) usedTime) / totalPassedTime) * 100;
        return preUsedPercentageTime;
    }
}