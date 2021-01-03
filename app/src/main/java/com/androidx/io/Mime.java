package com.androidx.io;

import com.androidx.content.IOProvider;

import java.io.File;
import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * Created by Relin
 * on 2018-06-06.
 * 根据Mime获取Suffix,根据Suffix获取Mine
 */
public class Mime {

    public static String _3GP = "video/3gpp";
    public static String APK = "video/application/vnd.android.package-archive";
    public static String ASF = "video/x-ms-asf";
    public static String AVI = "video/x-msvideo";
    public static String BIN = "application/octet-stream";
    public static String BMP = "image/bmp";
    public static String C = "text/plain";
    public static String CLASS = "application/octet-stream";
    public static String CONF = "text/plain";
    public static String CPP = "text/plain";
    public static String DOC = "application/msword";
    public static String EXE = "application/octet-stream";
    public static String GIF = "image/gif";
    public static String GTAR = "application/x-gtar";
    public static String GZ = "application/x-gzip";
    public static String H = "text/plain";
    public static String HTM = "text/html";
    public static String HTML = "text/html";
    public static String JAR = "application/java-archive";
    public static String JAVA = "text/plain";
    public static String JPEG = "image/jpeg";
    public static String JPG = "image/jpeg";
    public static String JS = "application/x-javascript";
    public static String LOG = "text/plain";
    public static String M3U = "audio/x-mpegurl";
    public static String M4A = "audio/x-mpegurl";
    public static String M4B = "audio/mp4a-latm";
    public static String M4P = "audio/mp4a-latm";
    public static String M4U = "video/vnd.mpegurl";
    public static String M4V = "video/x-m4v";
    public static String MOV = "video/quicktime";
    public static String MP2 = "audio/x-mpeg";
    public static String MP3 = "audio/x-mpeg";
    public static String MP4 = "video/mp4";
    public static String MPC = "application/vnd.mpohun.certificate";
    public static String MPE = "video/mpeg";
    public static String MPEG = "video/mpeg";
    public static String MPG = "video/mpeg";
    public static String MPG4 = "video/mp4";
    public static String MPGA = "audio/mpeg";
    public static String MSG = "application/vnd.ms-outlook";
    public static String OGG = "audio/ogg";
    public static String PDF = "application/pdf";
    public static String PNG = "image/png";
    public static String PPS = "application/vnd.ms-powerpoint";
    public static String PPT = "application/vnd.ms-powerpoint";
    public static String PROP = "text/plain";
    public static String RAR = "application/x-rar-compressed";
    public static String RC = "text/plain";
    public static String RMVB = "audio/x-pn-realaudio";
    public static String RTF = "application/rtf";
    public static String SH = "text/plain";
    public static String TAR = "application/x-tar";
    public static String TGZ = "application/x-compressed";
    public static String TXT = "text/plain";
    public static String WAV = "audio/x-wav";
    public static String WMA = "audio/x-ms-wma";
    public static String WMV = "audio/x-ms-wmv";
    public static String WPS = "application/vnd.ms-works";
    public static String XNL = "text/plain";
    public static String Z = "application/x-compress";
    public static String ZIP = "application/zip";
    public static String ANY = "*/*";
    public static String FOLDER = "file/*";

    private static TreeMap<String, String> map;

    static {
        map = new TreeMap<>();
        for (Field field : Mime.class.getFields()) {
            String name = field.getName();
            if (field.getType() == String.class) {
                try {
                    String value = (String) field.get(field);
                    name = name.replace("_3GP", "3GP");
                    map.put(name.toUpperCase(), value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件类型键值
     *
     * @return
     */
    public static TreeMap<String, String> map() {
        return map;
    }

    /**
     * 根据文件获取Mime
     *
     * @param file 文件
     * @return
     */
    public static String value(File file) {
        String path = file.getAbsolutePath();
        return IOProvider.getMimeType(path);
    }

    /**
     * 文件后缀获取Mime
     *
     * @param suffix 文件后缀
     * @return
     */
    public static String value(String suffix) {
        if (suffix == null || suffix.length() == 0) {
            return "";
        }
        return map.get(suffix.toUpperCase());
    }

    /**
     * 文件获取后缀名称
     *
     * @param file 文件名
     * @return
     */
    public static String suffix(File file) {
        String name = file.getName();
        if (name.contains(".")) {
            return name.split("\\.")[1].toLowerCase();
        }
        return "";
    }

    /**
     * 文件类型获取后缀名称
     *
     * @param mime 文件类型
     * @return
     */
    public static String suffix(String mime) {
        for (String suffix : map.keySet()) {
            String value = map.get(suffix);
            if (mime.equals(value)) {
                return suffix.toLowerCase();
            }
        }
        return "";
    }
}
