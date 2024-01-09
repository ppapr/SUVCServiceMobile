package com.example.suvcservice.CommonComponents;

public class DataProvider {

    public DataProvider(){

    }
    public boolean checkResult(String result) {
        if (result != "null" && result != "") {
            return true;
        } else {
            return false;
        }
    }
}
