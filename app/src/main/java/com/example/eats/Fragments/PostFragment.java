package com.example.eats.Fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.eats.BuildConfig.MAPS_API_KEY;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.eats.Geohashing.Geohasher;
import com.example.eats.Models.City;
import com.example.eats.Models.Place;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Response;

public class PostFragment extends Fragment {

    Boolean mNewImage;
    TextView mSetPrice;
    Geocoder mGeocoder;
    Button mSelectImage;
    TextView mSetCaption;
    Button mSubmitButton;
    Button mCaptureImage;
    Double mUserLatitude;
    Geohasher mGeohasher;
    ImageView mAddedImage;
    Double mUserLongitude;
    Bitmap mSelectedImage;
    TextView mSetCategory;
    private File mPhotoFile;
    ProgressBar mSavingPost;
    TextView mSetDescription;
    public final String APP_TAG = "EATS";
    public String mPhotoFileName = "photo.jpg";
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final String  GOOGLE_PLACES_GET_PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    private final String GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {

        mNewImage = true;
        mGeocoder = new Geocoder(getContext());
        mSetPrice = view.findViewById(R.id.setPrice);
        mSavingPost = view.findViewById(R.id.savingPost);
        mSetCaption = view.findViewById(R.id.setCaption);
        mAddedImage = view.findViewById(R.id.addedImage);
        mSelectImage = view.findViewById(R.id.selectImage);
        mCaptureImage = view.findViewById(R.id.captureImage);
        mSubmitButton = view.findViewById(R.id.submitButton);
        mSetCategory = view.findViewById(R.id.enterCategory);
        mSetDescription = view.findViewById(R.id.setDescription);
        mGeohasher = new Geohasher(mUserLatitude, mUserLongitude);

        mAddedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectImage.setVisibility(View.VISIBLE);
                mCaptureImage.setVisibility(View.VISIBLE);

            }
        });

        //capture image using camera
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        //select image from phone gallery
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavingPost.setVisibility(View.VISIBLE);
                String enteredCaption = mSetCaption.getText().toString();
                String enteredCategory = mSetCategory.getText().toString();
                String enteredDescription = mSetDescription.getText().toString();
                Number enteredPrice = mSetPrice.getText().toString().isEmpty() ? 0 : (Number) Integer.parseInt(mSetPrice.getText().toString());
                //feedback if no photo is taken
                if(mPhotoFile == null || mAddedImage.getDrawable() == null ||!mNewImage) {
                    Toast.makeText(getContext(), "no image added", Toast.LENGTH_SHORT).show();
                    mSavingPost.setVisibility(View.INVISIBLE);
                    return;
                }
                if(enteredCaption.isEmpty()) {
                    Toast.makeText(getContext(), "caption cannot be empty", Toast.LENGTH_SHORT).show();
                    mSavingPost.setVisibility(View.INVISIBLE);
                    return;
                }

                if(enteredCategory.isEmpty()){
                    Toast.makeText(getContext(), "category cannot be empty", Toast.LENGTH_SHORT).show();
                    mSavingPost.setVisibility(View.INVISIBLE);
                    return;
                }

                if(enteredDescription.isEmpty()) {
                    Toast.makeText(getContext(), "description cannot be empty", Toast.LENGTH_SHORT).show();
                    mSavingPost.setVisibility(View.INVISIBLE);
                    return;
                }

                mSubmitButton.setEnabled(false);
                ParseUser currentUser = ParseUser.getCurrentUser();
                checkIfCityExists(enteredCaption, enteredCategory,  currentUser,enteredPrice, enteredDescription, mSelectedImage);
                mSavingPost.setVisibility(View.INVISIBLE);
            }
        });


    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri(mPhotoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider2", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * Checks if the city the user made the post in is already stored in the database. If the city is already stored, a pointer to that city is attached to the user post
     * If the city is not stored, a new city is created
     * @param enteredCaption: the caption of the post
     * @param enteredCategory: the category of food to associate this post with
     * @param currentUser: the user that made the post
     * @param price: the cost of purchasing this post
     * @param enteredDescription: the details about the food in the post
     * @param selectedImage: the post image to associate with the post
     */
    private void checkIfCityExists(String enteredCaption, String enteredCategory, ParseUser currentUser, Number price, String enteredDescription, @NonNull Bitmap selectedImage) {

        String city = getCityFromUserLats();
        ParseQuery<City> parseQuery = new ParseQuery<City>(City.class);
        parseQuery.whereFullText("name", city);
        parseQuery.setLimit(1);
        parseQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> cities, ParseException e) {
                if(e != null) {
                    Log.i(APP_TAG, "error fetching city with given name " + e);
                    Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_LONG).show();;
                    e.printStackTrace();
                    return;
                }

                //only add city only if it does not exist in db
                if(cities.isEmpty()){
                    getPlace(city, null, enteredCaption, enteredCategory, currentUser, price, enteredDescription, selectedImage);
                } else {
                    getPlace(cities.get(0).getName(), cities.get(0), enteredCaption, enteredCategory, currentUser, price, enteredDescription, selectedImage);
                }
            }
        });

    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                mSelectedImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                mAddedImage.setImageBitmap(takenImage);
                mNewImage = true;
            } else { // Result was a failure
                mNewImage = false;
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }

        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            mPhotoFile = new File(photoUri.getPath());
            // Load the image located at photoUri into selectedImage
            mSelectedImage = loadFromUri(photoUri);
            Bitmap selectedImage = loadFromUri(photoUri);
            mAddedImage.setImageBitmap(selectedImage);
            mNewImage = true;
        } else mNewImage = false;

        //hide buttons
        mSelectImage.setVisibility(View.GONE);
        mCaptureImage.setVisibility(View.GONE);
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


    /**
     * Finds the city the user is in using their current coordinates.
     * Cities are needed to enable filtering by cities in search fragment
     * @return
     */
    private String getCityFromUserLats() {
        List<Address> fromLocation = null;
        try {
            fromLocation = mGeocoder.getFromLocation(mUserLatitude, mUserLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromLocation ==  null ? "unknown city" : fromLocation.get(0).getLocality();
    }

    /**
     * returns a place from the google maps api from a given search query
     * @param query: the name of the place to look for
     *
     */
    private void getPlace(String query, City foundCity, String enteredCaption, String enteredCategory, ParseUser currentUser, Number price, String enteredDescription, @NonNull Bitmap selectedImage) {
        //if a city was found with the name of query, go ahead and save
        if(foundCity != null) {
            getPhotoUrl(null, foundCity, enteredCaption, enteredCategory, currentUser, price, enteredDescription, selectedImage);
            return;
        }

        //if a city was not found, use google places API to get name of city and image url
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("key", MAPS_API_KEY);
        params.put("input", query);
        params.put("inputtype", "textquery");
        params.put("fields", "name");
        params.put("fields", "photos");

        //get data
        asyncHttpClient.get(GOOGLE_PLACES_API_BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                JSONArray possibleCandidates = null;
                try {
                    possibleCandidates = jsonObject.getJSONArray("candidates");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<JSONObject> candidates = getCandidates(possibleCandidates);
                List<Place> places = getFirstPlace(candidates,query);
                getPhotoUrl(places.get(0), foundCity, enteredCaption, enteredCategory, currentUser, price, enteredDescription, selectedImage);
                return;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("GET-PLACES","failed because " + response);
                return;
            }
        });
    }

    /**
     * Function returns a list of all candidates from a given jsonArray
     * @param candidates: The json array of candidates
     * @return: a list of all candiates from the given jsonArray
     */
    private List<JSONObject> getCandidates(JSONArray candidates) {
        List<JSONObject> result = new LinkedList<>();
        for(int i = 0; i < 1; i++) {
            JSONObject candidate = null;
            try {
                candidate = candidates.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("GET-CANDIDATES", e.toString());
                e.printStackTrace();
            }
            result.add(candidate);
        }

        return result;
    }

    /**
     * Function returns a list of a places from a given list of candidates
     * @param candidates: a list of json objects representing candidates returned from google places API
     * @return:  a list of a places from a given list of candidates
     */
    private List<Place> getFirstPlace(List<JSONObject> candidates, String name) {
        List<Place> places = new LinkedList<>();
        for(JSONObject candidate: candidates) {
            Place place = Place.fromJson(candidate,name);
            places.add(place);
        }

        return places;
    }

    /**
     * Retrives the url of the photo using this object's photo reference.
     * API calls are made using AsyncHTTP class and are made to the google maps api
     * @return: The url of the remote image
     */
    public void getPhotoUrl(Place place, City foundCity, String enteredCaption, String enteredCategory, ParseUser currentUser, Number price, String enteredDescription, @NonNull Bitmap selectedImage) {
        if(place == null) {
            savePost(foundCity, enteredCaption, enteredCategory, currentUser, price,  enteredDescription, selectedImage);
            return;
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("key", MAPS_API_KEY);
        requestParams.put("photo_reference",place.mPhotoReference);
        requestParams.put("maxwidth", 400);
        requestParams.put("maxheight",400);
        asyncHttpClient.get(GOOGLE_PLACES_GET_PHOTO_BASE_URL, requestParams, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, Response response) {
                String url = getUrl(response);

                //save city
                City city = new City();
                city.put(City.NAME, place.mName);
                city.put(city.IMAGE_URL, url);

                city.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null) {
                            Log.i("POST-FRAGMENT", "error occurred trying to save city " + e);
                            e.printStackTrace();
                            return;
                        }

                        //finished operation...save post with city
                        savePost(city, enteredCaption, enteredCategory, currentUser, price,  enteredDescription, selectedImage);
                    }
                });
                return;
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
                Log.i("GET-PLACE-URL","error occured no json " + errorResponse);
                return;
            }
        });
    }

    /**
     * Saves a new post to thr database
     * @param city: the city the post was made in
     * @param enteredCaption: the caption of the post
     * @param enteredCategory: the category of food to associate this post with
     * @param currentUser: the user that made the post
     * @param price: the cost of purchasing this post
     * @param enteredDescription: the details about the food in the post
     * @param selectedImage: the post image to associate with the post
     */
    private void savePost(City city, String enteredCaption, String enteredCategory, ParseUser currentUser, Number price, String enteredDescription, Bitmap selectedImage) {
        //finished operation...save post with city
        Post post = new Post();

        post.setPrice(price);
        post.put("city", city);
        post.setUser(currentUser);
        post.setLatitude(mUserLatitude);
        post.setCaption(enteredCaption);
        post.setCaption(enteredCaption);
        post.setLongitude(mUserLongitude);
        post.setDetails(enteredDescription);
        post.setGeohash(mGeohasher.geoHash(12));

        //save photo first
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream); //takes a lot of time but best solution so far
        byte[] byteArray = stream.toByteArray();
        selectedImage.recycle();
        ParseFile newPic =  new ParseFile("postImage.png", byteArray);

        newPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.i("POST-FRAGMENT", "error occurred trying to post image" + e);
                    e.printStackTrace();
                    return;
                }

                //check if category does not already exist
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.setLimit(1);
                query.include(Post.USER);
                query.addDescendingOrder("createdAt");
                query.whereFullText(Post.CATEGORY, enteredCategory.toLowerCase());
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> objects, ParseException e) {
                        if(e != null) {
                            Log.i("POST-FRAGMENT", "error occurred trying to find category equal to entered string" + e);
                            e.printStackTrace();
                            return;
                        }

                        if(objects.isEmpty()) {
                            post.setCategory(enteredCategory.toLowerCase());
                        } else {
                            post.setCategory(objects.get(0).getCategory());
                        }

                        post.setMedia(newPic);
                        //SAVE POST
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null) {
                                    Log.i("POST-FRAGMENT", "error occurred trying to post " + e.getMessage());
                                    Toast.makeText(getContext(), "error occurred. Try again.", Toast.LENGTH_SHORT).show();
                                    mSavingPost.setVisibility(View.GONE);
                                    return;
                                }

                                mSetPrice.setText("");
                                mSetCaption.setText("");
                                mSetCategory.setText("");
                                mSetDescription.setText("");
                                //clear image
                                mAddedImage.setImageResource(R.drawable.eats_logo);
                                Toast.makeText(getContext(), "saved successfully!.", Toast.LENGTH_SHORT).show();

                                //send notification to people that follow this user
                                ParsePush parsePush = new ParsePush();
                                parsePush.setChannel(ParseUser.getCurrentUser().getObjectId());
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("alert", enteredCaption);
                                    data.put("title", ParseUser.getCurrentUser().getUsername());
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                                parsePush.setData(data);
                                parsePush.sendInBackground();

                            }
                        });
                    }
                });

            }
        });
    }

    /**
     * returns the url of an image from a given response object
     * @param response: the response object to decode
     * @return: the url contained in the response
     */
    private String getUrl(Response response) {
        String responseStr = response.toString();
        String[] arr = responseStr.split(",");
        String inCorrectUrl =  arr[3];
        String correctUrl = "";
        for(int i = 0; i < inCorrectUrl.length(); i++) {
            if(inCorrectUrl.charAt(i) == 'h') {
                int k = i;
                while(k < inCorrectUrl.length() - 1) {
                    correctUrl += inCorrectUrl.charAt(k);
                    k++;
                }
                i = k;
            }
            if(inCorrectUrl.charAt(i) == '}') break;
        }
        return correctUrl;
    }



}