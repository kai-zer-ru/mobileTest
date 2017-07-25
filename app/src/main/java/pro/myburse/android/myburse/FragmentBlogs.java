package pro.myburse.android.myburse;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pro.myburse.android.myburse.Model.Blog;
import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.UI.AdapterBlogs;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;


public class FragmentBlogs extends Fragment implements ObservableScrollViewCallbacks{

    private App mApp;
    private Bus Otto;
    private ArrayList<Blog> mBlogs;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabUp;
    private ObservableRecyclerView mRV;
    private AdapterBlogs mAdapter;
    private  LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;

    public FragmentBlogs(){
       mBlogs = new ArrayList<>();
    }

    public static FragmentBlogs getInstance(){
        FragmentBlogs fragment = new FragmentBlogs();
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
        mAdapter = new AdapterBlogs(mBlogs);
        //mAdapter.setMode(Attributes.Mode.Single);
        mRV.setAdapter(mAdapter);
        mRV.setScrollViewCallbacks(this);

        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBlogs.clear();
                mAdapter.notifyDataSetChanged();
                updateBlogs();
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
        if (savedInstanceState == null){
            swipeRefreshLayout.setRefreshing(true);
            mBlogs.clear();
            mAdapter.notifyDataSetChanged();
            updateBlogs();
        }
    }

    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()){
            case "getBlogs":{
                swipeRefreshLayout.setRefreshing(true);
                mBlogs.clear();
                mAdapter.notifyDataSetChanged();
                updateBlogs();
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


    private void updateBlogs(){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","getBlogs");
        builder.appendQueryParameter("limit", String.valueOf(App.COUNT_CARDS));
        User user = mApp.getUser();
        if (user.isConnected()){
            builder.appendQueryParameter("user_id",user.getId());
            builder.appendQueryParameter("device_id",user.getDeviceId());
            builder.appendQueryParameter("access_key",user.getAccess_key());
        }

        String blogsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, blogsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("onResponse",response.toString());
                try {
                    JSONArray items = response.getJSONArray("items");
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    for (int i=0;i<items.length();i++){
                        JsonElement mJson =  parser.parse(items.get(i).toString());
                        Blog object = gson.fromJson(mJson, Blog.class);
                        mBlogs.add(object);
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
                isLoading = false;
                Log.wtf("onErrorResponse",error.toString());
                swipeRefreshLayout.setRefreshing(false);
                Utils.showErrorMessage(getContext(),error.toString());
            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateBlogs(Long previous_id){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","getBlogs");
        builder.appendQueryParameter("limit", String.valueOf(App.COUNT_CARDS));
        if (null != previous_id){
            builder.appendQueryParameter("offset", String.valueOf(previous_id));
        }
        User user = mApp.getUser();
        if (user.isConnected()){
            builder.appendQueryParameter("user_id",user.getId());
            builder.appendQueryParameter("device_id",user.getDeviceId());
            builder.appendQueryParameter("access_key",user.getAccess_key());
        }
        String blogsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, blogsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("onResponse",response.toString());
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i=0;i<items.length();i++){
                        JsonParser parser = new JsonParser();
                        JsonElement mJson =  parser.parse(items.get(i).toString());
                        Gson gson = new Gson();
                        Blog object = gson.fromJson(mJson, Blog.class);
                        mBlogs.add(object);
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
                swipeRefreshLayout.setRefreshing(false);
                Utils.showErrorMessage(getContext(),error.toString());

            }
        });

        SingleVolley.getInstance(getContext()).addToRequestQueue(request);
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
                    updateBlogs(mBlogs.get(mBlogs.size() - 1).getId());
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
