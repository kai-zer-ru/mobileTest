package pro.myburse.android.myburse;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        mLogin = rootView.findViewById(R.id.login);
        mPassword = rootView.findViewById(R.id.password);
        btnOK = rootView.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
                builder.appendQueryParameter("method", "login");
                String login = mLogin.getText().toString();

                if (login.startsWith("8")) {
                    login = "+7" + login.substring(1);
                    //editPhoneNumber.setText(mPhone);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    login = PhoneNumberUtils.formatNumber(login);
                } else {
                    login = PhoneNumberUtils.formatNumber(login, Locale.getDefault().getCountry());
                }

                if (Patterns.PHONE.matcher(login).matches()) {
                    login = login.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
                    mLogin.setText(login);
                } else {
                    login = mLogin.getText().toString();
                }

                builder.appendQueryParameter("login",login);
                builder.appendQueryParameter("password", mPassword.getText().toString());
                builder.appendQueryParameter("device_id", mApp.getDeviceId());
                String registerUrl=builder.build().toString();
                LoginActivity.showProgress("Подождите...");
                com.android.volley.Request request = new JsonObjectRequest(com.android.volley.Request.Method.GET, registerUrl, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoginActivity.hideProgress();
                        try {
                            if (response.getInt("error") == 0) {
                                Log.wtf("MyBurseLogin", response.toString());
                                JSONObject _response = response.getJSONObject("response");
                                int _count = _response.getInt("count");
                                if (_count>0){
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        @Override
                                        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                                                throws JsonParseException {
                                            try {
                                                return df.parse(json.getAsString());
                                            } catch (ParseException e) {
                                                return null;
                                            }
                                        }
                                    });
                                    Gson gson = gsonBuilder.create();

                                    JsonParser parser = new JsonParser();
                                    JSONArray items = _response.getJSONArray("items");
                                    JsonElement mJson =  parser.parse(items.get(0).toString());
                                    User object = gson.fromJson(mJson, User.class);
                                    object.setDeviceId(mApp.getDeviceId());
                                    mApp.setUser(object);
                                    getActivity().finish();
                                    Otto.post(new OttoMessage("updateProfile", object));
                                } else {
                                    // нет юзера, нет ошибки..)
                                    Utils.showErrorMessage(getContext(), "empty login: " + response.toString());
                                }
                            } else {
                                Utils.showErrorMessage(getContext(),"login "+response.getInt("error")+"\n"+response.getString("error_text"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.showErrorMessage(getContext(),"login Exception \n"+e.getMessage());
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

