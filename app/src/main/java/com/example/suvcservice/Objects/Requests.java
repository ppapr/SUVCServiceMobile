package com.example.suvcservice.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Requests implements Parcelable {
    public static Requests requests;
    int ID;
    String Description;
    String DateCreateRequest;
    String DateExecuteRequest;
    String UserRequestName;
    String UserExecutorName;
    String StatusName;
    String PriorityName;
    String EquipmentName;
    String Location;
    int IDStatus;
    int IDPriority;
    int IDEquipment;
    int IDUserRequest;
    int IDExecutorRequest;

    public Requests(int ID, String description, String dateCreateRequest, String dateExecuteRequest,
                    String userRequestName, String userExecutorName, String statusName, String priorityName,
                    String equipmentName, String location, int IDStatus, int IDPriority, int IDEquipment,
                    int IDUserRequest, int IDExecutorRequest) {
        this.ID = ID;
        Description = description;
        DateCreateRequest = dateCreateRequest;
        DateExecuteRequest = dateExecuteRequest;
        UserRequestName = userRequestName;
        UserExecutorName = userExecutorName;
        StatusName = statusName;
        PriorityName = priorityName;
        EquipmentName = equipmentName;
        Location = location;
        this.IDStatus = IDStatus;
        this.IDPriority = IDPriority;
        this.IDEquipment = IDEquipment;
        this.IDUserRequest = IDUserRequest;
        this.IDExecutorRequest = IDExecutorRequest;
    }

    public Requests(String description, String dateCreateRequest, String dateExecuteRequest,
                    int IDStatus, int IDPriority, int IDEquipment,
                    int IDUserRequest, int IDExecutorRequest) {
        Description = description;
        DateCreateRequest = dateCreateRequest;
        DateExecuteRequest = dateExecuteRequest;
        this.IDStatus = IDStatus;
        this.IDPriority = IDPriority;
        this.IDEquipment = IDEquipment;
        this.IDUserRequest = IDUserRequest;
        this.IDExecutorRequest = IDExecutorRequest;
    }

    protected Requests(Parcel in) {
        ID = in.readInt();
        Description = in.readString();
        DateCreateRequest = in.readString();
        DateExecuteRequest = in.readString();
        UserRequestName = in.readString();
        UserExecutorName = in.readString();
        StatusName = in.readString();
        PriorityName = in.readString();
        EquipmentName = in.readString();
        Location = in.readString();
        IDStatus = in.readInt();
        IDPriority = in.readInt();
        IDEquipment = in.readInt();
        IDUserRequest = in.readInt();
        IDExecutorRequest = in.readInt();
    }

    public static final Creator<Requests> CREATOR = new Creator<Requests>() {
        @Override
        public Requests createFromParcel(Parcel in) {
            return new Requests(in);
        }

        @Override
        public Requests[] newArray(int size) {
            return new Requests[size];
        }
    };

    public static Requests getRequests() {
        return requests;
    }

    public static void setRequests(Requests requests) {
        Requests.requests = requests;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDateCreateRequest() {
        return DateCreateRequest;
    }

    public void setDateCreateRequest(String dateCreateRequest) {
        DateCreateRequest = dateCreateRequest;
    }

    public String getDateExecuteRequest() {
        return DateExecuteRequest;
    }

    public void setDateExecuteRequest(String dateExecuteRequest) {
        DateExecuteRequest = dateExecuteRequest;
    }

    public String getUserRequestName() {
        return UserRequestName;
    }

    public void setUserRequestName(String userRequestName) {
        UserRequestName = userRequestName;
    }

    public String getUserExecutorName() {
        return UserExecutorName;
    }

    public void setUserExecutorName(String userExecutorName) {
        UserExecutorName = userExecutorName;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getPriorityName() {
        return PriorityName;
    }

    public void setPriorityName(String priorityName) {
        PriorityName = priorityName;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getIDStatus() {
        return IDStatus;
    }

    public void setIDStatus(int IDStatus) {
        this.IDStatus = IDStatus;
    }

    public int getIDPriority() {
        return IDPriority;
    }

    public void setIDPriority(int IDPriority) {
        this.IDPriority = IDPriority;
    }

    public int getIDEquipment() {
        return IDEquipment;
    }

    public void setIDEquipment(int IDEquipment) {
        this.IDEquipment = IDEquipment;
    }

    public int getIDUserRequest() {
        return IDUserRequest;
    }

    public void setIDUserRequest(int IDUserRequest) {
        this.IDUserRequest = IDUserRequest;
    }

    public int getIDExecutorRequest() {
        return IDExecutorRequest;
    }

    public void setIDExecutorRequest(int IDExecutorRequest) {
        this.IDExecutorRequest = IDExecutorRequest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Description);
        dest.writeString(DateCreateRequest);
        dest.writeString(DateExecuteRequest);
        dest.writeString(UserRequestName);
        dest.writeString(UserExecutorName);
        dest.writeString(StatusName);
        dest.writeString(PriorityName);
        dest.writeString(EquipmentName);
        dest.writeString(Location);
        dest.writeInt(IDStatus);
        dest.writeInt(IDPriority);
        dest.writeInt(IDEquipment);
        dest.writeInt(IDUserRequest);
        dest.writeInt(IDExecutorRequest);
    }
}
