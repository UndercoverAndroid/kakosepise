package com.kakosepise.test.kakosepise;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestionHolder  extends RecyclerView.ViewHolder {

    protected TextView title;

    public SuggestionHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
    }
}
