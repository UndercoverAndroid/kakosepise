package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button m_toastButton, m_viewAllButton;
    ListView m_list;
    ArrayAdapter m_customerArrayAdapter;
    DatabaseController m_db;
    static int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_list = findViewById(R.id.list_view);
        m_toastButton = findViewById(R.id.button_init);
        m_viewAllButton = findViewById(R.id.button2);
        m_db = new DatabaseController(MainActivity.this);
        final List<Entry> everyone = m_db.getAllEntries();
        showCustomersInListView();


        m_toastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entry newEntry = new Entry(++counter, "This is some content", "Test content", "content-test");
                boolean success = m_db.addEntry(newEntry);

                showCustomersInListView();
            }
        });

        m_viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomersInListView();

            }
        });
    }

    private void showCustomersInListView() {
        // Defining an adaptor that will fill out the list view
        m_customerArrayAdapter = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, m_db.getAllEntries());
        m_list.setAdapter(m_customerArrayAdapter);
    }

    public void sendMessage(View view) {


    }
}
