package org.jesusgift.clienttest;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        } catch (FileNotFoundException e) {}
        return null;
    }

    /**
     *Retreives all gallery images sorted by newest first
     * @param context Context
     * @return Cursor cursor
     * */
    public static Cursor getAllGalleryImages(Context context){
        File camDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());


        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        Cursor c = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = 'Camera' ",
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        );

        return c;
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
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public static File storeImage(Bitmap image, File path) {
        String path2 = path.getAbsolutePath()+"/tmp_img.jpg";

        File file = new File(path2);
        if(file.exists()) {
            file.delete();
            file = new File(path2);
        }
        if (file == null) {
            Log.d("Utility",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
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

}
