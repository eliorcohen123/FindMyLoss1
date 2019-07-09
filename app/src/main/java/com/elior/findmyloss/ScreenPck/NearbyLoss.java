package com.elior.findmyloss.ScreenPck;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.elior.findmyloss.AdapterPck.AdapterNearbyLoss;
import com.elior.findmyloss.OthersPck.ItemDecoration;
import com.elior.findmyloss.OthersPck.LossModel;
import com.elior.findmyloss.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NearbyLoss extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<LossModel> arrayListMyNearLoss;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private RecyclerView recyclerView;
    private AdapterNearbyLoss adapter;
    private android.support.v7.widget.SearchView searchView;
    private DrawerLayout drawer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_loss);

        initUI();
        showUI();
        myRecyclerView();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        swipeRefreshLayout = findViewById(R.id.swipe_containerFrag);
        recyclerView = findViewById(R.id.myListNearbyLoss);
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

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorOrange));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Vibration for 0.1 second
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }

                finish();
                startActivity(getIntent());  // Refresh activity

                Toast toast = Toast.makeText(NearbyLoss.this, "The list are refreshed!", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.getBackground().setColorFilter(getResources().getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorDarkBrown));
                toast.show();  // Toast

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void myRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemDecoration itemDecoration = new ItemDecoration(5);
        recyclerView.addItemDecoration(itemDecoration);

        arrayListMyNearLoss = new ArrayList<>();

        progressDialog = new ProgressDialog(NearbyLoss.this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                try {
                    arrayListMyNearLoss.clear();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NearbyLoss.this);
                    int myRadius = prefs.getInt("seek", 5000);
                    for (com.google.firebase.database.DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Double lat1 = (Double) postSnapshot.child("mLat").getValue();
                        Double lng1 = (Double) postSnapshot.child("mLng").getValue();
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, true);
                        if (ActivityCompat.checkSelfPermission(NearbyLoss.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.checkSelfPermission(NearbyLoss.this, Manifest.permission.ACCESS_COARSE_LOCATION);
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
                                double distanceMe;
                                Location locationA = new Location("Point A");
                                locationA.setLatitude(lat1);
                                locationA.setLongitude(lng1);
                                Location locationB = new Location("Point B");
                                locationB.setLatitude(location.getLatitude());
                                locationB.setLongitude(location.getLongitude());
                                distanceMe = locationA.distanceTo(locationB);  // in km
                                if (distanceMe < myRadius) {
                                    LossModel lossModel = postSnapshot.getValue(LossModel.class);
                                    arrayListMyNearLoss.add(lossModel);
                                }
                            }
                        }
                    }
                    adapter = new AdapterNearbyLoss(NearbyLoss.this, arrayListMyNearLoss);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {

                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    adapter.getFilter().filter(query);
                } catch (Exception e) {

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                try {
                    adapter.getFilter().filter(query);
                } catch (Exception e) {

                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
