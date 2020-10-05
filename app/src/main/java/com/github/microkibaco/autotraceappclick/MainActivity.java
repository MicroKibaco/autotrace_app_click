package com.github.microkibaco.autotraceappclick;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.microkibaco.track_sdk.SensorsReporter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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


// -------------------------test1-------------------------
      AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setNegativeButton("取消", (dialog, which) -> {})
                .setPositiveButton("确定", (dialog, which) -> {})
                .setTitle("小木箱").setMessage("一定要加油努力哦~").create();

        SensorsReporter.getSensorsDataApiInstance().trackDialog(this,alertDialog);

        // -------------------------test2-------------------------
        ViewGroup rootView = findViewById(R.id.rootView);
        AppCompatButton button = new AppCompatButton(this);
        button.setText("动态创建的button");
        button.setOnClickListener(v -> {

        });
        rootView.addView(button);
    }
}