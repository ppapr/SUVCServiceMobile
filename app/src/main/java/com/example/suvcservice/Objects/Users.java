package com.example.suvcservice.Objects;

import android.content.Context;
import android.content.SharedPreferences;

public class Users {

    public static Users user;
    int ID;
    String Name;
    String Surname;
    String MiddleName;
    String Login;
    String Password;
    int IDRole;

    public Users(int ID, String Name, String Surname, String MiddleName, String Login, String Password, int IDRole) {
        this.ID = ID;
        this.Name = Name;
        this.Surname = Surname;
        this.MiddleName = MiddleName;
        this.Login = Login;
        this.Password = Password;
        this.IDRole = IDRole;
    }

    public static void saveSystemBasket(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SaveSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putInt("ID", user.getId()).apply();
        sharedPreferencesEditor.putString("Name", user.getName()).apply();
        sharedPreferencesEditor.putString("Surname", user.getSurname()).apply();
        sharedPreferencesEditor.putString("MiddleName", user.getMiddleName()).apply();
        sharedPreferencesEditor.putString("Login", user.getLogin()).apply();
        sharedPreferencesEditor.putString("Password", user.getPassword()).apply();
        sharedPreferencesEditor.putInt("IDRole", user.getIDRole()).apply();
    }

    public static void loadSystemBasket(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SaveSettings", Context.MODE_PRIVATE);
        user = new Users(
                sharedPreferences.getInt("ID", 0),
                sharedPreferences.getString("Name", "null"),
                sharedPreferences.getString("Surname", "null"),
                sharedPreferences.getString("MiddleName", "null"),
                sharedPreferences.getString("Login", "null"),
                sharedPreferences.getString("Password", "null"),
                sharedPreferences.getInt("IDRole", 0));
    }

    public static void logoutUser(Context context){
        user = new Users(
                0,
                "null",
                "null",
                "null",
                "null",
                "null",
                0
        );
        saveSystemBasket(context);
    }

    public static boolean checkUser(){
        if(user.getId() == 0)
            return false;
        else
            return true;
    }
    public int getId() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    public int getIDRole() { return IDRole; }

    public void setName(String name) {
        this.Name = name;
    }

    public void setSurname(String surname) {
        this.Surname = surname;
    }

    public void setMiddleName(String middleName) {
        this.MiddleName = middleName;
    }

    public void setLogin(String login) {
        this.Login = login;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}
