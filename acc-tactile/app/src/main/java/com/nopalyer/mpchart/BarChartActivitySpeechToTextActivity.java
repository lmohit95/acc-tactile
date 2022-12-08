package com.nopalyer.mpchart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class BarChartActivitySpeechToTextActivity extends AppCompatActivity {

    private static final String TAG = "BarChartSpeechToText";
    private static final String OF = "of";
    private static final String ON = "on";
    private static final String INCLUDE_WHITESPACE = "include white space";
    private static final String NO_INCLUDE_WHITESPACE = "exclude white space";
    private final int REQUEST_CODE_SPEECH_INPUT = 22;
    private boolean shouldVibrate = true;
    private boolean shouldSpeakNoData = false;
    private ArrayList<BarEntry> barArraylist;
    private TextView xLabel;
    private TextView yLabel;
    private TextToSpeech textToSpeech;
    private Button speechToTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_speech_to_text);

        xLabel = findViewById(R.id.xlabel);
        yLabel = findViewById(R.id.ylabel);
        speechToTextButton = findViewById(R.id.speechToText);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            int REQUEST_MICROPHONE = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        }

        final BarChart barChart = findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArraylist, "Test Visualization");
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);

        setup();
        setupBarDataSet(barDataSet);
        setBarChartProperties(barChart);
        setChartValues(barChart);
    }

    /**
     * Setting properties of bar data set
     *
     * @param barDataSet
     */
    private void setupBarDataSet(final BarDataSet barDataSet) {
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
    }

    /**
     * Setting all the bar chart related properties
     *
     * @param barChart
     */
    private void setBarChartProperties(final BarChart barChart) {
        barChart.getDescription().setEnabled(true);
        barChart.setMaxHighlightDistance(10);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setHorizontalScrollBarEnabled(false);
        barChart.setVerticalScrollBarEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        Description graphDescription = new Description();
        graphDescription.setText("Year vs Time");
        graphDescription.setTextSize(15);
        graphDescription.setYOffset(0);
        barChart.setDescription(graphDescription);
    }

    /**
     * Listeners callbacks to implement functionality when bars are long pressed/highlighted
     *
     * @param barChart Barchart on which the callbacks are defined
     */
    private void setChartValues(final BarChart barChart) {
        barChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Need to check what can be done here
                return false;
            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, h.toString());
                int x = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);

                BarEntry entry = barArraylist.get(x);
                Log.d(TAG, barChart.toString());
                textToSpeech.speak(String.valueOf(entry.getY()), TextToSpeech.QUEUE_FLUSH, null);
                vibrate(entry.getY());
            }

            @Override
            public void onNothingSelected() {
                if (shouldSpeakNoData) {
                    textToSpeech.speak("No data", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    /**
     * Vibrates android device using Vibrator Service
     *
     * @param yValue Value based on which vibration pattern will be decided
     */
    private void vibrate(final float yValue) {
        if (shouldVibrate) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                long[] pattern = Utils.getFeedbackProfile(yValue);
                v.vibrate(pattern, -1);
                //v.vibrate(VibrationEffect.createOneShot(500L * (int) yValue, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
    }

    /**
     * Texttospeech functionality of summary. Called on opening the graph.
     */
    private void provideSummary() {
        //textToSpeech.addSpeech("Summary", "com.nopalyer.mpchart", R.raw.sample_sound);
//        textToSpeech.speak("This is a sample bar graph with x axis representing test1 value " +
//                "and y axis representing test2 value. Different colors are used for each bar. " +
//                "Whitespace is represented using No Data", TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Sets up the basic functionality of texttospeech
     */
    private void setup() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    provideSummary();
                }
            }
        });

        xLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(xLabel.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        yLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(yLabel.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        // Speech to Text
        speechToTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast.makeText(BarChartActivitySpeechToTextActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = Objects.requireNonNull(result).get(0).toLowerCase();
                Log.d(TAG, "Spoken Text : " + spokenText);
                if (spokenText.contains(OF)) {
                    Log.d(TAG, "Vibrations are off");
                    shouldVibrate = false;
                } else if (spokenText.contains(ON)) {
                    Log.d(TAG, "Vibrations are on");
                    shouldVibrate = true;
                } else if (spokenText.contains(INCLUDE_WHITESPACE)) {
                    shouldSpeakNoData = true;
                } else if (spokenText.contains(NO_INCLUDE_WHITESPACE)) {
                    shouldSpeakNoData = false;
                }
            }
        }
    }

    /**
     * Test data to make bars
     */
    private void getData() {
        barArraylist = new ArrayList<>();
        barArraylist.add(new BarEntry(3f, 1));
        barArraylist.add(new BarEntry(4f, 2));
        barArraylist.add(new BarEntry(5f, 3));
        barArraylist.add(new BarEntry(6f, 5));
    }
}
