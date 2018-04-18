package com.example.domin.multism;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.InputFilter;

/**
 * Created by domin on 21.02.2018.
 */

public class Setings extends PreferenceActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SetingFragment()).commit();

    }


    public static class SetingFragment extends PreferenceFragment {
        private String pas;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference);


            EditTextPreference editTextPreference1 = (EditTextPreference) getPreferenceManager().findPreference("DB_name");
            editTextPreference1.setSummary(editTextPreference1.getText());
            editTextPreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary((CharSequence) o);
                    return true;
                }
            });
            EditTextPreference editTextPreference2 = (EditTextPreference) getPreferenceManager().findPreference("name");
            assert editTextPreference2 != null;
            editTextPreference2.setSummary(editTextPreference2.getText());
            editTextPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary((CharSequence) o);
                    return true;
                }
            });
            EditTextPreference editTextPreference3 = (EditTextPreference) getPreferenceManager().findPreference("password");
            editTextPreference3.setSummary(editTextPreference3.getText());
            editTextPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    pas = o.toString();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pas.length(); i++) {
                        sb.append('*');
                    }
                    pas = sb.toString();
                    preference.setSummary(pas);
                    return true;
                }
            });
            editTextPreference3.setSummary(pas);
            EditTextPreference editTextPreference4 = (EditTextPreference) getPreferenceManager().findPreference("domena");
            editTextPreference4.setSummary(editTextPreference4.getText());
            editTextPreference4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary((CharSequence) o);
                    return true;
                }
            });

            EditTextPreference editTextPreference = (EditTextPreference) getPreferenceManager().findPreference("ip");

            editTextPreference.setSummary(editTextPreference.getText());
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary((CharSequence) o);
                    return true;
                }
            });
        }

     }
    }
