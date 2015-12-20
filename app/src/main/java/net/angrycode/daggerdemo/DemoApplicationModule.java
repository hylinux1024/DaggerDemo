package net.angrycode.daggerdemo;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lancelot on 15/12/20.
 */
@Module
public class DemoApplicationModule {

    private final Application application;

    public DemoApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application getApplication() {
        return application;
    }

}
