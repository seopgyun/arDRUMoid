package com.ardrumoid.data;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jinseop on 2015-11-15.
 */
public class SelectMusicData implements Parcelable {

    public String bgUrl, dataUrl, imgUrl, title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeString(bgUrl);
    	dest.writeString(dataUrl);
        dest.writeString(imgUrl);
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
    	bgUrl = in.readString();
    	dataUrl = in.readString();
    	imgUrl = in.readString();
        title = in.readString();
    }
}
