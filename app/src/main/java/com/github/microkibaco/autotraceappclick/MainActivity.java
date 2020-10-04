package com.github.microkibaco.autotraceappclick;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author MicroKibaco
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_click).setOnClickListener(v -> {
            Toast.makeText(this,"btn_click",Toast.LENGTH_SHORT).show();
        });
    }
}