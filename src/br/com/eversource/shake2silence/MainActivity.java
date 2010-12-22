package br.com.eversource.shake2silence;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class MainActivity extends PreferenceActivity {
	
	public static final String PREFERENCES_NAME = "shake2silence";
	
	public static final String ACTIVATE_PREF_KEY = "activateCheckBox";
	public static final String KEEP_SILENT_MODE_PREF_KEY = "keepSilenceCheckBox";
	
	private CheckBoxPreference activatePreference;
	private CheckBoxPreference keepPreference;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        activatePreference = (CheckBoxPreference) findPreference(ACTIVATE_PREF_KEY);
        activatePreference.setChecked(prefs.getBoolean(ACTIVATE_PREF_KEY, true));
        activatePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				savePreferences();
				return false;
			}
		});
        
        keepPreference = (CheckBoxPreference) findPreference(KEEP_SILENT_MODE_PREF_KEY);
        keepPreference.setChecked(prefs.getBoolean(KEEP_SILENT_MODE_PREF_KEY, true));
        keepPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				savePreferences();
				return false;
			}
		});
    }
    
    public void savePreferences() {
    	SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	
    	editor.putBoolean(ACTIVATE_PREF_KEY, activatePreference.isChecked());
    	editor.putBoolean(KEEP_SILENT_MODE_PREF_KEY, keepPreference.isChecked());
    	editor.commit();
    }
}