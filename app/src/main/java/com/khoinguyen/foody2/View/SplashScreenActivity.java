package com.khoinguyen.foody2.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.LocationRequest;
import com.khoinguyen.foody2.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    TextView txtPhienBan;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public static final int REQUEST_PERMISSION_LOCATION = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splashscreen);

        txtPhienBan = findViewById(R.id.txtPhienBan);

        sharedPreferences = getSharedPreferences("toado", MODE_PRIVATE);

        sharedPreferencesLanguage = getSharedPreferences("language", MODE_PRIVATE);
        int languageCode = sharedPreferencesLanguage.getInt("selected_language", 0);

        if (languageCode == 0) {
            changeLanguage("vi");
        }
        else {
            changeLanguage("en");
            String abc = "";
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location vitrihientai : locationResult.getLocations()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("latitude", String.valueOf(vitrihientai.getLatitude()));
                    editor.putString("longitude", String.valueOf(vitrihientai.getLongitude()));
                    Log.d("latitude", String.valueOf(vitrihientai.getLatitude()));
                    Log.d("longitude", String.valueOf(vitrihientai.getLongitude()));
                    editor.commit();
                }

//                Location vitrihientai = locationResult.getLocations().get(0);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("latitude", String.valueOf(vitrihientai.getLatitude()));
//                editor.putString("longitude", String.valueOf(vitrihientai.getLongitude()));
//                editor.commit();



            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            startLocationUpdates();
            TrangDangNhap();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_LOCATION);
//        } else {
//            startLocationUpdates();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                TrangDangNhap();
            } else {
                // Permissions were not granted
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
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

    public void TrangDangNhap () {
        // Chuyển qua trang đăng nhập hoặc trang chủ khi lấy location thành công
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtPhienBan.setText(getString(R.string.phienban) + " " + packageInfo.versionName);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent iDangNhap = new Intent(SplashScreenActivity.this, DangNhapActivity.class);
                    startActivity(iDangNhap);

                    // Kết thúc Activity (ko back lại được nữa)
                    finish();

                }
            }, 2000);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeLanguage (String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;

        // Yêu cầu app cập nhật lại file string
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

}