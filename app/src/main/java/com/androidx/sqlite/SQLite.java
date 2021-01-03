package com.androidx.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Relin
 * on 2018/9/15.<br/>
 * 抽象类是基础utils在你扩展这个
 * 使用数据库时可以使用。在createTable()方法中创建表
 * 在upGradeDatabase()中更新你的表数据库
 */

public class SQLite implements OnSQLiteOpenListener {

    /**
     * NULL
     */
    public static final String TYPE_NULL = "null";
    /**
     * 有符号整形，根据值的大小以1,2,3,4,6或8字节存放
     */
    public static final String TYPE_INTEGER = "integer";
    /**
     * 浮点型值，以8字节IEEE浮点数存放
     */
    public static final String TYPE_REAL = "real";
    /**
     * 文本字符串，使用数据库编码（UTF-8，UTF-16BE或者UTF-16LE）存放 、
     */
    public static final String TYPE_TEXT = "text";
    /**
     * 一个数据块，完全按照输入存放（即没有准换）
     */
    public static final String TYPE_BLOB = "blob";
    /**
     * 数值类型
     */
    public static final String TYPE_NUMERIC = "numeric";

    private final String TAG = "SQLiteHelper";
    private final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME = "AndroidSQLite.db";
    protected final String CREATE_TABLE_HEAD = "CREATE TABLE IF NOT EXISTS ";
    protected final String CREATE_PRIMARY_KEY = "TAB_ID INTEGER PRIMARY KEY AUTOINCREMENT,";

    private int version;
    private Context context;
    private SQLiteDatabase db;
    private String databaseName;
    private SQLiteOpen sqLiteOpen;
    private static SQLite sqLiteHelper;
    private OnSQLiteListener listener;

