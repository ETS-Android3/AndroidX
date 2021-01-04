package com.androidx.content;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Relin
 * on 2017/4/7.
 * 文件操作提供者
 */
public class IOProvider {

    /**
     * 日志标识
     */
    public static String TAG = IOProvider.class.getSimpleName();

    /**
     * 获取挂载的缓存文件夹
     *
     * @param context 上下文
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        return UriProvider.getExternalCacheDir(context);
    }

    /**
     * 创建文件
     * create file
     *
     * @param context 上下文对象
     * @param dir     文件夹名称
     * @param name    文件名称
     * @return
     */
    public static File createFile(Context context, String dir, String name) {
        if (name == null) {
            Log.i(TAG, "Please check you file name.");
            return null;
        }
        makeDirs(context, dir);
        File file = new File(getExternalCacheDir(context) + File.separator + dir + File.separator + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 创建新文件夹
     *
     * @param context 上下文对象
     * @param dir     文件夹名称
     * @return
     */
    public static String makeDirs(Context context, String dir) {
        File folder = new File(getExternalCacheDir(context) + File.separator + dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param file 将要删除的文件目录
     */
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = delete(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return file.delete();
    }

    /**
     * 计算文件大小
     *
     * @param file 文件夹
     * @return
     */
    public static long length(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0L;
                for (File content : children)
                    size += length(content);
                return size;
            } else {//如果是文件则直接返回其大小,以“Kb”为单位
                Log.i(TAG, " length " + file.length() + "kb");
                long size = file.length() / 1024;
                return size;
            }
        } else {
            Log.i(TAG, "The file or dir does not exist, please check the path is correct!");
            return 0;
        }
    }

    /**
     * 获取文件后缀
     *
     * @param path 路径
     * @return
     */
    public static String getSuffix(String path) {
        return getSuffix(new File(path));
    }

    /**
     * 获取文件后缀
     *
     * @param file 文件
     * @return
     */
    public static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    /**
     * 获取文件类型
     *
     * @param path 文件路径
     * @return
     */
    public static String getMimeType(String path) {
        return getMimeType(new File(path));
    }

    /**
     * 获取文件类型
     *
     * @param file 文件
     * @return
     */
    public static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null || !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }

    /**
     * 创建Url文件名称
     *
     * @param url 资源地址
     * @return
     */
    public static String buildNameByUrl(String url) {
        if (url.contains("/") && url.contains(".")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format.format(format) + ".zip";
    }

    /**
     * 获取Assets文件内容
     *
     * @param context 上下文
     * @param fileName 文件名
     * @return
     */
    public static String readAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        if (file == null) {
            return "The read file is empty and cannot be read.";
        }
        //定义一个字符串缓存，将字符串存放缓存中
        StringBuilder sb = new StringBuilder("");
        //定义一个fileReader对象，用来初始化BufferedReader
        FileReader reader;
        try {
            reader = new FileReader(file);
            //new一个BufferedReader对象，将文件内容读取到缓存
            BufferedReader bufferedReader = new BufferedReader(reader);
            String content;
            //逐行读取文件内容，不读取换行符和末尾的空格
            while ((content = bufferedReader.readLine()) != null) {
                //将读取的字符串添加换行符后累加存放在缓存中
                sb.append(content + "\n");
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 写出文件
     *
     * @param context 上下文
     * @param name    文件名称，例如：xxx.java
     * @param content 内容
     */
    public static void write(Context context, String name, String content) {
        File file = UriProvider.buildDir(context, UriProvider.DIRECTORY_TEXT);
        write(context, file.getAbsolutePath(), name, content);
    }

    /**
     * 写入文件
     *
     * @param context 上下文
     * @param dir     文件夹名字
     * @param name    文件名字
     * @param content 文件内容
     */
    public static void write(Context context, String dir, String name, String content) {
        File fileDir = new File(getExternalCacheDir(context) + File.separator + dir + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File classFile = new File(fileDir.getAbsolutePath(), name);
        if (!classFile.exists()) {
            try {
                classFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(classFile));
            pw.print(content);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Assets文件内容
     *
     * @param context 上下文
     * @param name    文件名
     * @return
     */
    public static String openAssets(Context context, String name) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(name)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取文件数据
     *
     * @return
     */
    public static StringBuffer read(String path) {
        StringBuffer stringBuilder = new StringBuffer();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

    /**
     * 将文件流转成文件
     *
     * @param inputStream 输入流
     * @param path        文件路径
     * @return
     */
    public static File decodeInputStream(InputStream inputStream, String path) {
        File file = new File(path);//文件夹
        if (file.getParentFile().isDirectory() && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入文件操作流程中
        int len;
        byte[] buffer = new byte[2048];
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * File转Bytes
     *
     * @param file
     * @return
     */
    public static byte[] decodeFile(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Bytes转文件
     *
     * @param bytes 字节数据
     * @param path  文件路径
     * @return
     */
    public static File decodeBytes(byte[] bytes, String path) {
        File file = new File(path);
        if (file.getParentFile().exists()) {
            file.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            file = new File(path);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 是否是图片
     *
     * @param file 文件
     * @return
     */
    public static boolean isImage(File file) {
        return ImageProvider.isImage(file);
    }

    /**
     * 是否是图片
     *
     * @param path 路径
     * @return
     */
    public static boolean isImage(String path) {
        return ImageProvider.isImage(path);
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param cls
     * @return
     */
    public static int findResId(String variableName, Class<?> cls) {
        try {
            Field idField = cls.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
