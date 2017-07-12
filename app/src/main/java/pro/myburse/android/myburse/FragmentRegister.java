package pro.myburse.android.myburse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import pro.myburse.android.myburse.Json.New;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;

public class FragmentRegister extends Fragment {

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private TextView editPhoneNumber;
    private TextView editEMail;
    private TextView editPassword;
    private Button btnRegister;
    private Button btnSMS;
    private SMSReceiver smsReceiver;
    private App mApp;
    private Bus Otto;


    private OnFragmentInteractionListener mListener;

    public FragmentRegister() {
    }

    public static FragmentRegister newInstance(String param1, String param2) {
        FragmentRegister fragment = new FragmentRegister();
  /*      Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public static FragmentRegister newInstance() {
        FragmentRegister fragment = new FragmentRegister();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        setRetainInstance(true);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_register, container, false);
        editPhoneNumber = viewRoot.findViewById(R.id.editPhone);
        editEMail = viewRoot.findViewById(R.id.editEmail);
        editPassword = viewRoot.findViewById(R.id.editPassword);
        btnSMS = viewRoot.findViewById(R.id.btnSMS);
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber =  editPhoneNumber.getText().toString();
                if (android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
                    Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
                    builder.appendQueryParameter("method","sendConfirmSms");
                    builder.appendQueryParameter("phone",phoneNumber);
                    builder.appendQueryParameter("device_id",mApp.getDevice_Id());

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
                                    Utils.showErrorMessage(getContext(), String.valueOf(error));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showErrorMessage(getContext(),error.toString());
                        }
                    });

                    SingleVolley.getInstance(getContext()).addToRequestQueue(request);                }
            }
        });

        editPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL) {
                        String formattedNumber = textView.getText().toString();
                        if (formattedNumber.startsWith("8")){
                            formattedNumber = "+7"+formattedNumber.substring(1);
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return viewRoot;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMSReceiver.ACTION);
        smsReceiver = new SMSReceiver();
        getActivity().registerReceiver(smsReceiver,filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getActivity().unregisterReceiver(smsReceiver);
    }

    private class SMSReceiver extends BroadcastReceiver{
        private final static String ACTION = "android.provider.Telephony.SMS_RECEIVED";

        SMSReceiver(){
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)){
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[])bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        Object o = pdus[i];
                        messages[i] = getIncomingMessage(o, bundle);
                    }
                    if (messages.length > -1) {
                        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
                        builder.appendQueryParameter("method","confirmPhone");
                        builder.appendQueryParameter("phone_hash",messages[0].getMessageBody().replaceAll("\\D+",""));
                        builder.appendQueryParameter("device_id",mApp.getDevice_Id());

                        String phoneUrl=builder.build().toString();

                        final Request request = new JsonObjectRequest(Request.Method.GET, phoneUrl, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.wtf("onResponse",response.toString());
                                try {
                                    int error = response.getInt("error");
                                    if (error==0){
                                        Toast.makeText(getContext(), "Ушло подтверждение СМС " + messages[0].getMessageBody().replaceAll("\\D+",""), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Utils.showErrorMessage(getContext(), String.valueOf(error));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                }
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

}
