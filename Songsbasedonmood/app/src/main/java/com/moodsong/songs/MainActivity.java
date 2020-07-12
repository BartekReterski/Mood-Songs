package com.moodsong.songs;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.CircularGauge;
import com.anychart.charts.Pie;
import com.anychart.charts.Pyramid;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.projects.alshell.vokaturi.Emotion;
import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

import java.util.ArrayList;
import java.util.List;


import umairayub.madialog.MaDialog;
import umairayub.madialog.MaDialogListener;

import static com.projects.alshell.vokaturi.Vokaturi.logD;

public class MainActivity extends AppCompatActivity {

    boolean showFirst = true;
    Vokaturi vokaturi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RuntimePermissions();


        final ImageView recordVoiceMood = findViewById(R.id.recordVoice);

        recordVoiceMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showFirst == true) {

                    recordVoiceMood.setImageResource(R.drawable.pause);

                    StartVoiceAlgorithm();

                    showFirst = false;
                } else {

                    recordVoiceMood.setImageResource(R.drawable.play);

                    StopVoiceAlgorithm();
                    showFirst = true;


                }


            }
        });

        ImageView imageViewInfo= findViewById(R.id.imageViewInfo);
        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaDialog.Builder(MainActivity.this)
                        .setTitle("Detect your voice")
                        .setMessage("This is only sample- reszte trzeba wymyslec  ")
                        .setPositiveButtonText("ok")
                        .setButtonOrientation(LinearLayout.HORIZONTAL)

                        .setPositiveButtonListener(new MaDialogListener() {
                            @Override
                            public void onClick() {

                            }
                        })

                        .build();
            }
        });


    }

    private void RuntimePermissions() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //    Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permissions denied", Toast.LENGTH_LONG).show();
            }
        };


        TedPermission.with(MainActivity.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();
    }


    private void StartVoiceAlgorithm() {

        try {

            vokaturi = Vokaturi.getInstance(getApplicationContext());
            vokaturi.startListeningForSpeech();


        } catch (VokaturiException exception) {
            //  Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
            System.out.println(exception.getMessage() + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        }
    }

    private void StopVoiceAlgorithm() {
        try {


            vokaturi = Vokaturi.getInstance(getApplicationContext());

            EmotionProbabilities emotionProbabilities = vokaturi.stopListeningAndAnalyze();
            logD("Neutrality: " + emotionProbabilities.Neutrality);
            logD("Happiness: " + emotionProbabilities.Happiness);
            logD("Sadness: " + emotionProbabilities.Sadness);
            logD("Anger: " + emotionProbabilities.Anger);
            logD("Fear: " + emotionProbabilities.Fear);


          double  neutrality = emotionProbabilities.Neutrality;
          double  happiness = emotionProbabilities.Happiness;
          double  sadness = emotionProbabilities.Sadness;
          double  anger = emotionProbabilities.Anger;
          double  fear = emotionProbabilities.Fear;




            emotionProbabilities.scaledValues(3);
            Emotion capturedEmotion = Vokaturi.extractEmotion(emotionProbabilities);
            Toast.makeText(this, capturedEmotion.toString(), Toast.LENGTH_LONG).show();





            ViewGroup viewGroup = findViewById(android.R.id.content);

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_chart, viewGroup, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();




            //pyramid wykres

            AnyChartView anyChartView= alertDialog.findViewById(R.id.piechart);

            String[] moods={"Neutrality","Happiness","Sadness","Anger","Fear"};
            double [] values= {neutrality,happiness,sadness,anger,fear};


            Pyramid column= AnyChart.pyramid();
            List<DataEntry> dataEntries= new ArrayList<>();
            for(int i=0; i<moods.length; i++){

                dataEntries.add(new ValueDataEntry(moods[i],values[i]));
            }

            column.data(dataEntries);
            column.title(capturedEmotion.toString());
            column.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER);
            anyChartView.setZoomEnabled(true);

            anyChartView.setChart(column);




            Button buttonOK = alertDialog.findViewById(R.id.buttonOk);
            buttonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "dasdsadsdsa", Toast.LENGTH_LONG).show();
                }
            });







        } catch (VokaturiException exception) {

            System.out.println(exception.getMessage() + "#############################################");
            Toast.makeText(getApplicationContext(), "Try again. Please speak louder", Toast.LENGTH_LONG).show();
        }


    }



}
