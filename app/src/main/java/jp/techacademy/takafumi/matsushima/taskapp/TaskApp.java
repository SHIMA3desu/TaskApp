package jp.techacademy.takafumi.matsushima.taskapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by qcq0447 on 2016/12/09.
 */

public class TaskApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
