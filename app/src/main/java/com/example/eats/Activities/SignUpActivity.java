package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
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
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    int mDefaultId;
    TextView mSetEmail;
    Button mSignUpButton;
    TextView mSetPassword;
    TextView mSetUserName;
    ImageView mSetUserPfp;
    private File mPhotoFile;
    TextView mConfirmPassword;
    public String mPhotoFileName = "photo.jpg";
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

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

        mSetUserPfp.setTag(R.drawable.default_image);
        mDefaultId = (int) mSetUserPfp.getTag();

        mSetUserPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "change this later by visiting your profile", Toast.LENGTH_LONG).show();
                //onPickPhoto(v);
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

//                int currId =  (int) mSetUserPfp.getTag();
//                if(mSetUserPfp.getDrawable() == null || currId == mDefaultId) {
//                    Toast.makeText(SignUpActivity.this, "no image added", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Toast.makeText(SignUpActivity.this, "everything is fine!", Toast.LENGTH_SHORT).show();
                saveUser(userEmail, userName, userPassword, mPhotoFile);
            }
        });

    }

    private void saveUser(String userEmail, String userName, String password, File photoFile) {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(userEmail);

//        ParseFile photo = new ParseFile(mPhotoFile);
//        photo.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null)  System.out.println("okay");
//            }
//        });
//// other fields can be set just like with ParseObject

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    goToHome();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
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
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
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

            // Load the selected image into a preview
            mSetUserPfp.setImageBitmap(selectedImage);
            mSetUserPfp.setTag(selectedImage.describeContents());
        }
    }

}