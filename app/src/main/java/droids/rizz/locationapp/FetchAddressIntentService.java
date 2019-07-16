package droids.rizz.locationapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIntentService";

    private ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra("receiver");
        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.d(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra("location");

        if (location == null) {
            errorMessage = "NO DATA";
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(301, null);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "NOT AVAILABLE";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "ERROR";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addressList == null || addressList.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "NO ADDRESS FOUND";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(301, null);
        } else {
            Address address = addressList.get(0);

            Log.d(TAG, "ADDRESS FOUND");
            deliverResultToReceiver(302, address);
//            deliverResultToReceiver(Constants.SUCCESS_RESULT, address.getLocality()+", "+address.getAdminArea());
        }


    }

    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("Address", address);
        mReceiver.send(resultCode, bundle);
    }



}
