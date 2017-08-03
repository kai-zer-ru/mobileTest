package pro.myburse.android.myburse;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.Firebase.Config;

public class App extends Application {

    public final static int SOCIAL_ID_VK = 1;
    public final static int SOCIAL_ID_OK = 2;
    public final static int SOCIAL_ID_FB = 3;
    //public final static String URL_BASE = "https://api.myburse.pro/";
    public final static String URL_BASE = "https://api-test.myburse.pro/";
    public final static int COUNT_CARDS=20;
    private static Bus Otto;
    private User mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        Otto = new Bus(ThreadEnforcer.MAIN);
    }

    public Bus getOtto(){
        return  Otto;
    }

    public int getUserId() {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        int user_id = pref.getInt("user_id",0);
        return user_id;
    }

    public void setUserId(int user_id) {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("user_id", user_id).apply();
    }

    public String getDeviceId() {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        return pref.getString("device_id",null);
    }

   public void setDeviceId(String mDeviceId) {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("device_id", mDeviceId).apply();
    }

/*    public String getSocli() {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        return pref.getString("device_id",null);;
    }

    public void setDeviceId(String mDeviceId) {
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("device_id", mDeviceId).apply();
    }*/

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }
}
