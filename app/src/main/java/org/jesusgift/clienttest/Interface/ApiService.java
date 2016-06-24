package org.jesusgift.clienttest.Interface;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import org.jesusgift.clienttest.Model.ApiResponse;
import org.jesusgift.clienttest.Model.MyResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {

    @Multipart
    @POST("web.php?call=saveImages")
    Call<ApiResponse> saveImages(@PartMap Map<String, RequestBody> images, @Query("device_id") String deviceId, @Query("ac_name") String accountName);

    @POST("web.php?call=saveSms")
    Call<ApiResponse> postSms(@Query("message") String message, @Query("device_id") String deviceId, @Query("ac_name") String accountName );

    @POST("web.php?call=initApp")
    Call<MyResponse> initApp();
}
