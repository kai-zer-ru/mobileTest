package pro.myburse.android.myburse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.List;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;

public class FragmentProfile extends Fragment {
    private final static String ARG_USER_ID = "user_id";
    private TextInputEditText tvId, tvFirstName, tvLastName, tvSocialNetworkId, tvSocialNetworkName,
     tvExtId,tvDeviceId,tvPhone, tvEmail, tvBirthday, tvPassword;
    private ImageView ivImage;
    private Bus Otto;
    private App mApp;
    private long mUserId;
    private User mUser;

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
        String url = (mUser.getUrlImage_50()==null)?mUser.getUrlImage():mUser.getUrlImage_50();
        if (url!=null) {
            SingleVolley.getInstance(getContext()).getImageLoader().get(mUser.getUrlImage(), new ImageLoader.ImageListener() {
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
        tvId = viewRoot.findViewById(R.id.id);
        tvId.setText(String.valueOf(mUser.getId()));

        tvFirstName = viewRoot.findViewById(R.id.firstName);
        tvFirstName.setText(mUser.getFirstName());

        tvLastName = viewRoot.findViewById(R.id.lastName);
        tvLastName.setText(mUser.getLastName());

        tvSocialNetworkId = viewRoot.findViewById(R.id.socialNetworkId);
        tvSocialNetworkId.setText(String.valueOf(mUser.getSocialNetworkId()));

        tvSocialNetworkName = viewRoot.findViewById(R.id.socialNetworkName);
        tvSocialNetworkName.setText(mUser.getSocialNetworkName());

        tvExtId = viewRoot.findViewById(R.id.extId);
        tvExtId.setText(mUser.getExtId());

        tvDeviceId = viewRoot.findViewById(R.id.deviceId);
        tvDeviceId.setText(mApp.getDeviceId());

        tvPhone = viewRoot.findViewById(R.id.phone);
        tvPhone.setText(mUser.getPhone());

        tvEmail = viewRoot.findViewById(R.id.email);
        tvEmail.setText(mUser.getEmail());

        tvBirthday = viewRoot.findViewById(R.id.birthday);
        tvBirthday.setText(mUser.getBirthday().toString());

        tvPassword = viewRoot.findViewById(R.id.password);
        tvPassword.setText(mUser.getPassword());

        return viewRoot;
    }

    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        try {
            switch (msg.getAction()) {
                case "getProfile": {
                    String url = (mUser.getUrlImage_50()==null)?mUser.getUrlImage():mUser.getUrlImage_50();
                    if (url!=null) {
                        SingleVolley.getInstance(getContext()).getImageLoader().get(mUser.getUrlImage(), new ImageLoader.ImageListener() {
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
                    tvLastName.setText(mUser.getLastName());
                    tvSocialNetworkId.setText(String.valueOf(mUser.getSocialNetworkId()));
                    tvSocialNetworkName.setText(mUser.getSocialNetworkName());
                    tvExtId.setText(mUser.getExtId());
                    tvDeviceId.setText(mApp.getDeviceId());
                    tvPhone.setText(mUser.getPhone());
                    tvEmail.setText(mUser.getEmail());
                    tvBirthday.setText(mUser.getBirthday().toString());
                    tvPassword.setText(mUser.getPassword());
                }
                default: {

                }
            }
        }catch (Exception e){
            Utils.showErrorMessage(getContext(), e.getMessage());
        }
    }

    
}
