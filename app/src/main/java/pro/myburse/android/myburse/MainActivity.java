package pro.myburse.android.myburse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.Firebase.Config;
import pro.myburse.android.myburse.Utils.Firebase.NotificationUtils;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    private Drawer mDrawer;
    private AccountHeader mAccountHeader;
    private Bus Otto;

    private App mApp;
    private User mUser;
    private Toolbar toolbar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// FIREBASE RECEIVER

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    Log.wtf("BroadcastReceiver","DEVICE_ID updated " + intent.getStringExtra("token"));
                    mApp.setDeviceId(intent.getStringExtra("token"));

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    Log.wtf("BroadcastReceiver","onReceive PUSH_NOTIFICATION " + intent.getStringExtra("message"));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
//
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mApp = (App) getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
        mUser = mApp.getUser();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        Drawable icon_news = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_information)
                .color(Color.GRAY);
        Drawable icon_shops = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_store)
                .color(Color.GRAY);
        Drawable icon_blogs = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_newspaper)
                .color(Color.GRAY);
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Вход | Регистрация")
                                .withIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_account_circle)
                                        .paddingDp(5)
                                        .colorRes(R.color.material_drawer_secondary_text)
                                        .backgroundColorRes(R.color.material_drawer_background)
                                )
                                .withIdentifier(1)                )
                .withCurrentProfileHiddenInList(true)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        mDrawer.closeDrawer();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return false;
                    }
                })
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return false;
                    }

                })
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .build();


        mDrawer = new DrawerBuilder()
                .withAccountHeader(mAccountHeader)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withActivity(this)
                .build();

        PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(0)
                .withIcon(icon_news)
                .withName("Новости")
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
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        getBlogs();
                        return false;
                    }
                });

        mDrawer.addItem(primaryDrawerItem);

        mUser = mApp.getUser();
        if (mUser!=null&&mUser.isConnected()&&mUser.getDeviceId().equals(mApp.getDeviceId())){
            updateProfile();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationUtils.clearNotifications(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount()==0) {
            getNews();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

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

    private void getBlogs(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FragmentBlogs.class.getSimpleName());

        if (fragment==null) {
            fragmentManager.popBackStack();
            fragment = FragmentBlogs.getInstance();

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        } else {
            Otto.post(new OttoMessage("getBlogs",null));
        }

    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            finish();
        }
    }

    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        try {
            switch (msg.getAction()) {
                case "updateProfile": {
                    updateProfile();
                    Otto.post(new OttoMessage("getNews",null));
                    Otto.post(new OttoMessage("getShops",null));
                    Toast.makeText(this, mApp.getUser().getAccess_key(), Toast.LENGTH_SHORT).show();
                }
                default: {

                }
            }
        }catch (Exception e){
            Utils.showErrorMessage(this, e.getMessage());
        }
    }

    private void updateProfile(){
        mUser = mApp.getUser();
        final IProfile profile = new ProfileDrawerItem()
                .withEmail(mUser.getEmail())
                .withName(mUser.getName());
        if (mUser.getUrlImage_50()!=null) {
            profile.withIcon(Uri.parse(mUser.getUrlImage_50()));
        } else if (mUser.getUrlImage()!=null){
            profile.withIcon(Uri.parse(mUser.getUrlImage()));
        }

        mAccountHeader.getProfiles().clear();
        mAccountHeader.removeProfile(0);
        mAccountHeader.addProfile(profile,0);
        mAccountHeader.setActiveProfile(0);
        mDrawer.openDrawer();

    }
}
