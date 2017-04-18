package com.delta.updatelibs.ui.update;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 更新
 * Interceptor for download
 * Created by Shufeng.Wu on 16/5/11.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private DownloadProgressListener listener;

    public DownloadProgressInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //拦截每次请求
        Response originalResponse = chain.proceed(chain.request());
        //包装响应体
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
