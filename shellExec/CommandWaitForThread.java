package com.sky.shellservice.utils;

import com.alibaba.fastjson.JSONObject;
import com.sky.shellservice.dto.ShellDto;
import com.sky.shellservice.service.ShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandWaitForThread extends Thread{


    public String eappPMServer;

    private static Logger log = LoggerFactory.getLogger("shell_logger");

    private String cmd;
    private boolean finish = false;
    private int exitValue = -1;
    private ShellDto shellDto;
    private String type;   //0 通用法网的执行  1 获取uuid执行  2eapp打包执行
    private ShellService shellService;

    public CommandWaitForThread(String cmd, ShellDto dto, String type, ShellService service) {
        this.cmd = cmd;
        this.shellDto = dto;
        this.type = type;
        this.shellService = service;
    }

    /**
     * 执行重制工程线程
     */

    private void resetProject(BufferedReader infoInput, BufferedReader errorInput, Process process) throws Exception {

        Boolean isSuccess = false;
        String line = "";
        try {
            while ((line = infoInput.readLine()) != null) {
                int flag = line.indexOf("=====>"); //代表有输出
                if (flag != -1) { //有输出标志
                    JSONObject obj = new JSONObject();
                    obj.put("process_id", this.shellDto.getProcessUUID());
                    obj.put("process_step", line);
                    obj.put("process_step_desc", line);
                    obj.put("user_id", shellDto.getUserId());
                    obj.put("version_code", shellDto.getVersionCode());
                    obj.put("version_name", shellDto.getVersionName());
                    obj.put("project_id", shellDto.getProjectId());
                    try {
                        String url = eappPMServer + "ShellLog/add.do";
                        String requestParamStr = JSONObject.toJSONString(obj);
                        HttpRequestTool.sendPost(url, null, requestParamStr);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
                log.info(line);
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
            }
            infoInput.close();
            errorInput.close();
            //阻塞执行线程直至脚本执行完成后返回
            this.exitValue = process.waitFor();
        } catch (Exception e) {
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
        }

    }


    //获取uuid
    private void setUUID(BufferedReader infoInput, BufferedReader errorInput) {
        Boolean isSuccess = false;
        String line = "";
        try {
            while ((line = infoInput.readLine()) != null) {
                int flag = line.indexOf("=====>UUIDis:"); //代表uuid
                if (flag != -1) { //有输出标志
                    shellService.mobileprovisionUUID = line.substring(line.indexOf(":") + 1);
                }
                log.info(line);
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
            }
            infoInput.close();
            errorInput.close();
        } catch (Exception e) {
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
        }
    }

    //安装p12文件
    private void installP12File(BufferedReader infoInput, BufferedReader errorInput){
        try {
            //执行脚本并等待脚本执行完成
            Boolean isSuccess = false;
            String line = "";
            while ((line = infoInput.readLine()) != null) {
                int flag = line.indexOf("=====>"); //代表有输出
                log.info(line);
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
            }
            infoInput.close();
            errorInput.close();
        } catch (Throwable e) {  //异常时候执行
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
        }
    }

    public void run() {
        try {
            //执行脚本并等待脚本执行完成
            Process process = Runtime.getRuntime().exec(cmd);
            Boolean isSuccess = false;
            //写出脚本执行中的过程信息
            BufferedReader infoInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            if (type.equals("1")) {//获取uuid执行
                setUUID(infoInput, errorInput);
                //阻塞执行线程直至脚本执行完成后返回
                this.exitValue = process.waitFor();
                return;
            } else if (type.equals("2")) //eapp打包执行
            {
                resetProject(infoInput, errorInput, process);
                //阻塞执行线程直至脚本执行完成后返回
                this.exitValue = process.waitFor();
                return;
            } else if (type.equals("3"))  //安装p12文件
            {
                installP12File(infoInput, errorInput);
                //阻塞执行线程直至脚本执行完成后返回
                this.exitValue = process.waitFor();
                return;
            }

            while ((line = infoInput.readLine()) != null) {
                int flag = line.indexOf("=====>"); //代表有输出
                if (flag != -1) { //有输出标志
                    JSONObject obj = new JSONObject();
                    obj.put("process_id", this.shellDto.getProcessUUID());
                    obj.put("process_step", line);
                    obj.put("process_step_desc", line);
                    obj.put("user_id", shellDto.getUserId());
                    obj.put("version_code", shellDto.getVersionCode());
                    obj.put("version_name", shellDto.getVersionName());
                    obj.put("project_id", shellDto.getProjectId());
                    try {
//                            shellLogDao.addShellLog(obj);
                        String url = eappPMServer + "ShellLog/add.do";
                        String requestParamStr = JSONObject.toJSONString(obj);
                        HttpRequestTool.sendPost(url, null, requestParamStr);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
                flag = line.indexOf("=====>完成"); //是否是最后输入出成功
                if (flag != -1) {
                    isSuccess = true;
                }
                log.info(line);
            }
            while ((line = errorInput.readLine()) != null) {
                log.error(line);
            }
            infoInput.close();
            errorInput.close();
            //更新打包任务表
            try {
                JSONObject obj = new JSONObject();
                obj.put("process_id", this.shellDto.getProcessUUID());
                obj.put("version_code", this.shellDto.getVersionCode());
                obj.put("version_name", this.shellDto.getVersionName());

                obj.put("app_code", this.shellDto.getAppCode());
                obj.put("package_name", this.shellDto.getPackageName());
                obj.put("apphome_url", this.shellDto.getApphomeUrl());
                obj.put("app_name", this.shellDto.getProjectName());
                if (isSuccess) {
                    obj.put("pack_status", "SUCCESS"); //成功
                } else {
                    obj.put("pack_status", "FAIL"); //失败
                }
                String url = eappPMServer + "PackTask/update.do";
                String requestParamStr = JSONObject.toJSONString(obj);
                HttpRequestTool.sendPost(url, null, requestParamStr);
            } catch (Exception e) {
                log.info(e.getMessage());
            }

            //阻塞执行线程直至脚本执行完成后返回
            this.exitValue = process.waitFor();
        } catch (Throwable e) {  //异常时候执行
            log.error("CommandWaitForThread accure exception,shell " + cmd, e);
            exitValue = 110;
        } finally {
            finish = true;
        }
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getExitValue() {
        return exitValue;
    }


}
