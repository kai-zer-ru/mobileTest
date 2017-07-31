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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;


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
                                    user.setId(response.getInt("user_id"));
                                    user.setFirstName(response.getString("first_name"));
                                    user.setMiddleName(response.getString("middle_name"));
                                    user.setLastName(response.getString("last_name"));
                                    user.setEmail(response.getString("email"));
                                    user.setLogin(response.getString("login"));
                                    user.setPhone(response.getString("phone"));
                                    user.setAccessKey(response.getString("access_key"));
                                    user.setBalanceBids(response.getInt("balance_bids"));
                                    user.setBalanceBonus(response.getInt("balance_bonus"));
                                    user.setBalanceMoney(response.getInt("balance_money"));
                                    user.setUrlImage_50(response.getString("avatar_url"));
                                    mApp.setUser(user);
                                    mApp.setLogin(response.getString("login"));
                                    LoginActivity.hideProgress();
                                    getActivity().finish();
                                    Otto.post(new OttoMessage("updateProfile", null));
                                } catch (JSONException e) {
                                    LoginActivity.hideProgress();
                                    Utils.showErrorMessage(getContext(), e.toString());
                                    e.printStackTrace();
                                }
                            } else {
                                LoginActivity.hideProgress();
                                Utils.showErrorMessage(getContext(),"login "+response.getInt("error")+"/n"+response.getString("error_text"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LoginActivity.hideProgress();
                            Utils.showErrorMessage(getContext(),"login JSONException "+e.getMessage());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoginActivity.hideProgress();
                        Log.wtf("login onErrorResponse",error.toString());
                        Utils.showErrorMessage(getContext(),"login "+error.toString());

                    }
                });

                SingleVolley.getInstance(getContext()).addToRequestQueue(request);

            }
        });
        return rootView;
    }

}

