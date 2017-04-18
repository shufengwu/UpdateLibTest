package com.delta.updatelibs.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Shufeng.Wu on 2016/12/27.
 */

public class StringUtils {
    //apk文件大小换算+单位
    //B、KB、MB转换
    public static String getChangedContentLength(int progress) {
        String res = null;
        if (progress < 1024) {
            res = progress + " B";
            //1024 * 1024
        } else if (progress < 1048576) {
            res = new BigDecimal((double) progress / 1024).setScale(2, BigDecimal.ROUND_DOWN) + " KB";
        } else {
            res = new BigDecimal((double) progress / 1024 / 1024).setScale(2, BigDecimal.ROUND_DOWN) + " MB";
        }
        return res;
    }

    //获取百分比
    public static int getPercentProgress(int progress,int maxProgress) {
        int res = 0;
        String tmp = new BigDecimal((double) progress / maxProgress * 100).setScale(0, BigDecimal.ROUND_DOWN) + "";
        res = (int) Double.parseDouble(tmp);
        return res;
    }

    public static String getHostName(String urlString) {
        String head = "";
        int index = urlString.indexOf("://");
        if (index != -1) {
            head = urlString.substring(0, index + 3);
            urlString = urlString.substring(index + 3);
        }
        index = urlString.indexOf("/");
        if (index != -1) {
            urlString = urlString.substring(0, index + 1);
        }
        return head + urlString;
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }
}
