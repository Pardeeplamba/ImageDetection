package com.app.imagedetection;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;


public class MainActivity2 extends AppCompatActivity {
    //AVLoadingIndicatorView avi;
  ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;

    Button imageButton;
    private static int RESULT_LOAD_IMG = 1;
    Bitmap bmp=BitmapHelper.getInstance().getBitmap();
Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uploadImage(bmp);
imageView=findViewById(R.id.imageView);
imageButton=findViewById(R.id.canny);


//        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
//        Bitmap bitmap1 = drawable.getBitmap();
        final Bitmap resizable = Bitmap.createScaledBitmap(bmp, 614, 738, false);
        bitmap = Bitmap.createScaledBitmap(resizable, 614, 738, false);
imageView.setImageBitmap(bitmap);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    cannyFilterAlg(bitmap);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot do edge detection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });



    }










    private Bitmap cannyFilterAlg(Bitmap Image) {
        Canny myCanny = new Canny();
        Bitmap cann=myCanny.process(Image);
        uploadImage(cann);
        imageView.setImageBitmap(cann);
        imageButton.setVisibility(View.GONE);
        return Image;
    }


    private void uploadImage(Bitmap bitmap) {
        Uri filePath=getImageUri(this,bitmap);
        if (filePath != null) {

            // Code for showing progressDialog while uploading


            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Toast
                                            .makeText(MainActivity2.this,
                                                    "Image Uploaded !!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            Toast
                                    .makeText(MainActivity2.this,
                                            "Failed to upload" + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


