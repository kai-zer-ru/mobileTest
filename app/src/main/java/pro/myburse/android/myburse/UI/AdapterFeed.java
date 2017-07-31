package pro.myburse.android.myburse.UI;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mikepenz.iconics.view.IconicsTextView;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pro.myburse.android.myburse.App;
import pro.myburse.android.myburse.Model.Feed;
import pro.myburse.android.myburse.R;
import pro.myburse.android.myburse.Utils.OttoMessage;
import pro.myburse.android.myburse.Utils.SingleVolley;



public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.FeedViewHolder> {
    ArrayList<Feed> mFeed;
    Context mContext;
    App mApp;
    Bus Otto;

    public AdapterFeed(ArrayList<Feed> news, App app){
        super();
        this.mFeed =news;
        mApp = app;
        Otto = mApp.getOtto();

    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.card, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, int position) {
        final Feed mNew = mFeed.get(position);
        ImageLoader imageLoader = SingleVolley.getInstance(mContext).getImageLoader();

        imageLoader.get(mNew.getOwner().getAvatarUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mOwnerImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        holder.mOwnerName.setText(mNew.getOwner().getName());
        holder.mItemType.setText(mNew.getItemType());
        holder.mCreatedAt.setText(mNew.getCreatedAtFormated());
        holder.mUpdatedAt.setText(mNew.getUpdatedAtFormated());
        holder.mTitle.setText(mNew.getTitle());

      imageLoader.get(mNew.getImage().getUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        //Picasso.with(holder.mImage.getContext()).load(mNew.getImage()).placeholder(android.R.drawable.progress_horizontal).into(holder.mImage);
        //holder.mText.setText(mNew.getText());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            holder.mPreview.setText(Html.fromHtml(mNew.getText(), Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.mPreview.setText(Html.fromHtml(mNew.getText()));
        }
        holder.mCounters.setText(String.format("{faw-comment} %d {faw-heart} %d",mNew.getCommentsCount(),mNew.getLikesCount()));
        holder.mRating.setVisibility(View.GONE);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mNew.getItemType()){
                    case Feed.TYPE_BLOG:  {
                        Otto.post(new OttoMessage("getPost", mNew.getId()));
                        break;
                    }
                    case Feed.TYPE_PRODUCT: {
                        break;
                    }
                    case Feed.TYPE_WALL:  {
                        break;
                    }

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        ImageView mOwnerImage;
        TextView mOwnerName;
        TextView mItemType;
        TextView mCreatedAt;
        TextView mUpdatedAt;
        TextView mTitle;
        ImageView mImage;
        TextView mPreview;
        IconicsTextView mCounters;
        MaterialRatingBar mRating;

        private FeedViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.cv);
            mOwnerImage = cv.findViewById(R.id.owner_avatar);
            mOwnerName = cv.findViewById(R.id.owner_name);
            mItemType = cv.findViewById(R.id.item_type);
            mCreatedAt = cv.findViewById(R.id.created);
            mUpdatedAt = cv.findViewById(R.id.updated);
            mTitle = cv.findViewById(R.id.title);
            mImage = cv.findViewById(R.id.image);
            mPreview = cv.findViewById(R.id.text);
            mCounters = cv.findViewById(R.id.counters);
            mRating = cv.findViewById(R.id.rating);
        }

    }
}
