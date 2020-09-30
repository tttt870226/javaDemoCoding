package com.skytech.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.skytech.service.AppService;
import com.skytech.utils.JsonFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * app相关
 * Created by xuww on 2019/07/04.
 * 转发接口
 */
@RestController
@RequestMapping("/app_base")
public class AppController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AppController.class.getName());

    // @Resource
    @Autowired(required = false)// 未配置数据源保障能启动起来
    private AppService appService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public JSONObject test() {
        JSONObject backJO = new JSONObject();
        try {
            JSONObject jsonObject = JsonFileUtil.getJson("userList.json");
            if (jsonObject != null && !jsonObject.isEmpty()) {
                JSONArray userList = jsonObject.getJSONArray("userList");
                logger.info("userList------>" + userList.size());
                backJO.put("code", "200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backJO;
    }


    @RequestMapping(value = "/checkNewVersion", method = RequestMethod.GET)
    public JSONObject checkNewVersion(@RequestParam Map map) {
        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);

        // 当前版本
        if (!checkParam(backJO, params, "curVersionCode")) {
            backJO.put("code", "201");
            return backJO;
        }
        // 1、android；2、ios
        if (!checkParam(backJO, params, "type")) {
            backJO.put("code", "201");
            return backJO;
        }

        try {
            long curVersion = params.getLongValue("curVersionCode");
            JSONObject lastVerJO = appService.getLastNewVersion(params);

            if (lastVerJO == null) {
                backJO.put("code", "201");
                backJO.put("msg", "版本库中无数据！");
                return backJO;
            }

            logger.info("checkNewVersion", "库中最新版本-------->" + backJO.toJSONString());
            // 服务器版本
            long serverVersion = lastVerJO.getLongValue("versioncode");
            // 版本号比对
            if (serverVersion - curVersion > 0) {
                // 所有属性都给backJO
                backJO = lastVerJO;
                backJO.put("hasUpdate", true);
            } else {
                backJO.put("hasUpdate", false);
            }
            backJO.put("code", "200");
            return backJO;
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;
        }
    }

    /**
     * 获取版本详细信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/selectItem", method = RequestMethod.GET)
    public JSONObject selectItem(@RequestParam Map map) throws Exception {

        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);

        // 1 android；2 ios
        if (!checkParam(backJO, params, "type")) {
            return backJO;
        }
        // 1 android；2 ios
        if (!checkParam(backJO, params, "versionCode")) {
            return backJO;
        }
        // 1 android；2 ios
        if (!checkParam(backJO, params, "versionName")) {
            return backJO;
        }
        try {
            JSONObject item = appService.selectItem(params);
            backJO.put("code", "200");
            backJO.put("item", item);
            return backJO;
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;

        }

    }

    /**
     * 获取app版本列表
     */
    @RequestMapping(value = "/getAppVersionList", method = RequestMethod.GET)
    public JSONObject getAppVersionList(@RequestParam Map map) {
        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);

        // 1 android；2 ios
        if (!checkParam(backJO, params, "type")) {
            return backJO;
        }

        if (!checkParam(params, "pageNum")) {
            params.put("pageNum", 1);
        }
        if (!checkParam(params, "pageSize")) {
            params.put("pageSize", 20);
        }

        try {

            List<JSONObject> versionList = appService.getAppVersionList(params);
            backJO.put("code", "200");
            backJO.put("versionList", versionList);
            return backJO;

        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;

        }
    }

    /**
     * 新增app版本
     */
    @RequestMapping(value = "/addAppVersion", method = RequestMethod.POST)
    public JSONObject addAppVersion(@RequestBody Map map) {
        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);

        // 版本值，如20190705
        if (!checkParam(backJO, params, "versionCode")) {
            return backJO;
        }
        // 版本名，如1.0.0
        if (!checkParam(backJO, params, "versionName")) {
            return backJO;
        }
        // 1 android；2 ios
        if (!checkParam(backJO, params, "type")) {
            return backJO;
        }
        // 1强制更新；2选择性更新
        if (!checkParam(backJO, params, "isStrong")) {
            return backJO;
        }
        // 下载地址，ios为更新页面
        if (!checkParam(backJO, params, "url")) {
            return backJO;
        }
        // --版本描述
        if (!checkParam(params, "desc")) {
            params.put("desc", "");
        }

        try {
            int num = appService.addAppVersion(params);
            backJO.put("code", "200");
            backJO.put("theVersion", params);
            backJO.put("msg", "新增成功");
            return backJO;
        } catch (DuplicateKeyException e) {
            backJO.put("code", "201");
            backJO.put("msg", params.getString("versionCode") + "版本已存在，不能重复插入！");
            backJO.put("errorMsg", e.getMessage());
            return backJO;
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;
        }
    }

    /**
     * 删除app版本
     */
    @RequestMapping(value = "/deleteAppVersion", method = RequestMethod.GET)
    public JSONObject deleteAppVersion(@RequestParam Map map) {
        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);
        // 版本值，如20190705
        if (!checkParam(backJO, params, "versionCode")) {
            return backJO;
        }
        try {
            int num = appService.deleteAppVersion(params);
            backJO.put("code", "200");
            backJO.put("msg", "删除成功！");
            return backJO;
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;
        }
    }

    /**
     * 更改app版本信息
     */
    @RequestMapping(value = "/updateAppVersion", method = RequestMethod.POST)
    public JSONObject updateAppVersion(@RequestBody Map map) {
        JSONObject backJO = new JSONObject();
        JSONObject params = new JSONObject(map);
        // 版本值，如20190705
        if (!checkParam(backJO, params, "versionCode")) {
            return backJO;
        }
        // 版本名，如1.0.0
        if (!checkParam(backJO, params, "versionName")) {
            return backJO;
        }
        // 1 android；2 ios
        if (!checkParam(backJO, params, "type")) {
            return backJO;
        }
        // 1强制更新；2选择性更新
        if (!checkParam(backJO, params, "isStrong")) {
            return backJO;
        }
        // 下载地址，ios为更新页面
        if (!checkParam(backJO, params, "url")) {
            return backJO;
        }
        // --版本描述
        if (!checkParam(params, "desc")) {
            params.put("desc", "");
        }
        try {
            int num = appService.updateAppVersion(params);
            backJO.put("code", "200");
            backJO.put("msg", "更新成功！");
            return backJO;
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
            return backJO;
        }
    }

    /**
     * 文件上传接口
     */
    @RequestMapping(value = "/upLoadPackage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject upLoadPackage(HttpServletRequest request) {
        JSONObject backJO = new JSONObject();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        String type = request.getParameter("type");
        if (StringUtils.isBlank(type)) {
            throw new RuntimeException("type 为空");
        }
        try {
            appService.upLoadPackage(paramMap,request,type);
            backJO.put("code", "200");
            backJO.put("msg", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            backJO.put("code", "201");
            backJO.put("msg", e.getMessage());
        }

        return backJO;
    }



}
