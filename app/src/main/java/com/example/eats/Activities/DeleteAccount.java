package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.eats.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class DeleteAccount extends AppCompatActivity {

    Button mDeleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        getSupportActionBar().hide();

        mDeleteAccountButton = findViewById(R.id.deleteAccountButton);

        mDeleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(DeleteAccount.this).create();
                alertDialog.setTitle("CAUTION");
                alertDialog.setMessage("This action cannot be undone. You will lose all data associated with your account");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.getCurrentUser().deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null) {
                                    System.out.println("error occurred deleting account " + e.getMessage());
                                    dialog.dismiss();
                                    return;
                                }
                            }
                        });
                        dialog.dismiss();

                        Intent intent = new Intent(DeleteAccount.this, SignUpActivity.class);
                        intent.putExtra("cameFromDelete", true);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

              alertDialog.show();
            }
        });
    }
}