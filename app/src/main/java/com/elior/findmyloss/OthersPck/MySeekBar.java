package com.elior.findmyloss.OthersPck;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.elior.findmyloss.R;

// The main class of the seekBar
public class MySeekBar extends Preference implements OnSeekBarChangeListener, OnPreferenceChangeListener {

    private SeekBar seekBar;
    private SharedPreferences sharedPreferences;
    private TextView txtSummary;
    private String units;

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.my_seekbar_preference, parent, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        seekBar = view.findViewById(R.id.seekBar);
        txtSummary = view.findViewById(R.id.summary);
        units = this.sharedPreferences.getString(getContext().getString(R.string.key_units), "1000");
        int max = (int) ((50000) / 0.9144d);
        seekBar.setMax(max);
        int currentProgress = this.sharedPreferences.getInt(getKey(), 0);
        String stringBuilder = String.valueOf(currentProgress) + units;
        setSummary(stringBuilder);
        seekBar.setProgress(currentProgress);
        seekBar.setOnSeekBarChangeListener(this);
        return view;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        StringBuilder result = new StringBuilder();
        result.append(progress);
        txtSummary.setText(result.toString());
        Editor editor = sharedPreferences.edit();
        editor.putInt(getKey(), progress);
        editor.apply();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int currentProgress = this.seekBar.getProgress();
        seekBar.setProgress((int) (currentProgress / 0.9144d));
        return true;
    }

}
