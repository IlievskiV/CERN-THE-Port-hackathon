package com.hackathon.casa;

import com.hackathon.casa.exceptions.GPSNotEnabledException;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTracker {

	private static final int GPS_MIN_UPDATE_DISTANCE = 2; // meters?
	private static final int GPS_MIN_UPDATE_TIME = 2000; // milliseconds

	private LocationManager locationManager;
	private final LocationListener locationListener;
	protected double longitude;
	protected double latitude;

	public LocationTracker(Context context) throws GPSNotEnabledException {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			throw new GPSNotEnabledException();
		}

		this.locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
			}

			@Override
			public void onProviderEnabled(String s) {
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPS_MIN_UPDATE_TIME, GPS_MIN_UPDATE_DISTANCE, locationListener);
	}

	public void stopTrackingLocation() {
		locationManager.removeUpdates(locationListener);
	}

}
