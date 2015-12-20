package net.angrycode.daggerdemo;

import net.angrycode.daggerdemo.reactive.ActionObservable;

import dagger.Module;
import dagger.Provides;

/**
 * Module 标识负责提供依赖的组件
 * Created by lancelot on 15/12/20.
 */
@Module
public class ObservableModule {

    /**
     * 用@Provides标识提供依赖的方法。
     */
    @Provides
    ActionObservable provideCharacterObservable() {
        return new ActionObservable();
    }

}
