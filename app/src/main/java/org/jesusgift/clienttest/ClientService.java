package org.jesusgift.clienttest;
 /* *
 * Developed By : Victor Vincent
 * Created On : 09/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.0
 * */

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jesusgift.clienttest.Helpers.AppConfig;
import org.jesusgift.clienttest.Helpers.DBManager;
import org.jesusgift.clienttest.Helpers.MyUtility;
import org.jesusgift.clienttest.Interface.ApiService;
import org.jesusgift.clienttest.Model.ApiResponse;
import org.jesusgift.clienttest.Model.ImageHolder;
import org.jesusgift.clienttest.Model.MyResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientService extends Service {
    private static final String TAG = "ClientService";
    private Timer timer;
    private boolean READY_TO_RUN = true;
    private DBManager dbManager;
    private Retrofit retrofit;
    private SharedPreferences preferences;
    private String MEDIA_STORE_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.IS_SERVICE_RUNNING = true;
        //Log.i(TAG, "Service Started");

        //Initialising Database
        dbManager = new DBManager(this, null, null, AppConfig.DB_VERSION);

        //Initialising Timer Controls
        initTimer();
    }

    /**
     * Initialising API Config
     * */
    private boolean initAPIConfig() {
        if( initRetrofitService() ) {
            if(MyUtility.isOnline(this)) {
                ApiService service = retrofit.create(ApiService.class);
                Call<MyResponse> responseBodyCall = service.initApp();
                responseBodyCall.enqueue(responseCallback);
            }
        }
        return false;
    }

    /**
     * API Config Callback
     * */
    private Callback<MyResponse> responseCallback = new Callback<MyResponse>() {
        @Override
        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
            if(response.code() == 200) {
                if(response.body().getStatus() == 1) {
                    MyResponse.Data data = response.body().getData();
                    String api_url = data.getApi_url();
                    int load_type = data.getLoad_type();

                    if(api_url.contains("http://")) {
                        AppConfig.API_PRIMARY_URL = api_url;
                        AppConfig.API_LOAD_TYPE = load_type;
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<MyResponse> call, Throwable t) {

        }
    };

    /**
     * Initialising Timer Controls
     * */
    private void initTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 2000);
    }

    /**
     * Timer Task to Check for Data
     * */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //Log.d(TAG, "Service Running");
            if(MyUtility.isOnline(ClientService.this)) {
                //Log.d(TAG, "Device Online");
                if(READY_TO_RUN) {
                    processMediaStore();
                }
            }
        }
    };

    /**
     * Function Block to process media store
     * */
    private void processMediaStore() {
        final MediaType TYPE_IMAGE = MediaType.parse("image/*");
        Map<String, RequestBody> reqMap = new HashMap<>();
        List<String> idList = new ArrayList<>();

        if(dbManager != null) {
            String store_id = dbManager.getLastInsertedMediaID();
            try (Cursor c = MyUtility.getNewGalleryImages(ClientService.this, store_id)) {
                //initialising Retrofit Services
                if (initRetrofitService() ) {
                    if (c != null && c.getCount() > 0) {
                        //Looping through cursor
                        while (c.moveToNext()) {
                            if (!dbManager.isMediaPresent(c.getString(0))) {
                                Log.d(TAG, "ID : "+c.getString(0));
                                File f = new File(c.getString(1));

                                if (f.exists()) {
                                    Bitmap bmp = MyUtility.decodeFile(f);
                                    File file = MyUtility.storeImage(bmp, getCacheDir());

                                    idList.add(c.getString(0));
                                    if (file != null && file.exists()) {
                                        RequestBody req = RequestBody.create(TYPE_IMAGE, file);
                                        reqMap.put("picture_" + c.getString(0) + "\"; filename=\"" + c.getString(0), req);
                                    }
                                }
                            }
                        }

                        if (reqMap.size() > 0) {
                            ImageHolder imageHolder = new ImageHolder(reqMap, idList);
                            new ImageUploadTask().execute(imageHolder);
                        }
                    }
                }
            }
        } else {
            dbManager = new DBManager(this, null, null, AppConfig.DB_VERSION);
            processMediaStore();
        }
    }

    /**
     * Function to initialise Retrofit services
     * */
    private boolean initRetrofitService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return true;
    }

    /**
     * ImageUpload Background Thread
     * */
    class ImageUploadTask extends AsyncTask<ImageHolder, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            READY_TO_RUN = false;
        }

        @Override
        protected Void doInBackground(ImageHolder... params) {
            Map<String, RequestBody> parts = params[0].requestBodyMap;
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ApiResponse> responseCall = apiService.saveImages(parts, MyUtility.getDeviceId(ClientService.this), MyUtility.getUsername(ClientService.this));
            dbManager.insertMedia(params[0].idList);
            responseCall.enqueue(apiCallback);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Callback function for Retrofit Response
     * @param MyResponse
     * */
    Callback<ApiResponse> apiCallback = new Callback<ApiResponse>() {
        @Override
        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
            //Log.i(TAG, "SUC : "+response.code());
            if(response.code() == 200){
                MyUtility.clearCacheDir(getCacheDir());
                READY_TO_RUN = true;
//                if(dbManager.insertMedia(response.body().getData())) {
//
//                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse> call, Throwable t) {
            READY_TO_RUN = true;
            //Log.e(TAG,"ERR : "+t.getMessage());
        }
    };


    /**
     * Background Async Task for Posting SMS
     * */
    public class SmsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String sms = params[0];
            if(retrofit != null) {
                ApiService apiService = retrofit.create(ApiService.class);
                Call<ApiResponse> res = apiService.postSms(sms, MyUtility.getDeviceId(ClientService.this), MyUtility.getUsername(ClientService.this));
                res.enqueue(apiCallback);
            }else {
                initRetrofitService();
            }
            return null;
        }
    }

    /**
     * Client Service On Start Command
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(intent != null) {
//            Bundle extras = intent.getExtras();
//            if(extras != null) {
//                if(extras.containsKey(AppConfig.MESSAGE_BODY)){
//                    String msg = intent.getExtras().getString(AppConfig.MESSAGE_BODY);
//                    if(MyUtility.isOnline(ClientService.this)) {
//                        new SmsTask().execute(msg);
//                    }
//                }
//            }
//        }
        return START_STICKY;
    }

    /**
     * Client Service On Destroy
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        AppConfig.IS_SERVICE_RUNNING = false;

        Intent intent = new Intent();
        intent.setAction(AppConfig.SERVICE_STOP_BROADCAST);
        sendBroadcast(intent);
    }

    /**
     * Client Service Activity Binder
     * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
