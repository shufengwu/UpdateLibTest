package com.delta.updatelibs.ui.update;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import com.delta.updatelibs.Constant;
import com.delta.updatelibs.entity.Download;
import com.delta.updatelibs.service.UpdateService;
import com.delta.updatelibs.utils.FileUtils;
import com.delta.updatelibs.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 更新
 * Created by Shufeng.Wu on 2016/12/27.
 */

public class DownloadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    int downloadCount = 0;
    private String urlStrl;
    private String authority;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    Download download;
    //@Inject
    UpdateService updateService;
    public static boolean isUpdating = false;

    public DownloadService() {
        super("DownloadService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        urlStrl = intent.getStringExtra("urlStr");
        //注入更新用ApiService
        //DaggerUpdateComponent.builder().updateClientModule(new UpdateClientModule(urlStrl, interceptor)).updateServiceModule(new UpdateServiceModule()).build().inject(this);
        authority = intent.getStringExtra("authority");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StringUtils.getHostName(urlStrl))
                .client(new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        updateService = retrofit.create(UpdateService.class);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("下载更新")
                .setContentText("下载中...")
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
        download();

    }

    DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(new DownloadProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            //不频繁发送通知，防止通知栏下拉卡顿
            int progress = (int) ((bytesRead * 100) / contentLength);
            if ((downloadCount == 0) || progress > downloadCount) {
                downloadCount +=1;
                download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                download.setProgress(progress);

                sendNotification(download);
            }
        }
    });

    private void sendIntent(Download download) {

        Intent intent = new Intent(Constant.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void sendDismissIntent() {

        Intent intent = new Intent(Constant.MESSAGE_DIALOG_DISMISS);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void sendFailed() {
        Intent intent = new Intent(Constant.MESSAGE_FAILED);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    public void download() {
        //showProgerssDialog(this);
        isUpdating = true;
        final File file = new File(Environment.getExternalStorageDirectory(), "update.apk");
        if (file.exists()) {
            file.delete();
        }
        updateService.download(urlStrl)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        try {
                            FileUtils.writeFile(inputStream, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InputStream>() {
                    @Override
                    public void onCompleted() {
                        //sendDismissIntent();
                        downloadCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        notificationManager.cancel(0);
                        notificationBuilder.setProgress(0, 0, false);
                        notificationBuilder.setContentText("不能连接到更新服务器!");
                        notificationManager.notify(0, notificationBuilder.build());
                        sendFailed();
                    }

                    @Override
                    public void onNext(InputStream inputStream) {
                        //if(notificationBuilder.)
                    }
                });

    }



    private void downloadCompleted() {
        if (download.getProgress() == 100) {
            download = new Download();
            download.setProgress(100);
            notificationManager.cancel(0);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("下载成功");
            notificationManager.notify(0, notificationBuilder.build());
            //安装apk
            final File file = new File(Environment.getExternalStorageDirectory(), "update.apk");
            sendIntent(download);
            installApk(this, file);
        } else {
            notificationManager.cancel(0);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("下载失败");
            notificationManager.notify(0, notificationBuilder.build());
            sendFailed();
        }

    }

    //安装apk
    private void installApk(Context context, File file) {

        if (Build.VERSION.SDK_INT < 24) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendDismissIntent();
            context.startActivity(intents);
        } else {
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(context, authority, file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                sendDismissIntent();
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    private void sendNotification(Download download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                StringUtils.getDataSize(download.getCurrentFileSize()) + "/" +
                        StringUtils.getDataSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }
}
