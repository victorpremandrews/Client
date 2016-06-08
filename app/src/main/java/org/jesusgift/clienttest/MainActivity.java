package org.jesusgift.clienttest;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SampleTag";
    Retrofit retrofit;
    DBManager dbManager;
    Button btnSync;
    ImageView img1, img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSync = (Button) findViewById(R.id.btnSync);
        btnSync.setOnClickListener(this);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);

        dbManager = new DBManager(this, null, null, AppConfig.DB_VERSION);
        processMediaStore();
    }

    /**
     * Process Media Store Images and upload to cloud
     * */
    private void processMediaStore(){
        initRetrofitService();
        final MediaType TYPE_IMAGE = MediaType.parse("image/*");
        Map<String, RequestBody> reqMap = new HashMap<>();

        try (Cursor c = MyUtility.getAllGalleryImages(this)) {
            while (c.moveToNext()) {

               File f = new File(c.getString(1));
               if(f.exists()) {
                   Bitmap bmp = MyUtility.decodeFile(f);
                   File file = MyUtility.storeImage(bmp, getCacheDir());

                   if(file != null && file.exists()) {
                       RequestBody req = RequestBody.create(TYPE_IMAGE, file);
                       reqMap.put("picture_"+c.getString(0)+"\"; filename=\""+c.getString(0), req);
                   }
               }
            }
            new ImageUploadTask().execute(reqMap);
        }
    }

    /**
     * Image Holder Class
     * */
    class ImageHolder {
        private File imageFile;
        private String storeId;
    }

    /**
     * Function to initialise Retrofit services
     * */
    private void initRetrofitService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * ImageUpload Background Thread
     * */
    class ImageUploadTask extends AsyncTask<Map<String, RequestBody>, Void, Void> {

        @Override
        protected Void doInBackground(Map<String, RequestBody>... params) {
            Map<String, RequestBody> parts = params[0];

            ApiMethods apiMethods = retrofit.create(ApiMethods.class);
            Call<MyResponse> responseCall = apiMethods.saveImages(parts);
            responseCall.enqueue(apiCallback);
            return null;
        }
    }

    /**
     * Callback function for Retrofit Response
     * @param MyResponse
     * */
    Callback<MyResponse> apiCallback = new Callback<MyResponse>() {
        @Override
        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
            Log.d(TAG, "SUC : "+response.code());
            if(response.code() == 200){
                Log.i(TAG, "UPLOADED STORE ID : "+response.body().getData()+" MSG : "+response.body().getMsg());
                dbManager.insertMedia(response.body().getData());
            }
        }

        @Override
        public void onFailure(Call<MyResponse> call, Throwable t) {
            Log.d(TAG,"ERR : "+t.getMessage());
        }
    };

    /**
     * OnClick Handler
     * @param v View
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSync:
                Log.d(TAG, "Onclick");
                processMediaStore();
                break;
        }
    }
}
