package com.android.example.accessguide;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

class PlaceDetailItem implements Parcelable {
    private final String Name;
    private String Address;
    private LatLng LatLng;

    PlaceDetailItem(String name, String address, LatLng latLng) {
        Name = name;
        Address = address;
        LatLng = latLng;
    }

    private PlaceDetailItem(Parcel in) {
        Name = in.readString();
        Address = in.readString();
        LatLng = in.readParcelable(com.google.android.gms.maps.model.LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Address);
        dest.writeParcelable(LatLng, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceDetailItem> CREATOR = new Creator<PlaceDetailItem>() {
        @Override
        public PlaceDetailItem createFromParcel(Parcel in) {
            return new PlaceDetailItem(in);
        }

        @Override
        public PlaceDetailItem[] newArray(int size) {
            return new PlaceDetailItem[size];
        }
    };

    String getName() {
        return Name;
    }

    String getAddress() {
        return Address;
    }

    LatLng getLatLng() {
        return LatLng;
    }
}
