package com.example.shop_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.example.shop_it.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


class myLocationListener implements LocationListener {
    private Context activityContext;

    public myLocationListener(Context context) {
        activityContext = context;
    }

    public void onProviderDisabled(String arg0) {
        Toast.makeText(activityContext, "Your GPS is disabled!", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String arg0) {
        Toast.makeText(activityContext, "Your GPS is enabled!", Toast.LENGTH_LONG).show();
    }

    public void onLocationChanged(Location location) {
        Toast.makeText(activityContext, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

public class MapsActivity extends AppCompatActivity {

    private ActivityMapsBinding binding;
    LocationManager locationManager;
    myLocationListener locationListener;
    databaseHelper dbh;
    String address;

    private int REQUEST_CODE = 111;
    int customer_id;
    TextView currentLocationTxt;
    Button saveLocationBtn, getCurrentLocationBtn;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private GoogleMap mMap;
    private Geocoder gecoder;
    private double selectedLat, selectedLng;
    private List<Address> addresses;
    private String selectedAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currentLocationTxt = (TextView) findViewById(R.id.currentLocationTxt);
        saveLocationBtn = (Button) findViewById(R.id.saveLocationBtn);
        getCurrentLocationBtn = (Button) findViewById(R.id.getCurrentLocationBtn);
        customer_id = getIntent().getExtras().getInt("customer_id");
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        getCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cart = dbh.get_customer_cart(customer_id);
                dbh.update_cart_delivery_address(cart.getInt(0), address);
                Intent intent = new Intent(MapsActivity.this, homeActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                checkConnectivity();
                if(networkInfo.isConnected() && networkInfo.isAvailable()){
                    selectedLat = latLng.latitude;
                    selectedLng = latLng.longitude;
                    getAddress(selectedLat, selectedLng);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
                //Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>(){
            @Override
            public void onSuccess(@NonNull Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            mMap = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Your current location");
                            googleMap.addMarker(options).showInfoWindow();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

                        }
                    });
                }
            }
        });
        }

    private void checkConnectivity() {
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }
    private void getAddress(double mLat, double mLng) {
        gecoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        if(mLat != 0){
            try {
                addresses = gecoder.getFromLocation(mLat, mLng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addresses != null){
                String mAddress = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                selectedAddress = mAddress;
                if(mAddress != null){
                     MarkerOptions markerOptions = new MarkerOptions();
                     LatLng latlng = new LatLng(mLat, mLng);
                     address = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2) + addresses.get(0).getAddressLine(3);
                     markerOptions.position(latlng).title(address);
                     mMap.addMarker(markerOptions).showInfoWindow();
                }
                else{
                    Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Latlng null", Toast.LENGTH_SHORT).show();
        }
    }
}