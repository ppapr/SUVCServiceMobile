package com.example.suvcservice.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SparesEquipment implements Parcelable {
    int ID;
    String SpareName;
    String Equipment;
    int IDEquipment;

    public SparesEquipment(int ID, String spareName, String equipment, int IDEquipment) {
        this.ID = ID;
        SpareName = spareName;
        Equipment = equipment;
        this.IDEquipment = IDEquipment;
    }
    public SparesEquipment(String spareName, int IDEquipment) {
        SpareName = spareName;
        this.IDEquipment = IDEquipment;
    }

    protected SparesEquipment(Parcel in) {
        ID = in.readInt();
        SpareName = in.readString();
        Equipment = in.readString();
        IDEquipment = in.readInt();
    }

    public static final Creator<SparesEquipment> CREATOR = new Creator<SparesEquipment>() {
        @Override
        public SparesEquipment createFromParcel(Parcel in) {
            return new SparesEquipment(in);
        }

        @Override
        public SparesEquipment[] newArray(int size) {
            return new SparesEquipment[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSpareName() {
        return SpareName;
    }

    public void setSpareName(String spareName) {
        SpareName = spareName;
    }

    public String getEquipment() {
        return Equipment;
    }

    public void setEquipment(String equipment) {
        Equipment = equipment;
    }

    public int getIDEquipment() {
        return IDEquipment;
    }

    public void setIDEquipment(int IDEquipment) {
        this.IDEquipment = IDEquipment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(SpareName);
        dest.writeString(Equipment);
        dest.writeInt(IDEquipment);
    }
}
