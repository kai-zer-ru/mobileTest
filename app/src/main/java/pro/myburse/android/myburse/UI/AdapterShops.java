package pro.myburse.android.myburse.UI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pro.myburse.android.myburse.R;
import pro.myburse.android.myburse.Utils.SingleVolley;
import pro.myburse.android.myburse.json.New;
import pro.myburse.android.myburse.json.Shop;

/**
 * Created by alexey on 06.07.17.
 */

public class AdapterShops extends RecyclerView.Adapter<AdapterShops.ShopViewHolder> {
    ArrayList<Shop> mShops;
    Context mContext;

    public AdapterShops(ArrayList<Shop> shops){
        super();
        this.mShops=shops;
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShopViewHolder holder, int position) {
        final Shop mShop = mShops.get(position);
        holder.mOwnerImage.setImageUrl(mShop.getOwner_avatar(), SingleVolley.getInstance(mContext).getImageLoader());
        holder.mOwnerName.setText(mShop.getOwner_name());
        holder.mDateAdd.setText(mShop.getDate_add());
        holder.mTitle.setText(mShop.getTitle());
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
                if (width < mShop.getImage_width()) {
                    double ratio = (double) mShop.getImage_width() / (double) mShop.getImage_height();
                    holder.mImage.getLayoutParams().width = width;
                    holder.mImage.getLayoutParams().height = (int) (width / ratio);
                }else{
                    holder.mImage.getLayoutParams().height = mShop.getImage_height();
                    holder.mImage.getLayoutParams().width = mShop.getImage_width();
                }

            }
        });
        holder.mImage.setImageUrl(mShop.getImage(),SingleVolley.getInstance(mContext).getImageLoader());
        holder.mPreview.setText(mShop.getText());
        holder.mRating.setNumStars(5);
        holder.mRating.setRating(mShop.getRating());
        //holder.mRating
        holder.mCounters.setText(String.format("{faw-comment} %d ",mShop.getReviews_count()));
    }


    @Override
    public int getItemCount() {
        return mShops.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        NetworkImageView mOwnerImage;
        TextView mOwnerName;
        TextView mDateAdd;
        TextView mTitle;
        NetworkImageView mImage;
        TextView mPreview;
        MaterialRatingBar mRating;
        IconicsTextView mCounters;


        private ShopViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.cv);
            mOwnerImage = cv.findViewById(R.id.shop_owner_img);
            mOwnerName = cv.findViewById(R.id.shop_owner_name);
            mDateAdd = cv.findViewById(R.id.shop_date_add);
            mTitle = cv.findViewById(R.id.shop_title);
            mImage = cv.findViewById(R.id.shop_img);
            mPreview = cv.findViewById(R.id.shop_preview);
            mRating = cv.findViewById(R.id.shop_rating);
            mCounters = cv.findViewById(R.id.shop_counters);
        }

    }
}
