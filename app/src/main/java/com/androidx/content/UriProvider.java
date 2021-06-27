package com.androidx.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Relin
 * Describe:Uri提供者
 * Date:2020/11/21 15:51
 */
public class UriProvider {

    /**
     * 日志标识
     */
    public static String TAG = UriProvider.class.getSimpleName();
    /**
     * 提供者文件夹
     */
    public final static String DIRECTORY_PROVIDER = "Provider";
    /**
     * 图像
     */
    public static final String DIRECTORY_IMAGE = "Images";
    /**
     * 图片
     */
    public static final String DIRECTORY_PICTURE = "Pictures";
    /**
     * 文档
     */
    public static final String DIRECTORY_DOCUMENT = "Documents";
    /**
     * 错误
     */
    public static final String DIRECTORY_BUG = "Bugs";
    /**
     * 文本
     */
    public static final String DIRECTORY_TEXT = "Texts";
    /**
     * Office文件
     */
    public static final String DIRECTORY_OFFICE = "Offices";
    /**
     * 日志
     */
    public static final String DIRECTORY_LOG = "Logs";
    /**
     * 相对路径
     */
    public static String DIRECTORY_RELATIVE = Environment.DIRECTORY_DOCUMENTS;
    /**
     * 相对路径 - 图像
     */
    public static String DIRECTORY_IMAGE_RELATIVE = Environment.DIRECTORY_DCIM;
    /**
     * 相对路径 - 图像
     */
    public static String DIRECTORY_PICTURE_RELATIVE = Environment.DIRECTORY_PICTURES;

    /**
     * 内存卡是否挂载
     *
     * @return
     */
    public static boolean isMounted() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 是否是外部储存文件
     *
     * @param uri
     * @return
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是下载文件
     *
     * @param uri
     * @return
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是媒体文件
     *
     * @param uri
     * @return
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是小米文件
     *
     * @param uri
     * @return
     */
    public static boolean isMttExternalStorageDocument(Uri uri) {
        return "com.tencent.mtt.fileprovider".equals(uri.getAuthority());
    }

    /**
     * 是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 是否可读
     *
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 获取缓存文件夹
     *
     * @param context 上下文对象
     * @param dir     文件夹名
     * @return
     */
    public static String getExternalCacheDir(Context context, String dir) {
        File directory = new File(context.getExternalCacheDir(), dir);
        return directory.getAbsolutePath();
    }

    /**
     * 删除缓存文件夹
     *
     * @param context 上下文对象
     * @param dir     文件夹名
     */
    public static void deleteExternalCacheDir(Context context, String dir) {
        File directory = new File(context.getExternalCacheDir(), dir);
        if (directory.exists()) {
            directory.delete();
        }
    }

    /**
     * 获取挂载的缓存文件夹
     *
     * @param context 上下文
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        File directory = context.getExternalCacheDir();
        return directory.getAbsolutePath();
    }

    /**
     * 删除缓存文件夹
     *
     * @param context 上下文对象
     */
    public static void deleteExternalCacheDir(Context context) {
        File directory = context.getExternalCacheDir();
        if (directory.exists()) {
            directory.delete();
        }
    }

    /**
     * 是否是图片
     *
     * @param context 上下文
     * @param uri     Uri
     * @return
     */
    public static boolean isImage(Context context, Uri uri) {
        if (uri == null || context == null) {
            return false;
        }
        return ImageProvider.isImage(getPath(context, uri));
    }

    /**
     * 构建时间
     *
     * @return
     */
    public static String buildDate() {
        return buildDate(null);
    }

    /**
     * 构建时间
     *
     * @param pattern 时间格式
     * @return
     */
    public static String buildDate(String pattern) {
        if (pattern == null || pattern.length() == 1) {
            pattern = "yyyyMMdd_HHmmss";
        }
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 构建图片名称
     *
     * @return
     */
    public static String buildImageName() {
        return "IMG_" + buildDate() + ".jpg";
    }

    /**
     * 构建内容提供者Uri
     *
     * @param context   上下文对象
     * @param file      文件
     * @param authority 权限
     * @return
     */
    public static Uri buildProviderUri(Context context, File file, @NonNull String authority) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, authority, file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        Log.i(TAG, "->buildProviderUri uri = " + contentUri.toString());
        return contentUri;
    }

    /**
     * 构建文件Uri
     *
     * @param file 文件
     * @return
     */
    public static Uri buildFileUri(File file) {
        Uri uri = Uri.fromFile(file);
        Log.i(TAG, "->buildFileUri uri = " + uri.toString());
        return uri;
    }

    /**
     * 构建文件夹
     *
     * @param context 上下文
     * @param type    类型名称，可自定义或使用一下定义常量
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.
     * @return
     */
    public static File buildDir(Context context, String type) {
        File directory = new File(context.getExternalCacheDir(), type);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    /**
     * 构建文件
     *
     * @param context 上下文对象
     * @param type    类型名称，可自定义或使用一下定义常量
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.
     * @param name    文件名称
     * @return
     */
    public static File buildFile(Context context, String type, String name) {
        File directory = buildDir(context, type);
        File file = new File(directory, name);
        Log.i(TAG, "->buildFile type = " + type + ",file=" + file.getAbsolutePath());
        return file;
    }

    /**
     * 构建文件
     *
     * @param directory 文件夹
     * @param name
     * @return
     */
    public static File buildFile(File directory, String name) {
        final File file = new File(directory, name);
        Log.i(TAG, "->buildFile name = " + name + ",file=" + file.getAbsolutePath());
        return file;
    }

    /**
     * 获取问价类型
     *
     * @param context   上下文对象
     * @param file      文件
     * @param authority 权限
     * @return
     */
    public static String getMimeType(Context context, File file, @NonNull String authority) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        ContentResolver resolver = context.getContentResolver();
        return resolver.getType(uri);
    }

