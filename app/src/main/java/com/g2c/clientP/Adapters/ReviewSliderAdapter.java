package com.g2c.clientP.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ReviewSliderItemBinding;
import com.g2c.clientP.model.ModelReview;

import java.util.List;

public class ReviewSliderAdapter extends RecyclerView.Adapter<ReviewSliderAdapter.SliderViewHolder> {
    private List<ModelReview> sliderItems;
    private ViewPager2 viewPager2;


    public ReviewSliderAdapter(List<ModelReview> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_slider_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        ModelReview review = sliderItems.get(position);

        holder.binding.name.setText(review.getName());
        holder.binding.description.setText(review.getDescription());
        holder.binding.N.setText(review.getName().substring(0,1));


       /* for(int i=review.getRating();i<=5;i++){




        }*/



        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }

    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        ReviewSliderItemBinding binding;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReviewSliderItemBinding.bind(itemView);
        }



    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
