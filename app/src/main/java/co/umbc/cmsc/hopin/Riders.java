package co.umbc.cmsc.hopin;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arjun on 5/7/17.
 */

public class Riders implements Parcelable {


    String name;
    double latitude;
    double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    Riders(String name, double latitude, double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Riders(Parcel in){
        this.name = in.readString();

    }

    private void readFromParcel(Parcel in) {
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getName());
    }

    public static final Parcelable.Creator<Riders> CREATOR = new Parcelable.Creator<Riders>() {

        @Override
        public Riders createFromParcel(Parcel source) {
            return new Riders(source);
        }

        @Override
        public Riders[] newArray(int size) {
            return new Riders[size];
        }


    };
}
