package ru.example.michael.saper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Locale;

public class PrefActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myLogs", "PrefActivity onCreate ");
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        updateSummary();
        // Register for changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Set up a call handler clicking on the menu item "Game Description"
        getPreferenceScreen().findPreference("prefDescription").setOnPreferenceClickListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        Log.d("myLogs", "PrefActivity onDestroy ");
        // Unregister from changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("keySize")) {
                // Notify that value was really changed
                int value = sharedPreferences.getInt("keySize", 80);
                Log.d("myLogs", "PrefActivity value: " + value);
            }

            if (key.equals("keyPlayers")) {
                // If there have been changes in "The number of players"
                int value = Integer.valueOf(sharedPreferences.getString("keyPlayers", ""));
                Log.d("myLogs", "keyPlayers value: " + value);
            }

            /*** Set locale (language) ***/
            if (key.equals("keyLang")) {
                sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String language = sharedPref.getString("keyLang", "");
                if (language.equals("English")) {
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration configuration = new Configuration();
                    configuration.locale = locale;
                    getBaseContext().getResources().updateConfiguration(configuration, null);
                } else if (language.equals("Русский")) {
                    Locale locale = new Locale("ru");
                    Locale.setDefault(locale);
                    Configuration configuration = new Configuration();
                    configuration.locale = locale;
                    getBaseContext().getResources().updateConfiguration(configuration, null);
                }
                // Updating the list of menu items in the selected language
                setPreferenceScreen(null);
                addPreferencesFromResource(R.xml.preferences);
                getPreferenceScreen().findPreference("prefDescription").setOnPreferenceClickListener(this);
            }
            // Updating description of menu items
            updateSummary();
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d("myLogs", "onActivityResult " + resultCode);

            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case Multiplayer.REQUEST_CONNECT_DEVICE:
                        int cntPlayers = data.getIntExtra("cntPlayers", 1);
                        if (cntPlayers == 1) {
                            // Set "Number of players" equal 1
                            Preference pref = findPreference("keyPlayers");
                            if (pref instanceof ListPreference) {
                                ListPreference listPref = (ListPreference) pref;
                                listPref.setValueIndex(0);
                            }
                        }
                        break;
                }
            }
        }

        /*** Updating description of menu items ***/
        void updateSummary() {
            Preference pref = findPreference("keyPlayers");
            upd(pref);
            pref = findPreference("keyDifficulty");
            upd(pref);
            pref = findPreference("keySize");
            upd(pref);
            pref = findPreference("keyLang");
            upd(pref);
        }

        void upd(Preference pref) {
            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntry());
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.d("myLogs", "onPreferenceClick");
            Intent descriptionIntent = new Intent(getApplicationContext(), Description.class);
            startActivity(descriptionIntent);
            return false;
        }
    }

