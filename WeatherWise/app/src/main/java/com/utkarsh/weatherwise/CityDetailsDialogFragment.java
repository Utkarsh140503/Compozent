package com.utkarsh.weatherwise;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CityDetailsDialogFragment extends DialogFragment {
    private String cityName;
    private String weatherInfo;
    private String dateTime;
    private boolean isDay;

    public CityDetailsDialogFragment(String cityName, String weatherInfo, String dateTime, boolean isDay) {
        this.cityName = cityName;
        this.weatherInfo = weatherInfo;
        this.dateTime = dateTime;
        this.isDay = isDay;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_city_details, null);

        TextView cityNameTextView = view.findViewById(R.id.dialogCityNameTextView);
        TextView weatherInfoTextView = view.findViewById(R.id.dialogWeatherInfoTextView);
        TextView dateTimeTextView = view.findViewById(R.id.dialogDateTimeTextView);

        cityNameTextView.setText(cityName);
        weatherInfoTextView.setText(weatherInfo);
        dateTimeTextView.setText("\nCurrent Date and Time: \n" + dateTime);

        int iconResId = isDay ? R.drawable.sun : R.drawable.moon;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setIcon(iconResId)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
