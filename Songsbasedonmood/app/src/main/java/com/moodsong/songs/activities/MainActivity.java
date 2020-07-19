package com.moodsong.songs.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pyramid;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.moodsong.songs.R;
import com.projects.alshell.vokaturi.Emotion;
import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;



import es.dmoral.toasty.Toasty;
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

        ImageView imageViewInfo = findViewById(R.id.imageViewInfo);
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


        ApiImageCalls();
    }


 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.darkTheme:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                }
                // Update the text view text style

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

    //zadeklarowanie uprawnień
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


        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();
    }


    private void StartVoiceAlgorithm() {

        try {

            vokaturi = Vokaturi.getInstance(getApplicationContext());
            vokaturi.startListeningForSpeech();
            Toasty.info(MainActivity.this, "Please speak and when you finish, press pause button", Toast.LENGTH_SHORT, true).show();


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


            double neutrality = emotionProbabilities.Neutrality;
            double happiness = emotionProbabilities.Happiness;
            double sadness = emotionProbabilities.Sadness;
            double anger = emotionProbabilities.Anger;
            double fear = emotionProbabilities.Fear;


            emotionProbabilities.scaledValues(5);
            Emotion capturedEmotion = Vokaturi.extractEmotion(emotionProbabilities);
            //     Toast.makeText(this, capturedEmotion.toString(), Toast.LENGTH_LONG).show();


            ViewGroup viewGroup = findViewById(android.R.id.content);

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_chart, viewGroup, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            //pyramid wykres

            AnyChartView anyChartView = alertDialog.findViewById(R.id.piechart);

            String[] moods = {"Neutrality", "Happiness", "Sadness", "Anger", "Fear"};
            double[] values = {neutrality, happiness, sadness, anger, fear};


            Pyramid column = AnyChart.pyramid();
            List<DataEntry> dataEntries = new ArrayList<>();
            for (int i = 0; i < moods.length; i++) {

                dataEntries.add(new ValueDataEntry(moods[i], values[i]));
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


    private void ApiImageCalls() {

        ImageView imageNeutral = findViewById(R.id.imageNeutral);
        ImageView imageHappy = findViewById(R.id.imageHappy);
        ImageView imageSad = findViewById(R.id.imageSad);
        ImageView imageAngry = findViewById(R.id.imageAngry);
        ImageView imageFear = findViewById(R.id.imageFear);


        imageNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HappyFunction();

            }
        });

        imageSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageAngry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageFear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void HappyFunction() {


        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.config_dialog, viewGroup, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();






                 //numberOfPropositionsSpinner
                 SearchableSpinner NumberOfTrack = alertDialog.findViewById(R.id.spinnerPropositionPerPage);
                 NumberOfTrack.setTitle("Set number of propositions on page");
                 Integer[] itemsPropositionsSpinner = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
                 ArrayAdapter<Integer> arrayAdapterPropositionsSpinner = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, itemsPropositionsSpinner);
                 NumberOfTrack.setAdapter(arrayAdapterPropositionsSpinner);

                 NumberOfTrack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                         Integer selectedTracks = Integer.valueOf(parent.getItemAtPosition(position).toString());

               /* Intent intentNumberOfTracks= new Intent(MainActivity.this,HappySongActivity.class);
                intentNumberOfTracks.putExtra("NumberofTracks",selectedTracks);
                startActivity(intentNumberOfTracks);*/


                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> parent) {

                     }
                 });


                 //yearSpinner
                 SearchableSpinner yearSpinner = alertDialog.findViewById(R.id.spinnerYear);
                 yearSpinner.setTitle("Set release year");
                 Integer[] itemsYear = {2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010, 2009, 2008, 2007, 2006, 2005, 2004, 2003, 2002, 2001, 2000, 1999, 1998, 1997, 1996, 1995, 1994, 1993, 1992, 1991, 1990, 1989, 1988, 1987, 1986, 1985, 1984, 1983, 1982, 1981, 1980, 1979, 1978, 1977, 1976, 1975, 1974, 1973, 1972, 1971, 1970, 1969, 1968, 1967, 1966, 1965, 1964, 1963, 1962, 1961, 1960, 1959, 1958, 1957, 1956, 1955, 1954, 1953, 1952, 1951, 1950, 1949, 1948, 1947, 1946, 1945, 1944, 1943, 1942, 1941, 1940, 1939, 1938, 1937, 1936, 1935, 1934, 1933, 1932, 1931, 1930};
                 ArrayAdapter<Integer> arrayAdapterYear = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, itemsYear);
                 yearSpinner.setAdapter(arrayAdapterYear);

                 yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                         Integer selectedYear = Integer.valueOf(parent.getItemAtPosition(position).toString());

               /* Intent intentYear= new Intent(MainActivity.this,HappySongActivity.class);
                intentYear.putExtra("Year",selectedYear);
                startActivity(intentYear);*/




                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> parent) {

                     }
                 });


                 //Genre
                 SearchableSpinner genreSpinner = alertDialog.findViewById(R.id.spinnerGenre);
                 genreSpinner.setTitle("Set genre");
                 String[] itemsGenre = {"Pop", "Electronic", "Funk/Soul"};
                 final ArrayAdapter<String> arrayAdapterGenre = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, itemsGenre);
                 genreSpinner.setAdapter(arrayAdapterGenre);


                 genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                         String selectedGenre = parent.getItemAtPosition(position).toString();


                         Intent intentGenre= new Intent(MainActivity.this,HappySongActivity.class);
                         intentGenre.putExtra("Genre",selectedGenre);
                         startActivity(intentGenre);



                     }




                     @Override
                     public void onNothingSelected(AdapterView<?> parent) {

                     }
                 });


             }


}











