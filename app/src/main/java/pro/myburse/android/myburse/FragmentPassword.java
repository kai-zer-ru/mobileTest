package pro.myburse.android.myburse;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;

public class FragmentPassword extends Fragment {
    private final static String ARG_USER_PWD = "user_password";
    private TextInputEditText tvPasswd, tvPasswdNew, tvPasswdConfirm;
    private Bus Otto;
    private App mApp;
    private String mOldPasswd;

    public FragmentPassword() {
    }

    public static FragmentPassword getInstance(String passwd){
        Fragment fragment = new FragmentPassword();
        Bundle args = new Bundle();
        args.putString(ARG_USER_PWD, passwd);
        fragment.setArguments(args);
        return (FragmentPassword) fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mUserId == 0 - текущий юзер, иначе другой
        if (getArguments()!=null){
            mOldPasswd = getArguments().getString(ARG_USER_PWD);
        }
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_password, container, false);
        tvPasswd = viewRoot.findViewById(R.id.passwd);
        if (mOldPasswd!=null){
            tvPasswd.setText(mOldPasswd);
        }
        tvPasswdNew = viewRoot.findViewById(R.id.passwd_new);
        tvPasswdConfirm = viewRoot.findViewById(R.id.passwd_confirm);

        tvPasswdNew.setError(null);

        Button btnPasswd = viewRoot.findViewById(R.id.btnPassword);
        btnPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_passwd = tvPasswdNew.getText().toString();
                //boolean cont = new_passwd.equals(tvPasswdConfirm.getText());
                if (new_passwd.equals(tvPasswdConfirm.getText().toString())) {
                    Toast.makeText(getContext(), "TODO FragmentPassword->setPassword("+new_passwd+")", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else{
                    tvPasswdNew.setError("Пароли не совпадают!");
                    tvPasswdNew.requestFocus();
                }
            }
        });
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState==null) {
            //Otto.post(new OttoMessage("getProfile",mUserId));
            //getProfile(mUserId);
        }
    }

    private void setPassword(String new_password){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
// TODO реализовать запрос на изменение пароля
/*        if (new_password == null){
            builder.appendQueryParameter("method","get_my_data");
        } else{
            builder.appendQueryParameter("method","get_user_data");
            builder.appendQueryParameter("ids", String.valueOf(id));
        }
        User user = mApp.getUser();
        if (user.isConnected()){
            builder.appendQueryParameter("user_id", String.valueOf(user.getId()));
            builder.appendQueryParameter("device_id",user.getDeviceId());
            builder.appendQueryParameter("access_key",user.getAccessKey());
        }
        String profileUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, profileUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("get_xx_data onResponse",response.toString());
                try {
                    int error = response.getInt("error");
                    if (error==0){
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
                            mUser = object;

                            if (mUser.getAvatarUrl()!=null) {
                                SingleVolley.getInstance(getContext()).getImageLoader().get(mUser.getAvatarUrl(), new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        ivImage.setImageBitmap(response.getBitmap());
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        ivImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_grey_50));
                                    }
                                });
                            }
                            tvId.setText(String.valueOf(mUser.getId()));
                            tvFirstName.setText(mUser.getFirstName());
                            tvMiddleName.setText(mUser.getMiddleName());
                            tvLastName.setText(mUser.getLastName());
//                            tvSocialNetworkId.setText(String.valueOf(mUser.getSocialNetworkId()));
                            tvSocialNetworkName.setText(mUser.getSocialType());
                            //tvExtId.setText(mUser.getSocialId());
                            //tvDeviceId.setText(mApp.getDeviceId());
                            tvPhone.setText(mUser.getPhone());
                            tvEmail.setText(mUser.getEmail());
                            tvBirthday.setText(mUser.getBirthday().toString());
                            //tvPassword.setText(mUser.getPassword());
                        } else {
                            // нет юзеров
                        }
                    } else {
                        Utils.showErrorMessage(getContext(),"get_xx_data"+error+"\n"+response.getString("error_text"));
                    }
                } catch (Exception e) {
                    Utils.showErrorMessage(getContext(),"get_xx_data Exception: "+e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("get_xx_data onErrorResponse",error.toString());
                Utils.showErrorMessage(getContext(),"get_xx_data onErrorResponse: "+error.toString());
            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);

*/
    }


    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()) {
            case "setNewPassword": {
                setPassword((String) msg.getData());
                break;
            }
            default: {

            }
        }
    }
    
}
