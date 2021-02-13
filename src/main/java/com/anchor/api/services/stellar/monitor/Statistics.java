package com.anchor.api.services.stellar.monitor;

public class Statistics {
    private boolean activeInLastCrawl;
    private boolean overLoadedInLastCrawl;
    private boolean validatingInLastCrawl;
    private float active30DaysPercentage;
    private float overLoaded30DaysPercentage;
    private float validating30DaysPercentage;
    private float active24HoursPercentage;
    private float overLoaded24HoursPercentage;
    private float validating24HoursPercentage;
    private boolean has24HourStats;
    private boolean has30DayStats;


    // Getter Methods

    public boolean getActiveInLastCrawl() {
        return activeInLastCrawl;
    }

    public boolean getOverLoadedInLastCrawl() {
        return overLoadedInLastCrawl;
    }

    public boolean getValidatingInLastCrawl() {
        return validatingInLastCrawl;
    }

    public float getActive30DaysPercentage() {
        return active30DaysPercentage;
    }

    public float getOverLoaded30DaysPercentage() {
        return overLoaded30DaysPercentage;
    }

    public float getValidating30DaysPercentage() {
        return validating30DaysPercentage;
    }

    public float getActive24HoursPercentage() {
        return active24HoursPercentage;
    }

    public float getOverLoaded24HoursPercentage() {
        return overLoaded24HoursPercentage;
    }

    public float getValidating24HoursPercentage() {
        return validating24HoursPercentage;
    }

    public boolean getHas24HourStats() {
        return has24HourStats;
    }

    public boolean getHas30DayStats() {
        return has30DayStats;
    }

    // Setter Methods

    public void setActiveInLastCrawl(boolean activeInLastCrawl) {
        this.activeInLastCrawl = activeInLastCrawl;
    }

    public void setOverLoadedInLastCrawl(boolean overLoadedInLastCrawl) {
        this.overLoadedInLastCrawl = overLoadedInLastCrawl;
    }

    public void setValidatingInLastCrawl(boolean validatingInLastCrawl) {
        this.validatingInLastCrawl = validatingInLastCrawl;
    }

    public void setActive30DaysPercentage(float active30DaysPercentage) {
        this.active30DaysPercentage = active30DaysPercentage;
    }

    public void setOverLoaded30DaysPercentage(float overLoaded30DaysPercentage) {
        this.overLoaded30DaysPercentage = overLoaded30DaysPercentage;
    }

    public void setValidating30DaysPercentage(float validating30DaysPercentage) {
        this.validating30DaysPercentage = validating30DaysPercentage;
    }

    public void setActive24HoursPercentage(float active24HoursPercentage) {
        this.active24HoursPercentage = active24HoursPercentage;
    }

    public void setOverLoaded24HoursPercentage(float overLoaded24HoursPercentage) {
        this.overLoaded24HoursPercentage = overLoaded24HoursPercentage;
    }

    public void setValidating24HoursPercentage(float validating24HoursPercentage) {
        this.validating24HoursPercentage = validating24HoursPercentage;
    }

    public void setHas24HourStats(boolean has24HourStats) {
        this.has24HourStats = has24HourStats;
    }

    public void setHas30DayStats(boolean has30DayStats) {
        this.has30DayStats = has30DayStats;
    }
}
