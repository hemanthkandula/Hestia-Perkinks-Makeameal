package com.hemanthkandula.apps.perkinksmakeameal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.cloudoki.demo3.TF.ClassifierActivity;
import com.cloudoki.demo3.googlecloud.IResults;
import com.cloudoki.demo3.googlecloud.MicrophoneStreamRecognizeClient;
import com.cloudoki.demo3.ocr.MainActivity_ocr;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IResults {

    private IResults Self = this;
    private TextView textView;
    private static final int RC_OCR_CAPTURE = 9003;

    private Thread runner = new Thread() {

        public void run(){

            try {
                Log.d("Main Activity", "Start");
                MicrophoneStreamRecognizeClient client;
                client = new MicrophoneStreamRecognizeClient(getResources().openRawResource(R.raw.naviga), Self);
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_speech);
        textView = (TextView)findViewById(R.id.textView);

        // checking permissions
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            int RECORD_AUDIO = 666;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            int ACCESS_NETWORK_STATE = 333;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    ACCESS_NETWORK_STATE);
        }


        final Button startButton = (Button) findViewById(R.id.startStreaming);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            runner.start();
            }
        });

        final Button stop = (Button) findViewById(R.id.stopStreaming);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    Log.d("Main Activity", "Stop");
                    runner.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setText(String text){
        runOnUiThread(new Runnable() {

            String text;
            public Runnable set(String text) {
                this.text = text;
                return this;
            }

            @Override
            public void run() {

                if(textView != null){
                    textView.setText(text+textView.getText());

                }
            }

        }.set(text));
    }

    @Override
    public void onPartial(String text) {

        setText("Partial: "+text+"\n");
    }

    @Override
    public void onFinal(String text) {

        setText("Final: "+text+"\n");

        if((text.toLowerCase().contains("oven".toLowerCase())) || (text.toLowerCase().contains("stove".toLowerCase())))
        {        Intent i = new Intent (this, ClassifierActivity.class);

            i.putExtra("final", text);


            Log.d(this.getClass().toString(),text);


            startActivity(i);
onStop();

            finish();
        }



        if(text.toLowerCase().contains("start".toLowerCase()) || text.toLowerCase().contains("start".toLowerCase()) || text.toLowerCase().contains("start".toLowerCase())){


            Intent i = new Intent (this, MainActivity_ocr.class);

            i.putExtra("final", text);


            Log.d(this.getClass().toString()+"st",text);

            startActivity(i);


            finish();
        }







//
//        Intent intent = new Intent(this, OcrCaptureActivity.class);
//        intent.putExtra(OcrCaptureActivity.AutoFocus, 1);
//        intent.putExtra(OcrCaptureActivity.UseFlash, 0);
//
//        startActivityForResult(intent, RC_OCR_CAPTURE);
    }


    public static List<String> getWords(String text) {
        List<String> words = new ArrayList<String>();
        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                words.add(text.substring(firstIndex, lastIndex));
            }
        }

        return words;
    }
}
