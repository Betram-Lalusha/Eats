package com.example.eats.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseUser;

import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder> {
    Context mContext;
    List<Post> mPosts;

    public UserProfileAdapter(Context context, List<Post> posts) {
        this.mPosts = posts;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_post, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserPost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserPost = itemView.findViewById(R.id.postImage);
        }

        public void bind(Post post) {
            Glide.with(mContext).load(post.getMedia().getUrl()).into(mUserPost);
        }
    }
}
