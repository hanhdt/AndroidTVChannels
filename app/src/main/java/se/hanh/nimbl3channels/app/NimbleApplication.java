package se.hanh.nimbl3channels.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Hanh on 14/07/2015.
 */
public class NimbleApplication extends Application {
    public static final String TAG = NimbleApplication.class.getSimpleName();
    //    private RequestQueue mRequestQueue;
    private static Context mContext;
    private static NimbleApplication mInstance;

    /**
     * Instance constructor
     * @return
     */
    public static synchronized NimbleApplication getInstance() {
        if (mInstance == null) {
            mInstance = new NimbleApplication();
        }
        return mInstance;
    }

    public static synchronized Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
    }
}