    /**
     * 获取文件类型
     *
     * @param context 上下文对象
     * @param uri     URI
     * @return
     */
    public static String getMimeType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    /**
     * 插入文件到缓存
     *
     * @param context 上下文对象
     * @param dirName 文件夹名称
     * @return
     */
    public static String insertExternalCacheDir(Context context, String dirName, Uri srcUri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, srcUri);
        File directory = new File(context.getExternalCacheDir(), dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File outFile = buildFile(directory, documentFile.getName());
        copy(context, srcUri, outFile.getAbsolutePath());
        return outFile.getAbsolutePath();
    }

    /**
     * 拷贝文件
     *
     * @param context 上下文对象
     * @param srcUri  资源Uri
     * @param outPath 输出路径
     */
    public static void copy(Context context, Uri srcUri, String outPath) {
        File outFile = new File(outPath);
        OutputStream os = null;
        InputStream is = null;
        try {
            os = new FileOutputStream(outFile);
            is = context.getContentResolver().openInputStream(srcUri);
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                os.write(buffer);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除Uri
     *
     * @param context 上下文对象
     * @param uri     Uri
     */
    public static int delete(Context context, Uri uri) {
        return context.getContentResolver().delete(uri, null, null);
    }

    /**
     * 删除URI
     *
     * @param context       上下文对象
     * @param uri           Uri
     * @param where         条件
     * @param selectionArgs 条件值
     */
    public static int delete(Context context, Uri uri, String where, String[] selectionArgs) {
        return context.getContentResolver().delete(uri, where, selectionArgs);
    }

    /**
     * 更新Uri
     *
     * @param context       上下文对象
     * @param uri           Uri
     * @param values        数据
     * @param where         条件
     * @param selectionArgs 条件值
     */
    public static int update(Context context, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        return context.getContentResolver().update(uri, values, where, selectionArgs);
    }

    /**
     * 获取文件路径
     *
     * @param context 上下文对象
     * @param uri     URI
     * @return
     */
    public static String getData(Context context, Uri uri) {
        return getData(context, uri, null, null);
    }

    /**
     * 添加文件到媒体扫描
     *
     * @param context 上下文
     * @param file    文件
     * @return
     */
    public static Uri insertMediaStoreImage(final Context context, File file) {
        return insertMediaStoreImage(context, file, DIRECTORY_PICTURE_RELATIVE);
    }

    /**
     * 添加文件到媒体扫描
     *
     * @param context      上下文
     * @param file         文件
     * @param relativePath 相对路径名称
     */
    public static Uri insertMediaStoreImage(final Context context, File file, String relativePath) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Uri uri = Uri.fromFile(file);
            Log.i(TAG, "->insertMediaStoreImage sendBroadcast Uri = " + uri.toString());
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            mediaScanIntent.setData(uri);
            context.sendBroadcast(mediaScanIntent);
            return uri;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.i(TAG, "->insertMediaStoreImage resolver.insert sendBroadcast Uri = " + file.getAbsolutePath());
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues(5);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, file.length());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
            //Action scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            context.sendBroadcast(mediaScanIntent);
            return uri;
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.i(TAG, "->insertMediaStoreImage openOutputStream Uri = " + file.getAbsolutePath());
            try {
                InputStream is = new FileInputStream(file);
                OutputStream os = context.getContentResolver().openOutputStream(uri);
                byte[] bytes = new byte[1024];
                while (is.read(bytes) != -1) {
                    os.write(bytes);
                }
                os.flush();
                os.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return uri;
        }
    }

    /**
     * 通过内容提供者获取文件路径
     *
     * @param context       上下文对象
     * @param uri           URI
     * @param selection     条件
     * @param selectionArgs 条件值
     * @return
     */
    public static String getData(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String path = null;
        final String column = MediaStore.Files.FileColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                Log.i(TAG, "->getData cursor count=" + cursor.getCount());
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(column));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "->getData failed exception=" + e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * 获取文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        if (uri != null) {
            Log.i(TAG, "->getPath uri=" + uri.toString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                Log.i(TAG, "->getPath isExternalStorageDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                Log.i(TAG, "->getPath isDownloadsDocument id=" + id);
                if (id.startsWith("raw:")) {
                    return id.replace("raw:", "");
                }
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getData(context, contentUri, null, null);
            }
            if (isMediaDocument(uri)) {
                final String documentId = DocumentsContract.getDocumentId(uri);
                final String[] split = documentId.split(":");
                final String type = split[0];
                Log.i(TAG, "->getPath isMediaDocument documentId=" + documentId + ",type=" + type);
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else if ("document".equals(type)) {
                    return UriProvider.insertExternalCacheDir(context, UriProvider.DIRECTORY_DOCUMENT, uri);
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getData(context, contentUri, selection, selectionArgs);
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String scheme = uri.getScheme();
                String authority = uri.getAuthority();
                String uriString = uri.toString();
                Log.i(TAG, "uri=" + uri.toString() + ",scheme=" + scheme + ",authority=" + authority);
                if (isMttExternalStorageDocument(uri)) {
                    String qqBrowser = "content://" + authority + "/QQBrowser/";
                    if (uriString.startsWith(qqBrowser)) {
                        try {
                            return URLDecoder.decode(uriString,"UTF-8").replace(qqBrowser, Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return getData(context, uri, null, null);
            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                Log.i(TAG, "->getPath file =" + uri.getPath());
                return uri.getPath();
            }
        }
        return null;
    }

}
