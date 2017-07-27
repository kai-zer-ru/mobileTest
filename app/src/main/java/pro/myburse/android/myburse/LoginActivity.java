package pro.myburse.android.myburse;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;



    public class LoginActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
        public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
        private static ProgressDialog pd;
        private Toolbar toolbar;
        static Context context;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            context = this;
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            homeAsUpByBackStack();

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new FragmentLogin())
                        .commit();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            return true;
        }

        @Override
        public void onBackStackChanged() {
            homeAsUpByBackStack();
        }

        private void homeAsUpByBackStack() {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        protected static void showProgress(String message) {
            pd = new ProgressDialog(context);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(message);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        protected static void hideProgress() {
            pd.dismiss();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

}

