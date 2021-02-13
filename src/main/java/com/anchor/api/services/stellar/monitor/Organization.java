package com.anchor.api.services.stellar.monitor;


import java.util.ArrayList;

public class Organization {
    private String id;
    private String name;
    private String dba;
    private String url;
    private String physicalAddress;
    private String keybase;
    private String officialEmail;
    ArrayList< Object > validators = new ArrayList < Object > ();
    private boolean subQuorumAvailable;
    private float subQuorum24HoursAvailability;
    private float subQuorum30DaysAvailability;
    private boolean has30DayStats;
    private boolean has24HourStats;
    private String dateDiscovered;
    private boolean isTierOneOrganization;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDba() {
        return dba;
    }

    public String getUrl() {
        return url;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public String getKeybase() {
        return keybase;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public boolean getSubQuorumAvailable() {
        return subQuorumAvailable;
    }

    public float getSubQuorum24HoursAvailability() {
        return subQuorum24HoursAvailability;
    }

    public float getSubQuorum30DaysAvailability() {
        return subQuorum30DaysAvailability;
    }

    public boolean getHas30DayStats() {
        return has30DayStats;
    }

    public boolean getHas24HourStats() {
        return has24HourStats;
    }

    public String getDateDiscovered() {
        return dateDiscovered;
    }

    public boolean getIsTierOneOrganization() {
        return isTierOneOrganization;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDba(String dba) {
        this.dba = dba;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public void setKeybase(String keybase) {
        this.keybase = keybase;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public void setSubQuorumAvailable(boolean subQuorumAvailable) {
        this.subQuorumAvailable = subQuorumAvailable;
    }

    public void setSubQuorum24HoursAvailability(float subQuorum24HoursAvailability) {
        this.subQuorum24HoursAvailability = subQuorum24HoursAvailability;
    }

    public void setSubQuorum30DaysAvailability(float subQuorum30DaysAvailability) {
        this.subQuorum30DaysAvailability = subQuorum30DaysAvailability;
    }

    public void setHas30DayStats(boolean has30DayStats) {
        this.has30DayStats = has30DayStats;
    }

    public void setHas24HourStats(boolean has24HourStats) {
        this.has24HourStats = has24HourStats;
    }

    public void setDateDiscovered(String dateDiscovered) {
        this.dateDiscovered = dateDiscovered;
    }

    public void setIsTierOneOrganization(boolean isTierOneOrganization) {
        this.isTierOneOrganization = isTierOneOrganization;
    }
}
