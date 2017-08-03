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
import android.widget.ImageView;

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

public class FragmentProfile extends Fragment {
    private final static String ARG_USER_ID = "user_id";
    private TextInputEditText tvId, tvFirstName, tvLastName, tvPhone, tvEmail, tvBirthday, tvSocialType;
    private ImageView ivImage;
    private Bus Otto;
    private App mApp;
    private long mUserId;
    private User mUser;
    private TextInputEditText tvMiddleName;

    public FragmentProfile() {
    }

    public static FragmentProfile getInstance(long user_id){
        Fragment fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, user_id);
        fragment.setArguments(args);
        return (FragmentProfile) fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mUserId == 0 - текущий юзер, иначе другой
        if (getArguments()!=null){
            mUserId = getArguments().getLong(ARG_USER_ID);
        }
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_profile, container, false);
        ivImage = viewRoot.findViewById(R.id.ivImage);
        tvId = viewRoot.findViewById(R.id.id);
        tvFirstName = viewRoot.findViewById(R.id.firstName);
        tvMiddleName = viewRoot.findViewById(R.id.middleName);
        tvLastName = viewRoot.findViewById(R.id.lastName);
        tvSocialType = viewRoot.findViewById(R.id.socialType);
        tvPhone = viewRoot.findViewById(R.id.phone);
        tvEmail = viewRoot.findViewById(R.id.email);
        tvBirthday = viewRoot.findViewById(R.id.birthday);
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState==null) {
            //Otto.post(new OttoMessage("getProfile",mUserId));
            getProfile(mUserId);
        }
    }

    private void getProfile(long id){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();

        if (id==0){
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
                            tvSocialType.setText(String.valueOf(mUser.getSocialType()));
                            tvPhone.setText(mUser.getPhone());
                            tvEmail.setText(mUser.getEmail());
                            tvBirthday.setText(mUser.getBirthday());
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


    }


    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()) {
            case "getProfile": {
                getProfile((long) msg.getData());
                break;
            }
            default: {

            }
        }
    }
    
}
