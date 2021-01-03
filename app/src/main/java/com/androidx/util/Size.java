package com.androidx.util;

import com.androidx.content.IOProvider;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Author: Relin
 * Describe:大小计算
 * Date:2020/11/29 0:10
 */
public class Size {

    /**
     * 列表大小
     *
     * @param list
     * @return
     */
    public static int of(List<?> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 文件大小
     *
     * @param file 文件
     * @return 大小，单位kb
     */
    public static long of(File file) {
        return IOProvider.length(file);
    }

    /**
     * 数组长度
     *
     * @param arr
     * @return
     */
    public static int of(String[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return arr.length;
    }

    /**
     * 数组长度
     *
     * @param arr
     * @return
     */
    public static int of(byte[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return arr.length;
    }

    /**
     * 数组长度
     *
     * @param arr
     * @return
     */
    public static int of(char[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return arr.length;
    }


    /**
     * 键值对大小
     *
     * @param map 键值对
     * @return
     */
    public static int of(Map map) {
        if (map == null) {
            return 0;
        }
        return map.size();
    }

}
