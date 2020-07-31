package com.kakosepise.test.kakosepise;

import org.json.JSONException;
import org.json.JSONObject;

public class Entry {
    private int m_ID;
    private String m_post_content;
    private String m_post_date;
    private String m_post_title;
    private String m_post_name;

    public Entry(int _ID, String _post_content, String _post_title, String _post_name, String _post_date) {
        this.m_ID = _ID;
        this.m_post_content = _post_content;
        this.m_post_title = _post_title;
        this.m_post_name = _post_name;
        this.m_post_date = _post_date;
    }

    public Entry(JSONObject _jsonObject) throws JSONException {
        this.m_ID = (int) _jsonObject.get(DatabaseController.m_ID);
        this.m_post_content = (String) _jsonObject.get(DatabaseController.m_POST_CONTENT);
        this.m_post_title = (String) _jsonObject.get(DatabaseController.m_POST_TITLE);
        this.m_post_name = (String) _jsonObject.get(DatabaseController.m_POST_NAME);
        this.m_post_date = (String) _jsonObject.get(DatabaseController.m_POST_DATE);

    }


    // Partial copy constructor, leaves ID unchanged
    public void copyContent(Entry _other) {
        this.m_post_content = _other.getM_post_content();
        this.m_post_name = _other.getM_post_name();
        this.m_post_title = _other.getM_post_title();
        this.m_post_date = _other.getM_post_date();
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

    public String getM_post_date() {
        return m_post_date;
    }

    public void setM_post_date(String m_post_date) {
        this.m_post_date = m_post_date;
    }

    @Override
    public String toString() {
        return m_ID +
                " | " + m_post_title +
                " | " + m_post_name;
    }
}
