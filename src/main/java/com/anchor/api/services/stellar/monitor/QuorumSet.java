package com.anchor.api.services.stellar.monitor;

import java.util.ArrayList;

public class QuorumSet {
    private float threshold;
    ArrayList<Object> validators = new ArrayList<Object>();
    ArrayList<Object> innerQuorumSets = new ArrayList<Object>();


    // Getter Methods

    public float getThreshold() {
        return threshold;
    }

    // Setter Methods

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
}
