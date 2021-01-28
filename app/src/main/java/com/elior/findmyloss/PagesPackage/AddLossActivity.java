package com.elior.findmyloss.PagesPackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.elior.findmyloss.ModelsPackage.LossModel;
import com.elior.findmyloss.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Date;

public class AddLossActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText userNameWrite, phoneWrite, placeWrite, descriptionWrite;
    private Button chooseButton, uploadButton;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;
    private String storage_Path = "My_Storage";
    private ImageView selectImage;
    private Uri filePathUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private int image_Request_Code = 7;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loss);

        initUI();
        initListeners();
        initLocation();
        showUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        userNameWrite = findViewById(R.id.userNameWrite);
        phoneWrite = findViewById(R.id.phoneWrite);
        placeWrite = findViewById(R.id.placeWrite);
        descriptionWrite = findViewById(R.id.descriptionWrite);
        chooseButton = findViewById(R.id.buttonChooseImage);
        uploadButton = findViewById(R.id.buttonUploadImage);
        selectImage = findViewById(R.id.imageSelect);
        coordinatorLayout = findViewById(R.id.myContent);
    }

    private void initListeners() {
        chooseButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void showUI() {
        setSupportActionBar(toolbar);

        findViewById(R.id.myButton).setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                selectImage.setImageBitmap(bitmap);
                chooseButton.setText("Image Selected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImageFileToFirebaseStorage() {
        if (filePathUri != null) {
            if (!TextUtils.isEmpty(userNameWrite.getText()) && !TextUtils.isEmpty(phoneWrite.getText())
                    && !TextUtils.isEmpty(placeWrite.getText()) && !TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text are not empty the movie will not be approved
                progressDialog.setTitle("Data is Uploading...");
                progressDialog.show();
            }
            final StorageReference storageReference2nd = storageReference.child(storage_Path + System.currentTimeMillis() + "." + getFileExtension(filePathUri));
            storageReference2nd.putFile(filePathUri).addOnSuccessListener(taskSnapshot -> storageReference2nd.getDownloadUrl().addOnSuccessListener(uri -> {
                if (ActivityCompat.checkSelfPermission(AddLossActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.checkSelfPermission(AddLossActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                }// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
                if (provider != null) {
                    location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        if (!TextUtils.isEmpty(userNameWrite.getText()) && !TextUtils.isEmpty(phoneWrite.getText())
                                && !TextUtils.isEmpty(placeWrite.getText()) && !TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text is not empty the movie will not be approved
                            Date date = new Date();
                            String date1 = date.toString().trim();
                            String name = userNameWrite.getText().toString().trim();
                            String phone = phoneWrite.getText().toString().trim();
                            String place = placeWrite.getText().toString().trim();
                            String description = descriptionWrite.getText().toString().trim();
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            progressDialog.dismiss();
                            Toast.makeText(AddLossActivity.this, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            LossModel lossModel = new LossModel(name, phone, place, date1, description, uri.toString(), lat, lng);
                            String imageUploadId = databaseReference.push().getKey();
                            assert imageUploadId != null;
                            databaseReference.child(imageUploadId).setValue(lossModel);
                            Snackbar.make(coordinatorLayout, R.string.item_removed_message, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo, v -> {
                                        // Respond to the click, such as by undoing the modification that caused
                                        // this message to be displayed
                                    })
                                    .show();
                        }
                    }
                }

                if (TextUtils.isEmpty(userNameWrite.getText())) {  // If the text are empty the movie will not be approved
                    userNameWrite.setError("Name is required!");  // Print text of error if the text are empty
                }

                if (TextUtils.isEmpty(phoneWrite.getText())) {  // If the text are empty the movie will not be approved
                    phoneWrite.setError("Phone is required!");  // Print text of error if the text are empty
                }

                if (TextUtils.isEmpty(placeWrite.getText())) {  // If the text are empty the movie will not be approved
                    placeWrite.setError("Place is required!");  // Print text of error if the text are empty
                }

                if (TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text are empty the movie will not be approved
                    descriptionWrite.setError("Description is required!");  // Print text of error if the text are empty
                }
            }));
        } else {
            Toast.makeText(AddLossActivity.this, "Please Select Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonChooseImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), image_Request_Code);
                break;
            case R.id.buttonUploadImage:
                uploadImageFileToFirebaseStorage();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.back_pressed) {
            onBackPressed();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

}
