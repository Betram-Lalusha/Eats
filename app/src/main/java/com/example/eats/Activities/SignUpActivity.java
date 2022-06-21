package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eats.Models.User;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    TextView mSetEmail;
    Button mSignUpButton;
    TextView mSetPassword;
    TextView mSetUserName;
    ImageView mSetUserPfp;
    TextView mConfirmPassword;
    private File mPhotoFile;
    public final String APP_TAG = "EATS";
    public String mPhotoFileName = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1044;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSetEmail = findViewById(R.id.setEmail);
        mSetPassword = findViewById(R.id.setPassword);
        mSetUserName = findViewById(R.id.setUserName);
        mSetUserPfp = findViewById(R.id.setUserfp);
        mSignUpButton = findViewById(R.id.signUpButton);
        mConfirmPassword = findViewById(R.id.confirmPassword);

        mSetUserPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mSetEmail.getText().toString();
                String userName = mSetUserName.getText().toString();
                String userPassword = mSetPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                if(userEmail.isEmpty() || userName.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "ensure all fields are filled in!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!userPassword.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mPhotoFile == null || mSetUserPfp.getDrawable() == null) {
                    Toast.makeText(SignUpActivity.this, "no image added", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveUser(userEmail, userName, userPassword, mPhotoFile);
            }
        });

    }

    private void saveUser(String userEmail, String userName, String password, File photoFile) {
        ParseUser user = new ParseUser();
        user.put("username", userName);
        user.put("email", userEmail);
        user.put("password", password);
        ParseFile photo;
        photo = new ParseFile(photoFile, "userProfilePic");

        photo.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                // If successful add file to user and signUpInBackground
                if(e != null) {
                    e.printStackTrace();
                    Log.i("POST-ACTIVITY", "something went wrong " + e);
                    Toast.makeText(SignUpActivity.this, "errornsaving parse file", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
       user.put("userProfilePic", photo);

        try {
            user.signUp();
            goToHome();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("POST-ACTIVITY", "something went wrong " + e);
            Toast.makeText(SignUpActivity.this, "error", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(SignUpActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri(mPhotoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(SignUpActivity.this, "com.codepath.fileprovider2", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(SignUpActivity.this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
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
                mSetUserPfp.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(SignUpActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}