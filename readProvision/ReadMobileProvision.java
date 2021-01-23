package com.sky.shellservice.utils;

import com.dd.plist.NSObject;
import com.dd.plist.XMLPropertyListParser;

import java.util.HashMap;
import java.util.Map;

public class ReadMobileProvision {

    /**
     * 读取mobileProvision文件
     *
     * @param path
     * @return
     */
    public static Map readMobileProvision(String path) throws Exception {
        String mobileprovisionStr = ReadJSONFile.readJsonFile(path);
        if (mobileprovisionStr.indexOf("<plist") < 0) {
            return null;
        }
        if (mobileprovisionStr.indexOf("</plist>") < 0) {
            return null;
        }
        String plistStr = mobileprovisionStr.substring(mobileprovisionStr.indexOf("<plist"), mobileprovisionStr.indexOf("</plist>") + 8);
        Map mobileprovisionInfo = readPlist(plistStr);
        return mobileprovisionInfo;
    }

    /**
     * 通过dd_plist解压plist文件 中间去掉gzip解压，不明确这个GZIP解压目的
     * @param plistStr
     * @return
     * @throws Exception
     */
    public static Map readPlist(String plistStr) throws Exception {
        byte[] bytes = plistStr.getBytes();
        //传入byte[]
        NSObject xx = XMLPropertyListParser.parse(bytes);
        //你要看你的是什么类型，直接强转
        HashMap obj = (HashMap) xx.toJavaObject();
        //获取节点
//        byte[] responseBody = (byte[]) obj.get("ResponseBody");
        //GZIP解压
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ByteArrayInputStream in = new ByteArrayInputStream(responseBody);
//        try {
//            GZIPInputStream ungzip = new GZIPInputStream(in);
//            byte[] buffer = new byte[256];
//            int n;
//            while ((n = ungzip.read(buffer)) >= 0) {
//                out.write(buffer, 0, n);
//            }
//        } catch (Exception e) {
//            System.out.println("解压失败" + e.getMessage());
//        }
        //再次解析plist
//        NSObject body = XMLPropertyListParser.parse(out.toByteArray());
//        HashMap bodyMap = (HashMap) body.toJavaObject();
        //result.put("body",new Gson().toJson(bodyMap));
        return obj;
    }


}
