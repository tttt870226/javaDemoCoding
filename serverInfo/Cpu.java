package com.skytech.model;

import cn.hutool.core.util.NumberUtil;

/**
 * CPU信息
 */
public class Cpu {
    /**
     * 核心数
     */
    private String cpuNum;

    /**
     * CPU总的使用率
     */
    private String total;

    /**
     * CPU系统使用率
     */
    private String sys;

    /**
     * CPU用户使用率
     */
    private String used;

    /**
     * CPU当前等待率
     */
    private String wait;

    /**
     * CPU当前空闲率
     */
    private String free;

    public String getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(String cpuNum) {
        this.cpuNum = cpuNum;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }
}
