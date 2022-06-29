package com.example.eats.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.example.eats.Activities.LoginActivity;
import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.Adapters.UserProfileAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.Point;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UserProfileFragment extends Fragment {

    TextView mUserBio;
    TextView mUserName;
    List<Post> mUserPosts;
    ParseUser mCurrentUser;
    RecyclerView mGridView;
    private File mPhotoFile;
    ProgressBar mProgressBar;
    ImageView mUserProfilePic;
    UserProfileAdapter mUserProfileAdapter;
    public String mPhotoFileName = "photo.jpg";
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final String APP_TAG = "USER-FRAGMENT";
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        mUserPosts = new LinkedList<Post>();
        mUserBio = view.findViewById(R.id.bio);
        mCurrentUser = ParseUser.getCurrentUser();
        mUserName = view.findViewById(R.id.username);
        mGridView = view.findViewById(R.id.gridView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mUserProfilePic = view.findViewById(R.id.userProfilePic);
        mUserProfileAdapter = new UserProfileAdapter(getContext(), mUserPosts);

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

        //query for user posts
        getUserPosts();

        mUserName.setText(mCurrentUser.getUsername());
        mUserBio.setText(mCurrentUser.getString("bio"));
        Glide.with(getContext()).load(mCurrentUser.getParseFile("userProfilePic").getUrl()).into(mUserProfilePic);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        mGridView.setLayoutManager(staggeredGridLayoutManager);
        mGridView.setAdapter(mUserProfileAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextPosts();
            }
        };

    }

    private void loadNextPosts() {
        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(16);
        query.include(Post.USER);
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());
        query.whereLessThan("createdAt", mUserPosts.get(mUserPosts.size() - 1).getDate());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }
                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
            }

        });

        //remove progress bar
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void getUserPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.setLimit(16);
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
               // mScrollListener.resetState();
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

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "USER-FRAGMENT");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("USER-FRAGMENT", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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

    private void saveNewProfilePic(@NonNull Bitmap selectedImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream); //takes a lot of time
        byte[] byteArray = stream.toByteArray();
        selectedImage.recycle();
        //mPhotoFile = bitmapToFile(getContext(), selectedImage, "newPicture.png");
        ///mCurrentUser.put("userProfilePic", new ParseFile(mPhotoFile));
        ParseFile newPic =  new ParseFile("userPic.png", byteArray);
       // mProgressBar.setVisibility(View.INVISIBLE);
        newPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    //Toast.makeText(getContext(), "Error saving picture. Try again",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    System.out.println("something went wrong saving picture. " + e);
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
                            System.out.println("something went wrong saving picture. " + e);
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

    // converts bitmap to file
    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

           //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

}