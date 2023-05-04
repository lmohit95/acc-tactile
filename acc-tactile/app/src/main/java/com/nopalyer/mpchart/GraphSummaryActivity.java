package com.nopalyer.mpchart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GraphSummaryActivity extends AppCompatActivity {

    private Button next, back;
    private TextView question;
    private EditText userInput;
    private int genericQuestionsIndex = 0, barChartQuestionsIndex = 0, lineChartQuestionsIndex = 0;
    private CHART_TYPE chartType;
    private int globalIndex = 0;

    enum CHART_TYPE {
        BAR,
        LINE,
        PIE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_summary);
        init();
    }

    private void init() {
        chartType = CHART_TYPE.BAR;
        genericQuestionsIndex = 0;
        barChartQuestionsIndex = 0;
        lineChartQuestionsIndex = 0;
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        userInput = findViewById(R.id.userInput);
        question = findViewById(R.id.question);

        question.setText(genericQuestions[genericQuestionsIndex]);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionConstants.answers.add(globalIndex, userInput.getText().toString());
                globalIndex++;
                userInput.setText("");
                if (genericQuestionsIndex == genericQuestions.length - 1) {

                    if (chartType == CHART_TYPE.BAR && barChartQuestionsIndex <= barChartQuestions.length - 1) {
                        question.setText(barChartQuestions[barChartQuestionsIndex]);
                        barChartQuestionsIndex++;
                    } else if (chartType == CHART_TYPE.LINE && lineChartQuestionsIndex <= lineChartQuestions.length - 1) {
                        question.setText(lineChartQuestions[lineChartQuestionsIndex]);
                        lineChartQuestionsIndex++;
                    } else {
                        finish();
                    }

                } else {
                    genericQuestionsIndex++;
                    question.setText(genericQuestions[genericQuestionsIndex]);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalIndex--;
                if (genericQuestionsIndex == 0) finish();
                if (genericQuestionsIndex > 0 && (barChartQuestionsIndex == 0 && lineChartQuestionsIndex == 0)) {
                    genericQuestionsIndex--;
                    question.setText(genericQuestions[genericQuestionsIndex]);
                } else if (barChartQuestionsIndex > 0) {
                    barChartQuestionsIndex--;
                    question.setText(barChartQuestions[barChartQuestionsIndex]);
                } else if (lineChartQuestionsIndex > 0) {
                    lineChartQuestionsIndex--;
                    question.setText(lineChartQuestions[lineChartQuestionsIndex]);
                }
            }
        });
    }

    private String[] genericQuestions = {
            "Provide a summary of the visualization",
            "What does X-axis represent?",
            "What does Y-axis represent?",
            "What are the critical points in the graph"
    };

    private String[] barChartQuestions = {
            "Is the visualization a stacked bar chart?",
            "Are the bars placed vertically?",
            "What is the magnitude of the tallest bar?",
            "What is the magnitude of the lowest bar?",
            "Are the bars continuous?"
    };

    private String[] lineChartQuestions = {
            "Are the lines in the visualization solid or dashed?",
            "What is the value of the highest point?",
            "What is the value of the lowest point?"
    };
}