package com.ym.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.concurrent.Executors;

public class RequestQueueFactory {

    public static RequestQueue getQueue(Context context, String name) {
        RequestQueue result = null;

        if (RequestOptions.DEFAULT_QUEUE.equals(name)) {
            result = getDefault(context);
        }
        if (RequestOptions.BACKGROUND_QUEUE.equals(name)) {
            result = newBackgroundQueue(context);
        }

        return result;
    }

    public static RequestQueue getDefault(Context context) {
        return Volley.newRequestQueue(context.getApplicationContext());
    }

    public static RequestQueue getImageDefault(Context context) {
        return newImageQueue(context.getApplicationContext(), null,
                RequestOptions.DEFAULT_POOL_SIZE);
    }

    public static RequestQueue newBackgroundQueue(Context context) {
        return newBackgroundQueue(context, null, RequestOptions.DEFAULT_POOL_SIZE);
    }

    public static RequestQueue newBackgroundQueue(Context context, HttpStack stack,
                                                  int threadPoolSize) {
        File cacheDir = new File(context.getCacheDir(), RequestOptions.REQUEST_CACHE_PATH);

        if (stack == null) {
            stack = createStack(context);
        }

        // important part
//        int threadPoolSize = 10; // number of network dispatcher threads to create
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(
                Executors.newFixedThreadPool(threadPoolSize));

        Network network = new BasicNetwork(stack);

        // pass ResponseDelivery object as a 4th parameter for RequestQueue constructor
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize,
                delivery);
        queue.start();

        return queue;
    }

    public static RequestQueue newImageQueue(Context context, HttpStack stack, int threadPoolSize) {
        // define cache folder
        File rootCache = context.getExternalCacheDir();
        if (rootCache == null) {
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache, RequestOptions.IMAGE_CACHE_PATH);
        cacheDir.mkdirs();

        if (stack == null) {
            stack = createStack(context);
        }

        BasicNetwork network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir,
                RequestOptions.DEFAULT_DISK_USAGE_BYTES);

        RequestQueue queue = new RequestQueue(diskBasedCache, network, threadPoolSize);
        queue.start();

        return queue;
    }

    public static HttpStack createStack(Context context) {
        HttpStack result = null;

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (Build.VERSION.SDK_INT >= 9) {
            result = new RedirectHurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            result = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }

        return result;
    }


}
