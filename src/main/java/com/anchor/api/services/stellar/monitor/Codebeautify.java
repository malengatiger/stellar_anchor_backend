package com.anchor.api.services.stellar.monitor;

import java.util.ArrayList;

public class Codebeautify {
    private String time;
    ArrayList< QuorumSet > transitiveQuorumSet = new ArrayList < QuorumSet > ();
    ArrayList < Object > scc = new ArrayList < Object > ();
    ArrayList < NodeData > nodes = new ArrayList < NodeData > ();
    ArrayList < Object > organizations = new ArrayList < Object > ();
    Statistics statistics;


    // Getter Methods

    public String getTime() {
        return time;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    // Setter Methods

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatistics(Statistics statisticsObject) {
        this.statistics = statisticsObject;
    }
}

