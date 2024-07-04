package JaveUtils;

import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JaveUtils {

    /**
     * 包含字符串
     *
     * @param originS 原字符串
     * @param subS    子字符串
     * @return 结果
     */
    public static boolean contains(String originS, String subS) {
        return originS.contains(subS);
    }

    /**
     * 包含字符串
     *
     * @param originS 原字符串
     * @param subS    字符串
     * @return 结果
     */
    public static boolean indexof(String originS, String subS) {
        return originS.indexOf(subS) == -1 ? false : true;
    }

    /**
     * 替换字符村啊
     *
     * @param str      总字符串
     * @param originS  替换目标字符串
     * @param replaceS 替换字符串
     * @return 结果
     */
    public static String replace(String str, String originS, String replaceS) {
        return str.replace(originS, replaceS);
    }

    /**
     * 全局替换
     *
     * @param str      总字符串
     * @param originS  替换目标字符串
     * @param replaceS 替换字符串
     * @return 结果
     */
    public static String replaceAll(String str, String originS, String replaceS) {
        str = str.replace("{", "\\{");
        str = str.replace("}", "\\}");
        return str.replaceAll(originS, replaceS);
    }

    /**
     * 判断字符串是否空
     *
     * @param str 字符串
     * @return 结果
     */
//    public static boolean stringUtilEmpty(String str) {
//        return StringUtils.isEmpty(str);
//    }

    /**
     * 截取字符串
     *
     * @param str   总字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String subString(String str, int start, int end) {
        return str.substring(start, end);
    }

    /**
     * 使用箭头方法试用
     *
     * @return 结果
     */
    public static List<JSONObject> jianBan() {
        List<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject obj = new JSONObject();
        obj.put("key", 11);
        obj.put("value", 1);
        jsonObjects.add(obj);
        obj = new JSONObject();
        obj.put("key", 12);
        obj.put("value", 2);
        jsonObjects.add(obj);
        Stream<JSONObject> stream = jsonObjects.stream().map(op -> {
            int value = op.getInt("value");
            int key = op.getInt("key");
            op.put("sum", value + key);
            return op;
        });
        jsonObjects = stream.toList();
        return jsonObjects;
    }

    /**
     * 数组转集合
     *
     * @return 结果
     */
    public static List<String> stringsToList() {
        String[] strs = {"1", "2", "3"};
        return Arrays.stream(strs).toList();
    }

    /**
     * 集合转数组
     *
     * @return 结果
     */
    public static String[] listToStrings() {
        List<String> list = Arrays.asList("1", "2");
        return (String[]) list.stream().toArray();
    }

    /**
     * 数组包含字符串
     *
     * @param list 数组
     * @param str  字符串
     * @return 结果
     */
    public boolean listContainStr(List<String> list, String str) {
        return list.contains(str);
    }

    public static void main(String[] args) {
        System.out.println("sss= " + listToStrings());
    }


}


