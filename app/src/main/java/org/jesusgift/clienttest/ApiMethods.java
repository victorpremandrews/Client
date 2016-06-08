package org.jesusgift.clienttest;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiMethods {

    @GET("web.php?call=users")
    Call<List<User>> users();

    @PUT("web.php?call=postImage")
    Call<ResponseBody> postImage(@Query("name") String name, @Query("image") String image);

    @Multipart
    @POST("web.php?call=saveImage")
    Call<MyResponse> saveImage(@Part MultipartBody.Part photo, @Query("store_id") String id);

    @Multipart
    @POST("web.php?call=saveImage")
    Call<MyResponse> saveImage(@Part("picture\"; filename=\"my_image.jpg\" ") RequestBody image, @Query("store_id") String id);

    @Multipart
    @POST("web.php?call=saveImages")
    Call<MyResponse> saveImages(@PartMap Map<String, RequestBody> images);

}
