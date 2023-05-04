package com.nopalyer.mpchart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLineChart, btnBarChart, btnPieChart, btnBarChartSpeechToText, btnLineChartSpeechToText, addInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLineChart = findViewById(R.id.btnLineChart);
        btnBarChart = findViewById(R.id.btnBarChart);
        btnPieChart = findViewById(R.id.btnPieChart);
        addInformation = findViewById(R.id.addInformation);
        btnBarChartSpeechToText = findViewById(R.id.btnBarChartSpeechToText);
        btnLineChartSpeechToText = findViewById(R.id.btnLineChartSpeechToText);
        btnLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LineChartActivity.class);
                startActivity(intent);
            }
        });

        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BarChartActivity.class);
                startActivity(intent);
            }
        });

        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PieChartActivity.class);
                startActivity(intent);
            }
        });

        btnBarChartSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarChartActivitySpeechToTextActivity.class);
                startActivity(intent);
            }
        });

        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PieChartActivity.class);
                startActivity(intent);
            }
        });

        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PieChartActivity.class);
                startActivity(intent);
            }
        });

        btnLineChartSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LineChartSpeechToTextActivity.class);
                startActivity(intent);
            }
        });

        addInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,GraphSummaryActivity.class);
                startActivity(intent);
            }
        });
    }
}
