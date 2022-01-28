package r3.aurangabad.rthreetrade;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitLoginRequest {
    @FormUrlEncoded
    @POST("selectloginman.php")
    Call<List<RetrofitLoginDetails>> getValidUser(@Field("imei")String imei);

}
