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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.otto.Bus;
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
import pro.myburse.android.myburse.Utils.SingleVolley;

public class FragmentProfile extends Fragment {

    private TextInputEditText tvId, tvFirstName, tvLastName, tvSocialNetworkId, tvSocialNetworkName,
     tvExtId,tvDeviceId,tvPhone, tvEmail, tvBirthday;
    private ImageView ivImage;
    private Bus Otto;
    private App mApp;
    private User mUser;

    public FragmentProfile() {
    }


    public static FragmentProfile newInstance() {
        return new FragmentProfile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        mUser = mApp.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_profile, container, false);
        ivImage = viewRoot.findViewById(R.id.ivImage);

        VKRequest yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,"photo_50"));

        yourRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    List usersArray = (VKList) response.parsedModel;

                    VKApiUserFull userFull = (VKApiUserFull) usersArray.get(0);
                    User mUser = mApp.getUser();
                    mUser.setUrlImage_50(((VKApiUserFull) usersArray.get(0)).photo_50);
                    mApp.setUser(mUser);
                    SingleVolley.getInstance(getContext()).getImageLoader().get(mUser.getUrlImage_50(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            ivImage.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ivImage.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.md_grey_50));
                        }
                    });
                }
            }
        );
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
        tvDeviceId.setText(mUser.getDeviceId());

        tvPhone = viewRoot.findViewById(R.id.phone);
        tvPhone.setText(mUser.getPhone());

        tvEmail = viewRoot.findViewById(R.id.email);
        tvEmail.setText(mUser.getEmail());

        tvBirthday = viewRoot.findViewById(R.id.birthday);
        tvBirthday.setText(mUser.getBirthday().toString());

        return viewRoot;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }



}
