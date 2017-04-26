package tr.edu.iyte.MyMessenger;

import android.app.Activity;
import tr.edu.iyte.AsmSimulator.AsmSimulator;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity {
    private static final String TAG5 = "Test Message";
    private static AsmSimulator asmSim;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;
    private Button startBtn;
    private Button stopBtn;
    private Button playBtn;
    private Button stopPlayBtn;
    private TextView text;
    public static String mediaRecorder="MEDIA_RECORDER";
    Button sendSmsButton;
    Button callphoneButton;
    EditText textPhoneNo;
    EditText textSMS;
    String phoneNo;
    String sms;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        text = (TextView) findViewById(R.id.text1);
        outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/myRecording.3gpp";

        asmSim= AsmSimulator.getInstance(getApplicationContext());
        Log.i(TAG5,"Audio Recorder1");
        startBtn = (Button) findViewById(R.id.start);

        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                start(v);
            }
        });

        stopBtn = (Button) findViewById(R.id.stop);
        stopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stop(v);
            }
        });

        playBtn = (Button) findViewById(R.id.play);

        playBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                play(v);
            }
        });

        stopPlayBtn = (Button) findViewById(R.id.stopPlay);

        stopPlayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopPlay(v);
            }
        });
        sendSmsButton = (Button) findViewById(R.id.sendSMSbutton);
        callphoneButton= (Button) findViewById(R.id.callPhonebutton);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

        sendSmsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                phoneNo = textPhoneNo.getText().toString();
                sms = textSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });


        callphoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNo));
                startActivity(callIntent);
            }
        });
    }


    public void start(View view) {
       // long startTime = System.currentTimeMillis();
        Object locationManager = asmSim.getCaarbacSystemService("LOCATION");
        Object recorder=asmSim.getCaarbacSystemService(mediaRecorder);

       // long stopTime = System.currentTimeMillis();
       // Log.i(TAG5,"Time Delay"+String.valueOf(stopTime - startTime));

        if(recorder!=null){
            myRecorder = (MediaRecorder)recorder;
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myRecorder.setOutputFile(outputFile);

            try {
                myRecorder.prepare();
                myRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

           // text.setText("Recording Point: Recording");
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);

            Toast.makeText(getBaseContext(), "Recording...",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getBaseContext(), "Media Recorder Not Found",
                    Toast.LENGTH_LONG).show();

        }

    }

    public void stop(View view) {

        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            text.setText("Recording Point: Stop recording");
            Toast.makeText(getBaseContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void play(View view) {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();
            playBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
            text.setText("Recording Point: Playing");
            Toast.makeText(getBaseContext(), "Start play the recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay(View view) {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                text.setText("Recording Point: Stop playing");
                Toast.makeText(getBaseContext(), "Stop playing the recording...",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
