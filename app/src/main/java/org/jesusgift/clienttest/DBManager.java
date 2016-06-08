package org.jesusgift.clienttest;/* *
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
import android.util.Log;

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
            Log.i(TAG, "TABLE "+TABLE_PICS+" CREATED!");
        } catch (Exception e) {
            Log.e(TAG, "Error in Query : "+tblPicsQuery);
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
        Log.d(TAG, "STORE ID : "+storeId+" INSERTED!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PICS);
        onCreate(db);
    }
}
