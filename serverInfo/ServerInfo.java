package com.skytech.model;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 服务器硬件和使用信息
 */
public class ServerInfo {
    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * CPU信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存信息
     */
    private Mem mem = new Mem();

    /**
     * 系统信息
     */
    private System sys = new System();

    /**
     * 磁盘信息
     */
    private List<Disk> disks = new LinkedList<Disk>();

    public Cpu getCpu()
    {
        return cpu;
    }

    public void setCpu(Cpu cpu)
    {
        this.cpu = cpu;
    }

    public Mem getMem()
    {
        return mem;
    }

    public void setMem(Mem mem)
    {
        this.mem = mem;
    }

    public System getSys()
    {
        return sys;
    }

    public void setSys(System sys)
    {
        this.sys = sys;
    }

    public List<Disk> getDisks()
    {
        return disks;
    }

    public void setDisks(List<Disk> disks)
    {
        this.disks = disks;
    }

    public void serverInfo() throws Exception
    {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        setCpuInfo(hal.getProcessor());

        setMemInfo(hal.getMemory());

        setSysInfo();

        setDisksInfo(si.getOperatingSystem());
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor)
    {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount()+"");
        cpu.setTotal(totalCpu+"");
        cpu.setSys(cSys+"");
        cpu.setUsed(user+"");
        cpu.setWait(iowait+"");
        cpu.setFree(idle+"");
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory)
    {
        float GB= 1024*1024*1024;
        float total_f = (float)(memory.getTotal()/GB);
        String total = String.format("%.2f",total_f);
        float Used_f = (memory.getTotal() - memory.getAvailable())/GB;
        String Used = String.format("%.2f",Used_f);
        float free_f = memory.getAvailable()/GB;
        String free = String.format("%.2f",free_f);
        mem.setTotal(total+"");
        mem.setUsed(Used+"");
        mem.setFree(free+"");
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo()
    {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Properties props = java.lang.System.getProperties();
        assert addr != null;
        sys.setComputerName(addr.getHostName());
//        sys.setComputerIp(NetUtil.getLocalhostStr());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置磁盘信息
     */
    private void setDisksInfo(OperatingSystem os)
    {
        FileSystem fileSystem = os.getFileSystem();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray)
        {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            Disk disk = new Disk();
            disk.setDirName(fs.getMount());
            disk.setSysTypeName(fs.getType());
            disk.setTypeName(fs.getName());
            disk.setTotal(convertFileSize(total));
            disk.setFree(convertFileSize(free));
            disk.setUsed(convertFileSize(used));
            float Usage_f = ((float)(used))/total;
            String Usage = String.format("%.2f",Usage_f*100);
            disk.setUsage(Usage+"");
//            disk.setUsage(NumberUtil.mul(NumberUtil.div(used, total, 4), 100));
            disks.add(disk);
        }
    }

    /**
     * 字节转换
     */
    public String convertFileSize(long size)
    {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb)
        {
            return String.format("%.1f GB", (float) size / gb);
        }
        else if (size >= mb)
        {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        }
        else if (size >= kb)
        {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        }
        else
        {
            return String.format("%d B", size);
        }
    }

}
