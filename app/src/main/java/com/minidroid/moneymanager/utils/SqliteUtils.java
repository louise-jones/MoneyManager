package com.minidroid.moneymanager.utils;

import android.content.ContentValues;
import android.content.Context;

import com.minidroid.moneymanager.db.SqliteManager;

/**
 * Sqlite工具类
 * @author minidroid
 * @date 2017/4/21
 */
public class SqliteUtils {
    public static void update(Context context, String count, Double changeMoney) {
        SqliteManager.QueryResult result = SqliteManager.getInstance(context).
                query("account", "accountname=?", new String[]{count});
        if (result.cursor != null && result.cursor.getCount() != 0) {
            result.cursor.moveToFirst();
            Double money = result.cursor.getDouble(result.cursor.getColumnIndex("money"));
            //Double changeValues = Double.parseDouble(changeMoney);
            ContentValues updateValues = new ContentValues();
            updateValues.put("money", money + changeMoney);
            SqliteManager.getInstance(context).updateItem
                    ("account", "accountname=?", new String[]{count}, updateValues);
        }
        result.cursor.close();
        result.db.close();
    }
}
