package com.kakosepise.test.kakosepise;

import java.util.Deque;
import java.util.List;

public class LoaderThread implements Runnable{

    private DatabaseController m_db;
    private List<String> m_lines;
    private int m_start;
    private int m_end;
    public LoaderThread(DatabaseController _db, List<String> _lines, int _start, int _end) {
        m_db = _db;
        m_lines = _lines;
        m_start = _start;
        m_end = _end;
    }

    @Override
    public void run() {
        for (int i = m_start; i < m_end; i++) {
            m_db.getWritableDatabase();
            m_db.execCommand(m_lines.get(i).trim());
            m_db.close();
        }

    }
}
