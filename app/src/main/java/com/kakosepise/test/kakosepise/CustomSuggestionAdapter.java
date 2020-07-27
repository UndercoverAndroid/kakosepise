package com.kakosepise.test.kakosepise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class CustomSuggestionAdapter extends SuggestionsAdapter<Entry, SuggestionHolder> {


    public CustomSuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(Entry suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getM_post_title());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
        return new SuggestionHolder(view);
    }
}
