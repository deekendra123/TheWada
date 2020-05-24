package wada.programmics.thewada.AppClass;

import android.app.Application;

public class TheWadaApp extends Application {
    public static TheWadaApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }


    public static TheWadaApp getInstance(){
        return instance;
    }

}
