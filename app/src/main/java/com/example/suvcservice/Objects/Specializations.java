package com.example.suvcservice.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Specializations implements Parcelable {
    public static Specializations specializations;
    int ID;
    String NameSpecialization;

    public Specializations(int ID, String nameSpecialization) {
        this.ID = ID;
        NameSpecialization = nameSpecialization;
    }

    protected Specializations(Parcel in) {
        ID = in.readInt();
        NameSpecialization = in.readString();
    }

    public static final Creator<Specializations> CREATOR = new Creator<Specializations>() {
        @Override
        public Specializations createFromParcel(Parcel in) {
            return new Specializations(in);
        }

        @Override
        public Specializations[] newArray(int size) {
            return new Specializations[size];
        }
    };

    public static Specializations getSpecializations() {
        return specializations;
    }

    public static void setSpecializations(Specializations specializations) {
        Specializations.specializations = specializations;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNameSpecialization() {
        return NameSpecialization;
    }

    public void setNameSpecialization(String nameSpecialization) {
        NameSpecialization = nameSpecialization;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(NameSpecialization);
    }
}
