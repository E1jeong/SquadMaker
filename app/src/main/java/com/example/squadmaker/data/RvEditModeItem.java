package com.example.squadmaker.data;

public class RvEditModeItem {

    private String playerName;
    private String playerBackNumber;

    public String getPlayerName() {

        return playerName ;
    }
    public String getPlayerBackNumber() {

        return playerBackNumber ;
    }

    public RvEditModeItem(String playerName, String playerBackNumber) {
        this.playerName = playerName;
        this.playerBackNumber = playerBackNumber;
    }


}
