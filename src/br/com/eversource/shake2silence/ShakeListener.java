package br.com.eversource.shake2silence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ShakeListener extends Service implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelSensor;
	private long lastRead;
	private float lastAccelX;
	private boolean shakeStarted = false;
	private int shakeCount;
	
	private static final int SHAKE_INTERVAL = 100;
	private static final int SHAKE_MAX = 3;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Shake2Silence", "starting service");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		boolean accelSupported = sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);

		if (!accelSupported) {
			// on accelerometer on this device
			sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER));
			this.stopSelf();
			return;
		}
		resetValues();
	}
	
	private void backToNormalMode() {
		SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE);
		if (!prefs.getBoolean(MainActivity.KEEP_SILENT_MODE_PREF_KEY, true)) {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				Toast.makeText(getApplicationContext(),
						getApplicationContext().getText(R.string.back_normal_mode),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void resetValues() {
		shakeStarted = false;
		lastAccelX = 0f;
		lastRead = 0;
		shakeCount = 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		resetValues();
		backToNormalMode();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float accelX = event.values[SensorManager.DATA_X];
		long now = System.currentTimeMillis();
		
		//read interval
		if ( now - lastRead >= SHAKE_INTERVAL ) {
			Log.d("Shake2Silence", "Reading accelerometer");
			now = lastRead;
			
			//side-to-side shake
			if ( lastAccelX > 0 &&
					(lastAccelX > 0 && accelX < 0) || (lastAccelX < 0 && accelX > 0) ) {
				if ( shakeStarted ) {
					//how many shakes?
					if ( ++shakeCount > SHAKE_MAX ) {
						Log.d("Shake2Silence", "Changing to the silent mode");
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
							audioManager
									.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							Toast.makeText(getApplicationContext(),
									getApplicationContext().getText(R.string.shake_message),
									Toast.LENGTH_SHORT).show();
							Log.d("Shake2Silence", "Resetting values and unregistering listeners");
							resetValues();
							sensorManager.unregisterListener(this, accelSensor);							
						}						
					} else {
						//not enough...
						Log.d("Shake2Silence", "Shaked times: "+shakeCount);
						shakeStarted = false;
					}
				} else {
					Log.d("Shake2Silence", "Starting a shake!");
					shakeStarted = true;
				}				
			}
			lastAccelX = accelX;
		}
	}
}