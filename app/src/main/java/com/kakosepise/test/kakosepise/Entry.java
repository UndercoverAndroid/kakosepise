package com.kakosepise.test.kakosepise;

import org.w3c.dom.Entity;

public class Entry {
    private int m_ID;
    private String m_post_content;
    private String m_post_title;
    private String m_post_name;

    public Entry(int m_ID, String m_post_content, String m_post_title, String m_post_name) {
        this.m_ID = m_ID;
        this.m_post_content = m_post_content;
        this.m_post_title = m_post_title;
        this.m_post_name = m_post_name;
    }

    // Partial copy constructor, leaves ID unchanged
    public void copyContent(Entry _other) {
        this.m_post_content = _other.getM_post_content();
        this.m_post_name = _other.getM_post_name();
        this.m_post_title = _other.getM_post_title();
    }

    public Entry() {
    }

    public int getM_ID() {
        return m_ID;
    }

    public void setM_ID(int m_ID) {
        this.m_ID = m_ID;
    }

    public String getM_post_content() {
        return m_post_content;
    }

    public void setM_post_content(String m_post_content) {
        this.m_post_content = m_post_content;
    }

    public String getM_post_title() {
        return m_post_title;
    }

    public void setM_post_title(String m_post_title) {
        this.m_post_title = m_post_title;
    }

    public String getM_post_name() {
        return m_post_name;
    }

    public void setM_post_name(String m_post_name) {
        this.m_post_name = m_post_name;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "m_ID=" + m_ID +
                ", m_post_content='" + m_post_content + '\'' +
                ", m_post_title='" + m_post_title + '\'' +
                ", m_post_name='" + m_post_name + '\'' +
                '}';
    }
}
