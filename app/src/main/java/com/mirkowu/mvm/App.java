package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_screen.AutoSizeManager;
import com.mirkowu.lib_util.LogUtil;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(BuildConfig.DEBUG);
        AutoSizeManager.init(this);
    }
}
