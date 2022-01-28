package r3.aurangabad.rthreetrade;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitInsertUser {
        @FormUrlEncoded
        @POST("insert_loginmanagement.php")
        Call<List<InsertUserValues>> insertUser(@Field("details")String details);
}
