package com.hackathon.casa;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hackathon.casa.exceptions.GPSNotEnabledException;

public class MainActivity extends Activity {
	private static final int DELAY_STARTING_TIME = 1000;
	private static final int UPDATE_INTERVAL_IN_MILISECONDS = 1000;
	private static final String APP_TAG = "com.hackathon.casa.measure";

	private LocationTracker locationTracker;
	private AltitudTracker altitudTracker;
	private Timer timer;

	private TextView longitudView;
	private TextView latitudView;
	private TextView altitudView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			locationTracker = new LocationTracker(this);
		} catch (GPSNotEnabledException e) {
			// TODO(fabriph): fall back showing that GPS is not enabled
		}
		altitudTracker = new AltitudTracker(this);

		longitudView = (TextView) findViewById(R.id.longitud);
		latitudView = (TextView) findViewById(R.id.latitud);
		altitudView = (TextView) findViewById(R.id.altitud);

		// Wait until sensors are ready, then start measuring.
		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						sleep(1000);
						if (locationTracker.isReady()
								&& altitudTracker.isReady()) {
							startMeasuring();
							return;
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}.run();
	}

	private void startMeasuring() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO(fabriph): record this in the DB.
				updateView();
			}
		}, DELAY_STARTING_TIME, UPDATE_INTERVAL_IN_MILISECONDS);
	}

	@Override
	protected void onResume() {
		super.onResume();
		altitudTracker.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		altitudTracker.onPause();
	}

	private void updateView() {
		StringBuilder sb = new StringBuilder("Lat:");
		sb.append(locationTracker.getLatitude());
		sb.append(" Lon:");
		sb.append(locationTracker.getLongitude());
		sb.append(" Alt:");
		sb.append(altitudTracker.getAltitud());
		Log.d(APP_TAG, sb.toString());
		longitudView.setText(String.valueOf(locationTracker.getLatitude()));
		latitudView.setText(String.valueOf(locationTracker.getLongitude()));
		altitudView.setText(String.valueOf(altitudTracker.getAltitud()));
	}

}
