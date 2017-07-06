package pro.myburse.android.myburse;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pro.myburse.android.myburse.UI.AdapterNews;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.json.New;

/**
 * Created by alexey on 04.07.17.
 */

public class FragmentNews extends Fragment implements ObservableScrollViewCallbacks{

    private App mApp;
    private Bus Otto;
    private ArrayList<New> mNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ObservableRecyclerView mRV;
    private AdapterNews mAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int PERMISSION_REQUEST_ACCESS_LOCATION = 0;
    private  LinearLayoutManager linearLayoutManager;
    private Location mCurrentLocation;
    private boolean isLoading = false;


    public FragmentNews(){
       mNews = new ArrayList<>();
    }

    public static FragmentNews getInstance(){
        Bundle args = new Bundle();
        //args.putSerializable(KEY_ORDER, o);
        FragmentNews fragment = new FragmentNews();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_news,container,false);

        mRV = viewRoot.findViewById(R.id.rv);
        linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterNews(mNews);
        //mAdapter.setMode(Attributes.Mode.Single);
        mRV.setAdapter(mAdapter);
        mRV.setScrollViewCallbacks(this);

        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNews.clear();
                mAdapter.notifyDataSetChanged();
                getCurrentLocation();
            }
        });
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_LOCATION);
        } else {
            swipeRefreshLayout.setRefreshing(true);
            mNews.clear();
            mAdapter.notifyDataSetChanged();
            getCurrentLocation();
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
                    updateNews(null);
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
                mNews.clear();
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


    private void updateNews(final Location location){
        mCurrentLocation=location;
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","getNews");
        if (location != null) {
            builder.appendQueryParameter("longitude", String.valueOf(location.getLongitude()));
            builder.appendQueryParameter("latitude", String.valueOf(location.getLatitude()));
        }
        String newsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, newsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("onResponse",response.toString());
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i=0;i<items.length();i++){
                        JsonParser parser = new JsonParser();
                        JsonElement mJson =  parser.parse(items.get(i).toString());
                        Gson gson = new Gson();
                        New object = gson.fromJson(mJson, New.class);
                        mNews.add(object);
                    }
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("onErrorResponse",error.toString());
            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateNews(final Location location, Long previous_id){
        mCurrentLocation=location;
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","getNews");
        if (location != null) {
            builder.appendQueryParameter("longitude", String.valueOf(location.getLongitude()));
            builder.appendQueryParameter("latitude", String.valueOf(location.getLatitude()));
        }
        if (null != previous_id){
            builder.appendQueryParameter("last_news_id", String.valueOf(previous_id));
        }
        String newsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, newsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("onResponse",response.toString());
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i=0;i<items.length();i++){
                        JsonParser parser = new JsonParser();
                        JsonElement mJson =  parser.parse(items.get(i).toString());
                        Gson gson = new Gson();
                        New object = gson.fromJson(mJson, New.class);
                        mNews.add(object);
                    }
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isLoading=false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Log.wtf("onErrorResponse",error.toString());
            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        updateNews(location);
                    }

                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateNews(null);
                    }

                });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if(scrollY > 0) //check for scroll down
        {
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (!isLoading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    isLoading=true;
                    Log.wtf("onScrollChanged","Update news last_news_id = "+mNews.get(mNews.size() - 1).getId());
                    updateNews(mCurrentLocation, mNews.get(mNews.size() - 1).getId());
                }
            }
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
