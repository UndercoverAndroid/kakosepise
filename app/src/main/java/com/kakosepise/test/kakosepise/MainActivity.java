package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button toastButton;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list_view);
        toastButton = findViewById(R.id.button_login);

        toastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entry newEntry = new Entry(1, "This is some content", "Test content", "content-test");
                DatabaseController db = new DatabaseController(MainActivity.this);
                boolean success = db.addEntry(newEntry);

                Toast.makeText(MainActivity.this, "Success= " + success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessage(View view) {


    }
}
