package com.hackathon.casa;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AltitudeTracker {

	private static final double Rc = 8.31432; // Universal Gas Constant.
	private static final double g = 9.80665; // Gravitational Acceleration.
	private static final double M = 0.0289644; // Molar Mass of Earth's air.

	private SensorEventListener sensorEventListener;
	private double initialPressure;
	private SensorManager sensorManager;
	private double actualTemperature;
	private double actualPressure;
	private boolean isReady = false;

	public AltitudeTracker(Context context) {
		sensorEventListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO(fabriph): maybe there is no need for this to be
				// synchronized.
				synchronized (this) {
					if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
						actualTemperature = getTemperature(event);
					} else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
						actualPressure = getPressure(event);
					}
				}
			}

			private double getPressure(SensorEvent event) {
				// TODO(fabriph): check units.
				return event.values[0];
			}

			private double getTemperature(SensorEvent event) {
				// TODO(fabriph): check units.
				return event.values[0] + 273.15;
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

		sensorManager = (SensorManager) context
				.getSystemService(Service.SENSOR_SERVICE);
		registerSensor(sensorManager
				.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE));
		registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));

		initializePressure();
	}

	private void initializePressure() {
		// TODO(fabriph): I think there is a better way to run a thread
		// according Google official documentation.
		new Thread() {
			@Override
			public void run() {
				// add delay
				// TODO(fabriph): calculate initial pressure
				// add delay
				isReady = true;
			}
		}.run();
	}

	private void registerSensor(Sensor sensor) {
		sensorManager.registerListener(sensorEventListener, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	// TODO(fabriph): check if initial temperature is not required to get a
	// better estimation given that temperature may vary.
	public double getAltitude() {
		return (-((Rc * actualTemperature) / (M * g)) * Math.log(actualPressure
				/ initialPressure));
	}

	public void onResume() {
		registerSensor(sensorManager
				.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE));
		registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
	}

	public void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
	}

	void stopTrackingAltitude() {
		sensorManager.unregisterListener(sensorEventListener);
	}

	public boolean isReady() {
		return isReady;
	}
}
