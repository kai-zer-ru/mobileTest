package pro.myburse.android.myburse;

import android.app.Application;
import android.content.SharedPreferences;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import pro.myburse.android.myburse.Utils.Firebase.Config;

/**
 * Created by alexey on 04.07.17.
 */

public class App extends Application {

    public final static String URL_BASE = "https://api.myburse.pro/";
    public final static int COUNT_CARDS=20;
    private static Bus Otto;
    private String Device_Id;



    @Override
    public void onCreate() {
        super.onCreate();
        Otto = new Bus(ThreadEnforcer.MAIN);
    }

    public Bus getOtto(){
        return  Otto;
    }

    public String getDevice_Id() {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        Device_Id = pref.getString("regId",null);
        return Device_Id;
    }

    public void setDevice_Id(String device_Id) {
        Device_Id = device_Id;
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", device_Id);

    }
}
