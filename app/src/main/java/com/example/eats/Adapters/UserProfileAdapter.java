package com.example.eats.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder>{

    Context mContext;
    List<Post> mPosts;
    HashSet<String> mAlreadyAdded;


    public UserProfileAdapter(Context context, List<Post> posts) {
        this.mPosts = posts;
        this.mContext = context;
        this.mAlreadyAdded = new HashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_profile_post, parent, false);
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
        for(Post post: list)  {
            if(mAlreadyAdded.add(post.getObjectId())) mPosts.add(post);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        ImageView mPostImage;
        TextView mUserPostCaption;
        ConstraintLayout mUserPost;
        TextView mUserPostDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserPost = itemView.findViewById(R.id.userPost);
            mPostImage = itemView.findViewById(R.id.postImage);
            mUserPostCaption = itemView.findViewById(R.id.userPostCaption);
            mUserPostDescription = itemView.findViewById(R.id.userPostDescription);

        }

        public void bind(Post post) {
            mUserPostCaption.setText(post.getCaption());
            mUserPostDescription.setText(post.getDetails());
            Glide.with(mContext).load(post.getMedia().getUrl()).into(mPostImage);

            //listens for click event
            mUserPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("post", Parcels.wrap(post));
                    mContext.startActivity(intent);
                }
            });


        //user can long press item to delete it
            mUserPost.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    post.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.i("LONG_CLICK","something went wrong deleting an item " + e);
                                Toast.makeText(mContext, "something went wrong deleting the image. Try again", Toast.LENGTH_LONG).show();
                            }

                            mPosts.remove(post);
                            notifyDataSetChanged();
                        }
                    });
                    return false;
                }
            });
        }
    }
}


