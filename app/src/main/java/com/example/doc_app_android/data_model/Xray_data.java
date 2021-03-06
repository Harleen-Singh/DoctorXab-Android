package com.example.doc_app_android.data_model;

public class Xray_data {
    private String xray_ID , category , date , time;
    private String imageUrl ,  reportId  , reportDate ,  reportData;

    public Xray_data(String xray_ID, String category, String date, String time, String imageUrl, String reportId, String reportDate, String reportData) {
        this.xray_ID = xray_ID;
        this.category = category;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.reportId = reportId;
        this.reportDate = reportDate;
        this.reportData = reportData;
    }

    public Xray_data(String xray_ID, String category, String date, String time) {
        this.xray_ID = xray_ID;
        this.category = category;
        this.date = date;
        this.time = time;
    }

    public String getXray_ID() {
        return xray_ID;
    }

    public void setXray_ID(String xray_ID) {
        this.xray_ID = xray_ID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }
}
