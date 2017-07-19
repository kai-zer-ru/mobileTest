package pro.myburse.android.myburse;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkAsyncTask;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.base.SocialNetworkListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.odnoklassniki.OkPerson;
import com.github.gorbin.asne.odnoklassniki.OkSocialNetwork;
import com.github.gorbin.asne.vk.VKPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.myburse.android.myburse.Model.New;
import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.util.OkIOUtil;
import ru.ok.android.sdk.util.OkNetUtil;
import ru.ok.android.sdk.util.OkScope;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class FragmentLogin extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener,
        OnLoginCompleteListener,
        OnRequestDetailedSocialPersonCompleteListener,
        OnRequestSocialPersonCompleteListener

{
    public static SocialNetworkManager mSocialNetworkManager;
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - LinkedIn
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Vkontakte
     * 6 - Odnoklassniki
     * 7 - Instagram
     */
    private Button btnFB;
    private Button btnVK;
    private Button btnOK;
    private Button btnMyBurse;
    private Button btnRegister;
    private App mApp;
    private Bus Otto;
    private SocialNetwork socialNetwork;

    public FragmentLogin() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Otto.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ((LoginActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener
        btnFB = rootView.findViewById(R.id.btnFB);
        btnFB.setOnClickListener(loginClick);

        btnVK = rootView.findViewById(R.id.btnVK);
        btnVK.setOnClickListener(loginClick);

        btnOK = rootView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(loginClick);

        btnMyBurse = rootView.findViewById(R.id.btnMyBurse);
        btnMyBurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
                            .replace(R.id.container, FragmentMyBurseLogin.newInstance())
                            .addToBackStack(null)
                            .commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnRegister = rootView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
                            .replace(R.id.container, FragmentRegister.newInstance())
                            .addToBackStack(null)
                            .commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);
        String OK_APP_ID = getActivity().getString(R.string.ok_app_id);
        String OK_PUBLIC_KEY = getActivity().getString(R.string.ok_public_key);
        String OK_SECRET_KEY = getActivity().getString(R.string.ok_secret_key);

        //Chose permissions
        String[] okScope = new String[] {
                OkScope.VALUABLE_ACCESS,
                "GET_EMAIL"
        };
        String[] vkScope = new String[] {
/*                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,*/
                //VKScope.NOHTTPS
                //VKScope.STATUS
                "email"
        };

        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_birthday")); //, user_friends, user_location

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(LoginActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Init and add to manager OkSocialNetwork
            OkSocialNetwork okNetwork = new OkSocialNetwork(this, OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope);
            mSocialNetworkManager.addSocialNetwork(okNetwork);

            //Init and add to manager OkSocialNetwork
            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, LoginActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case VkSocialNetwork.ID:
                    btnVK.setText("Вход VK");
                    break;
                case OkSocialNetwork.ID:
                    btnOK.setText("Вход OK");
                    break;
                case FacebookSocialNetwork.ID:
                    btnFB.setText("Вход FB");
                    break;
            }
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            AccessToken at = socialNetwork.getAccessToken();
            Log.wtf("DEBUD TOKEN",at.toString());
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    //Login listener

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginActivity.showProgress("Подождите...");
            int networkId = 0;
            switch (view.getId()){
                case R.id.btnVK:
                    networkId = VkSocialNetwork.ID;
                    break;
                case R.id.btnOK:
                    networkId = OkSocialNetwork.ID;
                    break;
                case R.id.btnFB:
                    networkId = FacebookSocialNetwork.ID;
                    break;
            }
            mApp.setUser(new User());
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                socialNetwork.requestLogin();
                //LoginActivity.showProgress("Подождите...");
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    private void startProfile(int networkId){
        socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestDetailedSocialPersonCompleteListener(this);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        if (networkId!=FacebookSocialNetwork.ID) {
            socialNetwork.requestDetailedCurrentPerson();
        }else {
            socialNetwork.requestCurrentPerson();
        }
    }

    @Override
    public void onLoginSuccess(int networkId) {
        LoginActivity.hideProgress();
        startProfile(networkId);
    }

    @Override
    public void onRequestDetailedSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        final User mUser = new User();
        mUser.setDeviceId(mApp.getDeviceId());
        mApp.setUser(mUser);
        switch (socialNetworkId){
            case
                VkSocialNetwork.ID: {
                    final VKPerson vkPerson = (VKPerson) socialPerson;
                    mUser.setExtId(vkPerson.id);
                    mUser.setFirstName(vkPerson.firstName);
                    mUser.setLastName(vkPerson.lastName);
                    mUser.setEmail(vkPerson.email);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy");
                    try {
                        mUser.setBirthday(dateFormat.parse(vkPerson.birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mUser.setBirthday(null);
                    }
                    mUser.setSocialNetworkName("ВКонтакте");
                    mUser.setSocialNetworkId(App.SOCIAL_ID_VK);
                    mUser.setPhone(vkPerson.mobilePhone);
                    mUser.setUrlImage(vkPerson.avatarURL);
                    VKRequest yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50"));
                    yourRequest.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            List usersArray = (VKList) response.parsedModel;
                            mUser.setUrlImage_50(((VKApiUserFull) usersArray.get(0)).photo_50);
                            mApp.setUser(mUser);
                            registerSocialNetwork(mUser);
                        }
                    });
                        break;
                }
                case OkSocialNetwork.ID:{
                    final OkPerson okPerson= (OkPerson) socialPerson;
                    mUser.setExtId(okPerson.id);
                    mUser.setDeviceId(mApp.getDeviceId());
                    mUser.setFirstName(okPerson.firstName);
                    mUser.setLastName(okPerson.lastName);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        mUser.setBirthday(dateFormat.parse(okPerson.birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mUser.setBirthday(null);
                    }
                    mUser.setSocialNetworkId(App.SOCIAL_ID_OK);
                    mUser.setSocialNetworkName("Одноклассники");
                    mUser.setEmail(okPerson.email);
                    mUser.setUrlImage(okPerson.avatarURL);
                    mApp.setUser(mUser);

                    registerSocialNetwork(mUser);
                    break;
                }
                case FacebookSocialNetwork.ID:{
                    final FacebookPerson fbPerson= (FacebookPerson) socialPerson;
                    mUser.setExtId(fbPerson.id);
                    mUser.setDeviceId(mApp.getDeviceId());
                    mUser.setFirstName(fbPerson.firstName);
                    mUser.setLastName(fbPerson.lastName);
                    if (fbPerson.birthday!=null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy");
                        try {
                            mUser.setBirthday(dateFormat.parse(fbPerson.birthday));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            mUser.setBirthday(null);
                        }
                    }
                    mUser.setSocialNetworkId(App.SOCIAL_ID_FB);
                    mUser.setSocialNetworkName("Facebook");
                    mUser.setEmail(fbPerson.email);
                    mUser.setUrlImage(fbPerson.avatarURL);
                    mApp.setUser(mUser);
                    registerSocialNetwork(mUser);
                    break;

                }
            }
        }

    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        Log.wtf("DEBUG", socialPerson.toString());
        final User mUser = new User();
        mUser.setDeviceId(mApp.getDeviceId());
        mApp.setUser(mUser);
        if (socialNetworkId==FacebookSocialNetwork.ID){
            fbRequestDetailedSocialPerson(socialPerson.id);
        }

    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        LoginActivity.hideProgress();
        Utils.showErrorMessage(getContext(),errorMessage);
    }


    public void fbRequestDetailedSocialPerson(String userId) {
        final Session currentSession = Session.getActiveSession();


        Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser me, Response response) {
                if (response.getError() != null) {
                    return;
                }
                FacebookPerson facebookPerson = new FacebookPerson();
                facebookPerson.id = me.getId();
                facebookPerson.name = me.getName();
                facebookPerson.avatarURL = String.format("https://graph.facebook.com/%s/picture?type=square", me.getId());
                if(me.getLink() != null) {
                    facebookPerson.profileURL = me.getLink();
                } else {
                    facebookPerson.profileURL = String.format("https://www.facebook.com/", me.getId());
                }
                if(me.getProperty("email") != null){
                    facebookPerson.email = me.getProperty("email").toString();
                }
                facebookPerson.firstName = me.getFirstName();
                facebookPerson.middleName = me.getMiddleName();
                facebookPerson.lastName = me.getLastName();
                if(me.getProperty("gender") != null) {
                    facebookPerson.gender = me.getProperty("gender").toString();
                }
                facebookPerson.birthday = me.getBirthday();
                if(me.getLocation() != null) {
                    facebookPerson.city = me.getLocation().getProperty("name").toString();
                }
                onRequestDetailedSocialPersonSuccess(FacebookSocialNetwork.ID, facebookPerson);
            }
        });

        Bundle bundle = new Bundle(); bundle.putString("fields", "id,first_name,last_name,email"); request.setParameters(bundle);
        request.executeAsync();
    }

    private void registerMyBurse(final User user){

    }

    private void registerSocialNetwork(final User user){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","socialCallBack");
        builder.appendQueryParameter("social_type", String.valueOf(user.getSocialNetworkId()));
        builder.appendQueryParameter("device_id", user.getDeviceId());
        builder.appendQueryParameter("social_user_id", user.getExtId());
        builder.appendQueryParameter("firstname", user.getFirstName());
        builder.appendQueryParameter("lastname", user.getLastName());
        builder.appendQueryParameter("avatar", (user.getUrlImage_50()==null)?user.getUrlImage():user.getUrlImage_50());
        builder.appendQueryParameter("birthday", (user.getBirthday()==null)?"": new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday()));
        builder.appendQueryParameter("email", (user.getEmail()==null)?"":user.getEmail());
        String registerUrl=builder.build().toString();

        com.android.volley.Request request = new JsonObjectRequest(com.android.volley.Request.Method.GET, registerUrl, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("error") == 0) {
                        Log.wtf("registerSocialNetwork", response.toString());
                        try {
                            user.setId(response.getString("user_id"));
                            user.setEmail(response.getString("email"));
                            user.setPhone(response.getString("phone_number"));
                            user.setAccess_key(response.getString("access_key"));
                            user.setBalance_bids(response.getInt("balance_bids"));
                            user.setBalance_bonus(response.getInt("balance_bonus"));
                            user.setBalance_money(response.getInt("balance_money"));
                            user.setUrlImage_50(response.getString("avatar"));
                            mApp.setUser(user);
                            LoginActivity.hideProgress();
                            Otto.post(new OttoMessage("updateProfile", null));
                            getActivity().onBackPressed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LoginActivity.hideProgress();
                        Utils.showErrorMessage(getContext(),"Ошибка: "+response.getInt("error")+response.getString("error_text"));
                    }
                } catch (JSONException e) {
                    LoginActivity.hideProgress();
                    e.printStackTrace();
                    Utils.showErrorMessage(getContext(),"Ошибка разбора: "+e.getMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoginActivity.hideProgress();
                Log.wtf("onErrorResponse",error.toString());
                Utils.showErrorMessage(getContext(),error.toString());

            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
    }

}

