package com.nopalyer.mpchart;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class BarChartActivity extends AppCompatActivity {

    private static final String TAG = "BarChartActivity";
    private static final String VIBRATIONS_TEXT = "Vibrations";
    private boolean shouldVibrate = true;
    private ArrayList<BarEntry> barArraylist;
    private TextView xLabel;
    private TextView yLabel;
    private TextToSpeech textToSpeech;
    private Button tactileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        xLabel = findViewById(R.id.xlabel);
        yLabel = findViewById(R.id.ylabel);
        tactileButton = findViewById(R.id.tactileButton);

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
                textToSpeech.speak("No data", TextToSpeech.QUEUE_FLUSH, null);
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
        textToSpeech.speak("This is a sample bar graph with x axis representing test1 value " +
                "and y axis representing test2 value. Different colors are used for each bar. " +
                "Whitespace is represented using No Data", TextToSpeech.QUEUE_FLUSH, null);
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

        tactileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shouldVibrate = !shouldVibrate;
                String vibrationText = shouldVibrate ? " ON" : " OFF";
                vibrationText = VIBRATIONS_TEXT + vibrationText;
                textToSpeech.speak(vibrationText, TextToSpeech.QUEUE_FLUSH, null);
                tactileButton.setText(vibrationText);
            }
        });
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
