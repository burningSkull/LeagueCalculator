package com.northernthrone.leaguecalculator;

import java.util.List;

public class Season {

    private int seasonNumber;
    private List groups;

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public List getGroups() {
        return groups;
    }

    public void setGroups(List groups) {
        this.groups = groups;
    }

    public Season(int seasonNumber, List groups) {
        this.seasonNumber = seasonNumber;
        this.groups = groups;
    }
}
