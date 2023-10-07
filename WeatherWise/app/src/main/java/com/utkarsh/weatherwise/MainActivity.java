package com.utkarsh.weatherwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText locationEditText;
    private ImageView getWeatherButton;
    private TextView weatherTextView, cityNameTextView;
    private RecyclerView cityRecyclerView;
    private CityAdapter cityAdapter;
    private CardView detailsCardView;
    Toolbar toolbar;

    private List<CityItem> commonCities = new ArrayList<>();
    private String apiKey = "YOUR_API_KEY_HERE";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationEditText = findViewById(R.id.locationEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        weatherTextView = findViewById(R.id.weatherTextView);
        cityRecyclerView = findViewById(R.id.cityRecyclerView);
        detailsCardView = findViewById(R.id.detailsCardView);
        cityNameTextView = findViewById(R.id.cityNameTextView);

        // Initialize RecyclerView and Adapter
        cityAdapter = new CityAdapter(commonCities);
        cityRecyclerView.setAdapter(cityAdapter);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        cityRecyclerView.addItemDecoration(itemDecoration);

        // Add some common cities with image resources
        commonCities.add(new CityItem("New York", R.drawable.nyc));
        commonCities.add(new CityItem("New Delhi", R.drawable.nd));
        commonCities.add(new CityItem("Allahabad", R.drawable.alld));
        commonCities.add(new CityItem("San Francisco", R.drawable.sanfrancisco));
        commonCities.add(new CityItem("Dehradun", R.drawable.dehradun));
        commonCities.add(new CityItem("Boston", R.drawable.boston));

        // Set onClickListener for the Get Weather button
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationEditText.getText().toString();
                new FetchWeatherTask().execute(location);
                if(!location.isEmpty()){
                    Toast.makeText(MainActivity.this, "Showing Weather Info. for "+location, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "No city found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for the cities in the RecyclerView
        cityAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String city) {
                // Update the city name EditText with the clicked city
                locationEditText.setText(city);
                new FetchWeatherTask().execute(city);
                if(!city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Showing Weather Info. for "+city, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "No city found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Inflate the menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_portfolio:
                // Open the portfolio URL when the "Portfolio" option is clicked
                openPortfolio();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to open the portfolio URL
    private void openPortfolio() {
        String portfolioUrl = "https://utkarsh140503.github.io/Portfolio/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(portfolioUrl));
        startActivity(intent);
    }

    // AsyncTask to fetch weather data
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String location = params[0];
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Inside the FetchWeatherTask's onPostExecute method
        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject data = new JSONObject(json);
                    JSONObject main = data.getJSONObject("main");
                    double temperatureCelsius = main.getDouble("temp") - 273.15; // Convert Kelvin to Celsius
                    double temperatureFahrenheit = (temperatureCelsius * 9/5) + 32; // Convert Celsius to Fahrenheit
                    double humidity = main.getDouble("humidity");
                    double tempMin = main.getDouble("temp_min") - 273.15;
                    double tempMax = main.getDouble("temp_max") - 273.15;

                    // Get the city name from the EditText (the clicked city)
                    final String cityName = locationEditText.getText().toString();
                    final String weatherInfo = "Temperature: " + String.format("%.2f°C / %.2f°F", temperatureCelsius, temperatureFahrenheit)
                            + "\nHumidity: " + humidity + "%"
                            + "\nMin Temperature: " + String.format("%.2f°C", tempMin)
                            + "\nMax Temperature: " + String.format("%.2f°C", tempMax);

                    weatherTextView.setText(weatherInfo);

                    // Determine if it's day or night
                    final boolean isDay = isDayTime(data);
                    int drawableResId = isDay ? R.drawable.sun : R.drawable.moon;

                    // Set the label above the information with the city name and appropriate drawable
                    if(!cityName.isEmpty()){
                        cityNameTextView.setText("Showing weather for " + cityName);
                    }else{
                        cityNameTextView.setText("Select/Type city name!");
                    }
                    cityNameTextView.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);
                    detailsCardView.setVisibility(View.VISIBLE);
                    weatherTextView.setVisibility(View.VISIBLE);
                    cityNameTextView.setVisibility(View.VISIBLE); // Show the label

                    // Set onClickListener for the detailsCardView to show the dialog
                    detailsCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create and show the dialog with city details
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault());
                            String dateTime = sdf.format(new Date(System.currentTimeMillis()));

                            CityDetailsDialogFragment dialogFragment = new CityDetailsDialogFragment(cityName, weatherInfo, dateTime, isDay);
                            dialogFragment.show(getSupportFragmentManager(), "CityDetailsDialog");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                cityNameTextView.setText("Select/Type city name!");
                weatherTextView.setText("Failed to fetch weather data");
            }
        }

        // Helper method to determine if it's day or night
        private boolean isDayTime(JSONObject data) throws JSONException {
            long sunriseTime = data.getJSONObject("sys").getLong("sunrise") * 1000;
            long sunsetTime = data.getJSONObject("sys").getLong("sunset") * 1000;
            long currentTime = System.currentTimeMillis();

            return currentTime >= sunriseTime && currentTime <= sunsetTime;
        }
    }
}