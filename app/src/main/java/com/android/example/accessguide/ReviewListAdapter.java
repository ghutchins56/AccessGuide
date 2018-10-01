package com.android.example.accessguide;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseUser;

class ReviewListAdapter extends FirebaseRecyclerAdapter<Review, ReviewHolder>  {

    ReviewListAdapter(@NonNull FirebaseRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder, int position,
            @NonNull final Review model) {
        holder.getTextView().setText(model.getText());
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item,
                parent, false);
        return new ReviewHolder(view);
    }

    Review getReview(FirebaseUser user) {
        ObservableSnapshotArray<Review> reviews = getSnapshots();
        int index = 0;
        int size = reviews.size();
        Review review = null;
        while (index < size) {
            review = reviews.get(index);
            if (review.getUserId().equals(user.getUid())) index = size;
            else index++;
        }
        return review;
    }
}
