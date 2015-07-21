package se.hanh.nimbl3channels.app;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Hanh on 14/07/2015.
 */
public class NimbleApplication extends Application {
    public static final String TAG = NimbleApplication.class.getSimpleName();
    private static Context mContext;
    private static NimbleApplication mInstance;

    private RefWatcher mRefWatcher;

    public static RefWatcher getRefWatcher(Context context){
        NimbleApplication application = (NimbleApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
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
        mRefWatcher = LeakCanary.install(this);
    }


}
