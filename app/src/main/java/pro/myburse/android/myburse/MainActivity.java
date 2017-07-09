package pro.myburse.android.myburse;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.squareup.otto.Bus;

import pro.myburse.android.myburse.Utils.OttoMessage;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_NEWS = 0;
    private static final int DRAWER_SHOPS = 1;
    private static final int DRAWER_BLOGS = 2;

    private Drawer mDrawer;
    private Bus Otto;

    private App mApp;
    private Toolbar toolbar;

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
                        Toast.makeText(MainActivity.this, "onSelection", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount()==0) {
            getNews();
        }
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
            super.onBackPressed();
        }
    }

}
