package xinrong.git.myapplication;

import android.app.Application;

import org.xutils.x;

/**
 * Created by nike on 16/1/9.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);

    }
}
