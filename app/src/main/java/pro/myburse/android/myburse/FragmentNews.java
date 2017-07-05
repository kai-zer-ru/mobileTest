package pro.myburse.android.myburse;


import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.json.New;

/**
 * Created by alexey on 04.07.17.
 */

public class FragmentNews extends Fragment {

    private App mApp;
    private Bus Otto;
    private ArrayList<New> News;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRV;


    public FragmentNews(){
        News = new ArrayList<>();
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
        GridLayoutManager glm = new GridLayoutManager(getContext(),2);
        mRV.setLayoutManager(glm);
        swipeRefreshLayout = viewRoot.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "тут SWIPE_REFRESH", Toast.LENGTH_SHORT).show();
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
    }


    @Subscribe
    public void OttoDispatch(OttoMessage msg){
        switch (msg.getAction()){
            case "getNews":{
                updateNews((Location) msg.getData());
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

        Uri.Builder builder = Uri.parse(App.URL_BASE).buildUpon();

        builder.appendQueryParameter("method","getNews");
        builder.appendQueryParameter("news_types",New.TYPE_PRODUCT+","+New.TYPE_WALL+","+New.TYPE_BLOG);
        if (location != null) {
            builder.appendQueryParameter("longitude", String.valueOf(location.getLongitude()));
            builder.appendQueryParameter("latitude", String.valueOf(location.getLatitude()));
        }
        //builder.appendQueryParameter("order","time_start-desc");
        //builder.appendQueryParameter("action_type","all");

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
                        News.add(object);
                    }

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

}
