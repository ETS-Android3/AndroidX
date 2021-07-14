package com.androidx.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Author: Relin
 * Describe:
 * Date:2020/11/21 20:35
 */
public class ImageProvider {

    /**
     * 日志标识
     */
    public final static String TAG = ImageProvider.class.getSimpleName();

    /**
     * 构建文件
     *
     * @param context 上下文对象
     * @param type    文件夹类型，例如{@link Environment#DIRECTORY_PICTURES}
     * @param suffix  文件后缀，不包含"."
     * @return
     */
    public static File buildFile(Context context, String type, String suffix) {
        String name = "IMG_" + UriProvider.buildDate() + "." + suffix;
        return UriProvider.buildFile(context, type, name);
    }

    /**
     * 构建文件,默认构建后缀为.jpg的图片文件
     *
     * @param context 上下文对象
     * @return
     */
    public static File buildFile(Context context) {
        String name = "IMG_" + UriProvider.buildDate() + ".jpg";
        return UriProvider.buildFile(context, Environment.DIRECTORY_PICTURES, name);
    }

    /**
     * 构建文件
     *
     * @param context 上下文对象
     * @param type    文件夹类型，例如{@link Environment#DIRECTORY_PICTURES}
     * @return
     */
    public static File buildFile(Context context, String type) {
        String name = "IMG_" + UriProvider.buildDate() + ".jpg";
        return UriProvider.buildFile(context, type, name);
    }

    /**
     * 获取字节
     *
     * @param bitmap 位图
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    /**
     * 获取大小
     *
     * @param bitmap 位图
     * @return
     */
    public static int getSize(Bitmap bitmap) {
        return getBytes(bitmap).length;
    }

