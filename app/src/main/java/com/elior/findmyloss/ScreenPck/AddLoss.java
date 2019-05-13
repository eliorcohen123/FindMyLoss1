package com.elior.findmyloss.ScreenPck;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.Button;
import android.widget.EditText;

import com.elior.findmyloss.R;
import com.firebase.client.Firebase;

import java.util.Date;

public class AddLoss extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText userNameWrite, phoneWrite, placeWrite, descriptionWrite;
    private Firebase firebase;
    private Button btnWrite;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loss);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        findViewById(R.id.myButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userNameWrite = findViewById(R.id.userNameWrite);
        phoneWrite = findViewById(R.id.phoneWrite);
        placeWrite = findViewById(R.id.placeWrite);
        descriptionWrite = findViewById(R.id.descriptionWrite);
        btnWrite = findViewById(R.id.btnWrite);
        coordinatorLayout = findViewById(R.id.myContent);

        Firebase.setAndroidContext(this);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String time = date.toString();
                if (!TextUtils.isEmpty(userNameWrite.getText()) && !TextUtils.isEmpty(phoneWrite.getText())
                        && !TextUtils.isEmpty(placeWrite.getText()) && !TextUtils.isEmpty(descriptionWrite.getText())) {  // If the text are not empty the movie will not be approved
                    firebase = new Firebase(getString(R.string.Firebase_Key));
                    firebase.child(time).child("userName").setValue(userNameWrite.getText().toString());
                    firebase.child(time).child("phone").setValue(phoneWrite.getText().toString());
                    firebase.child(time).child("place").setValue(placeWrite.getText().toString());
                    firebase.child(time).child("description").setValue(descriptionWrite.getText().toString());
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

                if (TextUtils.isEmpty(userNameWrite.getText())) {  // If the text are empty the movie will not be approved
                    userNameWrite.setError("Name is required!");  // Print text of error if the text are empty
                }

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
                            firebase = new Firebase(getString(R.string.Firebase_Key));
                            firebase.child(time).child("date").setValue(time);
                            firebase.child(time).child("lat").setValue(location.getLatitude());
                            firebase.child(time).child("lng").setValue(location.getLongitude());
                        }
                    }
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
