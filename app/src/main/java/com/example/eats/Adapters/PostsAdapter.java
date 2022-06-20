package com.example.eats.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseObject;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    Context mContext;
    List<Post> mPosts;

    public PostsAdapter(Context context, List<Post> posts) {
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

        TextView mCaption;
        TextView mOwnerName;
        ImageView mOwnerPfp;
        ImageView mPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCaption = itemView.findViewById(R.id.caption);
            mOwnerName = itemView.findViewById(R.id.ownerName);
            mOwnerPfp = itemView.findViewById(R.id.ownerPfp);
            mPostImage = itemView.findViewById(R.id.postImage);
        }

        public void bind(Post post) {
            mCaption.setText(post.getCaption());
            mOwnerName.setText(post.getParseUser().getUsername());
            Glide.with(mContext).load(post.getMedia().getUrl()).into(mPostImage);
            Glide.with(mContext).load(post.getParseUser().getParseFile("userProfilePic").getUrl()).into(mOwnerPfp);
        }
    }
}
