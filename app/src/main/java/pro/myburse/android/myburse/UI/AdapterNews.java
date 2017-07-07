package pro.myburse.android.myburse.UI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mikepenz.iconics.view.IconicsTextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pro.myburse.android.myburse.R;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.json.New;

/**
 * Created by alexey on 06.07.17.
 */

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
        holder.mOwnerImage.setImageUrl(mNew.getOwner_avatar(), SingleVolley.getInstance(mContext).getImageLoader());
        holder.mOwnerName.setText(mNew.getOwner_name());
        holder.mItemType.setText(mNew.getItem_type());
        holder.mDateAdd.setText(mNew.getDate_add());
        holder.mTitle.setText(mNew.getTitle());
        holder.mImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        final ViewTreeObserver observer = holder.mImage.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    holder.mImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int width = holder.mImage.getWidth();
                if (width < mNew.getImage_width()) {
                    double ratio = (double) mNew.getImage_width() / (double) mNew.getImage_height();
                    holder.mImage.getLayoutParams().width = width;
                    holder.mImage.getLayoutParams().height = (int) (width / ratio);
                }else{
                    holder.mImage.getLayoutParams().height = mNew.getImage_height();
                    holder.mImage.getLayoutParams().width = mNew.getImage_width();
                }

            }
        });
        holder.mImage.setImageUrl(mNew.getImage(),SingleVolley.getInstance(mContext).getImageLoader());
        holder.mPreview.setText(mNew.getText());
        holder.mCounters.setText(String.format("{faw-comment} %d {faw-heart} %d",mNew.getComments_count(),mNew.getLikes_count()));
    }


    @Override
    public int getItemCount() {
        return mNews.size();
    }

    static class NewViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        NetworkImageView mOwnerImage;
        TextView mOwnerName;
        TextView mItemType;
        TextView mDateAdd;
        TextView mTitle;
        NetworkImageView mImage;
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
