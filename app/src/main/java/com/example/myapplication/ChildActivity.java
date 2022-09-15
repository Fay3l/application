package com.example.myapplication;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.telephony.MbmsDownloadSession;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class ChildActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    TextView textview;
    EditText edittext;
    private Button buttonspeech;
    private TextToSpeech tts;
    public static final String PARAM_REPONSE = "MOT_REPONSE";
    private GestureDetector gesture;

    public  void speakFrenchAndEnglish(String Texttospeech , Locale locale)
    {
        tts.setLanguage(locale);
        tts.speak(Texttospeech, TextToSpeech.QUEUE_ADD, null,"");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);


        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        Intent intent = getIntent();
        final String mot = intent.getStringExtra(MainActivity.PARAM_SOURCE);
        textview = (TextView) findViewById(R.id.TextViewTitreMotSource);
        edittext = (EditText) findViewById(R.id.mot_edittextChild);
        gesture = new GestureDetector(this,this);

        textview.append("\t"+mot);


        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.FRENCH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        Translator frenchenglishTranslator = Translation.getClient(options);

        TranslateRemoteModel englishModel =
                new TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build();

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        //modelManager.download(englishModel, conditions);
        frenchenglishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {

                                          }
                                      }

                )
                .addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {

                                                  }
                                              });
        frenchenglishTranslator.translate(mot)
        .addOnSuccessListener(new OnSuccessListener<String>() {
                                  @Override
                                  public void onSuccess(String s) {
                                        edittext.setText(s);
                                      //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                                  }
                              }

        )
                .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                          }
                                      }
                );
        //edittext.setText(frenchenglishTranslator.translate(mot).toString());
        getLifecycle().addObserver(frenchenglishTranslator);

        Button btnRetour =(Button)findViewById(R.id.buttonRetourChild);
        btnRetour.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent retour = new Intent();
                retour.putExtra(ChildActivity.PARAM_REPONSE, edittext.getText().toString() );
                setResult(MainActivity.RESULT_OK,retour);
                               finish(); // fin de l’activité
            }

        });
        buttonspeech = findViewById(R.id.btnTraduireChild);
        tts = new TextToSpeech(this,new TextToSpeech.OnInitListener(){
        @Override
        public void onInit ( int status){
            if (status == TextToSpeech.SUCCESS) {
                int resInit;
                String langueConfig = Locale.getDefault().getLanguage();
                if (langueConfig.equals(Locale.ENGLISH.getLanguage()))
                    resInit = tts.setLanguage(Locale.ENGLISH);
                else
                    resInit = tts.setLanguage(Locale.FRENCH);
                if (resInit == TextToSpeech.LANG_MISSING_DATA ||
                        resInit == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.d("TTS" , "Langage non supporté...");

                    else
                    buttonspeech.setEnabled(true);
            }
                else{
                Log.d("TTS" , "INIT ERREUR...");
            }

        }
        });


        buttonspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speakFrenchAndEnglish("Traduction en anglais du mot "+mot+" est",Locale.FRENCH);
                speakFrenchAndEnglish(edittext.getText().toString(),Locale.ENGLISH);


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null)
            tts.shutdown();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
        if (e1.getX()>e2.getX()&&Math.abs(e1.getX()-e2.getX())>200)
        {
            Intent retour = new Intent();
            retour.putExtra(ChildActivity.PARAM_REPONSE, edittext.getText().toString() );
            setResult(MainActivity.RESULT_OK,retour);
            finish();
        }
        return true;
    }
}
