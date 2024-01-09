package com.example.suvcservice.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Equipment implements Parcelable {
    int ID;
    String EquipmentName;
    String EquipmentDescription;
    String NetworkName;
    String InventoryName;
    int IDOwnerEquipment;
    int IDStatus;
    int LocationAuditorium;

    public Equipment(int ID, String equipmentName, String equipmentDescription,
                     String networkName, String inventoryName, int IDOwnerEquipment,
                     int IDStatus, int locationAuditorium) {
        this.ID = ID;
        EquipmentName = equipmentName;
        EquipmentDescription = equipmentDescription;
        NetworkName = networkName;
        InventoryName = inventoryName;
        this.IDOwnerEquipment = IDOwnerEquipment;
        this.IDStatus = IDStatus;
        LocationAuditorium = locationAuditorium;
    }

    public Equipment(String equipmentName, String equipmentDescription,
                     String networkName, String inventoryName, int IDOwnerEquipment) {
        EquipmentName = equipmentName;
        EquipmentDescription = equipmentDescription;
        NetworkName = networkName;
        InventoryName = inventoryName;
        this.IDOwnerEquipment = IDOwnerEquipment;
    }


    public Equipment(int ID, String equipmentName, String equipmentDescription,
                     String networkName, String inventoryName, int IDOwnerEquipment) {
        this.ID = ID;
        EquipmentName = equipmentName;
        EquipmentDescription = equipmentDescription;
        NetworkName = networkName;
        InventoryName = inventoryName;
        this.IDOwnerEquipment = IDOwnerEquipment;
    }

    protected Equipment(Parcel in) {
        ID = in.readInt();
        EquipmentName = in.readString();
        EquipmentDescription = in.readString();
        NetworkName = in.readString();
        InventoryName = in.readString();
        IDOwnerEquipment = in.readInt();
        IDStatus = in.readInt();
        LocationAuditorium = in.readInt();
    }

    public static final Creator<Equipment> CREATOR = new Creator<Equipment>() {
        @Override
        public Equipment createFromParcel(Parcel in) {
            return new Equipment(in);
        }

        @Override
        public Equipment[] newArray(int size) {
            return new Equipment[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getEquipmentDescription() {
        return EquipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        EquipmentDescription = equipmentDescription;
    }

    public String getNetworkName() {
        return NetworkName;
    }

    public void setNetworkName(String networkName) {
        NetworkName = networkName;
    }

    public String getInventoryName() {
        return InventoryName;
    }

    public void setInventoryName(String inventoryName) {
        InventoryName = inventoryName;
    }

    public int getIDOwnerEquipment() {
        return IDOwnerEquipment;
    }

    public void setIDOwnerEquipment(int IDOwnerEquipment) {
        this.IDOwnerEquipment = IDOwnerEquipment;
    }

    public int getIDStatus() {
        return IDStatus;
    }

    public void setIDStatus(int IDStatus) {
        this.IDStatus = IDStatus;
    }

    public int getLocationAuditorium() {
        return LocationAuditorium;
    }

    public void setLocationAuditorium(int locationAuditorium) {
        LocationAuditorium = locationAuditorium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(EquipmentName);
        dest.writeString(EquipmentDescription);
        dest.writeString(NetworkName);
        dest.writeString(InventoryName);
        dest.writeInt(IDOwnerEquipment);
        dest.writeInt(IDStatus);
        dest.writeInt(LocationAuditorium);
    }
}
