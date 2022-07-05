package com.example.eats.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>{

    Context mContext;
    List<Post> mPosts;

    public SearchResultsAdapter(Context context, List<Post> posts) {
        this.mPosts = posts;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_search_item, parent, false);
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

        TextView mPostPrice;
        ImageView mSearchItem;
        TextView mPostOwnerName;
        FrameLayout mSearchCover;
        ImageView mPostOwnerImage;
        TextView mPostOwnerDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPostPrice = itemView.findViewById(R.id.postPrice);
            mSearchItem = itemView.findViewById(R.id.searchItem);
            mSearchCover = itemView.findViewById(R.id.searchCover);
            mPostOwnerName = itemView.findViewById(R.id.postOwnerName);
            mPostOwnerImage = itemView.findViewById(R.id.postOwnerImage);
            mPostOwnerDistance = itemView.findViewById(R.id.postOwnerDistance);

        }

        public void bind(Post post) {
            mPostOwnerDistance.setText("1km");//hard coded...will be changed later
            mPostPrice.setText("$" + String.valueOf(post.getPrice()));
            mPostOwnerName.setText(post.getParseUser().getUsername());
            Glide.with(mContext).load(post.getMedia().getUrl()).into(mSearchItem);
            Glide.with(mContext).load(post.getParseUser().getParseFile("userProfilePic").getUrl()).into(mPostOwnerImage);

            mSearchItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("post", Parcels.wrap(post));
                    mContext.startActivity(intent);
                }
            });

        }
    }


}
