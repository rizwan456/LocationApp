package droids.rizz.locationapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import droids.rizz.locationapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ActivityMainBinding mainBinding;
    private String redirectMethod = null;
    private GoogleApiClient googleApiClient;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double longitude;
    private double lattitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setUp();
    }

    private void setUp() {
        if (!Utility.checkPermissionRequest(Permission.LOCATION, activity())) {
            redirectMethod = "checkAndRaiseLocationRequest";
            Utility.raisePermissionRequest(Permission.LOCATION, activity());
            return;
        }
        createLocationRequest();
    }

    private void createLocationRequest() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(3 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                MainActivity.this.updateRequestLocation();
            } catch (ApiException exception) {
                exception.printStackTrace();
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(MainActivity.this, Utility.generateRequestCodes().get("ACCESS_GOOGLE_LOCATION"));
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }


    public Activity activity() {
        return this;
    }

    public void updateRequestLocation() {
        fusedLocationProviderClient = null;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdate();
    }

    public void startLocationUpdate() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            longitude = location.getLongitude();
                            lattitude = location.getLatitude();
                            mainBinding.currentLocation.setText("long:" + longitude + " Lat:" + lattitude);
                            fetchAddress(location);
                        }
                        stopLocationUpdate();
                        return;
                    }
                }
            };

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(2 * 1000);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public void stopLocationUpdate() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utility.generateRequestCodes().get("LOCATION_REQUEST")) {
            if (Utility.checkPermissionRequest(Permission.LOCATION, activity())) {
                reDirect(redirectMethod);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                //mainBinding.currentLocation.setText("long:" + longitude + " Lat" + lattitude);
            }
        } else if (requestCode == Utility.generateRequestCodes().get("COARSE_LOCATION_REQUEST")) {
            if (Utility.checkPermissionRequest(Permission.COARSE_LOCATION, activity())) {
                reDirect(redirectMethod);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                //mainBinding.currentLocation.setText("long:" + longitude + " Lat" + lattitude);
            }
        }

    }

    private void reDirect(String redirectMethod) {
        if (redirectMethod == null) {
            return;
        }

        switch (redirectMethod) {
            case "checkAndGetLastLocationRequest":
                checkAndGetLastLocationRequest();
                break;
            case "checkAndRaiseLocationRequest":
                checkAndRaiseLocationRequest();
                break;
        }
    }

    public void checkAndGetLastLocationRequest() {
        if (!Utility.checkPermissionRequest(Permission.LOCATION, activity()) && !Utility.checkPermissionRequest(Permission.COARSE_LOCATION, activity())) {
            redirectMethod = "checkAndGetLastLocationRequest";
            if (!Utility.checkPermissionRequest(Permission.LOCATION, activity()))
                Utility.raisePermissionRequest(Permission.LOCATION, activity());
            if (!Utility.checkPermissionRequest(Permission.COARSE_LOCATION, activity()))
                Utility.raisePermissionRequest(Permission.COARSE_LOCATION, activity());

            return;
        }
        getLastLocationRequest();
    }

    private void getLastLocationRequest() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity(), location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                longitude = location.getLongitude();
                lattitude = location.getLatitude();
            }
        });
    }

    public void checkAndRaiseLocationRequest() {
        if (!Utility.checkPermissionRequest(Permission.LOCATION, activity())) {
            redirectMethod = "checkAndRaiseLocationRequest";
            Utility.raisePermissionRequest(Permission.LOCATION, activity());
            return;
        }
        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utility.generateRequestCodes().get("ACCESS_GOOGLE_LOCATION")) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    updateRequestLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    //updateRequestLocation();
                    /*if (getSupportFragmentManager().getFragments().get(0) instanceof AddressDetailsFragment) {
                        ((AddressDetailsFragment) getSupportFragmentManager().getFragments().get(0)).updateUI();
                    }*/
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    updateRequestLocation();
                    break;
            }
        }
    }


    public void fetchAddress(Location location) {
        Intent intent = new Intent(activity(), FetchAddressIntentService.class);
        intent.putExtra("receiver", new AddressResultReceiver(new Handler()));
        intent.putExtra("location", location);
        activity().startService(intent);
    }


    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            if (resultCode == 302) {
                pickUpAddresss(resultData.getParcelable("Address"));
            } else {
                Toast.makeText(MainActivity.this, "Address not found! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void pickUpAddresss(Address resultData) {
        ArrayList<String> address = new ArrayList<>();

        for (int i = 0; i <= resultData.getMaxAddressLineIndex(); i++) {
            address.add(resultData.getAddressLine(i));
            if (i == 0 || i == 1)
                //resultData.getAddressLine(i);//city
                mainBinding.location.setText( resultData.getAddressLine(i));

            resultData.getLocality();
            resultData.getAdminArea();
            resultData.getCountryName();
            resultData.getPostalCode();

        }
    }


}
