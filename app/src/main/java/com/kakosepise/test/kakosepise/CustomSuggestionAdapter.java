package com.kakosepise.test.kakosepise;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class CustomSuggestionAdapter extends SuggestionsAdapter<Entry, SuggestionHolder> {


    public CustomSuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindSuggestionHolder(Entry suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getM_post_title());
        String contentPreview = suggestion.getM_post_content();
        contentPreview = contentPreview.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        holder.subTitle.setText(Html.fromHtml(contentPreview, Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
    }

    @Override
    public int getSingleViewHeight() {
        return 30;
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
        return new SuggestionHolder(view);
    }
}
