package pro.myburse.android.myburse;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.odnoklassniki.OkSocialNetwork;
import com.github.gorbin.asne.vk.VKPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.squareup.otto.Bus;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;
import ru.ok.android.sdk.util.OkScope;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class FragmentLogin extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestDetailedSocialPersonCompleteListener {
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
        btnFB = (Button) rootView.findViewById(R.id.btnFB);
        btnFB.setOnClickListener(loginClick);

        btnVK = (Button) rootView.findViewById(R.id.btnVK);
        btnVK.setOnClickListener(loginClick);

        btnOK = (Button) rootView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(loginClick);

        btnMyBurse = (Button) rootView.findViewById(R.id.btnMyBurse);
        btnMyBurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "MyBurse вход", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
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
                OkScope.VALUABLE_ACCESS
        };
        String[] vkScope = new String[] {
/*                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,*/
                //VKScope.NOHTTPS
                //VKScope.STATUS
        };

        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends, user_location, user_birthday"));

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
                    btnVK.setText("Show VK profile");
                    break;
                case OkSocialNetwork.ID:
                    btnOK.setText("Show OK profile");
                    break;
                case FacebookSocialNetwork.ID:
                    btnOK.setText("Show FB profile");
                    break;
            }
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
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
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    LoginActivity.showProgress("Подождите...");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        LoginActivity.hideProgress();
        startProfile(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        LoginActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void startProfile(int networkId){
        socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestDetailedSocialPersonCompleteListener(this);
        socialNetwork.requestDetailedCurrentPerson();
    }

    @Override
    public void onRequestDetailedSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        switch (socialNetworkId){
            case
                VkSocialNetwork.ID: {
                    final User mUser = mApp.getUser();
                    VKRequest yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50"));
                    yourRequest.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            List usersArray = (VKList) response.parsedModel;
                            mUser.setUrlImage_50(((VKApiUserFull) usersArray.get(0)).photo_50);
                            mApp.setUser(mUser);
                        }
                    });


                    final VKPerson vkPerson = (VKPerson) socialPerson;

                    if (mUser.getExtId() == null || (mUser.getExtId() != null && mUser.getExtId().equals(socialPerson.id))) {
                        AlertDialog.Builder d = new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("Обновить информацию профиля из ВКонтакте?")
                            .setCancelable(false)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
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
                                    mApp.setUser(mUser);
                                    try {
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                                .replace(R.id.container, FragmentProfile.newInstance())
                                                .addToBackStack(null)
                                                .commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Utils.showErrorMessage(getContext(), e.getMessage());
                                    }
                                    Otto.post(new OttoMessage("updateProfile", null));
                                }
                            })
                            .setNegativeButton("Het", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                        d.show();
                        break;
                    }
                }

            }
        }
    }

