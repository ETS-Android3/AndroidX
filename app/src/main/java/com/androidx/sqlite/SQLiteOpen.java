package com.androidx.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Relin
 * on 2018-09-14.<br/>
 * 数据库打开工具，该工具是继承系统SDK的SQLiteOpenHelper类，
 * 可以帮助你打开或创建一个数据库，同时监听数据库的创建和升级。<br/>
 */
public class SQLiteOpen extends SQLiteOpenHelper {

    private OnSQLiteOpenListener helperListener;

    public SQLiteOpen(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, OnSQLiteOpenListener helperListener) {
        super(context, name, factory, version);
        this.helperListener = helperListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (helperListener != null) {
            helperListener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (helperListener != null) {
            helperListener.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
