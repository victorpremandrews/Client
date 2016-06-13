package org.jesusgift.clienttest.Helpers;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class MyUtility {

    public static final String TAG = "Utility";

    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=250;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all gallery images sorted by newest first
     * @param context Context
     * @return Cursor cursor
     * */
    public static Cursor getAllGalleryImages(Context context, String idList){
        String inClause = "";
        if(idList != null)
            inClause = idList;

        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };



        //MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = 'Camera' AND "+
        return context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.ImageColumns._ID+" NOT IN ("+inClause+") ",
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT 0, 3"
        );
    }

    public static int getGalleryImagesCount(Context context) {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        //MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = 'Camera' AND "+
        Cursor c =  context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED
        );

        if(c != null ) {
            return c.getCount();
        }
        return 0;
    }

    public static Cursor getRecentImage(Context context){
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        return context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT 0, 4"
        );
    }

    /**
     * To Check whether the file Exists in path
     * @param path String File Path
     * @return boolean
     * */
    public static boolean isFileExists(String path){
        File file = new File(path);
        if(file.exists())
            return true;

        return false;
    }

    /**
     * Decode Bitmap to Base64
     * @param image Bitmap Image Bitmap
     * @return String Encoded Image String
     * */
    public static String decodeBitmap(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Function to store bitmap to provided path
     * @param image [Image Bitmap]
     * @param path [Folder Path to store]
     * @return file [File of the stored bitmap]
     * */
    public static File storeImage(Bitmap image, File path) {
        String path2 = path.getAbsolutePath()+"/tmp_"+Math.random()+".jpg";
        File file = new File(path2);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return file;
    }

    /**
     * Clear Cache Directory
     * @param dir
     * */
    public static void clearCacheDir(File dir) {
        if(dir.exists()) {
            for (File f : dir.listFiles()) {
                boolean res = f.delete();
            }
        }
    }

    /**
     * Function to check if the phone is connected to internet
     * @param context
     * @return boolean
     * */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Function to get the google username
     * @param context
     * @return string
     * */
    public static String getUsername(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    /**
     * Function to return the device unique id
     * @param context
     * @return string
     * */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}