    /**
     * 获取图片需要旋转得角度
     *
     * @param path 图片路径
     * @return
     */
    public static int angle(String path) {
        int angle = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
            Log.i(TAG, "->angle = " + angle + ",path = " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return angle;
    }

    /**
     * 选择图片
     *
     * @param source 图片位图
     * @param angle  旋转角度
     * @return
     */
    public static Bitmap rotate(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.i(TAG, "->rotate angle = " + angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * 旋转图片
     *
     * @param pathName  图片路径
     * @param angle     旋转角度
     * @param outWidth  目标宽度
     * @param outHeight 目标高度
     * @return
     */
    public static Bitmap rotate(String pathName, int angle, int outWidth, int outHeight) {
        Bitmap source;
        if (outWidth > 0 && outHeight > 0) {
            source = BitmapFactory.decodeFile(pathName, buildOptions(pathName, outWidth, outHeight));
        } else {
            source = BitmapFactory.decodeFile(pathName);
        }
        Log.i(TAG, "->rotate pathName = " + pathName + ",angle = " + angle + ",outWidth = " + outWidth + ",outHeight = " + outHeight);
        return rotate(source, angle);
    }

    /**
     * 旋转图片
     *
     * @param path  图片路径
     * @param angle 旋转角度
     * @return
     */
    public static Bitmap rotate(String path, int angle) {
        return rotate(path, angle, -1, -1);
    }

    /**
     * 旋转图片
     *
     * @param data      数据
     * @param offset    开始
     * @param length    长度
     * @param angle     角度
     * @param outWidth  目标宽度
     * @param outHeight 目标高度
     * @return
     */
    public static Bitmap rotate(byte[] data, int offset, int length, int angle, int outWidth, int outHeight) {
        Log.i(TAG, "->rotate angle = " + angle + ",outWidth = " + outWidth + ",outHeight = " + outHeight);
        Bitmap source;
        if (outWidth > 0 && outHeight > 0) {
            source = BitmapFactory.decodeByteArray(data, offset, length, buildOptions(data, offset, length, outWidth, outHeight));
        } else {
            source = BitmapFactory.decodeByteArray(data, offset, length);
        }
        return rotate(source, angle);
    }

    /**
     * 旋转图片
     *
     * @param inputStream 输入流
     * @param outPadding  内间距
     * @param angle       角度
     * @param outWidth    目标宽度
     * @param outHeight   目标高度
     * @return
     */
    public static Bitmap rotate(InputStream inputStream, Rect outPadding, int angle, int outWidth, int outHeight) {
        Log.i(TAG, "->rotate angle = " + angle + ",outWidth = " + outWidth + ",outHeight = " + outHeight);
        Bitmap source;
        if (outWidth > 0 && outHeight > 0) {
            source = BitmapFactory.decodeStream(inputStream, outPadding, buildOptions(inputStream, outPadding, outWidth, outHeight));
        } else {
            source = BitmapFactory.decodeStream(inputStream, outPadding, null);
        }
        return rotate(source, angle);
    }


    /**
     * Bitmap压缩为ByteArrayOutputStream
     *
     * @param bitmap 位图
     * @param format 格式
     * @param max    限制大小,压缩到<=max,单位KB
     * @return
     */
    public static ByteArrayOutputStream compress(Bitmap bitmap, Bitmap.CompressFormat format, int max) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, bos);
        int options = 100;
        long length = bos.toByteArray().length;
        long useTime = System.currentTimeMillis();
        Log.i(TAG, "->compress format = " + format + ",max = " + max + "kb");
        Log.i(TAG, "->compress before length = " + (length / 1024) + "kb");
        while (max > 0 && bos.toByteArray().length / 1024 > max) {
            bos.reset();
            options -= 10;
            bitmap.compress(format, options, bos);
        }
        length = bos.toByteArray().length;
        useTime = System.currentTimeMillis() - useTime;
        Log.i(TAG, "->compress after length = " + (length / 1024) + "kb" + ",useTime = " + useTime + "ms");
        return bos;
    }

    /**
     * 压缩图片
     *
     * @param bitmap 位图
     * @param max    限制大小,压缩到< = max,单位KB
     * @param format 格式
     * @return
     */
    public static Bitmap compress(Bitmap bitmap, int max, Bitmap.CompressFormat format) {
        ByteArrayOutputStream bos = compress(bitmap, format, max);
        byte[] bytes = bos.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 压缩图片
     *
     * @param bitmap     图片位图
     * @param format     图片路径
     * @param max        限制大小,压缩到<=max,单位KB
     * @param outPutPath 输出文件路径
     * @return
     */
    public static File compress(Bitmap bitmap, Bitmap.CompressFormat format, int max, String outPutPath) {
        ByteArrayOutputStream bos = compress(bitmap, format, max);
        BufferedOutputStream out = null;
        File file = new File(outPutPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(bos.toByteArray());
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 异步压缩图片
     *
     * @param context      上下文对象
     * @param bitmap       图片位图
     * @param outPutPath   图片输出路径
     * @param format       格式{@link Bitmap.CompressFormat#PNG}
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @param max          限制大小,压缩到<=max,单位KB
     * @return
     */
    public static void compressAsync(Context context, Bitmap bitmap, String outPutPath, Bitmap.CompressFormat format, int targetWidth, int targetHeight, int max, OnImageCompressListener listener) {
        ImageCompressor compressor = new ImageCompressor(context, bitmap, outPutPath, listener);
        compressor.format(format);
        compressor.width(targetWidth);
        compressor.height(targetHeight);
        compressor.max(max);
        compressor.start();
    }

    /**
     * 解析成文件
     *
     * @param bitmap     图片位图
     * @param format     图片路径
     * @param quality    图片质量[1-100]
     * @param outPutPath 输出文件路径
     * @return
     */
    public static File decodeBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int quality, String outPutPath) {
        File outFile = new File(outPutPath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
            bitmap.compress(format, quality, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }

    /**
     * Bitmap解析成文件
     *
     * @param bitmap     位图
     * @param outPutPath 输出路径
     * @return
     */
    public static File decodeBitmap(Bitmap bitmap, String outPutPath) {
        return decodeBitmap(bitmap, Bitmap.CompressFormat.JPEG, 100, outPutPath);
    }

    /**
     * 解析成文件
     *
     * @param context 上下文对象
     * @param bitmap  图片位图
     * @param format  图片路径
     * @param quality 图片质量[1-100]
     * @return
     */
    public static File decodeBitmap(Context context, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        File outFile = buildFile(context);
        return decodeBitmap(bitmap, format, quality, outFile.getAbsolutePath());
    }

    /**
     * 解析成文件
     *
     * @param context 上下文对象
     * @param bitmap  图片位图
     * @return
     */
    public static File decodeBitmap(Context context, Bitmap bitmap) {
        File outFile = buildFile(context);
        return decodeBitmap(bitmap, Bitmap.CompressFormat.JPEG, 100, outFile.getAbsolutePath());
    }

    /**
     * 按大小解析图片
     *
     * @param pathName  图片路径
     * @param outWidth  目标宽度
     * @param outHeight 目标高度
     * @return
     */
    public static Bitmap decodeBounds(String pathName, int outWidth, int outHeight) {
        Log.i(TAG, "->decodeFile outWidth = " + outWidth + ",outHeight = " + outHeight + ",pathName = " + pathName);
        BitmapFactory.Options options = buildOptions(pathName, outWidth, outHeight);
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 按大小解析图片
     *
     * @param inputStream 输入流
     * @param outPadding  内边距
     * @param outWidth    目标宽度
     * @param outHeight   目标高度
     * @return
     */
    public static Bitmap decodeBounds(InputStream inputStream, Rect outPadding, int outWidth, int outHeight) {
        Log.i(TAG, "->decodeStream outWidth = " + outWidth + ",outHeight = " + outHeight);
        BitmapFactory.Options options = buildOptions(inputStream, outPadding, outWidth, outHeight);
        return BitmapFactory.decodeStream(inputStream, outPadding, options);
    }

    /**
     * 按大小解析图片
     *
     * @param data         数据
     * @param offset       开始
     * @param length       长度
     * @param targetWidth  宽度
     * @param targetHeight 高度
     * @return
     */
    public static Bitmap decodeBounds(byte[] data, int offset, int length, int targetWidth, int targetHeight) {
        Log.i(TAG, "->decodeBounds targetWidth = " + targetWidth + ",targetHeight = " + targetHeight);
        BitmapFactory.Options options = buildOptions(data, offset, length, targetWidth, targetHeight);
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    /**
     * 解析为最大值为max的Bitmap,单位kb
     *
     * @param pathName 文件路径
     * @param max      文件最大值，单位kb
     * @return
     */
    public static Bitmap decodeBounds(String pathName, int max) {
        return decodeBounds(pathName, Bitmap.CompressFormat.JPEG, max);
    }

    /**
     * 解析为最大值为max的Bitmap,单位kb
     *
     * @param pathName 文件路径
     * @param format   图片格式
     * @param max      文件最大值，单位kb
     * @return
     */
    public static Bitmap decodeBounds(String pathName, Bitmap.CompressFormat format, int max) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        int inSampleSize = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, bos);
        byte[] bytes = getBytes(bitmap);
        long useTime = System.currentTimeMillis();
        while (bytes.length / 1024 > max) {
            inSampleSize++;
            bitmap = decodeBounds(pathName, bitmap.getWidth() / inSampleSize, bitmap.getHeight() / inSampleSize);
            bos.reset();
            bitmap.compress(format, 100, bos);
            bytes = bos.toByteArray();
        }
        useTime = System.currentTimeMillis() - useTime;
        Log.i(TAG, "->decodeBounds pathName = " + pathName + ",max = " + max + ",inSampleSize = " + inSampleSize + ",useTime = " + useTime + "ms");
        return bitmap;
    }

    /**
     * 构建需要宽宽高的Bitmap options
     *
     * @param pathName  文件路径
     * @param outWidth  目标宽度
     * @param outHeight 目标高度
     * @return
     */
    public static BitmapFactory.Options buildOptions(String pathName, int outWidth, int outHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = Math.min(width / outWidth, height / outHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = inSampleSize;
        options.inPurgeable = true;
        Log.i(TAG, "->buildOptions outWidth = " + outWidth + ",outHeight = " + outHeight + ",inSampleSize = " + inSampleSize + ",pathName = " + pathName);
        return options;
    }

    /**
     * 构建需要宽宽高的Bitmap options
     *
     * @param data      数据
     * @param offset    开始
     * @param length    长度
     * @param outWidth  目标宽度
     * @param outHeight 目标高度
     * @return
     */
    public static BitmapFactory.Options buildOptions(byte[] data, int offset, int length, int outWidth, int outHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = Math.min(width / outWidth, height / outHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPurgeable = true;
        Log.i(TAG, "->buildOptions outWidth = " + outWidth + ",outHeight = " + outHeight + ",inSampleSize = " + inSampleSize);
        return options;
    }

    /**
     * 构建需要宽宽高的Bitmap options
     *
     * @param inputStream 输入流
     * @param outPadding  内间距
     * @param outWidth    目标宽度
     * @param outHeight   目标高度
     * @return
     */
    public static BitmapFactory.Options buildOptions(InputStream inputStream, Rect outPadding, int outWidth, int outHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, outPadding, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = Math.min(width / outWidth, height / outHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPurgeable = true;
        Log.i(TAG, "->buildOptions outWidth = " + outWidth + ",outHeight = " + outHeight + ",inSampleSize = " + inSampleSize);
        return options;
    }

    /**
     * 判断文件是否是图片
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap == null) {
            return false;
        }
        return true;
    }

    /**
     * 是否是图像
     *
     * @param path 图像路径
     * @return
     */
    public static boolean isImage(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        String format = path.toUpperCase();
        if (format.endsWith(".JPG") || path.endsWith(".JPEG") || path.endsWith(".PNG")) {
            return true;
        }
        return false;
    }

    /**
     * 多行Base64处理为单行
     *
     * @param base64 base64字符
     * @return
     */
    public static String singleBase64(String base64) {
        return base64.replaceAll("[\\s*\t\n\r]", "");
    }

    /**
     * 文件转字符串
     *
     * @param file
     * @return
     */
    public static String encodeBase64(File file) {
        return encodeBase64(file, false);
    }

    /**
     * 文件转字符串
     *
     * @param file   文件
     * @param encode 是否URLEncoder
     * @return
     */
    public static String encodeBase64(File file, boolean encode) {
        byte[] bytes = IOProvider.decodeFile(file);
        String stringFile = Base64.encodeToString(bytes, Base64.DEFAULT);
        if (encode) {
            try {
                stringFile = URLEncoder.encode(stringFile, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringFile;
    }

    /**
     * Base64转File
     *
     * @param base64 Base64字符
     * @param path   路径
     */
    public static File decodeBase64(String base64, String path) {
        return decodeBase64(base64, path, false);
    }

    /**
     * Base64转File
     *
     * @param base64 Base64字符
     * @param path   路径
     * @param decode 是否URL解密
     */
    public static File decodeBase64(String base64, String path, boolean decode) {
        File file = new File(path);
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
        try {
            byte[] buffer = Base64.decode(decode ? URLDecoder.decode(base64, "UTF-8") : base64, Base64.CRLF);
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, TAG + " decodeBase64 Exception:" + e.toString());
        }
        return file;
    }

    /**
     * 文件转Base64String
     *
     * @param bitmap 位图
     * @return
     */
    public static String encodeBase64(Bitmap bitmap) {
        return encodeBase64(bitmap, false);
    }

    /**
     * 图片转Base64String
     *
     * @param bitmap 位图
     * @param encode 是否Url加密
     * @return
     */
    public static String encodeBase64(Bitmap bitmap, boolean encode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String stringBitmap = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        if (encode) {
            try {
                stringBitmap = URLEncoder.encode(stringBitmap, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBitmap;
    }

    /**
     * Base64转Bitmap,默认不URLDecoder
     *
     * @param base64 图片Base64文本
     * @return
     */
    public static Bitmap decodeBase64(String base64) {
        return decodeBase64(base64, false);
    }

    /**
     * Base64转Bitmap
     *
     * @param base64 图片Base64文本
     * @param decode 是否Url解密
     * @return
     */
    public static Bitmap decodeBase64(String base64, boolean decode) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(decode ? URLDecoder.decode(base64, "UTF-8") : base64, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 解析Uri为Bitmap
     *
     * @param context 上下文
     * @param uri     位图Uri
     * @return
     */
    public static Bitmap decodeUri(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}
