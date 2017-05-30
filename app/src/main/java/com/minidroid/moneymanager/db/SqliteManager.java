package com.minidroid.moneymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 * 数据库的管理类
 * Created by minidroid on 2017/4/20 18:10.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class SqliteManager {
    private static volatile SqliteManager mInstance;
    private File mFile;

    private SqliteManager(final Context context) {
        String path;
        // 判断SD卡是否存在,并且申请读写权限
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "理财小能手";
        } else {
//            getFilesDir()表示/data/data/<application package>/files目录
            path = context.getFilesDir() + File.separator + "data";
        }
        boolean success = new File(path).mkdirs();
        mFile = new File(path, "data.db");

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        //创建数据库表（用户表、收支表、账户表、时间表）
        db.execSQL("create table if not exists user(_id INTEGER PRIMARY KEY AUTOINCREMENT,username varchar(20)," +
                "password varchar(32),phone varchar(11),email varchar(255),head INTEGER)");
        db.execSQL("create table if not exists inout(_id INTEGER PRIMARY KEY AUTOINCREMENT,year " +
                "INTEGER,month INTEGER,day INTEGER,week INTEGER,resourceid INTEGER,money varchar(20)," +
                "inout varchar(20),class varchar(20),account varchar(20),time varchar(20),other varchar(60))");
        db.execSQL("create table if not exists account(_id INTEGER PRIMARY KEY AUTOINCREMENT,accountname varchar(20),money varchar(20),user_id INTEGER)");
        db.execSQL("create table if not exists time(_id INTEGER PRIMARY KEY AUTOINCREMENT,time varchar(20),value varchar(20))");
        db.close();
    }

    /**
     * 获取数据库管理类的实例（单例）
     *
     * @param context
     * @return
     */
    public static SqliteManager getInstance(Context context) {
        //双重校验锁
        if (mInstance == null) {
            synchronized (SqliteManager.class) {
                if (mInstance == null) {
                    mInstance = new SqliteManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 查询记录是否在表中
     *
     * @param table
     * @param where
     * @param args
     * @return
     */
    public boolean isExistInTable(String table, String where, String[] args) {
        boolean flag = false;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        Cursor cursor = db.query(table, null, where, args, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;
    }

    /**
     * 插入记录到表中
     *
     * @param table
     * @param cv
     * @return
     */
    public boolean insertItem(String table, ContentValues cv) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        if (db.insert(table, null, cv) == 1) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    /**
     * 从表中删除记录
     *
     * @param table
     * @param where
     * @param args
     * @return
     */
    public boolean delteItem(String table, String where, String[] args) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        int i = db.delete(table, where, args);
        db.close();
        if (i > 0) return true;
        return false;
    }

    /**
     * 从表中更新记录
     *
     * @param table
     * @param where
     * @param args
     * @param values
     * @return
     */
    public boolean updateItem(String table, String where, String[] args, ContentValues values) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        int i = db.update(table, values, where, args);
        db.close();
        if (i > 0) return true;
        return false;
    }

    /**
     * 从表中查询记录
     *
     * @param table
     * @param where
     * @param args
     * @return
     */
    public QueryResult query(String table, String where, String[] args) {
        QueryResult queryResult = new QueryResult();
        queryResult.db = SQLiteDatabase.openOrCreateDatabase(mFile, null);
        queryResult.cursor = queryResult.db.query(table, null, where, args, null, null, null);
        return queryResult;
    }

    /**
     * 查询结果封装内部类
     */
    public class QueryResult {
        public SQLiteDatabase db;
        public Cursor cursor;
    }
}

