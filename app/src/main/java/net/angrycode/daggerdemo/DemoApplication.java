package net.angrycode.daggerdemo;

import android.app.Application;

/**
 * Created by lancelot on 15/12/20.
 */
public class DemoApplication extends Application {

    private ApplicationComponent component;
    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .demoApplicationModule(new DemoApplicationModule(this))
                .build();
    }

    void inject(MainActivity activity){
        component.inject(activity);
    }
}
