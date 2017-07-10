package pro.myburse.android.myburse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.otto.Bus;

import pro.myburse.android.myburse.Utils.Firebase.Config;
import pro.myburse.android.myburse.Utils.Firebase.NotificationUtils;
import pro.myburse.android.myburse.Utils.OttoMessage;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_NEWS = 0;
    private static final int DRAWER_SHOPS = 1;
    private static final int DRAWER_BLOGS = 2;

    private Drawer mDrawer;
    private Bus Otto;

    private App mApp;
    private Toolbar toolbar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mApp = (App) getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
// icon account_unregistered
        Drawable icon_acc = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_account)
                .color(Color.WHITE);
                //.sizeDp(10);

        Drawable icon_news = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_information)
                //.backgroundColor(ContextCompat.getColor(this,R.color.md_grey_200))
                .color(Color.GRAY);
        Drawable icon_shops = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_store)
                //.backgroundColor(ContextCompat.getColor(this,R.color.md_grey_200))
                .color(Color.GRAY);
        Drawable icon_blogs = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_newspaper)
                //.backgroundColor(ContextCompat.getColor(this,R.color.md_grey_200))
                .color(Color.GRAY);

                //.sizeDp(8);
// Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Войти | Зарегистрироваться").withIcon(icon_acc)
                )
                .withCurrentProfileHiddenInList(true)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        mDrawer.closeDrawer();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        Toast.makeText(MainActivity.this, "onSelection", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        Toast.makeText(MainActivity.this, "click!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                })
                .build();
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .build();

        mDrawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withActivity(this)
                .build();
// Create drawer items
        PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(0)
                .withIcon(icon_news)
                .withName("Новости")
                //.withDescription("Последние события MyBurse")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        getNews();
                        return false;
                    }
                });

        mDrawer.addItem(primaryDrawerItem);

        primaryDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withIcon(icon_shops)
                .withName("Магазины")
                //.withDescription("Ближайшие к Вам магазины")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        getShops();
                        return false;
                    }
                });

        mDrawer.addItem(primaryDrawerItem);

        primaryDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withIcon(icon_blogs)
                .withName("Блоги")
                //.withDescription("Обновления в блогах")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(MainActivity.this, "Fragment BLOGS", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

        mDrawer.addItem(primaryDrawerItem);

// FIREBASE RECEIVER

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    Log.wtf("BroadcastReceiver","onReceive REGISTRATION_COMPLETE " + intent.getStringExtra("token"));

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();

                    String message = intent.getStringExtra("message");
                    Log.wtf("BroadcastReceiver","onReceive PUSH_NOTIFICATION " + intent.getStringExtra("message"));
                    Toast.makeText(getApplicationContext(), "onReceive PUSH_NOTIFICATION " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount()==0) {
            getNews();
        }
//FIREBASE

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.wtf("onResume", "Firebase reg id/app token: " + regId);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void getNews(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FragmentNews.class.getSimpleName());
        if (fragment==null) {
            fragmentManager.popBackStack();
            fragment = FragmentNews.getInstance();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        } else {
            Otto.post(new OttoMessage("getNews",null));
        }
    }

    private void getShops(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FragmentShops.class.getSimpleName());

        if (fragment==null) {
            fragmentManager.popBackStack();
            fragment = FragmentShops.getInstance();

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        } else {
            Otto.post(new OttoMessage("getShops",null));
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            //super.onBackPressed();
            finish();
        }
    }

}
