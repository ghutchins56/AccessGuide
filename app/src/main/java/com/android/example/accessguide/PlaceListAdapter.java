package com.android.example.accessguide;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

class PlaceListAdapter extends FirebaseRecyclerAdapter<PlaceItem, PlaceNameHolder>  {
    private PlaceDetailItems PlaceDetailItems;
    private RecyclerView RecyclerViewPlaceList;
    private ImageView ImageViewWheelchairRamp;
    private ImageView ImageViewPoweredByGoogle;
    private OnItemClickListener Listener;

    public interface OnItemClickListener {
        void onItemClicked(PlaceItem placeItem);
    }

    PlaceListAdapter(@NonNull FirebaseRecyclerOptions<PlaceItem> options) {
        super(options);
    }

    @Override
    public void onDataChanged() {
        if (getItemCount() == 0) {
            RecyclerViewPlaceList.setVisibility(View.INVISIBLE);
            ImageViewWheelchairRamp.setVisibility(View.VISIBLE);
            ImageViewPoweredByGoogle.setVisibility(View.INVISIBLE);
        } else {
            PlaceDetailItems.putItems(getSnapshots());
            RecyclerViewPlaceList.setVisibility(View.VISIBLE);
            ImageViewWheelchairRamp.setVisibility(View.INVISIBLE);
            ImageViewPoweredByGoogle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull PlaceNameHolder holder, int position,
            @NonNull final PlaceItem model) {
        PlaceDetailItem placeDetailItem = PlaceDetailItems.getItem(model.getPlaceId());
        String name;
        if (placeDetailItem == null) name = "";
        else name = PlaceDetailItems.getItem(model.getPlaceId()).getName();
        holder.getTextView().setText(name);
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listener.onItemClicked(model);
            }
        });
    }

    @NonNull
    @Override
    public PlaceNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list_item,
                parent, false);
        return new PlaceNameHolder(view);
    }

    void setPlaceDetailItems(PlaceDetailItems placeDetailItems) {
        PlaceDetailItems = placeDetailItems;
    }

    void setRecyclerViewPlaceList(RecyclerView view) {
        RecyclerViewPlaceList = view;
    }

    void setImageViewWheelchairRamp(ImageView view) {
        ImageViewWheelchairRamp = view;
    }

    void setImageViewPoweredByGoogle(ImageView view) {
        ImageViewPoweredByGoogle = view;
    }

    void setListener(OnItemClickListener listener) {
        Listener = listener;
    }


}
