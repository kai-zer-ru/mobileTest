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

import pro.myburse.android.myburse.R;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.Model.New;



public class AdapterNews extends RecyclerView.Adapter<AdapterNews.NewViewHolder> {
    ArrayList<New> mNews;
    Context mContext;

    public AdapterNews(ArrayList<New> news){
        super();
        this.mNews=news;
    }

    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_new, parent, false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewViewHolder holder, int position) {
        final New mNew = mNews.get(position);
        ImageLoader imageLoader = SingleVolley.getInstance(mContext).getImageLoader();

        imageLoader.get(mNew.getOwner_avatar(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mOwnerImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        holder.mOwnerName.setText(mNew.getOwner_name());
        holder.mItemType.setText(mNew.getItem_type());
        holder.mDateAdd.setText(mNew.getDate_add());
        holder.mTitle.setText(mNew.getTitle());

        imageLoader.get(mNew.getImage(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.mImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("ImageLoader","OnErrorResponse\n"+error.toString());
            }
        });
        //holder.mPreview.setText(mNew.getText());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            holder.mPreview.setText(Html.fromHtml(mNew.getText(), Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.mPreview.setText(Html.fromHtml(mNew.getText()));
        }
        holder.mCounters.setText(String.format("{faw-comment} %d {faw-heart} %d",mNew.getComments_count(),mNew.getLikes_count()));
    }


    @Override
    public int getItemCount() {
        return mNews.size();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        ImageView mOwnerImage;
        TextView mOwnerName;
        TextView mItemType;
        TextView mDateAdd;
        TextView mTitle;
        ImageView mImage;
        TextView mPreview;
        IconicsTextView mCounters;


        private NewViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.cv);
            mOwnerImage = cv.findViewById(R.id.new_owner_img);
            mOwnerName = cv.findViewById(R.id.new_owner_name);
            mItemType = cv.findViewById(R.id.new_item_type);
            mDateAdd = cv.findViewById(R.id.new_date_add);
            mTitle = cv.findViewById(R.id.new_title);
            mImage = cv.findViewById(R.id.new_img);
            mPreview = cv.findViewById(R.id.new_preview);
            mCounters = cv.findViewById(R.id.ic_counters);
        }

    }
}
