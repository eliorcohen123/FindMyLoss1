package com.elior.findmyloss.ScreenPck;

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
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.elior.findmyloss.OthersPck.LossModel;
import com.elior.findmyloss.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;

public class AddLoss extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText userNameWrite, phoneWrite, placeWrite, descriptionWrite;
    private Button chooseButton, uploadButton;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;
    private String storage_Path = "My_Storage";
    private ImageView selectImage;
    private Uri FilePathUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private int image_Request_Code = 7;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loss);

        initUI();
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
        chooseButton = findViewById(R.id.ButtonChooseImage);
        uploadButton = findViewById(R.id.ButtonUploadImage);
        selectImage = findViewById(R.id.imageSelect);
        coordinatorLayout = findViewById(R.id.myContent);
    }

    private void showUI() {
        setSupportActionBar(toolbar);

        findViewById(R.id.myButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else
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

        progressDialog = new ProgressDialog(AddLoss.this);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), image_Request_Code);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageFileToFirebaseStorage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
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
        if (FilePathUri != null) {
            if (!TextUtils.isEmpty(userNameWrite.getText()) && !TextUtils.isEmpty(phoneWrite.getText())
                    && !TextUtils.isEmpty(placeWrite.getText()) && !TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text are not empty the movie will not be approved
                progressDialog.setTitle("Data is Uploading...");
                progressDialog.show();
            }
            final StorageReference storageReference2nd = storageReference.child(storage_Path + System.currentTimeMillis() + "." + getFileExtension(FilePathUri));
            storageReference2nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            criteria = new Criteria();
                            String provider = locationManager.getBestProvider(criteria, true);
                            if (ActivityCompat.checkSelfPermission(AddLoss.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.checkSelfPermission(AddLoss.this, Manifest.permission.ACCESS_COARSE_LOCATION);
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
                                            && !TextUtils.isEmpty(placeWrite.getText()) && !TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text are not empty the movie will not be approved
                                        Date date = new Date();
                                        String date1 = date.toString().trim();
                                        double lat = location.getLatitude();
                                        double lng = location.getLongitude();
                                        String name = userNameWrite.getText().toString().trim();
                                        String phone = phoneWrite.getText().toString().trim();
                                        String place = placeWrite.getText().toString().trim();
                                        String description = descriptionWrite.getText().toString().trim();

                                        progressDialog.dismiss();
                                        Toast.makeText(AddLoss.this, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                                        @SuppressWarnings("VisibleForTests")
                                        LossModel lossModel = new LossModel(name, phone, place, date1, lat, lng, description, uri.toString());
                                        String imageUploadId = databaseReference.push().getKey();
                                        databaseReference.child(imageUploadId).setValue(lossModel);
                                        Snackbar.make(coordinatorLayout, R.string.item_removed_message, Snackbar.LENGTH_LONG)
                                                .setAction(R.string.undo, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // Respond to the click, such as by undoing the modification that caused
                                                        // this message to be displayed
                                                    }
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
                        }
                    });
                }
            });
        } else {
            Toast.makeText(AddLoss.this, "Please Select Image", Toast.LENGTH_LONG).show();
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
