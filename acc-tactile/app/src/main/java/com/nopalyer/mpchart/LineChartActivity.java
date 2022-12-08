package com.nopalyer.mpchart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Locale;

public class LineChartActivity extends AppCompatActivity {

    private static final String TAG = "LineChartActivity";
    private final ArrayList<Entry> lineDataList = new ArrayList<>();
    private LineChart lineChart;
    private TextToSpeech textToSpeech;
    private boolean shouldVibrate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        lineChart = findViewById(R.id.lineChart);
        final LineDataSet lineDataSet = new LineDataSet(getLineChartDataSet(), "solid line");
        final LineDataSet lineDataSet2 = new LineDataSet(getLineChartDataSet2(), "dashed line");
        final ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        final ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        //lineDataSet.enableDashedLine(5.0f, 18.0f, -1.0f);
        iLineDataSets.add(lineDataSet);
        lineDataSets.add(lineDataSet);
//        iLineDataSets.add(lineDataSet2);
//        lineDataSets.add(lineDataSet2);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        setupLineChart(lineChart);
        setupLineChartProperties(lineDataSets);
        setup();
    }

    private void setupLineChartProperties(@NonNull final ArrayList<LineDataSet> lineDataSets) {
        for (LineDataSet lineDataSet : lineDataSets) {
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setCircleColor(Color.RED);
            lineDataSet.setDrawCircles(true);
            lineDataSet.setLineWidth(8);
            lineDataSet.setCircleRadius(6);
            lineDataSet.setCircleHoleRadius(6);
            lineDataSet.setValueTextSize(10);
            lineDataSet.setValueTextColor(Color.BLACK);
        }
    }

    private void setupLineChart(@NonNull final LineChart lineChart) {
        lineChart.invalidate();
        lineChart.setNoDataText("Data not Available");
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHorizontalScrollBarEnabled(false);
        lineChart.setVerticalScrollBarEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
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
                }
            }
        });

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, h.toString() + " " + e.toString());
                int x = lineChart.getLineData().getDataSetForEntry(e).getEntryIndex(e);
                if (x + 1 >= lineDataList.size()) {
                    textToSpeech.speak("End of chart", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Entry entry = lineDataList.get(x);
                    Entry nextEntry = lineDataList.get(x + 1);
                    float currentYValue = entry.getY();
                    float nextYValue = nextEntry.getY();
                    float yDiff = nextYValue - currentYValue;
                    if (yDiff < 0) {
                        textToSpeech.speak("Go down", TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        textToSpeech.speak("Go up", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                //textToSpeech.speak("No data", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    private void vibrate(final int yValue) {
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

    private ArrayList<Entry> getLineChartDataSet() {
        ArrayList<Entry> dataSet = new ArrayList<Entry>();
        Entry entry = new Entry(0, 4);
        dataSet.add(entry);
        lineDataList.add(entry);
        entry = new Entry(1, 1);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(2, 5);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(3, 12);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(4, 9);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(5, 18);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(6, 17);
        lineDataList.add(entry);
        dataSet.add(entry);
        return dataSet;
    }

    private ArrayList<Entry> getLineChartDataSet2() {
        ArrayList<Entry> dataSet = new ArrayList<Entry>();
        Entry entry = new Entry(0, 4);
        dataSet.add(entry);
        lineDataList.add(entry);
        entry = new Entry(1, 3);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(2, 6);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(3, 9);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(4, 8);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(5, 3);
        lineDataList.add(entry);
        dataSet.add(entry);
        entry = new Entry(6, 7);
        lineDataList.add(entry);
        dataSet.add(entry);
        return dataSet;
    }
}
