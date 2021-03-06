package com.example.eats.Adapters;

import static com.example.eats.R.drawable.ic_baseline_star_24;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Activities.OtherUserProfileActivity;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    Context mContext;
    public List<Post> mPosts;
    HashSet<String> mAlreadyAdded;

    public PostsAdapter(Context context, List<Post> posts) {
        this.mPosts = posts;
        this.mContext = context;
        this.mAlreadyAdded = new HashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.single_post_layout, parent, false);
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

        TextView mPrice;
        TextView mCaption;
        TextView mDistance;
        TextView mCategory;
        TextView mOwnerName;
        ImageView mOwnerPfp;
        ImageView mPostImage;
        ImageButton mFollowButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.priceOfPost);
            mCaption = itemView.findViewById(R.id.captionOfPost);
            mOwnerName = itemView.findViewById(R.id.nameOfOwner);
            mOwnerPfp = itemView.findViewById(R.id.imageOfOwner);
            mPostImage = itemView.findViewById(R.id.imageOfPost);
            mDistance = itemView.findViewById(R.id.distanceOfPost);
            mCategory = itemView.findViewById(R.id.categoryOfPost);
            mFollowButton = itemView.findViewById(R.id.followButton);


        }

        public void bind(Post post) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            List<String> accountsFollowing = (List<String>) currentUser.get("following");

            mCaption.setText(post.getCaption());
            mCategory.setText(post.getCategory());
            mOwnerName.setText(post.getParseUser().getUsername());
            mDistance.setText(formatDistance(post.distanceFromUser) + " miles");
            Glide.with(mContext).load(post.getMedia().getUrl()).into(mPostImage);
            mPrice.setText(post.getPrice() > 0 ? "$" + String.valueOf(post.getPrice()) : "free");
            if(post.getParseUser().getParseFile("userProfilePic") != null) {
                Glide.with(mContext).load(post.getParseUser().getParseFile("userProfilePic").getUrl()).into(mOwnerPfp);
            } else mOwnerPfp.setImageResource(R.drawable.default_image);

            if(accountsFollowing != null) {
                if(accountsFollowing.contains(post.getParseUser().getObjectId())) {
                    mFollowButton.setImageResource(ic_baseline_star_24);
                } else {
                    mFollowButton.setImageResource(R.drawable.ic_baseline_star_border_24);
                }
            }

            //event listener for tap on image
            mPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);

                    //use url instead of passing image to detail activity
                    intent.putExtra("post", Parcels.wrap(post));
                    mContext.startActivity(intent);
                }
            });

            mOwnerPfp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OtherUserProfileActivity.class);

                    //use url instead of passing image to detail activity
                    intent.putExtra("user", Parcels.wrap(post.getParseUser()));
                    mContext.startActivity(intent);
                }
            });

            //click follow button to follow or unfollow account
            mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> accountsFollowing = (List<String>) currentUser.get("following");
                    Log.d("FOLLOWED", "followed accounts " + accountsFollowing);
                    if(accountsFollowing == null) accountsFollowing = new LinkedList<>();

                    //remove user if they already follow this account
                    if(!accountsFollowing.contains(post.getParseUser().getObjectId())) {
                        mFollowButton.setImageResource(ic_baseline_star_24);
                        // mFollowButton.setBackgroundResource(R.drawable.star_bkg);
                        accountsFollowing.add(post.getParseUser().getObjectId());
                        ParsePush.subscribeInBackground(post.getParseUser().getObjectId());
                    } else {
                        mFollowButton.setImageResource(R.drawable.ic_baseline_star_border_24);
                        accountsFollowing.remove(post.getParseUser().getObjectId());
                        ParsePush.unsubscribeInBackground(post.getParseUser().getObjectId());
                    }
                    Log.d("FOLLOWED", "followed accounts2 " + accountsFollowing);
                    currentUser.put("following", accountsFollowing);
                    currentUser.saveInBackground();


                }
            });
        }

        /**
         * formats distance as a string in miles after converting from kilometers
         * @param distanceFromUser: the distance of a post from the user
         * @return: the formatted distance in miles as a string
         */
        private String formatDistance(Double distanceFromUser) {
           return String.valueOf(Math.round(distanceFromUser * 0.621371));
        }
    }
}
