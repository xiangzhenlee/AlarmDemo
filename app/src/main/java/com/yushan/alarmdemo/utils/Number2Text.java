package com.yushan.alarmdemo.utils;

/**
 * Created by beiyong on 2018-2-5.
 */

public class Number2Text {

    static String[] units = {"","十","百","千","万","十万","百万","千万","亿","十亿","百亿","千亿","万亿" };
    static char[] numArray = {'零','一','二','三','四','五','六','七','八','九'};

    /**
     * 将整数转换成汉字数字
     * @param num 需要转换的数字
     * @return 转换后的汉字
     */
    public static String formatInteger(int num) {
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    continue;
                } else {
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    public static int displaceWeekday(String date) {
        int returnDay = -1;
        switch (date) {
            case "周一":
                returnDay = 0;
                break;
            case "周二":
                returnDay = 1;
                break;
            case "周三":
                returnDay = 2;
                break;
            case "周四":
                returnDay = 3;
                break;
            case "周五":
                returnDay = 4;
                break;
            case "周六":
                returnDay = 5;
                break;
            case "周日":
                returnDay = 6;
                break;
        }
        return returnDay;
    }
}
