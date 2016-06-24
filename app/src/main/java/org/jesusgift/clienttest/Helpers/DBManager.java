package org.jesusgift.clienttest.Helpers;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import org.jesusgift.clienttest.Helpers.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    public static final String TAG = "DBManager";
    public static final String DATABASE_NAME = "MediaStore.db";

    public static final String TABLE_PICS = "tbl_pics";
    public static final String COLUMN_PICS_ID = "id";
    public static final String COLUMN_PICS_STORE_ID = "store_id";
    public static final String COLUMN_PICS_IS_UPLOADED = "is_uploaded";

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, AppConfig.DB_NAME, factory, AppConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tblPicsQuery = "CREATE TABLE "+TABLE_PICS+" ( " +
                COLUMN_PICS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_PICS_STORE_ID + " TEXT,"+
                COLUMN_PICS_IS_UPLOADED +" INTEGER )";

        try {
            db.execSQL(tblPicsQuery);
            //i(TAG, "TABLE "+TABLE_PICS+" CREATED!");
        } catch (Exception e) {
            //Log.e(TAG, "Error in Query : "+tblPicsQuery);
        }

    }

    public boolean isMediaPresent(String storeId) {
        String query = "SELECT * FROM "+TABLE_PICS+" WHERE "+COLUMN_PICS_STORE_ID +" = "+storeId;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            return true;
        }
        db.close();
        return false;
    }

    public void insertMedia(String storeId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PICS_IS_UPLOADED, "1");
        contentValues.put(COLUMN_PICS_STORE_ID, storeId);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PICS, null, contentValues);
        db.close();
        //Log.i(TAG, "STORE ID : "+storeId+" INSERTED!");
    }

    public boolean insertMedia(List<String> idList) {
        if( idList != null) {
            SQLiteDatabase db = getWritableDatabase();
            for (String id : idList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PICS_IS_UPLOADED, "1");
                values.put(COLUMN_PICS_STORE_ID, id);

                db.insert(TABLE_PICS, null, values);
                //Log.i(TAG, "STORE ID : "+id+" INSERTED!");
            }
            db.close();
        }
        return true;
    }

    public String getMediaIdsAsCommaSeperated() {
        try(SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT "+COLUMN_PICS_STORE_ID+" FROM "+TABLE_PICS+" WHERE "+COLUMN_PICS_IS_UPLOADED+" = 1";
            Cursor c = db.rawQuery(query, null);
            List<String> idList = new ArrayList<>();
            while (c.moveToNext()) {
                idList.add(c.getString(0));
            }
            if(idList.size() > 0)
                return TextUtils.join(",", idList);
        }
        return null;
    }

    public String getLastInsertedMediaID() {
        try(SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT MAX("+COLUMN_PICS_STORE_ID+") FROM "+TABLE_PICS+" WHERE 1";
            Cursor c = db.rawQuery(query, null);

            if(c != null && c.moveToFirst()) {
                if(c.getString(0) != null) {
                    return c.getString(0);
                }
            }
        }
        return "0";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PICS);
        onCreate(db);
    }
}
