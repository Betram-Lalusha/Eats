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

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileAdapter extends ArrayAdapter<Post> {
    List<Post> mPosts;
    public UserProfileAdapter(@NonNull Context context, int resource, @NonNull List<Post> posts) {
        super(context, 0, posts);

        this.mPosts = posts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_post, parent, false);
        }
        Post post = getItem(position);
        ImageView userPost = listitemView.findViewById(R.id.postImage);
        Glide.with(getContext()).load(post.getMedia().getUrl()).into(userPost);

        //listens for click event
        userPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("post", Parcels.wrap(post));
                getContext().startActivity(intent);
            }
        });

        //user can long press item to delete it
        userPost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                post.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.i("LONG_CLICK","something went wrong deleting an item " + e);
                            Toast.makeText(getContext(), "something went wrong deleting the image. Try again", Toast.LENGTH_LONG).show();
                        }

                        mPosts.remove(position);
                        notifyDataSetChanged();
                    }
                });
                return false;
            }
        });

        return listitemView;
    }
}

