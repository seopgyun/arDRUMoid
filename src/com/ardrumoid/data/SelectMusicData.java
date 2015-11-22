package com.ardrumoid.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectMusicData implements Parcelable {

    public String url, title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
    }

    public static final Parcelable.Creator<SelectMusicData> CREATOR = new Parcelable.Creator<SelectMusicData>() {
        public SelectMusicData createFromParcel(Parcel in) {
            return new SelectMusicData(in);
        }

        public SelectMusicData[] newArray(int size) {
            return new SelectMusicData[size];
        }
    };

    public SelectMusicData() {
    }

    private SelectMusicData(Parcel in) {
        url = in.readString();
        title = in.readString();
    }
}
