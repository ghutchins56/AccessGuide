package com.android.example.accessguide;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

class ReviewHolder extends ViewHolder {
    private final TextView TextViewReview;
    private View ItemView;

    ReviewHolder(View itemView) {
        super(itemView);
        ItemView = itemView;
        TextViewReview = itemView.findViewById(R.id.text_view_review);
    }

    TextView getTextView() {
        return TextViewReview;
    }

    View getItemView() {
        return ItemView;
    }
}
