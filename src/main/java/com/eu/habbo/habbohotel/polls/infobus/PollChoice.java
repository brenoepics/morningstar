package com.eu.habbo.habbohotel.polls.infobus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PollChoice {

    private final int id;


    private final String name;


    private int votes;


    public PollChoice(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.votes = 0;
    }

    public PollChoice(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.votes = 0;
    }

    public PollChoice(Integer id, String name, Integer votes) {
        this.id = id;
        this.name = name;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
