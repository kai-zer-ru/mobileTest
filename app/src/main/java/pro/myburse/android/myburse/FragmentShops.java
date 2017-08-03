package pro.myburse.android.myburse;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.Date;

import pro.myburse.android.myburse.Model.Shop;
import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.UI.AdapterShops;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;


public class FragmentShops extends Fragment implements ObservableScrollViewCallbacks{

    private App mApp;
    private Bus Otto;
    private ArrayList<Shop> mShops;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabUp;
    private ObservableRecyclerView mRV;
    private AdapterShops mAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int PERMISSION_REQUEST_ACCESS_LOCATION = 0;
    private  LinearLayoutManager linearLayoutManager;
    private Location mCurrentLocation;
    private boolean isLoading = false;
    private boolean alreadyLoaded = false;

    public FragmentShops(){
       mShops = new ArrayList<>();
    }

    public static FragmentShops getInstance(){
        FragmentShops fragment = new FragmentShops();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_list,container,false);

        mRV = viewRoot.findViewById(R.id.rv);
        linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterShops(mShops);
        //mAdapter.setMode(Attributes.Mode.Single);
        mRV.setAdapter(mAdapter);
        mRV.setScrollViewCallbacks(this);

        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mShops.clear();
                mAdapter.notifyDataSetChanged();
                getCurrentLocation();
            }
        });

        mFabUp = viewRoot.findViewById(R.id.fab_up);
        mFabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
                linearLayoutManager.scrollToPositionWithOffset(0,0);
            }
        });
        mFabUp.hide();
        return viewRoot;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState==null && !alreadyLoaded) {
            alreadyLoaded = true;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_LOCATION);
            } else {
                swipeRefreshLayout.setRefreshing(true);
                mShops.clear();
                mAdapter.notifyDataSetChanged();
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    updateShops(null,0);
                }
                return;
            }
        }
    }

    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()){
            case "getShops":{
                swipeRefreshLayout.setRefreshing(true);
                mShops.clear();
                mAdapter.notifyDataSetChanged();
                getCurrentLocation();
            }
            default:{

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Otto.unregister(this);
    }

    private void updateShops(final Location location, long last_id){
        mCurrentLocation=location;
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","get_shops");
        builder.appendQueryParameter("limit", String.valueOf(App.COUNT_CARDS));
        if (location != null) {
            builder.appendQueryParameter("filter[longitude]", String.valueOf(location.getLongitude()));
            builder.appendQueryParameter("filter[latitude]", String.valueOf(location.getLatitude()));
        }
        if (0 != last_id){
            builder.appendQueryParameter("last_id", String.valueOf(last_id));
        }
        User user = mApp.getUser();
        if (user!=null && user.isConnected()){
            builder.appendQueryParameter("user_id", String.valueOf(user.getId()));
            builder.appendQueryParameter("device_id",user.getDeviceId());
            builder.appendQueryParameter("access_key",user.getAccessKey());
        }
        String newsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, newsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                Log.wtf("onResponse get_shops",response.toString());
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
                            for (int i=0;i<items.length();i++){
                                JsonElement mJson =  parser.parse(items.get(i).toString());
                                Shop object = gson.fromJson(mJson, Shop.class);
                                mShops.add(object);
                            }
                        } else {
                        // нет новостей
                        }
                } else {
                    Utils.showErrorMessage(getContext(),"get_shops "+error+"\n"+response.getString("error_text"));
                }
                        mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isLoading=false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                Log.wtf("get_shops onErrorResponse",error.toString());
                Utils.showErrorMessage(getContext(),error.toString());

            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
    }

    public void getCurrentLocation() {
        mFabUp.hide();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        updateShops(location,0);
                    }

                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateShops(null,0);
                    }

                });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if(scrollY<0) {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
                mFabUp.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            } else {
                mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        }
        if(scrollY > 0) {
            int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            if (!isLoading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-(App.COUNT_CARDS/2)) {
                    isLoading=true;
                    mFabUp.hide();
                    updateShops(mCurrentLocation, mShops.get(mShops.size() - 1).getId());
                }
            }
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState==ScrollState.UP) {
            mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
        }else if (scrollState==ScrollState.DOWN) {
            if (linearLayoutManager.findLastVisibleItemPosition()>0) {
                mFabUp.show();
                mFabUp.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }else{
                mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        }
    }
}
