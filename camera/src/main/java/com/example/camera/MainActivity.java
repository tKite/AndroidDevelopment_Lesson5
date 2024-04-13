package com.example.camera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.camera.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public	class	MainActivity	extends	AppCompatActivity implements ActivityResultCallback<ActivityResult>{
    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean is_permissions_granted = false;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private	Uri	imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        permissions_request();
    }


    private void permissions_request() {
        final boolean storage_permission = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final boolean camera_permission = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        if(storage_permission && camera_permission) {
            is_permissions_granted = true;
        } else {
            is_permissions_granted = false;
            ActivityCompat.requestPermissions(this, new	String[] { android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("check", "onRequestPermissionsResult: " + String.valueOf(requestCode));
        if(requestCode == REQUEST_CODE_PERMISSION) {
            is_permissions_granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    public void OnUpdatePhoto(View v) {
        Log.i("check", "OnUpdatePhotoButtonClicked");
        if(!is_permissions_granted) {
            Log.i("", "\tHas no permissions...");
            permissions_request();
            return;
        }

        try {
            File file = createImageFile();
            String authorities = "com.mirea.asd.camera.fileprovider";
            Log.i("check", "\tGet authorities: " + authorities);
            imageUri = FileProvider.getUriForFile(this, authorities, file);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraActivityResultLauncher.launch(cameraIntent);
        } catch (IOException e) {
            Log.d("", e.toString());
        }
    }
    @Override
    public void onActivityResult(ActivityResult o) {
        Log.i("check", "onActivityResult: " + String.valueOf(o.getResultCode()));
        if(o.getResultCode() != Activity.RESULT_OK)
            return;

        Intent data = o.getData();
        binding.imageView.setImageURI(imageUri);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd____HH_mm_ss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }
}