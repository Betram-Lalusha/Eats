package com.example.eats.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eats.Models.Post;
import com.example.eats.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{

    Context mContext;
    List<Post> mPosts;
    public  String mClickedCategory;

    public CategoriesAdapter(Context context, List<Post> posts) {
        this.mPosts = posts;
        this.mContext = context;
        mClickedCategory = "";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        TextView mCategory;
        Boolean mAlreadyClicked;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAlreadyClicked = false;
            mCategory = itemView.findViewById(R.id.categoryItem);

        }

        public void bind(Post post) {
            mCategory.setText(post.getCategory());

            //listens for click event
            mCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //change color
                    changeColor();
                }
            });

        }

        /**
         * Changes the background of a selected category
         * The item turns orange when first clicked and white when clicked again.
         * These color cues help the user know what categories are currently being used to filter posts
         */
        private void changeColor() {
            //che
            if(!mAlreadyClicked) {
                mAlreadyClicked = true;
                mCategory.setBackgroundResource(R.drawable.clicked_category);
            } else {
                mAlreadyClicked = false;
                mCategory.setBackgroundResource(R.drawable.search_bck);
            }
        }
    }


}
