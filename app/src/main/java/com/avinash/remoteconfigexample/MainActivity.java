package com.avinash.remoteconfigexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseRemoteConfig firebaseRemoteConfig;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        textView = findViewById( R.id.textView );

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);


        firebaseRemoteConfig.setDefaultsAsync( R.xml.remote_config_defaults );
        firebaseRemoteConfig.activate();

        showMessage( firebaseRemoteConfig );

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                fetchNewConfig();
            }
        },2000 );



    }

    private void showMessage(FirebaseRemoteConfig firebaseRemoteConfig) {
        textView.setTextColor( Color.parseColor(firebaseRemoteConfig.getString("text_color")));
        textView.setTextSize((float) firebaseRemoteConfig.getValue("text_size").asDouble());
        textView.setText(firebaseRemoteConfig.getString("text_str"));
    }

    private void fetchNewConfig() {

        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d("Firebase", "Config params updated: " + updated);
                            Toast.makeText(MainActivity.this, "Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        showMessage( firebaseRemoteConfig );
                    }
                });

    }

}
