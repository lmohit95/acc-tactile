package com.nopalyer.mpchart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class LineChartSpeechToTextActivity extends AppCompatActivity {

    private static final String TAG = "LineChartSToTActivity";
    private final int REQUEST_CODE_SPEECH_INPUT = 22;
    private final ArrayList<Entry> lineDataList = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private Button speechToTextButton;
    private TextView xLabel;
    private TextView yLabel;

    private boolean isSolidLine = true;

    // Features which can be turned on/off
    private boolean shouldVibrate = false;
    private boolean shouldSpeak = true;

    enum FEATURES {
        AUDIO,
        TACTILE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        speechToTextButton = findViewById(R.id.speechToText);
        LineChart lineChart = findViewById(R.id.lineChart);
        xLabel = findViewById(R.id.xlabel);
        yLabel = findViewById(R.id.ylabel);
        yLabel.setRotation(90f);

        final LineDataSet lineDataSet = new LineDataSet(getLineChartDataSet(), "solid line");
        final LineDataSet lineDataSet2 = new LineDataSet(getLineChartDataSet2(), "dashed line");
        final ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        final ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        lineDataSets.add(lineDataSet);
        isSolidLine = true;

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        setupLineChart(lineChart);
        setupLineChartProperties(lineDataSets);
        setChartValues(lineChart);
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
     * Listeners callbacks to implement functionality when lines are long pressed/highlighted
     *
     * @param lineChart LineChart on which the callbacks are defined
     */
    private void setChartValues(final LineChart lineChart) {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        lineChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Need to check what can be done here
                return false;
            }
        });

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = lineChart.getLineData().getDataSetForEntry(e).getEntryIndex(e);
                if (x + 1 >= lineDataList.size()) {
                    textToSpeech.speak("End of chart", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Entry entry = lineDataList.get(x);
                    Entry nextEntry = lineDataList.get(x + 1);
                    float currentYValue = entry.getY();
                    float nextYValue = nextEntry.getY();
                    float yDiff = nextYValue - currentYValue;
                    if (shouldSpeak) {
                        if (yDiff < 0) {
                            textToSpeech.speak("Go down", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            textToSpeech.speak("Go up", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    if (shouldVibrate) {
                        vibrate(entry.getY());
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                if (shouldSpeak) {
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
        if (shouldSpeak) {
            textToSpeech.speak("This is a sample line graph with x axis representing time in years " +
                    "and y axis representing human population in millions." +
                    "Whitespace is represented using the phrase No Data. A google assistant button is " +
                    "located at the top left corner to help you with any queries", TextToSpeech.QUEUE_FLUSH, null);
        }
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
                    Log.d(TAG, e.getMessage());
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
                answerPredefinedQuestions(spokenText);
                enableFeatures(spokenText);
            }
        }
    }

    private void enableFeatures(final String featureFlag) {
        Log.d(TAG, "Feature Flag : " + featureFlag);
        if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_AUDIO_FEATURES)) {
            respondToEnableFeatures(FEATURES.AUDIO, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_AUDIO_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.AUDIO, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_AUDIO_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.AUDIO, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_AUDIO_FEATURES)) {
            respondToEnableFeatures(FEATURES.AUDIO, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_AUDIO_FEATURES)) {
            respondToEnableFeatures(FEATURES.AUDIO, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_AUDIO_FEATURES)) {
            respondToEnableFeatures(FEATURES.AUDIO, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_AUDIO_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.AUDIO, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_AUDIO_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.AUDIO, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_TACTILE_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_TACTILE_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_TACTILE_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_TACTILE_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_TACTILE_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_TACTILE_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_TACTILE_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_TACTILE_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_VIBRATION_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_ON_VIBRATION_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_VIBRATION_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.SWITCH_OF_VIBRATION_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_VIBRATION_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_ON_VIBRATION_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, true);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_VIBRATION_FEEDBACK)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        } else if (featureFlag.contains(GeneralSpokenConstants.TURN_OF_VIBRATION_FEATURES)) {
            respondToEnableFeatures(FEATURES.TACTILE, false);
        }
    }

    private void respondToEnableFeatures(final FEATURES feature, final boolean enable) {
        if (feature == FEATURES.AUDIO) {
            if (enable) {
                shouldSpeak = true;
                speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_SWITCHED_ON);
            } else {
                shouldSpeak = false;
                speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_SWITCHED_OFF);
            }
        } else {
            if (enable) {
                shouldVibrate = true;
                speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_SWITCHED_ON);
            } else {
                shouldVibrate = false;
                speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_SWITCHED_OFF);
            }
        }
    }

    private void answerPredefinedQuestions(@NonNull String question) {
        Log.d(TAG, "Question : " + question);
        if (question.contains(QuestionConstants.SOLID_DASHED_LINE)) {
            speakAnswer(isSolidLine ? AnswerConstants.SOLID_LINE : AnswerConstants.DASHED_LINE);
        }  else if (question.contains(QuestionConstants.HIGHEST_POINT_LINE)) {
            float highestLinePoint = 0;
            for (Entry entry : lineDataList) {
                highestLinePoint = Math.max(highestLinePoint, entry.getY());
            }
            speakAnswer(AnswerConstants.HIGHEST_POINT_LINE + "" + highestLinePoint);
        } else if (question.contains(QuestionConstants.LOWEST_POINT_LINE)) {
            float lowestLinePoint = 0;
            for (Entry entry : lineDataList) {
                lowestLinePoint = Math.min(lowestLinePoint, entry.getY());
            }
            speakAnswer(AnswerConstants.LOWEST_POINT_LINE + " " + lowestLinePoint);
        } else if (question.contains(QuestionConstants.DESCRIBE_THE_GRAPH)) {
            speakAnswer(AnswerConstants.DESCRIBE_THE_GRAPH_LINE_CHART_ANSWER);
        } else if (question.contains(QuestionConstants.X_AND_Y)) {
            speakAnswer(AnswerConstants.X_AND_Y_ANSWER);
        } else if (question.contains(QuestionConstants.TACTILE_ON_OFF)) {
            speakAnswer(shouldVibrate ? AnswerConstants.TACTILE_ANSWER_YES : AnswerConstants.TACTILE_ANSWER_NO);
        }
    }

    private void speakAnswer(@NonNull String answer) {
        Log.d(TAG, "Answer : " + answer);
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
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
