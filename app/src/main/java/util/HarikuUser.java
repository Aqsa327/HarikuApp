package util;

import android.app.Application;

public class HarikuUser extends Application {
    private String username;
    private String userID;

    private static HarikuUser instance;

    // Mengikuti Singleton design pattern

    public static HarikuUser getInstance() {
        if (instance == null) {
            instance = new HarikuUser();
        }
        return instance;
    }

    public HarikuUser() {
        // Konstruktor kosong
    }

    //Getter
    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    //Setter
    public void setUsername (String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