    /**
     * 基础的数据库构造方法<br/>
     *
     * @param context 上下文
     */
    public SQLite(Context context) {
        this.context = context;
        databaseName = DATABASE_NAME;
        version = DATABASE_VERSION;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteOpen(context, databaseName, null, version, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     *
     * @param context
     * @param databaseVersion you should upgrade you database when version change
     */
    public SQLite(Context context, int databaseVersion) {
        this.context = context;
        databaseName = DATABASE_NAME;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteOpen(context, databaseName, null, databaseVersion, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     *
     * @param context
     * @param databaseName if you want to defined you database path
     *                     you should add a path before of database name.
     * @param version      you should upgrade you database when version change
     */
    public SQLite(Context context, String databaseName, int version) {
        this.context = context;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteOpen(context, databaseName, null, version, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 获取数据库对象
     *
     * @param context 上下文对象
     * @return
     */
    public static SQLite with(Context context) {
        if (sqLiteHelper == null) {
            synchronized (SQLite.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLite(context);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 获取数据库助手
     *
     * @param context      上下文
     * @param databaseName 数据库名称
     * @param version      数据库版本号
     * @return
     */
    public static SQLite with(Context context, String databaseName, int version) {
        if (sqLiteHelper == null) {
            synchronized (SQLite.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLite(context, databaseName, version);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 获取数据库助手
     *
     * @param context         上下文
     * @param databaseVersion 数据库版本号
     * @return
     */
    public static SQLite with(Context context, int databaseVersion) {
        if (sqLiteHelper == null) {
            synchronized (SQLite.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLite(context, databaseVersion);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 设置数据库助手监听
     *
     * @param listener
     */
    public void setOnSQLiteListener(OnSQLiteListener listener) {
        this.listener = listener;
    }

    /**
     * 创建数据表
     * 例如: create table if not exists user (_id integer primary key autoincrement,user_name text,user_sex text,user_pwd text)
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (listener != null) {
            listener.onCreate(db);
        }
    }

    /**
     * 升级数据库
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (listener != null) {
            listener.onUpgrade(db, version, newVersion);
        }
    }

    /**
     * 建表
     *
     * @param table   表名
     * @param columns 列名
     * @return
     */
    public void createTable(String table, String[] columns) {
        if (TextUtils.isEmpty(table)) {
            return;
        }
        if (columns == null) {
            return;
        }
        if (columns.length == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + table);
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        for (String key : columns) {
            sb.append(key + " text");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.i(TAG, "create table = " + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 建表
     *
     * @param table   表名
     * @param columns 列名
     * @param types   数据类型
     * @return
     */
    public void createTable(String table, String[] columns, String[] types) {
        if (TextUtils.isEmpty(table)) {
            return;
        }
        if (columns == null) {
            return;
        }
        if (columns.length == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + table);
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i] + " " + types[i]);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.i(TAG, "create table = " + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 创建表
     *
     * @param cls 类名
     */
    public void createTable(Class<?> cls) {
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + cls.getSimpleName());
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            if (!name.startsWith("$") && !name.equals("serialVersionUID")) {
                sb.append(name + " text");
                sb.append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        db.execSQL(sb.toString());
    }

    /**
     * 插入数据
     *
     * @param table
     * @param contentValues
     * @return
     */
    public long insert(String table, ContentValues contentValues) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.insert(table, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "->insert exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 插入对象数据
     *
     * @param obj
     * @return 插入数据
     */
    public long insert(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String key = field.getName();
            String value = "";
            try {
                Class<?> fieldType = field.getType();
                if (!key.equals("$change") && !key.equals("serialVersionUID")) {
                    if (fieldType == String.class || fieldType == Character.class) {
                        contentValues.put(key, (String) field.get(obj));
                    }
                    if (fieldType == int.class) {
                        contentValues.put(key, (int) field.get(obj));
                    }
                    if (fieldType == long.class) {
                        contentValues.put(key, (long) field.get(obj));
                    }
                    if (fieldType == double.class) {
                        contentValues.put(key, (double) field.get(obj));
                    }
                    if (fieldType == float.class) {
                        contentValues.put(key, (float) field.get(obj));
                    }
                    if (fieldType == boolean.class) {
                        contentValues.put(key, (boolean) field.get(obj));
                    }
                    if (fieldType == short.class) {
                        contentValues.put(key, (short) field.get(obj));
                    }
                    //如果不是一般数据类型
                    if (!fieldType.isPrimitive() && fieldType == List.class) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            List<?> list = (List<?>) field.get(obj);
                            insert(list.get(i));
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return insert(cls.getSimpleName(), contentValues);
    }

    /**
     * 插入数据
     *
     * @param sql sql语句
     * @return
     */
    public void insert(String sql) {
        execSQL(sql);
    }

    /**
     * 删除数据
     *
     * @param table
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return 删除的条数
     */
    public long delete(String table, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "->delete exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     *
     * @param table
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return 删除的条数
     */
    public long delete(Class table, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table.getSimpleName(), whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "->delete exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     *
     * @param sql sql语句
     * @return
     */
    public void delete(String sql) {
        execSQL(sql);
    }

    /**
     * 更新数据
     *
     * @param table
     * @param contentValues you is gonging to update values
     * @param whereClause   for example "name = ?"
     * @param whereArgs     for example new String[]{"Mary"}
     * @return
     */
    public long update(String table, ContentValues contentValues, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.update(table, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "->update exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     *
     * @param obj         data object
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return
     */
    public long update(Object obj, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field != null) {
                field.setAccessible(true);
                String name = field.getName();
                String value = "";
                try {
                    value = String.valueOf(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!name.equals("$change") && !name.equals("serialVersionUID")) {
                    contentValues.put(name, value);
                }
            }
        }
        try {
            result = db.update(obj.getClass().getSimpleName(), contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "->update exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     * 例如 update user set user_name = 'Jerry' where user_name = 'Mary'
     *
     * @param sql
     * @return
     */
    public void update(String sql) {
        execSQL(sql);
    }

    /**
     * 查询数据
     *
     * @param sql 数据库语句
     * @return
     */
    public List<Map<String, String>> query(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<Map<String, String>> queryList = new ArrayList<Map<String, String>>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                map.put(columnNames[i], cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            queryList.add(map);
        }
        cursor.close();
        return queryList;
    }

    /**
     * 查询数据
     *
     * @param cls 实体类
     * @param <T> 实体类泛型
     * @return
     */
    public <T> List<T> query(Class<T> cls) {
        return query(cls, "select * from " + cls.getSimpleName());
    }

    /**
     * 查询数据
     *
     * @param cls 实体类
     * @param sql sql语句
     * @param <T> 实体类泛型
     * @return 实体列表
     */
    public <T> List<T> query(Class<T> cls, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<T> queryList = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T bean = null;
            try {
                bean = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < columnNames.length; i++) {
                Field field = findField(cls, columnNames[i]);
                if (field != null) {
                    field.setAccessible(true);
                    try {
                        if (field.getType() == String.class) {
                            field.set(bean, cursor.getString(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (field.getType() == long.class) {
                            field.set(bean, cursor.getLong(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (field.getType() == int.class) {
                            field.set(bean, cursor.getInt(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (field.getType() == float.class) {
                            field.set(bean, cursor.getFloat(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (field.getType() == double.class) {
                            field.set(bean, cursor.getDouble(cursor.getColumnIndex(columnNames[i])));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            queryList.add(bean);
        }
        cursor.close();
        return queryList;
    }

    /**
     * 查找存在的字段
     *
     * @param cls  类
     * @param name 字段名
     * @return
     */
    protected Field findField(Class cls, String name) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 执行SQL语句
     *
     * @param sql sql语句
     */
    public void execSQL(String sql) {
        Log.i(TAG, "->exec sql sql = " + sql);
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();//Notice:if you beginTransaction() method and endTransaction() method you must use this method.if you don't while your data insert、update、delete fail.
        } catch (Exception e) {
            Log.e(TAG, "->exec sql exception = " + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表
     *
     * @param table 数据表
     */
    public void dropTable(String table) {
        db.beginTransaction();
        String sql = "drop table if exists " + table;
        try {
            db.execSQL(sql);
            Log.i(TAG, "->drop table sql = " + sql);
            db.setTransactionSuccessful();//Notice:if you beginTransaction() method and endTransaction() method you must use this method.if you don't while your data insert、update、delete fail.
        } catch (Exception e) {
            Log.e(TAG, TAG + "->exec sql exception =" + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表中的数据
     *
     * @param table
     */
    public void deleteTable(Class table) {
        deleteTable(table.getSimpleName());
    }

    /**
     * 清除表中数据
     *
     * @param table 数据表
     */
    public void deleteTable(String table) {
        db.beginTransaction();
        //除去表内的数据，但并不删除表本身
        String sql = "delete from " + table;
        try {
            db.execSQL(sql);
            Log.i(TAG, "->drop table sql = " + sql);
            db.setTransactionSuccessful();//Notice:if you beginTransaction() method and endTransaction() method you must use this method.if you don't while your data insert、update、delete fail.
        } catch (Exception e) {
            Log.e(TAG, "->exec sql exception = " + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除数据库
     */
    public void dropDatabase() {
        context.deleteDatabase(sqLiteOpen.getDatabaseName());
        Log.i(TAG, "->drop database database Name = " + sqLiteOpen.getDatabaseName());
    }

}
