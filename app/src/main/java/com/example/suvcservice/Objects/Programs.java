package com.example.suvcservice.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Programs implements Parcelable {
    public static Programs programs;
    int ID;
    String NameProgram;
    String VersionProgram;
    String DescriptionProgram;
    int IDSpecialization;

    public Programs(int ID, String nameProgram, String versionProgram, String descriptionProgram, int IDSpecialization) {
        this.ID = ID;
        NameProgram = nameProgram;
        VersionProgram = versionProgram;
        DescriptionProgram = descriptionProgram;
        this.IDSpecialization = IDSpecialization;
    }

    protected Programs(Parcel in) {
        ID = in.readInt();
        NameProgram = in.readString();
        VersionProgram = in.readString();
        DescriptionProgram = in.readString();
        IDSpecialization = in.readInt();
    }

    public static final Creator<Programs> CREATOR = new Creator<Programs>() {
        @Override
        public Programs createFromParcel(Parcel in) {
            return new Programs(in);
        }

        @Override
        public Programs[] newArray(int size) {
            return new Programs[size];
        }
    };

    public static Programs getPrograms() {
        return programs;
    }

    public static void setPrograms(Programs programs) {
        Programs.programs = programs;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNameProgram() {
        return NameProgram;
    }

    public void setNameProgram(String nameProgram) {
        NameProgram = nameProgram;
    }

    public String getVersionProgram() {
        return VersionProgram;
    }

    public void setVersionProgram(String versionProgram) {
        VersionProgram = versionProgram;
    }

    public String getDescriptionProgram() {
        return DescriptionProgram;
    }

    public void setDescriptionProgram(String descriptionProgram) {
        DescriptionProgram = descriptionProgram;
    }

    public int getIDSpecialization() {
        return IDSpecialization;
    }

    public void setIDSpecialization(int IDSpecialization) {
        this.IDSpecialization = IDSpecialization;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(NameProgram);
        dest.writeString(VersionProgram);
        dest.writeString(DescriptionProgram);
        dest.writeInt(IDSpecialization);
    }
}
