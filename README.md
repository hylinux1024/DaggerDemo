###一步一步学Dagger之初步使用
#####什么是依赖
当A组件中使用了B组件的方法时，就认为A组件依赖于B组件，B就是A的依赖。例如：  
				
	class A{
		B b;
		A(B b){
			this.b = b;
		}
		...
		//other fileds or methods
	}
	class B{
		...
		void doSomething();
		
		//other fileds or methods
	}
#####Dagger中的相关基础注解符号的说明
>
* @Inject: 通常在需要依赖的地方使用这个注解。换句话说，你用它告诉Dagger这个类或者字段需要依赖注入。这样，Dagger就会构造一个这个类的实例并满足他们的依赖。
>
* @Module: Modules类里面的方法专门提供依赖，所以我们定义一个类，用@Module注解，这样Dagger在构造类的实例的时候，就知道从哪里去找到需要的依赖。modules的一个重要特征是它们设计为分区并组合在一起（比如说，在我们的app中可以有多个组成在一起的modules）。
>
* @Provide: 在modules中，我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖。
>
* @Component: Components从根本上来说就是一个注入器，也可以说是@Inject和@Module的桥梁，它的主要作用就是连接这两个部分。 Components可以提供所有定义了的类型的实例，比如：我们必须用@Component注解一个接口然后列出所有的@Modules组成该组件，如果缺失了任何一块都会在编译的时候报错。所有的组件都可以通过它的modules知道依赖的范围。
>
* @Scope: Scopes可是非常的有用，Dagger2可以通过自定义注解限定注解作用域。后面会演示一个例子，这是一个非常强大的特点，因为就如前面说的一样，没必要让每个对象都去了解如何管理他们的实例。在scope的例子中，我们用自定义的@PerActivity注解一个类，所以这个对象存活时间就和 activity的一样。简单来说就是我们可以定义所有范围的粒度(@PerFragment, @PerUser, 等等)。
>
* @Qualifier: 当类的类型不足以鉴别一个依赖的时候，我们就可以使用这个注解标示。例如：在Android中，我们会需要不同类型的context，所以我们就可以定义 qualifier注解“@ForApplication”和“@ForActivity”，这样当注入一个context的时候，我们就可以告诉 Dagger我们想要哪种类型的context。



####简单的方式实现依赖注入
使用Dagger的依赖注入框架就可以不必关心对象的创建流程。例如传统的方式是`IUser user = new UserImpl();`进行创建。接口IUser与UserImpl是强耦合关系，而使用Dagger就是可以打破这种强耦合的关系。
####Dagger使用步骤
1. 构建依赖，提供消费对象（使用@Module注解符号）；  
2. 构建注解器（Injector），连接消费者与提供者（使用@Component注解符号）。
3. 使用依赖，在消费对象中注入的需要的对象（在使用依赖的地方使用@Inject注解符号）； 

######构建依赖Module
例如下面的一个例子，ActionObservable是一个在在MainActivity消费的，需要被注入的对象。
那么使用Dagger构建依赖。提供依赖的类按照规范最好是使用`Module`结尾，提供的方法也以`provide`开头
	
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
######构建注解器Component
注解器injector是连接消费依赖和提供依赖的桥梁。例如
		
		@Component(modules = {ObservableModule.class})
		public interface ActivityComponent {
    		/**
     		* 将消费依赖注入
     		* @param activity 该参数必须是真正消费依赖的类型，不能是其父类。因为dagger2在编译时			* 生成依赖注入的代码,会到inject方法的参数类型中寻找可以注入的对象，但是实际上这些对象存			* 在于MainActivity
     		*/
    		void inject(MainActivity activity);

		} 
######使用依赖
在使用依赖的地方`@Inject`标识，告诉Dagger这个使用框架进行注入的对象。例如
	
	//这是一个简单的Demo，页面上有一个FAB，点击之后在页面上响应点击，
	public class MainActivity extends AppCompatActivity implements ActionObserver {
    
    @Inject
    ActionObservable mActionObservable;//这个是需要构造的对象，这个对象是用一个观察者模式进行模拟点击的。

    TextView mShowTv;//用于显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ...
		//mActionObservable = new ActionObservable();//一般的方式创建对象
		
		ActivityComponent component = DaggerActivityComponent.builder()
		.observableModule(new ObservableModule()).build();
	    component.inject(this);//Dagger方式，即依赖注入的方式
        ...
        //这个时候就可以使用mActionObservable对象了。

    }

这时候，出现了一个问题。是不是每一个注入的对象都需要在相应的使用`compnent.inject()`呢？这样的话如果有多个依赖那么`MainActivity`代码也会出现大量`component.inject（）`代码，我们当然是不希望看到这样的。其实这个时候我们就可以使用多层依赖的方式，来避免这种情况。
#####多层依赖实现
首先看一下是使用场景，我们希望使用`Application.inject()`一次之后，然后相关的依赖就注入了，不再调用各自的Component分别注入了。

######构建Application依赖
在application中 构建Application Module 

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
    	...

	}
######构建全局注解器


	public class DemoApplication extends Application {

    	private ApplicationComponent component;
    	@Override
    	public void onCreate() {
        	super.onCreate();
        	component = DaggerApplicationComponent.builder()
                	.demoApplicationModule(new DemoApplicationModule(this))
                	.build();
    	}
		//这里可以是使用基类，但这里简化操作
    	void inject(MainActivity activity){
        	component.inject(activity);
    	}
	}

######修改ActivityComponent
	/**
	* component就是一个注入器(Injector)，连接提供依赖和消费依赖的桥梁
    * component后增加ActivityComponent了dependencies参数，
    * 使得一个Component成为了另一个Component的依赖
    */
    @PerActivity
    @Component(dependencies = ApplicationComponent.class,
    modules =  {ObservableModule.class})
    public interface ActivityComponent {
    	/**
     	* 将消费依赖注入
     	* @param activity 该参数必须是真正消费依赖的类型，不能是其父类。因为dagger2在编译时生成		* 依赖注入的代码，会到inject方法的参数类型中寻找可以注入的对象，但是实际上这些对象存在于			MainActivity
     	*/
    	void inject(MainActivity activity);

    	ActionObservable getActionObservable();
	}
其中dependencies参数就是告诉Dagger该组件依赖于ApplicationComponent,同时这里使用了一个自定义限定符`@PerActivity`。Dagger2中Component依赖于另一个Component必须使用一个限定符来标识，否则编译不通过。  
`@PerActivity`的定义

	/**
	* A scoping annotation to permit objects whose lifetime should
    * conform to the life of the activity to be memorized in the
    * correct component.
    */
	@Scope
	@Retention(RUNTIME)
	public @interface PerActivity {}

######多层依赖的使用

这样，如果一个app中有多处依赖，那么就可以使用Application来进行管理。

	/**
     * 被注入的对象不能够声明为private
     */
    @Inject
    ActionObservable mActionObservable;

    TextView mShowTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DemoApplication application = (DemoApplication)getApplication();
        application.inject(this);//多层依赖方式注入

        ...
        
    }
本文Demo连接地址[DaggerDemo](https://github.com/ACCoder/DraggerDemo)