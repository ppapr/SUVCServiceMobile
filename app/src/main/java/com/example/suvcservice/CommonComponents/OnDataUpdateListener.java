package com.example.suvcservice.CommonComponents;

import com.example.suvcservice.Objects.Requests;

import java.util.List;

public interface OnDataUpdateListener {
    void onDataUpdated(List<Requests> updatedData);
}
