package br.com.eversource.shake2silence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
		boolean activate = prefs.getBoolean(MainActivity.ACTIVATE_PREF_KEY, true);
		
		if ( activate ) {
			if ( tm.getCallState() == TelephonyManager.CALL_STATE_RINGING ) {
				Intent i = new Intent(context, ShakeListener.class);
				i.addCategory(Intent.CATEGORY_DEFAULT);
				context.startService(i);
			} else {
				Log.d("Shake2Silence", "STATE: "+tm.getCallState());
				Intent i = new Intent(context, ShakeListener.class);
				i.addCategory(Intent.CATEGORY_DEFAULT);
				context.stopService(i);
			}
		}
	}
}