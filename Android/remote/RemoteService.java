package com.mobitant.bookapp.remote;

import com.mobitant.bookapp.best.BestItem;
import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.item.KeepItem;
import com.mobitant.bookapp.item.MemberInfoItem;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 서버에 호출할 메소드를 선언하는 인터페이스
 */
public interface RemoteService {
    String BASE_URL = "http://192.168.0.6:3000";
    //String BASE_URL = "http://www.mobitantapp.com:3300";
    String MEMBER_ICON_URL = BASE_URL + "/member/";
    String IMAGE_URL = BASE_URL + "/img/";

    //사용자 정보
    @GET("/member/{phone}")
    Call<MemberInfoItem> selectMemberInfo(@Path("phone") String phone);

    @POST("/member/info")
    Call<String> insertMemberInfo(@Body MemberInfoItem memberInfoItem);

    @FormUrlEncoded
    @POST("/member/phone")
    Call<String> insertMemberPhone(@Field("phone") String phone);

    @Multipart
    @POST("/member/icon_upload")
    Call<ResponseBody> uploadMemberIcon(@Part("member_seq") RequestBody memberSeq,
                                        @Part MultipartBody.Part file);

    //도서 정보
    @GET("/book/info/{seq}")
    Call<BookInfoItem> selectBookInfo(@Path("seq") int bookInfoSeq,
                                      @Query("member_seq") int memberSeq);

    @POST("/book/info/insert")
    Call<String> insertBookInfo(@Body BookInfoItem infoItem);

    @POST("/book/info/update")
    Call<String> updateBookInfo(@Body BookInfoItem infoItem);

    @Multipart
    @POST("/book/info/image")
    Call<ResponseBody> uploadBookImage(@Part("info_seq") RequestBody infoSeq,
                                       @Part MultipartBody.Part file);

    @GET("/book/list")
    Call<ArrayList<BookInfoItem>> listBookInfo(@Query("member_seq") int memberSeq,
                                               @Query("order_type") String orderType,
                                               @Query("current_page") int currentPage);

    @DELETE("/book/list/{member_seq}/{seq}")
    Call<ArrayList<BookInfoItem>> deleteBookInfo(@Path("member_seq") int memberSeq,
                                                 @Path("seq") int bookInfoSeq);

    //즐겨찾기
    @POST("/keep/{member_seq}/{info_seq}")
    Call<String> insertKeep(@Path("member_seq") int memberSeq,
                            @Path("info_seq") int infoSeq);

    @DELETE("/keep/{member_seq}/{info_seq}")
    Call<String> deleteKeep(@Path("member_seq") int memberSeq,
                            @Path("info_seq") int infoSeq);

    @GET("/keep/list")
    Call<ArrayList<KeepItem>> listKeep(@Query("member_seq") int memberSeq);

    //순위 정보 가져오기
    @GET("/best/list")
    Call<ArrayList<BestItem>> BestInfo(@Query("term") String term,
                                       @Query("kind") String kind);
}