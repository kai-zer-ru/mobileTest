package pro.myburse.android.myburse;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by alexey on 04.07.17.
 */

public class App extends Application {

    public static String URL_BASE = "https://api.myburse.pro/";

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
