package com.carpa.library.client;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 10/11/2017.
 */

public interface Services {
    String BASE_URL = "http://android.afrisatt.info:8080/";
    String PATH = "GCCLIB/library/process/lib";

    @GET(PATH)
    Call<ResponseBody> queryService(@Query("country") String country, @Query("lan") String language, @Header("cmd") String cmd);

}
