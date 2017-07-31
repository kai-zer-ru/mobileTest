package pro.myburse.android.myburse;


import android.Manifest;
import android.content.pm.PackageManager;
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

import pro.myburse.android.myburse.Model.Feed;
import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.UI.AdapterFeed;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;



public class FragmentNews extends Fragment implements ObservableScrollViewCallbacks{

    private final int PERMISSION_REQUEST_ACCESS_LOCATION = 0;

    private App mApp;
    private Bus Otto;
    private ArrayList<Feed> mFeed;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabUp;
    private ObservableRecyclerView mRV;
    private AdapterFeed mAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private  LinearLayoutManager linearLayoutManager;
    private Location mCurrentLocation;
    private boolean isLoading = false;
    private boolean alreadyLoaded = false;


    public FragmentNews(){
       mFeed = new ArrayList<>();
    }

    public static FragmentNews getInstance(){
        FragmentNews fragment = new FragmentNews();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mRV = viewRoot.findViewById(R.id.rv);
        linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterFeed(mFeed, mApp);
        mRV.setAdapter(mAdapter);
        mRV.setScrollViewCallbacks(this);

        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeed.clear();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState==null && !alreadyLoaded) {
            alreadyLoaded = true;
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_LOCATION);
            } else {
                swipeRefreshLayout.setRefreshing(true);
                mFeed.clear();
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
                    swipeRefreshLayout.setRefreshing(true);
                    getCurrentLocation();
                } else {
                    updateNews(null,0);
                }
                return;
            }
        }
    }

    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()){
            case "getNews":{
                swipeRefreshLayout.setRefreshing(true);
                mFeed.clear();
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

    private void updateNews(final Location location, long previous_id){
        mCurrentLocation=location;
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();


        builder.appendQueryParameter("method","get_feed");
        builder.appendQueryParameter("limit", String.valueOf(App.COUNT_CARDS));
        if (location != null) {
            builder.appendQueryParameter("filters[longitude]", String.valueOf(location.getLongitude()));
            builder.appendQueryParameter("filters[latitude]", String.valueOf(location.getLatitude()));
        }
        if (0!= previous_id){
            builder.appendQueryParameter("last_id", String.valueOf(previous_id));
        }
        User user = mApp.getUser();
        if (user.isConnected()){
            builder.appendQueryParameter("user_id", String.valueOf(user.getId()));
            builder.appendQueryParameter("device_id",user.getDeviceId());
            builder.appendQueryParameter("access_key",user.getAccessKey());
        }
        String newsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, newsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                Log.wtf("onResponse",response.toString());
                try{
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
                            for (int i=0;i<_count;i++){
                                JsonElement mJson =  parser.parse(items.get(i).toString());
                                Feed object = gson.fromJson(mJson, Feed.class);
                                mFeed.add(object);
                            }
                        } else {
                            // нет новостей
                        }
                    } else {
                        Utils.showErrorMessage(getContext(),"get_feed"+error+"\n"+response.getString("error_text"));
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Utils.showErrorMessage(getContext(),"get_feed JSONException: "+e.toString());
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                isLoading=false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                Log.wtf("get_feed onErrorResponse",error.toString());
                Utils.showErrorMessage(getContext(),"get_blogs onErrorResponse: "+error.toString());
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
                        updateNews(location,0);
                    }

                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateNews(null,0);
                    }

                });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //mFabUp.show();
        if(scrollY<0) {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
                mFabUp.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            } else {
                mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        }
        if(scrollY > 0) {
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-(App.COUNT_CARDS/2)) {
                    mFabUp.hide();
                    isLoading=true;
                    Log.wtf("onScrollChanged","Update news last_news_id = "+ mFeed.get(mFeed.size() - 1).getId());
                    updateNews(mCurrentLocation, mFeed.get(mFeed.size() - 1).getId());
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
            if (linearLayoutManager.findFirstVisibleItemPosition()>0) {
                mFabUp.show();
                mFabUp.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }else{
                mFabUp.animate().translationY(mFabUp.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        }
    }
}
