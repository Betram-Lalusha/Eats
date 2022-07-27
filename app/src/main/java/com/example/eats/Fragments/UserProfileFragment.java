package com.example.eats.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.LoginActivity;
import com.example.eats.Activities.SettingsActivity;
import com.example.eats.Adapters.UserProfileAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Interface.OnClickInterface;
import com.example.eats.Models.City;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class UserProfileFragment extends Fragment {

    TextView mUserBio;
    TextView mUserName;
    String mPostClicked;
    List<Post> mUserPosts;
    ParseUser mCurrentUser;
    private File mPhotoFile;
    List<Post> mCachedPosts;
    ProgressBar mProgressBar;
    ImageView mUserProfilePic;
    ProgressBar mRvProgressBar;
    RecyclerView mRecyclerView;
    ImageButton mSettingsButton;
    HashSet<String> mAlreadyAdded;
    List<Post> mRetrievedCachedPosts;
    UserProfileAdapter mUserProfileAdapter;
    private OnClickInterface mPostClickInterface;
    public String mPhotoFileName = "photo.jpg";
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final String APP_TAG = "USER-FRAGMENT";
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    ConstraintLayout mAlertBox;
    Button mDeletePostButton;
    Button mCancelDeletePostButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPostClicked = "";


        mPostClickInterface = new OnClickInterface() {
            @Override
            public void setClick(String item) {
                //set new Id of clicked item
                mPostClicked = item;

                //show alert box
                mAlertBox.setVisibility(View.VISIBLE);
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        mCachedPosts = new ArrayList<>();
        mAlreadyAdded = new HashSet<>();
        mUserPosts = new LinkedList<Post>();
        mUserBio = view.findViewById(R.id.bio);
        mCurrentUser = ParseUser.getCurrentUser();
        mRetrievedCachedPosts = new ArrayList<>();
        mUserName = view.findViewById(R.id.username);
        mRecyclerView = view.findViewById(R.id.rvUserPosts);
        mProgressBar = view.findViewById(R.id.progressBar);
        mRvProgressBar = view.findViewById(R.id.rvProgressBar);
        mUserProfilePic = view.findViewById(R.id.userProfilePic);
        mSettingsButton = view.findViewById(R.id.settingsButton);

        mAlertBox = view.findViewById(R.id.alertBox);
        mDeletePostButton = view.findViewById(R.id.deletePostButton);
        mCancelDeletePostButton = view.findViewById(R.id.cancelDeleteButton);

        mUserProfileAdapter = new UserProfileAdapter(getContext(), mUserPosts, mPostClickInterface);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(40);

        //press profile picture to set new profile picture
        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        //long press user profile picture to log out
        mUserProfilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return false;
            }
        });

        //cancel delete operation
        mCancelDeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set selected post id to empty
                mPostClicked = "";
                //hide alert box
                mAlertBox.setVisibility(View.GONE);
            }
        });

        //delete post
        mDeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete post
                mAlertBox.setVisibility(View.GONE);
                deleteUserPost();
            }
        });

        //go to settings activity
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        //query for user posts
        mRetrievedCachedPosts = getCachedPosts();
        if(mRetrievedCachedPosts.isEmpty()){
            getUserPosts();
        }

        mUserName.setText(mCurrentUser.getUsername());
        mUserBio.setText(mCurrentUser.getString("bio"));
        Glide.with(getContext()).load(mCurrentUser.getParseFile("userProfilePic").getUrl()).into(mUserProfilePic);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mUserProfileAdapter);
        //to add space between elements in recycler view
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextData();
                for(Post post: mUserPosts) mAlreadyAdded.add(post.getObjectId());
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);

    }

    protected void getUserPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(5);
        query.include(Post.USER);
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("objectId", mAlreadyAdded);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }

                // save received posts to list and notify adapter of new data
                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
                mEndlessRecyclerViewScrollListener.resetState();

                for(Post post: posts) mAlreadyAdded.add(post.getObjectId());
                Log.d("INFINTE", "added " + mAlreadyAdded);
                if(mRetrievedCachedPosts.size() < 5) {
                    ParseObject.pinAllInBackground(mCurrentUser.getObjectId(), posts);
                }
            }
        });
    }


    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            mPhotoFile = new File(photoUri.getPath());
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            saveNewProfilePic(selectedImage);
        }
    }

    /**
     * Method saves image to parse and changes the profile picture of the user to the selected image.
     * It saves the image to parse by first converting it to a byteArray as advised by the parse documentation
     * @param selectedImage: The bitmap to set as the new user profile picture
     */
    private void saveNewProfilePic(@NonNull Bitmap selectedImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream); //takes a lot of time but best solution so far
        byte[] byteArray = stream.toByteArray();
        selectedImage.recycle();
        ParseFile newPic =  new ParseFile("userPic.png", byteArray);
        newPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    //Toast.makeText(getContext(), "Error saving picture. Try again",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.i("HOME", "something went wrong saving picture. " + e);
                    Toast.makeText(getContext(), "Error ocurred saving image :( . Try again", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                mCurrentUser.put("userProfilePic", newPic);
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            //Toast.makeText(getContext(), "Error saving picture. Try again",Toast.LENGTH_LONG).show();
                            Log.i("HOME", "something went wrong saving picture. " + e);
                            Toast.makeText(getContext(), "Error ocurred saving image :( . Try again", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        // Load the selected image into a view so user can see it changed
                        Glide.with(getContext()).load(mCurrentUser.getParseFile("userProfilePic").getUrl()).into(mUserProfilePic);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * Checks local database for cached posts
     * @return: all cached objects in the user local storage
     */
    public List<Post> getCachedPosts() {
        List<Post> retrievedPosts = new ArrayList<>();

        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.USER);
        parseQuery.ignoreACLs();
        parseQuery.addDescendingOrder("createdAt");
        parseQuery.whereNotContainedIn("objectId", mAlreadyAdded);

        try {
            retrievedPosts = parseQuery.fromPin(mCurrentUser.getObjectId()).find();
            mUserPosts.addAll(retrievedPosts);
            mUserProfileAdapter.notifyDataSetChanged();
            for(Post post: retrievedPosts) {
                mAlreadyAdded.add(post.getObjectId());
            }
        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }

    /**
     * Loads more posts from the database when infinte scroll is triggered.
     * Only posts already not in the cache are retrieved
     */
    private void loadNextData() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.whereNotContainedIn("objectId", mAlreadyAdded);
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }

                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
                mEndlessRecyclerViewScrollListener.resetState();
                for(Post post: posts) mAlreadyAdded.add(post.getObjectId());

            }

        });
    }

    /**
     *
     * deletes a post whose object id is equal to mPostClicked
     */
    public void deleteUserPost() {
        if(mPostClicked.isEmpty()) return;
        mRvProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("objectId", mPostClicked);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("DELETING USER POST", "something went wrong deleting post " + e.toString());
                    e.printStackTrace();
                    mRvProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                Post post = posts.get(0);
                post.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.i("DELETING USER POST", "something went wrong deleting post " + e.toString());
                            e.printStackTrace();
                            mRvProgressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        mUserPosts.remove(post);
                        mUserProfileAdapter.notifyDataSetChanged();
                        mRvProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

}