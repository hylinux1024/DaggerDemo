package net.angrycode.daggerdemo;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lancelot on 15/12/20.
 */
@Singleton
@Component(modules = {DemoApplicationModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

    // Exported for child-components.
    Application application();
}
