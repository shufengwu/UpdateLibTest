package com.delta.updatelibs.service;

import com.delta.updatelibs.Constant;
import com.delta.updatelibs.entity.Update;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Shufeng.Wu on 2017/3/15.
 */

public interface UpdateService {

    //更新
    @GET(Constant.bundleJsonUrl)
    Call<Update> getUpdate();

    //更新
    @GET(Constant.bundleJsonUrl)
    Call<Update> getUpdate1();

    //下载更新
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
