package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private Button btnTraduire;
    private EditText Bonjour;
    private Button btnVocale;
    public static final int CODE_RETOUR_RECOGNIZER = 333;
    public static final String PARAM_SOURCE= "MOT_SOURCE";
public static final int CODE_REQUETE=1010;
public static final int CODE_RETOUR =222;
private BroadcastReceiver connexionReceiver;
private GestureDetector gesture;






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CODE_REQUETE) {
            if (resultCode==MainActivity.RESULT_OK)
            { Toast.makeText(this, data.getStringExtra(ChildActivity.PARAM_REPONSE), Toast.LENGTH_SHORT).show();
            }
        }
        switch(requestCode) {
            case (MainActivity.CODE_RETOUR):
            case(MainActivity.CODE_RETOUR_RECOGNIZER):
                switch(resultCode)
                {
                    case( Activity.RESULT_OK):
                        if (data != null)
                        {
                            ArrayList<String> mots =
                                    data.getStringArrayListExtra(
                                            RecognizerIntent.EXTRA_RESULTS);
                            if (mots.size()>0)
                                this.Bonjour.setText(mots.get(0));
                        } return;
                    case (Activity.RESULT_CANCELED):
                        Toast.makeText(this,"",
                                Toast.LENGTH_LONG).show();
                        return;
                }
        } }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTraduire = findViewById(R.id.buttonTraduire);
        Bonjour =findViewById(R.id.mot_edittext);
        btnVocale = findViewById(R.id.buttonReconnaissance);
        gesture = new GestureDetector(this,this);


        IntentFilter filtreConnectivity = new
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        connexionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm =
                        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null){
                    if(activeNetwork.isConnected()==true)
                    {
                        if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {Toast.makeText(MainActivity.this,
                                "Connecté à internet",
                                Toast.LENGTH_LONG).show();
                                }
                        else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){ Toast.makeText(MainActivity.this,
                                "Connecté à internet",
                                Toast.LENGTH_LONG).show(); }
                    }
                    else { Toast.makeText(MainActivity.this,
                            "Aucune connection internet",
                            Toast.LENGTH_LONG).show(); }
                }
                else { Toast.makeText(MainActivity.this,
                        "Aucune connection internet",
                        Toast.LENGTH_LONG).show(); }
            }
        };
        registerReceiver(connexionReceiver,filtreConnectivity);


        btnVocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        "fr-FR");
                try{
                    startActivityForResult(intent, CODE_RETOUR_RECOGNIZER);
                }
                catch(ActivityNotFoundException ex)
                {
                    Toast.makeText(MainActivity.this,
                            "Erreur",
                            Toast.LENGTH_LONG).show();
                }


            }
        });




        btnTraduire.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View v){

                Intent intent = new
                        Intent(MainActivity.this,ChildActivity.class);
               intent.putExtra(PARAM_SOURCE, Bonjour.getText().toString());
               startActivityForResult(intent,MainActivity.CODE_REQUETE);
          }

        });



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
        if (e1.getX()<e2.getX()&&Math.abs(e1.getX()-e2.getX())>200)
        {
            Intent intent = new
                    Intent(MainActivity.this,ChildActivity.class);
            intent.putExtra(PARAM_SOURCE, Bonjour.getText().toString());
            startActivityForResult(intent,MainActivity.CODE_REQUETE);
        }
        return true;
    }
}