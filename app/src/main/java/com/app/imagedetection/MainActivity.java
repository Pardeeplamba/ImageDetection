package com.app.imagedetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button camerabutton,gallerybutton,next;
    public static final int PICKCODE=1000;
    public static final int PERMISSIONCODE=1001;
    public static final int PERMISSIONCODES=1002;
    public static final int CAMERACODE=101;

    EditText imageurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>9)
        {
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        imageurl=findViewById(R.id.imageurl);
        camerabutton=findViewById(R.id.camera);
        gallerybutton=findViewById(R.id.gallery);
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap= loadbitmapfromurl(imageurl.getText().toString().trim());
                BitmapHelper.getInstance().setBitmap(bitmap);
                if(BitmapHelper.getInstance().getBitmap()==null)
                    Toast.makeText(MainActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                    startActivity(intent);
                }
            }
        });

        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)
                    {
                        String[] permission={Manifest.permission.CAMERA};
                        requestPermissions(permission,PERMISSIONCODES);
                    }
                    else
                    {
                        pickimagefromcamera();
                    }
                }
                else
                {
                    pickimagefromcamera();
                }
            }
        });
        gallerybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                    {
                        String[] permission={Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permission,PERMISSIONCODE);
                     }
                    else
                    {
                        pickimagefromgallery();
                    }
                }
                else
                {
                    pickimagefromgallery();
                }

            }
        });

    }

    private void pickimagefromcamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERACODE);
    }

    private Bitmap loadbitmapfromurl(String source) {
        try {
            URL url=new URL(source);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            Bitmap mybitmap= BitmapFactory.decodeStream(inputStream);
            return mybitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void pickimagefromgallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PICKCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSIONCODE:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    pickimagefromgallery();
                }
                else
                {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSIONCODES:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    pickimagefromcamera();
                }
                else
                {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK && requestCode==PICKCODE)
        {
//            imageView.setImageURI(data.getData());
//
//            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//            Bitmap bitmap1 = drawable.getBitmap();
//            BitmapHelper.getInstance().setBitmap(bitmap1);
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                BitmapHelper.getInstance().setBitmap(bitmap);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

//            Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");

            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
startActivity(intent);
        }
        if(resultCode==RESULT_OK && requestCode==CAMERACODE)
        {
//            imageView.setImageURI(data.getData());
//            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
            rotateImage(bitmap1,90);
            BitmapHelper.getInstance().setBitmap(bitmap1);

            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent);
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}