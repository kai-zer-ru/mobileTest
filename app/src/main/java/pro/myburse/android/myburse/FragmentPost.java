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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pro.myburse.android.myburse.Model.Blog;
import pro.myburse.android.myburse.Model.User;
import pro.myburse.android.myburse.UI.AdapterPost;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Utils.Utils;


public class FragmentPost extends Fragment implements ObservableScrollViewCallbacks{

    private App mApp;
    private Bus Otto;
    private ArrayList<Blog> mPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabUp;
    private ObservableRecyclerView mRV;
    private AdapterPost mAdapter;
    private  LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean alreadyLoaded = false;
    private long post_id;

    public FragmentPost(){
        mPosts = new ArrayList<>();
    }

    public static FragmentPost getInstance(long post_id){
        FragmentPost fragment = new FragmentPost();
        Bundle args = new Bundle();
        args.putLong("post_id", post_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApp = (App) getActivity().getApplication();
        Otto = mApp.getOtto();
        Otto.register(this);
        if (getArguments()!=null){
            post_id = getArguments().getLong("post_id");
        } else{
            post_id=-1;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_list,container,false);

        mRV = viewRoot.findViewById(R.id.rv);
        linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterPost(mPosts);
        mRV.setAdapter(mAdapter);
        mRV.setScrollViewCallbacks(this);

        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPosts.clear();
                mAdapter.notifyDataSetChanged();
                updatePosts(post_id);
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
            swipeRefreshLayout.setRefreshing(true);
            mPosts.clear();
            mAdapter.notifyDataSetChanged();
            updatePosts(post_id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Otto.unregister(this);
    }


    private void updatePosts(long id){
        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();
        builder.appendQueryParameter("method","get_blog_post");
        User user = mApp.getUser();
        if (user!=null) {
            builder.appendQueryParameter("user_id", String.valueOf(user.getId()));
            builder.appendQueryParameter("device_id", user.getDeviceId());
            builder.appendQueryParameter("access_key", user.getAccessKey());
        }
        builder.appendQueryParameter("post_id", String.valueOf(id));


        String postsUrl=builder.build().toString();

        Request request = new JsonObjectRequest(Request.Method.GET, postsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                Log.wtf("onResponse get_blog_post",response.toString());
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
                                Blog object = gson.fromJson(mJson, Blog.class);
                                mPosts.add(object);
                            }
                        } else {
                            // нет новостей
                        }
                    } else {
                        Utils.showErrorMessage(getContext(),"get_blog_post "+error+"\n"+response.getString("error_text"));
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
                    updatePosts(post_id);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
