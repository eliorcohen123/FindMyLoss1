package com.elior.findmyloss.AdapterPck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.elior.findmyloss.OthersPck.Loss;
import com.elior.findmyloss.R;

public class AdapterAllLoss extends RecyclerView.Adapter<AdapterAllLoss.ViewHolder> implements Filterable {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name, phone, place, km, description;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name1);
            phone = itemView.findViewById(R.id.phone1);
            place = itemView.findViewById(R.id.place1);
            km = itemView.findViewById(R.id.km1);
            description = itemView.findViewById(R.id.description1);
            relativeLayout = itemView.findViewById(R.id.relative1);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select action");
            MenuItem share = menu.add(Menu.NONE, 1, 1, "Share details");
            MenuItem report = menu.add(Menu.NONE, 2, 2, "Reporting Lost");

            share.setOnMenuItemClickListener(onChange);
            report.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Loss loss = lossList.get(getAdapterPosition());
                if (item.getItemId() == 1) {
                    String name = loss.getmName();
                    String phone = loss.getmPhone();
                    String place = loss.getmPlace();
                    String description = loss.getmDescription();
                    String date = loss.getmDate();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nPhone: " + phone + "\nPlace: " + place +
                            "\nDescription: " + description + "\nDate: " + date);
                    sendIntent.setType("text/plain");
                    mInflater.getContext().startActivity(sendIntent);
                } else if (item.getItemId() == 2) {
                    String name = loss.getmName();
                    String phone = loss.getmPhone();
                    String place = loss.getmPlace();
                    String description = loss.getmDescription();
                    String date = loss.getmDate();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"reportlost1@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Reporting Lost");
                    i.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nPhone: " + phone + "\nPlace: " + place +
                            "\nDescription: " + description + "\nDate: " + date + "\n\nThe above loss was found.");
                    try {
                        mInflater.getContext().startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(mInflater.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        };
    }

    private Context context;
    private List<Loss> lossList;
    private List<Loss> lossListFiltered;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private final LayoutInflater mInflater;

    public AdapterAllLoss(Context context, List<Loss> lossList) {
        this.lossList = lossList;
        this.context = context;
        this.lossListFiltered = lossList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_all_loss, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Loss loss = lossListFiltered.get(position);

        holder.name.setText(loss.getmName());
        holder.phone.setText(loss.getmPhone());
        holder.place.setText(loss.getmPlace());

        locationManager = (LocationManager) mInflater.getContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
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
                locationA.setLatitude(loss.getmLat());
                locationA.setLongitude(loss.getmLng());
                Location locationB = new Location("Point B");
                locationB.setLatitude(location.getLatitude());
                locationB.setLongitude(location.getLongitude());
                distanceMe = locationA.distanceTo(locationB) / 1000;   // in km
                String distanceKm1;
                if (distanceMe < 1) {
                    int dis = (int) (distanceMe * 1000);
                    distanceKm1 = String.valueOf(dis) + " Meters";
                    holder.km.setText(distanceKm1);
                } else if (distanceMe >= 1) {
                    String disM = String.format("%.2f", distanceMe);
                    distanceKm1 = String.valueOf(disM) + " Km";
                    holder.km.setText(distanceKm1);
                }
            } else {
                holder.km.setText("No location");
            }
        }

        holder.description.setText(loss.getmDescription());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    String phone = "tel:" + lossList.get(position).getmPhone();
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
                    context.startActivity(i);
                } else {
                    ActivityCompat.requestPermissions((Activity) mInflater.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lossListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    lossListFiltered = lossList;
                } else {
                    List<Loss> filteredList = new ArrayList<>();
                    for (Loss row : lossList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getmName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    lossListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = lossListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                lossListFiltered = (ArrayList<Loss>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
