package com.delta.updatelibs.base;

import android.app.Application;
import android.util.Log;
import com.delta.updatelibs.Constant;
import com.delta.updatelibs.entity.Update;
import com.delta.updatelibs.service.UpdateService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shufeng.Wu on 2017/3/15.
 */

public class UpdateBaseApplication extends Application {

    private static final int TOME_OUT = 10;
    private Retrofit retrofitUpdateInfo;
    private UpdateService updateInfoService;
    private final Map<String, String> mapUpdateInfo = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        retrofitUpdateInfo = new Retrofit.Builder()
                .baseUrl(Constant.BASE_UPDATE_URL)//域名
                .client(new OkHttpClient.Builder()
                        .connectTimeout(TOME_OUT, TimeUnit.SECONDS)
                        .readTimeout(TOME_OUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .build();
        updateInfoService = retrofitUpdateInfo.create(UpdateService.class);
        final retrofit2.Call<Update> call = updateInfoService.getUpdate1();

        new Thread() {
            @Override
            public void run() {
                try {
                    Response<Update> response = call.execute(); // 同步
                    Update update = response.body();
                    mapUpdateInfo.put("version", update.getVersion());
                    mapUpdateInfo.put("versionCode", update.getVersionCode());
                    mapUpdateInfo.put("description", update.getDescription());
                    mapUpdateInfo.put("url", update.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                    mapUpdateInfo.put("version", "网络请求失败！");
                    mapUpdateInfo.put("versionCode", "0");
                    mapUpdateInfo.put("description", "");
                    mapUpdateInfo.put("url", "");
                }
            }
        }.start();
    }

    public Map<String, String> getUpdateInfoAtStart() {
        return mapUpdateInfo;
    }

    public Map<String, String> checkUpdateInfo() {
        retrofitUpdateInfo = new Retrofit.Builder()
                .baseUrl(Constant.BASE_UPDATE_URL)//域名
                .client(new OkHttpClient.Builder()
                        .connectTimeout(TOME_OUT, TimeUnit.SECONDS)
                        .readTimeout(TOME_OUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .build();
        updateInfoService = retrofitUpdateInfo.create(UpdateService.class);
        final retrofit2.Call<Update> call = updateInfoService.getUpdate1();

        new Thread() {
            @Override
            public void run() {
                try {
                    Response<Update> response = call.execute(); // 同步
                    Update update = response.body();
                    mapUpdateInfo.put("version", update.getVersion());
                    mapUpdateInfo.put("versionCode", update.getVersionCode());
                    mapUpdateInfo.put("description", update.getDescription());
                    mapUpdateInfo.put("url", update.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                    mapUpdateInfo.put("version", "网络请求失败！");
                    mapUpdateInfo.put("versionCode", "0");
                    mapUpdateInfo.put("description", "");
                    mapUpdateInfo.put("url", "");
                }
            }
        }.start();

        return mapUpdateInfo;
    }


}
