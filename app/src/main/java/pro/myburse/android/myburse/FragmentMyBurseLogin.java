package pro.myburse.android.myburse;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.odnoklassniki.OkPerson;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;
import ru.ok.android.sdk.util.OkScope;

import static pro.myburse.android.myburse.R.id.btnMyBurse;


public class FragmentMyBurseLogin extends Fragment
{

    private EditText mLogin;
    private EditText mPassword;
    private Button btnOK;
    private App mApp;
    private Bus Otto;

    public static FragmentMyBurseLogin newInstance() {
        FragmentMyBurseLogin fragment = new FragmentMyBurseLogin();
        return fragment;
    }

    public FragmentMyBurseLogin() {
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
        View rootView = inflater.inflate(R.layout.fragment_myburse_login, container, false);
        ((LoginActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener
        mLogin = rootView.findViewById(R.id.login);
        mLogin.setText(mApp.getLogin());
        mPassword = rootView.findViewById(R.id.password);
        btnOK = rootView.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User();
                user.setDeviceId(mApp.getUser().getDeviceId());
                Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
                builder.appendQueryParameter("method","login");
                String login =  mLogin.getText().toString();
                if (Patterns.PHONE.matcher(login).matches()){
                    login = login.replace(" ","").replace("(","").replace(")","").replace("-","");
                }

                builder.appendQueryParameter("login",login);
                builder.appendQueryParameter("password", mPassword.getText().toString());
                builder.appendQueryParameter("device_id", user.getDeviceId());
                String registerUrl=builder.build().toString();
                LoginActivity.showProgress("Подождите...");
                com.android.volley.Request request = new JsonObjectRequest(com.android.volley.Request.Method.GET, registerUrl, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("error") == 0) {
                                Log.wtf("MyBurseLogin", response.toString());
                                try {
                                    user.setId(response.getString("user_id"));
                                    user.setFirstName(response.getString("firstname"));
                                    user.setMiddleName(response.getString("middlename"));
                                    user.setLastName(response.getString("lastname"));
                                    user.setEmail(response.getString("email"));
                                    user.setLogin(response.getString("login"));
                                    user.setPhone(response.getString("phone_number"));
                                    user.setAccess_key(response.getString("access_key"));
                                    user.setBalance_bids(response.getInt("balance_bids"));
                                    user.setBalance_bonus(response.getInt("balance_bonus"));
                                    user.setBalance_money(response.getInt("balance_money"));
                                    user.setUrlImage_50(response.getString("avatar"));
                                    mApp.setUser(user);
                                    mApp.setLogin(response.getString("login"));
                                    LoginActivity.hideProgress();
                                    getActivity().finish();
                                    Otto.post(new OttoMessage("updateProfile", null));
                                } catch (JSONException e) {
                                    LoginActivity.hideProgress();
                                    e.printStackTrace();
                                }
                            } else {
                                LoginActivity.hideProgress();
                                Utils.showErrorMessage(getContext(),"Ошибка: "+response.getInt("error")+" "+response.getString("error_text"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LoginActivity.hideProgress();
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
        });
        return rootView;
    }

}

