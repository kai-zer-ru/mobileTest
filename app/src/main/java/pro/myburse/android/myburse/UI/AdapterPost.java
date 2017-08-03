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

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pro.myburse.android.myburse.Model.Blog;
import pro.myburse.android.myburse.R;
import pro.myburse.android.myburse.Utils.SingleVolley;


public class AdapterPost extends RecyclerView.Adapter<AdapterPost.BlogViewHolder> {
    ArrayList<Blog> mBlogs;
    Context mContext;

    public AdapterPost(ArrayList<Blog> blogs){
        super();
        this.mBlogs=blogs;
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.card, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BlogViewHolder holder, final int position) {
        final Blog mBlog = mBlogs.get(position);
        ImageLoader imageLoader = SingleVolley.getInstance(mContext).getImageLoader();

        imageLoader.get(mBlog.getOwner().getAvatarUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mOwnerImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        holder.mOwnerName.setText(mBlog.getOwner().getName());
        holder.mItemType.setVisibility(View.GONE);

        holder.mCreated.setText(mBlog.getCreatedAtFormated());
        holder.mUpdated.setText(mBlog.getUpdatedAtFormated());
        holder.mTitle.setText(mBlog.getTitle());

        imageLoader.get(mBlog.getImage().getUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        //holder.mText.setText(mNew.getText());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            holder.mText.setText(Html.fromHtml(mBlog.getText(), Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.mText.setText(Html.fromHtml(mBlog.getText()));
        }
  //      holder.mRating.setNumStars(5);
  //      holder.mRating.setRating(mBlog.getRating());
        holder.mRating.setVisibility(View.GONE);
        holder.mCounters.setText(String.format("{faw-comment} %d {faw-heart} %d",mBlog.getCommentsCount(),mBlog.getLikesCount()));

    }


    @Override
    public int getItemCount() {
        return mBlogs.size();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        ImageView mOwnerImage;
        TextView mOwnerName;
        TextView mItemType;
        TextView mCreated;
        TextView mUpdated;
        TextView mTitle;
        ImageView mImage;
        TextView mText;
        MaterialRatingBar mRating;
        IconicsTextView mCounters;


        private BlogViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.cv);
            mOwnerImage = cv.findViewById(R.id.owner_avatar);
            mOwnerName = cv.findViewById(R.id.owner_name);
            mItemType = cv.findViewById(R.id.item_type);
            mCreated = cv.findViewById(R.id.created);
            mUpdated = cv.findViewById(R.id.updated);
            mTitle = cv.findViewById(R.id.title);
            mImage = cv.findViewById(R.id.image);
            mText = cv.findViewById(R.id.text);
            mRating = cv.findViewById(R.id.rating);
            mCounters = cv.findViewById(R.id.counters);

        }

    }
}