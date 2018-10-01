package com.android.example.accessguide;

import android.os.Parcel;
import android.os.Parcelable;

class PlaceItem implements Parcelable {
    private String PlaceItemId;
    private String PlaceId;

    PlaceItem() {}

    PlaceItem(String placeItemId, String placeId) {
        PlaceItemId = placeItemId;
        PlaceId = placeId;
    }

    private PlaceItem(Parcel in) {
        PlaceItemId = in.readString();
        PlaceId = in.readString();
    }

    public static final Creator<PlaceItem> CREATOR = new Creator<PlaceItem>() {
        @Override
        public PlaceItem createFromParcel(Parcel in) {
            return new PlaceItem(in);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

    public String getPlaceItemId() {
        return PlaceItemId;
    }

    void setPlaceItemId(String placeItemId) {
        PlaceItemId = placeItemId;
    }

    public String getPlaceId() {
        return PlaceId;
    }

    void setPlaceId(String placeId) {
        PlaceId = placeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(PlaceItemId);
        parcel.writeString(PlaceId);
    }
}

