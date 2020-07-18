package com.kakosepise.test.kakosepise;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class MainActivity extends AppCompatActivity {

    Button m_toastButton, m_viewAllButton;
    ListView m_list;
    ArrayAdapter m_customerArrayAdapter;
    DatabaseController m_db;
    static int counter = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_list = findViewById(R.id.list_view);
        m_toastButton = findViewById(R.id.button_init);
        m_viewAllButton = findViewById(R.id.button2);

        // Database initialization
        // Step 1 - We create an empty database
        m_db = new DatabaseController(MainActivity.this);

        // Step 2 - We fill it up with the rows from dataInit.sql in the asset folder
        updateDatabase();
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

        m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry clickedEntry = (Entry) adapterView.getItemAtPosition(position);
                m_db.deleteEntry(clickedEntry);
                showCustomersInListView();
                Toast.makeText(MainActivity.this, "You deleted an item", Toast.LENGTH_SHORT);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateDatabase() {
        // We skip the update if we already did the initial inserts
        if (!m_db.isFilled()) {
            List<String> lines = new ArrayList<>();
            lines = Collections.synchronizedList(lines);
            int numOfThreads = 8;

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(DatabaseController.m_INIT_PATH)));

                // Collect all sql insert statements line by line
                String nextSql;
                while ((nextSql = reader.readLine()) != null) {
                    lines.add(nextSql);
                    //m_db.execCommand(nextSql.trim());
                }
                // Calculate how many lines every thread should work on
                int numOflines = lines.size();
                int step = (int) (numOflines / (double) numOfThreads);
                Thread[] threads = new Thread[numOfThreads];
                // Divide threads by line number
                int currentThreadIndex=0;
                for (int i = 0; i < numOflines; i += step) {
                    if (i + step >= numOflines) {
                        threads[currentThreadIndex] = new Thread(new LoaderThread(m_db, lines, i, numOflines));
                    }else
                    {
                        threads[currentThreadIndex] = new Thread(new LoaderThread(m_db, lines, i, i + step));
                    }
                    threads[currentThreadIndex].start();
                    currentThreadIndex++;
                }
                for (int i = 0; i < numOfThreads; i++) {
                    threads[i].join();
                }
            } catch (IOException | InterruptedException e) {
                //TODO: Log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        }
    }

    private void showCustomersInListView() {
        // Defining an adaptor that will fill out the list view
        m_customerArrayAdapter = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, m_db.getAllEntries());
        m_list.setAdapter(m_customerArrayAdapter);
    }

    public void sendMessage(View view) {


    }
}
