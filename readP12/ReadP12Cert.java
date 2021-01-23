package com.sky.shellservice.utils;

import sun.security.x509.X509CertImpl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class ReadP12Cert {
    public static Map getCnOu(String filePath, String pwd) {
        //文件路径
        final String KEYSTORE_FILE = filePath;
        //生成p12文件时的密码，Google的统一密码是"notasecret"
        final String KEYSTORE_PASSWORD = pwd;
        final String KEYSTORE_ALIAS = "alias";

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            InputStream fis = new FileInputStream(KEYSTORE_FILE);
            char[] nPassword = null;

            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }

            ks.load(fis, nPassword);
            fis.close();
            System.out.println("keystore type=" + ks.getType());

            //循环获取别名
            Enumeration enumm = ks.aliases();
            String keyAlias = null;
            if (enumm.hasMoreElements()) // we are readin just one certificate.
            {
                keyAlias = (String) enumm.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }

            // Now once we know the alias, we could get the keys.
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            //第一种获取私钥的方式
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();

            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + pubkey);
            //第二种获取私钥的方式，这个是写在KeyStore文档里的，似乎是比较推荐的那种
            X509CertImpl cert509 = (X509CertImpl) cert;
            System.out.println("cert ===info=" + cert509.getSubjectDN().getName());
            boolean inStart = false;
            String certInfo = cert509.getSubjectDN().getName();
            Date expire_date = cert509.getNotAfter();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expire_dateStr =  sdf.format(expire_date);
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < certInfo.length(); i++) {  //处理双引号里，含有,问题
                String c = certInfo.substring(i, i + 1);
                if (inStart) {  //在双引号之间且是逗号
                    if (",".equals(c)) {
                        strBuf.append("{&&}");
                        continue;
                    }
                }
                if ("\"".equals(c)) {
                    inStart = !inStart;
                }
                strBuf.append(c);
            }
            String OU = "";
            String CN = "";
            String[] array = strBuf.toString().split(",");
            for (int i = 0; i < array.length; i++) {
                String g_str = array[i];
                String g_str2 = g_str.replace("{&&}", ",");
                int index = g_str2.indexOf("OU=");
                if (index != -1) {  //团队id
                    OU = g_str2.substring(g_str.indexOf("=") + 1);
                }
                index = g_str2.indexOf("CN=");
                if (index != -1) {   //证书名字
                    CN = g_str2.substring(g_str.indexOf("=") + 1);
                }
            }
            Map map = new HashMap();
            map.put("OU",OU);
            map.put("CN",CN);
            map.put("expireDate",expire_dateStr);
            System.out.println("ou===>" + OU);
            return map;
//            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(nPassword);
//            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("privatekey", protParam);
//            PrivateKey myPrivateKey = pkEntry.getPrivateKey();
//            //base64解码，获取真正信息
//            byte[] a = myPrivateKey.getEncoded();
//            System.out.println(new BASE64Encoder().encode(a));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}