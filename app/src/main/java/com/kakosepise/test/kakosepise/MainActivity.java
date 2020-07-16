package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button m_toastButton, m_viewAllButton;
    ListView m_list;
    static int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_list = findViewById(R.id.list_view);
        m_toastButton = findViewById(R.id.button_init);
        m_viewAllButton = findViewById(R.id.button2);

        m_toastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entry newEntry = new Entry(++counter, "This is some content", "Test content", "content-test");
                DatabaseController db = new DatabaseController(MainActivity.this);
                boolean success = db.addEntry(newEntry);

                Toast.makeText(MainActivity.this, "Success = " + success, Toast.LENGTH_SHORT).show();
            }
        });

        m_viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseController db = new DatabaseController(MainActivity.this);
                List<Entry> everyone = db.getAllEntries();

                Toast.makeText(MainActivity.this, everyone.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessage(View view) {


    }
}
