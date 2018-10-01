package com.android.example.accessguide;

import android.support.annotation.NonNull;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.HashMap;

class PlaceDetailItems implements OnCompleteListener<PlaceBufferResponse> {
    private final HashMap<String, PlaceDetailItem> PlaceDetailItems;
    private GeoDataClient Client;
    private int Index;
    private ObservableSnapshotArray<PlaceItem> PlaceItems;
    private PlaceListAdapter Adapter;

    PlaceDetailItems() {
        PlaceDetailItems = new HashMap<>();
    }

    PlaceDetailItem getItem(String id) {
        return PlaceDetailItems.get(id);
    }

    void putItems(ObservableSnapshotArray<PlaceItem> placeItems) {
        PlaceDetailItems.clear();
        PlaceItems = placeItems;
        Index = 0;
        Client.getPlaceById(PlaceItems.get(Index).getPlaceId()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
        if (task.isSuccessful()) {
            PlaceBufferResponse buffer = task.getResult();
            Place place = buffer.get(0);
            String id = place.getId();
            String name = (String) place.getName();
            String address = (String) place.getAddress();
            LatLng latLng = place.getLatLng();
            buffer.release();
            PlaceDetailItems.put(id, new PlaceDetailItem(name, address, latLng));
        }
        Index++;
        if (Index < PlaceItems.size()) {
            Client.getPlaceById(PlaceItems.get(Index).getPlaceId()).addOnCompleteListener(this);
        } else {
            Adapter.notifyDataSetChanged();
        }
    }

    void setClient(GeoDataClient client) {
        Client = client;
    }

    void setAdapter(PlaceListAdapter adapter) {
        Adapter = adapter;
    }
}
