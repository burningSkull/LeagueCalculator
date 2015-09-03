package com.northernthrone.leaguecalculator;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String name;
    private final List<String> players = new ArrayList<>();
    private final List<Match> matches = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPlayers() {
        return players;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public Group(String name) {
        this.name = name;
    }
}
