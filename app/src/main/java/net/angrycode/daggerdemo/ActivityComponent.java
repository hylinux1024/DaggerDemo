package net.angrycode.daggerdemo;

import net.angrycode.daggerdemo.reactive.ActionObservable;

import javax.inject.Singleton;

import dagger.Component;

/**
 * component就是一个注入器(Injector)，连接提供依赖和消费依赖的桥梁
 * component后增加ActivityComponent了dependencies参数，
 * 使得一个Component成为了另一个Component的依赖
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = {ObservableModule.class})
public interface ActivityComponent {
    /**
     * 将消费依赖注入
     * @param activity 该参数必须是真正消费依赖的类型，不能是其父类。因为dagger2在编译时生成依赖注入的代码，
     *                 会到inject方法的参数类型中寻找可以注入的对象，但是实际上这些对象存在于MainActivity
     */
    void inject(MainActivity activity);

    ActionObservable getActionObservable();
}
