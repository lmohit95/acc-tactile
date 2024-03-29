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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class BarChartActivitySpeechToTextActivity extends AppCompatActivity {

    private static final String TAG = "BarChartSpeechToText";
    private final int REQUEST_CODE_SPEECH_INPUT = 22;
    private ArrayList<BarEntry> barArraylist;
    private TextView xLabel;
    private TextView yLabel;
    private TextToSpeech textToSpeech;
    private Button speechToTextButton;
    private final boolean isStackedBarChart = false;
    private float selectedYIndex = -1;
    private final GRAPH_AXIS graphAxis = GRAPH_AXIS.ABOVE_AXIS;
    private int maxBarValue = Integer.MIN_VALUE;
    private int minBarValue = Integer.MAX_VALUE;
    private final boolean continuousBars = false;

    // Features which can be turned on/off
    private boolean shouldVibrate = false;
    private boolean shouldSpeak = true;

    enum GRAPH_AXIS {
        BELOW_AXIS,
        ABOVE_AXIS,
        MIXED
    }

    enum FEATURES {
        AUDIO,
        TACTILE
    }

    boolean areBarsVertical = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_speech_to_text);

        xLabel = findViewById(R.id.xlabel);
        yLabel = findViewById(R.id.ylabel);
        yLabel.setRotation(90f);
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
        BarDataSet barDataSet = new BarDataSet(barArraylist, "Time (years) vs Human Population (Billions)");
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

        barChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                Entry entry = barChart.getEntryByTouchPoint(me.getX(), me.getY());
                if (entry != null) {
                    selectedYIndex = me.getY();
                }
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //barChart.highlightValue(null);
                int x = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                Log.d(TAG, h.getYPx() + " " + e.getY());
                if (h.getYPx() <= selectedYIndex) {
                    BarEntry entry = barArraylist.get(x);
                    Log.d(TAG, barChart.toString());
                    if (shouldSpeak) {
                        textToSpeech.speak(String.valueOf(entry.getY()), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    if (shouldVibrate) {
                        vibrate(entry.getY());
                    }
                } else {
                    barChart.highlightValue(null);
                }
            }

            @Override
            public void onNothingSelected() {
                if (shouldSpeak) {
                    //textToSpeech.speak("No data", TextToSpeech.QUEUE_FLUSH, null);
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
//        if (shouldSpeak) {
//            textToSpeech.addSpeech("Summary", "com.nopalyer.mpchart", R.raw.sample_sound);
//            textToSpeech.speak("This is a sample bar graph with x axis representing test1 value " +
//                    "and y axis representing test2 value. Different colors are used for each bar. " +
//                    "Whitespace is represented using No Data. A google assistant button is " +
//                    "located at the top left corner to help you with any queries", TextToSpeech.QUEUE_FLUSH, null);
//        }/'
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
                if (shouldSpeak) {
                    speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_ALREADY_SWITCHED_ON);
                } else {
                    speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_SWITCHED_ON);
                }
                shouldSpeak = true;
            } else {
                if (!shouldSpeak) {
                    speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_ALREADY_SWITCHED_OFF);
                } else {
                    speakAnswer(GeneralSpokenConstants.VOICE_FEEDBACK_SWITCHED_OFF);
                }
                shouldSpeak = false;
            }
        } else {
            if (enable) {
                if (shouldVibrate) {
                    speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_ALREADY_SWITCHED_ON);
                } else {
                    speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_SWITCHED_ON);
                }
                shouldVibrate = true;
            } else {
                if (!shouldVibrate) {
                    speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_ALREADY_SWITCHED_OFF);
                } else {
                    speakAnswer(GeneralSpokenConstants.TACTILE_FEEDBACK_SWITCHED_OFF);
                }
                shouldVibrate = false;
            }
        }
    }

    private void answerPredefinedQuestions(@NonNull String question) {
        Log.d(TAG, "Question : " + question);
        if (question.contains(QuestionConstants.CRITICAL_POINTS)) {
            speakAnswer(QuestionConstants.answers.get(3));
        } else if (question.contains(QuestionConstants.STACKED_BAR_CHART)) {
            speakAnswer(isStackedBarChart
                    ? AnswerConstants.STACKED_BAR_CHART_ANSWER_YES
                    : AnswerConstants.STACKED_BAR_CHART_ANSWER_NO);
        } else if (question.contains(QuestionConstants.DESCRIBE_THE_GRAPH)) {
            speakAnswer(QuestionConstants.answers.get(0));
        } else if (question.contains(QuestionConstants.TACTILE_ON_OFF)) {
            speakAnswer(shouldVibrate ? AnswerConstants.TACTILE_ANSWER_YES
                    : AnswerConstants.TACTILE_ANSWER_NO);
        } else if (question.contains(QuestionConstants.X_AND_Y)) {
            speakAnswer(QuestionConstants.answers.get(1) + QuestionConstants.answers.get(2));
        } else if (question.contains(QuestionConstants.TALLEST_BAR)) {
            calculateTallestBar();
            speakAnswer(QuestionConstants.answers.get(6));
        } else if (question.contains(QuestionConstants.SMALLEST_BAR)) {
            calculateSmallestBar();
            speakAnswer(QuestionConstants.answers.get(7));
        } else if (question.contains(QuestionConstants.BARS_HORIZONTALLY_VERTICALLY)) {
            speakAnswer(QuestionConstants.answers.get(5));
        } else if (question.contains(QuestionConstants.BARS_BELOW_ABOVE_AXIS)) {
            if (graphAxis == GRAPH_AXIS.ABOVE_AXIS) {
                speakAnswer(AnswerConstants.BARS_ABOVE_AXIS_ANSWER);
            } else if (graphAxis == GRAPH_AXIS.BELOW_AXIS) {
                speakAnswer(AnswerConstants.BARS_BELOW_AXIS_ANSWER);
            } else {
                speakAnswer(AnswerConstants.BARS_BELOW_ABOVE_AXIS_ANSWER);
            }
        } else if (question.contains(QuestionConstants.BARS_CONTINUOUS)) {
            speakAnswer(QuestionConstants.answers.get(8));
        } else {
            speakAnswer(AnswerConstants.DFEAULT);
        }
    }

    private void calculateTallestBar() {
        for (BarEntry barEntry : barArraylist) {
            maxBarValue = Math.max(maxBarValue, (int) barEntry.getY());
        }
    }

    private void calculateSmallestBar() {
        for (BarEntry barEntry : barArraylist) {
            minBarValue = Math.min(minBarValue, (int) barEntry.getY());
        }
    }

    private void speakAnswer(@NonNull String answer) {
        Log.d(TAG, "Answer : " + answer);
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Test data to make bars
     */
    private void getData() {
        barArraylist = new ArrayList<>();
        barArraylist.add(new BarEntry(3f, 1.5f));
        barArraylist.add(new BarEntry(4f, 2.7f));
        barArraylist.add(new BarEntry(5f, 3.6f));
        barArraylist.add(new BarEntry(6f, 5.2f));
    }
}
