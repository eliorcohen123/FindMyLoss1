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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.elior.findmyloss.OthersPck.LossModel;
import com.elior.findmyloss.R;

public class AdapterNearbyLoss extends RecyclerView.Adapter<AdapterNearbyLoss.ViewHolder> implements Filterable {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name, phone, place, km, description;
        private ImageView imageView;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name1);
            phone = itemView.findViewById(R.id.phone1);
            place = itemView.findViewById(R.id.place1);
            km = itemView.findViewById(R.id.km1);
            description = itemView.findViewById(R.id.description1);
            imageView = itemView.findViewById(R.id.image1);
            relativeLayout = itemView.findViewById(R.id.relative1);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select action");
            MenuItem share = menu.add(Menu.NONE, 1, 1, "Share Details");
            MenuItem report = menu.add(Menu.NONE, 2, 2, "Reporting Lost");

            share.setOnMenuItemClickListener(onChange);
            report.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LossModel lossModel = lossList.get(getAdapterPosition());
                if (item.getItemId() == 1) {
                    String name = lossModel.getmName();
                    String phone = lossModel.getmPhone();
                    String place = lossModel.getmPlace();
                    String description = lossModel.getmDescription();
                    String date = lossModel.getmDate();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nPhone: " + phone + "\nPlace: " + place +
                            "\nDescription: " + description + "\nDate: " + date);
                    sendIntent.setType("text/plain");
                    mInflater.getContext().startActivity(sendIntent);
                } else if (item.getItemId() == 2) {
                    String name = lossModel.getmName();
                    String phone = lossModel.getmPhone();
                    String place = lossModel.getmPlace();
                    String description = lossModel.getmDescription();
                    String date = lossModel.getmDate();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"reportlost1@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Reporting Lost");
                    i.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nPhone: " + phone + "\nPlace: " + place +
                            "\nDescription: " + description + "\nDate: " + date + "\n\nThe above lossModel was found.");
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
    private List<LossModel> lossList;
    private List<LossModel> lossListNearbyFiltered;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private final LayoutInflater mInflater;

    public AdapterNearbyLoss(Context context, List<LossModel> lossList) {
        this.lossList = lossList;
        this.context = context;
        this.lossListNearbyFiltered = lossList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_nearby_loss, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LossModel lossModel = lossListNearbyFiltered.get(position);

        holder.name.setText(lossModel.getmName());
        holder.phone.setText(lossModel.getmPhone());
        holder.place.setText(lossModel.getmPlace());

        locationManager = (LocationManager) mInflater.getContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
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
                locationA.setLatitude(lossModel.getmLat());
                locationA.setLongitude(lossModel.getmLng());
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
            }
        }
        holder.description.setText(lossModel.getmDescription());

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

        Glide.with(mInflater.getContext()).load(lossModel.getmImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return lossListNearbyFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    lossListNearbyFiltered = lossList;
                } else {
                    List<LossModel> filteredList = new ArrayList<>();
                    for (LossModel row : lossList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getmName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    lossListNearbyFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = lossListNearbyFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                lossListNearbyFiltered = (ArrayList<LossModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
