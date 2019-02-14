package com.dxxx.flowlayoutdemo;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageview = findViewById(R.id.imageview);
        final EditText input = findViewById(R.id.input);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (input.getText().toString() == null || input.getText().toString().equals("")) {

                } else {
                    imageview.setImageBitmap(Text2BitmapUtil.drawRectf(MainActivity.this, input.getText().toString()));

                }
                startActivity(new Intent(MainActivity.this,ShowActivity.class));

            }
        });
    }
}
