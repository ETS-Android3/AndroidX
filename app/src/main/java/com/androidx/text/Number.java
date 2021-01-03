package com.androidx.text;

/**
 * 数字格式化
 */
public class Number {

    /**
     * Double 格式
     *
     * @param number
     * @return
     */
    public static double parseDouble(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return 0.00d;
        }
        if (!number.contains(".")) {
            number = number + ".00";
        }
        return Double.parseDouble(number);
    }

    /**
     * Integer 格式
     *
     * @param number
     * @return
     */
    public static int parseInt(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return 0;
        }
        if (number.contains(".")) {
            number = number.substring(0, number.lastIndexOf("."));
        }
        return Integer.parseInt(number);
    }

    /**
     * Long 格式
     *
     * @param number
     * @return
     */
    public static long parseLong(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return 0;
        }
        return Long.parseLong(number);
    }

    /**
     * Float 格式
     *
     * @param number
     * @return
     */
    public static float parseFloat(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return 0.00f;
        }
        if (!number.contains(".")) {
            number = number + ".00";
        }
        return Float.parseFloat(number);
    }

    /**
     * String 格式
     *
     * @param number
     * @return
     */
    public static String toInt(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return "0";
        }
        return number;
    }

    /**
     * Long 格式
     *
     * @param number
     * @return
     */
    public static String toLong(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return "0";
        }
        return number;
    }

    /**
     * Double 格式
     *
     * @param number
     * @return
     */
    public static String toDouble(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return "0.00";
        }
        return number;
    }

    /**
     * Float 格式
     *
     * @param number
     * @return
     */
    public static String toFloat(String number) {
        if (number == null || number.equals("null") || number.length() == 0) {
            return "0.00";
        }
        return number;
    }

}
