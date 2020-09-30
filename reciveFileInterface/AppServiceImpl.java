package com.skytech.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.skytech.mapper.AppDao;
import com.skytech.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by ww on 2018/03/20
 */
@Service
public class AppServiceImpl implements AppService {


    //    @Resource
    // 未配置数据源保障能启动起来
    @Autowired(required = false)
    private AppDao appDao;

    /**
     * 获取android/ios库中最新版本
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getLastNewVersion(JSONObject params) throws Exception {
        if (appDao == null) return null;
        return appDao.getLastNewVersion(params);
    }

    /**
     * 获取版本详细信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject selectItem(JSONObject params) throws Exception {
        if (appDao == null) return null;
        return appDao.selectItem(params);
    }

    /**
     * 获取app版本列表
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public List<JSONObject> getAppVersionList(JSONObject params) throws Exception {
        if (appDao == null) return null;
        return appDao.getAppVersionList(params);
    }

    /**
     * 新增app版本
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int addAppVersion(JSONObject params) throws Exception {
        if (appDao == null) return 0;
        return appDao.addAppVersion(params);
    }

    /**
     * 删除app版本
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int deleteAppVersion(JSONObject params) throws Exception {
        if (appDao == null) return 0;
        return appDao.deleteAppVersion(params);
    }

    /**
     * 更改app版本信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int updateAppVersion(JSONObject params) throws Exception {
        if (appDao == null) return 0;
        return appDao.updateAppVersion(params);
    }

    /**
     * 上传文件
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int upLoadPackage(MultiValueMap paramMap, HttpServletRequest request, String type) throws Exception {
        paramMap.add("type", "file");
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = (multipartHttpServletRequest).getMultiFileMap().get("file").get(0);
        paramMap.add("file", file.getInputStream());
        String isCovered = request.getParameter("isCovered");  //是否覆盖
        //获取tomcat的路径
        String path = request.getSession().getServletContext().getRealPath("/");
        String full_path = path + "Eapp/_files/ios/phone/";
        if ("android".equals(type)) {
            full_path = path + "Eapp/_files/android/phone/";
        } else if ("ios".equals(type)) {
            full_path = path + "Eapp/_files/ios/phone/";
        }
        uploadFile(file, full_path,isCovered);
        return 0;
    }

    /**
     * 保存文件
     *
     * @param multipartFile
     * @param dirPath
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile multipartFile, String dirPath,String isCovered) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        String localFileName = fileName;
        String filePath = dirPath + File.separator + localFileName;
        //旧的包进行重命名
        if ("1".equals(isCovered)) {  //重命名旧的包
            String name = fileName.substring(0, fileName.lastIndexOf("."));
            String newFileName = name + System.currentTimeMillis() + fileSuffix;
            ReName(filePath, newFileName);
        }
        File localFile = new File(filePath);
        File imagePath = new File(dirPath);
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        multipartFile.transferTo(localFile);
        return localFileName;
    }

    /**
     * 重命名
     *
     * @param path
     * @param newname
     * @return
     */
    public boolean ReName(String path, String newname) {//文件重命名
        //Scanner scanner=new Scanner(System.in);
        File file = new File(path);
        if (file.exists()) {
            File newfile = new File(file.getParent() + File.separator + newname);//创建新名字的抽象文件
            if (file.renameTo(newfile)) {
                System.out.println("重命名成功！");
                return true;
            } else {
                System.out.println("重命名失败！新文件名已存在");
                return false;
            }
        } else {
            System.out.println("重命名文件不存在！");
            return false;
        }

    }


}
