package com.utkarsh.crescendo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private MediaPlayer mediaPlayer;
    private ImageView playPauseButton, image;
    private SeekBar seekBar;
    private Handler handler;
    private CardView firstCardView, secondCardView;
    private static final int REQUEST_CODE_AUDIO = 1;
    private TextView songNameTextView; // TextView for displaying song name

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "com.utkarsh.crescendo.notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songRecyclerView = findViewById(R.id.songRecyclerView);
        playPauseButton = findViewById(R.id.playPauseButton);
        seekBar = findViewById(R.id.seekBar);
        handler = new Handler();
        firstCardView = findViewById(R.id.firstCardView);
        secondCardView = findViewById(R.id.secondCardView);
        songNameTextView = findViewById(R.id.songNameTextView); // Initialize TextView

        songNameTextView.setText("Select Audio From Device by clicking on the button on the left!\n\nEnjoy Pre-Saved Song by clicking on the songs below!");

        // Create a list of songs (you should replace these with your actual songs)
        songList = new ArrayList<>();
        songList.add(new Song("Khalid - Eastside (with Halsey & Khalid)", R.drawable.eastside, R.raw.song1));
        songList.add(new Song("Powfu - death bed (coffee for your head)", R.drawable.deathbed, R.raw.song2));
        songList.add(new Song("Joji - SLOW DANCING IN THE DARK", R.drawable.slowdancing, R.raw.song3));
        songList.add(new Song("Post Malone - Goodbyes (feat. Young Thug)", R.drawable.goodbye, R.raw.goodbye));
        songList.add(new Song("The Weeknd - Alone Again", R.drawable.aloneagain, R.raw.aloneagain));
        songList.add(new Song("Post Malone - Mourning", R.drawable.mourning, R.raw.mourning));

        songAdapter = new SongAdapter(songList, this::onSongItemClick);
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playPauseButton.setOnClickListener(v -> onPlayPauseClick());

        // Initialize the MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            updateSeekBar();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageView backwardButton = findViewById(R.id.backwardButton);
        ImageView forwardButton = findViewById(R.id.forwardButton);

        backwardButton.setOnClickListener(v -> onBackwardClick());
        forwardButton.setOnClickListener(v -> onForwardClick());

        image = findViewById(R.id.image);
        // Set an OnClickListener for the firstCardView to open the file picker
        firstCardView.setOnClickListener(v -> openAudioFilePicker());
        image.setOnClickListener(v -> openAudioFilePicker());
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

    private void openAudioFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri selectedAudioUri = data.getData();
            if (selectedAudioUri != null) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this, selectedAudioUri);
                    mediaPlayer.prepareAsync();
                    playPauseButton.setImageResource(R.drawable.pause);
                    seekBar.setProgress(0);
                    findViewById(R.id.controlLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.seekBar).setVisibility(View.VISIBLE);
                    // Update the song name in the secondCardView
                    songNameTextView.setText(getSongNameFromUri(selectedAudioUri));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading audio file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getSongNameFromUri(Uri uri) {
        // You can implement logic here to extract the song name from the URI.
        // For simplicity, let's just use the last segment of the URI.
        secondCardView.setVisibility(View.VISIBLE);
        return "Currently Playing\n\nAudio From the Device\n"+uri.getLastPathSegment();
    }

    private void onBackwardClick() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int newPosition = currentPosition - 5000; // 5 seconds backward

        if (newPosition < 0) {
            newPosition = 0;
        }

        mediaPlayer.seekTo(newPosition);
    }

    private void onForwardClick() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int newPosition = currentPosition + 5000; // 5 seconds forward

        if (newPosition > mediaPlayer.getDuration()) {
            newPosition = mediaPlayer.getDuration();
        }

        mediaPlayer.seekTo(newPosition);
    }

    private void onSongItemClick(Song song) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(getResources().openRawResourceFd(song.getAudioResourceId()));
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        playPauseButton.setImageResource(R.drawable.pause);
        seekBar.setProgress(0);
        findViewById(R.id.controlLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.seekBar).setVisibility(View.VISIBLE);

        // Update the song name in the secondCardView using the title property
        secondCardView.setVisibility(View.VISIBLE);
        TextView songNameTextView = findViewById(R.id.songNameTextView);
        songNameTextView.setText("Currently Playing\n\n"+song.getTitle());
    }

    private void onPlayPauseClick() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause);
            updateSeekBar();
        }
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}


