package pro.myburse.android.myburse;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;

public class FragmentRegister extends Fragment {


    private static final int REQUEST_SMS = 0;
    private EditText editPhoneNumber;
    private EditText editSMS;
    private Button btnConfirm;
    private Button btnSMS;
    private SMSReceiver smsReceiver=null;
    private App mApp;
    private Bus Otto;
    private String mPhone;


    public FragmentRegister() {
    }

    public static FragmentRegister newInstance() {
        FragmentRegister fragment = new FragmentRegister();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_register, container, false);


        btnSMS = viewRoot.findViewById(R.id.btnSMS);
        editPhoneNumber = viewRoot.findViewById(R.id.editPhone);
        editSMS = viewRoot.findViewById(R.id.editSMS);
        btnConfirm = viewRoot.findViewById(R.id.btnConfirm);

        editSMS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnConfirm.setVisibility(View.VISIBLE);
                return false;
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSMS.setEnabled(false);
                mPhone = editPhoneNumber.getText().toString();
                if (mPhone.startsWith("8")){
                    mPhone = "+7"+mPhone.substring(1);
                    editPhoneNumber.setText(mPhone);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    mPhone = PhoneNumberUtils.formatNumber(mPhone);
                }else{
                    mPhone = PhoneNumberUtils.formatNumber(mPhone, Locale.getDefault().getCountry());
                }
                editPhoneNumber.setText(mPhone);

                if (Patterns.PHONE.matcher(mPhone).matches()){
                    Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
                    builder.appendQueryParameter("method","sendConfirmSms");
                    builder.appendQueryParameter("phone",mPhone);
                    builder.appendQueryParameter("device_id",mApp.getDeviceId());

                    String smsUrl=builder.build().toString();

                    Request request = new JsonObjectRequest(Request.Method.GET, smsUrl, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.wtf("onResponse",response.toString());
                            try {
                                int error = response.getInt("error");
                                if (error==0){
                                    Toast.makeText(getContext(), "Запрос СМС подтверждения отправлен", Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.showErrorMessage(getContext(), response.getString("error_text"));
                                    btnSMS.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                btnSMS.setEnabled(true);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showErrorMessage(getContext(),error.toString());
                            btnSMS.setEnabled(true);
                        }
                    });

                    SingleVolley.getInstance(getContext()).addToRequestQueue(request);
                }else{
                    Utils.showErrorMessage(getContext(),"Неправильный номер телефона");
                }
            }
        });

        editPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL) {
                        String formattedNumber = textView.getText().toString();
                        if (formattedNumber.startsWith("8")){
                            formattedNumber = "+7"+formattedNumber.substring(1);
                            textView.setText(formattedNumber);
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            formattedNumber = PhoneNumberUtils.formatNumber(formattedNumber);
                        }else{
                            formattedNumber = PhoneNumberUtils.formatNumber(formattedNumber, Locale.getDefault().getCountry());
                        }
                        textView.setText(formattedNumber);
                    }
                return false;
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = mApp.getUser();
                user.setPhone(editPhoneNumber.getText().toString());
                mApp.setUser(user);
                sendPhoneConfirmation(editSMS.getText().toString().trim());
            }
        });
        return viewRoot;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestSmsPermission();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (smsReceiver!=null) {
            getActivity().unregisterReceiver(smsReceiver);
        }
    }

    private void sendPhoneConfirmation(String text){
        final String sms = text.replaceAll("\\D+","");
        editSMS.setText(sms);
        btnConfirm.setEnabled(false);
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","confirmPhone");
        builder.appendQueryParameter("phone_hash",sms);
        builder.appendQueryParameter("device_id",mApp.getDeviceId());

        String phoneUrl=builder.build().toString();

        final Request request = new JsonObjectRequest(Request.Method.GET, phoneUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("onResponse",response.toString());
                final User mUser = new User();
                mUser.setDeviceId(mApp.getDeviceId());
                mUser.setPhone(mPhone);
                try {
                    mUser.setId(response.getString("user_id"));
                } catch (JSONException e) {
                    Utils.showErrorMessage(getContext(),e.toString());
                    e.printStackTrace();
                }
                mUser.setPassword(sms);
                mApp.setUser(mUser);
                try {
                    int error = response.getInt("error");
                    if (error==0){
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
                    } else {
                        Utils.showErrorMessage(getContext(), "Код ошибки: "+String.valueOf(error)+"\n"+response.getString("error_text"));
                        btnConfirm.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnConfirm.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showErrorMessage(getContext(),error.toString());
            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);

    }

    private class SMSReceiver extends BroadcastReceiver{
        private final static String ACTION = "android.provider.Telephony.SMS_RECEIVED";

        SMSReceiver(){
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(ACTION)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        assert pdus != null;
                        final SmsMessage[] messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++) {
                            Object o = pdus[i];
                            messages[i] = getIncomingMessage(o, bundle);
                        }
                        if (messages.length > -1) {
                            sendPhoneConfirmation(messages[0].getMessageBody());
                        }
                    }
                }
            }catch (Exception e){
                Utils.showErrorMessage(getContext(),e.getMessage());
            }
        }

        private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
            SmsMessage currentSMS;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
            } else {
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
            }
            return currentSMS;
        }
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(getActivity(), permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[]{permission};
            requestPermissions(permission_list, REQUEST_SMS);
        }else{
            IntentFilter filter = new IntentFilter();
            filter.setPriority(2147483647);
            filter.addAction(SMSReceiver.ACTION);
            smsReceiver = new SMSReceiver();
            getActivity().registerReceiver(smsReceiver,filter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(SMSReceiver.ACTION);
                filter.setPriority(2147483647);
                smsReceiver = new SMSReceiver();
                getActivity().registerReceiver(smsReceiver,filter);

            } else {
                editSMS.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);

            }
        }
    }


}
