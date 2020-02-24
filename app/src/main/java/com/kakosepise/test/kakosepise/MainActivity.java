package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void sendMessage(View view)
    {
        EditText editMessage=(EditText)findViewById(R.id.search);
        TextView textView = (TextView) findViewById(R.id.textView);

        String messageString=editMessage.getText().toString();

        textView.setText(messageString);

        editMessage.setText("");

    }
}
