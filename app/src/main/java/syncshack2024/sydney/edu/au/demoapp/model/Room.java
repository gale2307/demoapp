package syncshack2024.sydney.edu.au.demoapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import syncshack2024.sydney.edu.au.demoapp.enums.SportsCategory;

public class Room {
    private String uid;
    private String title;
    private String description;
    private SportsCategory sportsCategory;
    private double lat;
    private double lng;
    private String geohash;
    private @ServerTimestamp Date startDate;
    private @ServerTimestamp Date endDate;

    public Room(String uid, String title, String description, Date startDate, Date endDate) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportsCategory getSportsCategory() {
        return sportsCategory;
    }

    public void setSportsCategory(SportsCategory sportsCategory) {
        this.sportsCategory = sportsCategory;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
