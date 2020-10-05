package com.github.microkibaco.autotraceappclick;

import android.os.Bundle;
import android.widget.Toast;

import com.github.microkibaco.track_sdk.SensorsReporter;

import androidx.appcompat.app.AlertDialog;
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



      AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setNegativeButton("取消", (dialog, which) -> {})
                .setPositiveButton("确定", (dialog, which) -> {})
                .setTitle("小木箱").setMessage("一定要加油努力哦~").create();

        SensorsReporter.getSensorsDataApiInstance().trackDialog(this,alertDialog);
    }
}