package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStream;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final Handler handler = new Handler();

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    private ArrayList text;

    private Socket s = null;
    private OutputStream out = null;
    private PrintWriter output = null;
    private BufferedReader input = null;
    public static String st;

    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSend = (Button) findViewById(R.id.sendRequest);

        mTextViewReplyFromServer = findViewById(R.id.serverReply);
        textview = findViewById(R.id.textView);

        buttonSend.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.sendRequest:
                sendMessage(text.toString());
                break;
        }
    }

    private void sendMessage(final String msg){
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (s == null){
                        s = new Socket("192.168.43.114", 80);
                        out = s.getOutputStream();
                        output = new PrintWriter(out);
                        input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    }

                    output.println(msg);
                    output.flush();

                    String stmp = input.readLine();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (stmp.trim().length() != 0) {
                                st = stmp;
                            }
                        }
                    });
                    //output.close();
                    //out.close();
                    //s.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        thread.start();
    }

    public void displayServerReply(View view){
        mTextViewReplyFromServer.setText(st);
    }

    public void GoogleVoice(View view){

        Intent Google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        Google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        Google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        Google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "what can i do for you");                                  //设置
        startActivityForResult(Google_intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){                                                                                                    //拿到Text
            case 1:
                if (resultCode == RESULT_OK && data!= null) {
                    text = data.getCharSequenceArrayListExtra(RecognizerIntent.EXTRA_RESULTS);                                                         //应该可以打出来
                    textview.setText(text.toString());
                }
        }
    }

}