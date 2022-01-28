package r3.aurangabad.rthreetrade;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitReceiptSync {
    //String BaseUrl="http://www.r3infoservices.com/offline/kapiltrade/";
    @FormUrlEncoded
    @POST("insertReceiptSync.php")
    Call<ResponseBody> insertReceipt(
            @Field("receipt")String receipt,
            @Field("receiptall")String receiptall,
            @Field("receiptdet")String receiptdet,
            @Field("salesman")String salesman,
            @Field("Ins_Date")String Ins_Date,
            @Field("location")String location,
            @Field("New")String New,
            @Field("recsequence")String recSequence
    );
}
