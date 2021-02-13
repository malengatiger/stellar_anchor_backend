package com.anchor.api.services.stellar.monitor;

import java.util.ArrayList;

public class NodeData {
    private String ip;
    private int port;
    private String publicKey;
    private String ledgerVersion;
    private String overlayVersion;
    private String overlayMinVersion;
    private String versionStr;
    private boolean active;
    private boolean overLoaded;
    private QuorumSet QuorumSetObject;
    private GeoData GeoDataObject;
    private Statistics StatisticsObject;
    private String dateDiscovered;
    private String dateUpdated;
    private boolean isValidator;
    private boolean isFullValidator;
    private boolean isValidating;
    private int index;
    private String isp;


    // Getter Methods

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getLedgerVersion() {
        return ledgerVersion;
    }

    public String getOverlayVersion() {
        return overlayVersion;
    }

    public String getOverlayMinVersion() {
        return overlayMinVersion;
    }

    public String getVersionStr() {
        return versionStr;
    }

    public boolean getActive() {
        return active;
    }

    public boolean getOverLoaded() {
        return overLoaded;
    }

    public QuorumSet getQuorumSet() {
        return QuorumSetObject;
    }

    public GeoData getGeoData() {
        return GeoDataObject;
    }

    public Statistics getStatistics() {
        return StatisticsObject;
    }

    public String getDateDiscovered() {
        return dateDiscovered;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public boolean getIsValidator() {
        return isValidator;
    }

    public boolean getIsFullValidator() {
        return isFullValidator;
    }

    public boolean getIsValidating() {
        return isValidating;
    }

    public int getIndex() {
        return index;
    }

    public String getIsp() {
        return isp;
    }

    // Setter Methods

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setLedgerVersion(String ledgerVersion) {
        this.ledgerVersion = ledgerVersion;
    }

    public void setOverlayVersion(String overlayVersion) {
        this.overlayVersion = overlayVersion;
    }

    public void setOverlayMinVersion(String overlayMinVersion) {
        this.overlayMinVersion = overlayMinVersion;
    }

    public void setVersionStr(String versionStr) {
        this.versionStr = versionStr;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOverLoaded(boolean overLoaded) {
        this.overLoaded = overLoaded;
    }

    public void setQuorumSet(QuorumSet quorumSetObject) {
        this.QuorumSetObject = quorumSetObject;
    }

    public void setGeoData(GeoData geoDataObject) {
        this.GeoDataObject = geoDataObject;
    }

    public void setStatistics(Statistics statisticsObject) {
        this.StatisticsObject = statisticsObject;
    }

    public void setDateDiscovered(String dateDiscovered) {
        this.dateDiscovered = dateDiscovered;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setIsValidator(boolean isValidator) {
        this.isValidator = isValidator;
    }

    public void setIsFullValidator(boolean isFullValidator) {
        this.isFullValidator = isFullValidator;
    }

    public void setIsValidating(boolean isValidating) {
        this.isValidating = isValidating;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
