package com.ashishlakhmani.ashishtexteditor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        TextView textView = findViewById(R.id.textView);

        if (getIntent().getSerializableExtra("map") != null) {
            HashMap<Integer, String> map = (HashMap<Integer, String>) getIntent().getSerializableExtra("map");

            String text = "";
            for (Map.Entry<Integer, String> me : map.entrySet()) {
                text = text + me.getKey() + ". " + me.getValue() + "\n";
                textView.setText(text);
            }
        }
    }
}
