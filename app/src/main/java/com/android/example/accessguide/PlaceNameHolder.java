package com.android.example.accessguide;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class PlaceNameHolder extends RecyclerView.ViewHolder {
    private final TextView TextViewName;
    private View ItemView;

    PlaceNameHolder(View itemView) {
        super(itemView);
        ItemView = itemView;
        TextViewName = itemView.findViewById(R.id.item_text_view_name);
    }

    TextView getTextView() {
        return TextViewName;
    }

    View  getItemView() {
       return ItemView;
    }
}
