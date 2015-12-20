package net.angrycode.daggerdemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.angrycode.daggerdemo.reactive.ActionObservable;
import net.angrycode.daggerdemo.reactive.ActionObserver;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements ActionObserver {

    /**
     * 被注入的对象不能够声明为private
     */
    @Inject
    ActionObservable mActionObservable;

    TextView mShowTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mActionObservable = new ActionObservable();//一般的方式创建对象
//        ActivityComponent component = DaggerActivityComponent.builder().observableModule(new ObservableModule()).build();
//        component.inject(this);//Dagger方式，即依赖注入的方式

        DemoApplication application = (DemoApplication)getApplication();
        application.inject(this);//多层依赖方式注入


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mActionObservable.notifyObservers("Action Click:" + SystemClock.elapsedRealtime());
            }
        });

        mShowTv = (TextView) findViewById(R.id.tv_show);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionObservable.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionObservable.unregister(this);
    }

    @Override
    public void action(String desc) {
        mShowTv.setText(desc);
    }
}
