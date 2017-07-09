package pro.myburse.android.myburse;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by alexey on 04.07.17.
 */

public class App extends Application {

    public final static String URL_BASE = "https://api.myburse.pro/";
    public final static int COUNT_CARDS=20;
    private static Bus Otto;



    @Override
    public void onCreate() {
        super.onCreate();
        Otto = new Bus(ThreadEnforcer.MAIN);
    }

    public Bus getOtto(){
        return  Otto;
    }

}
