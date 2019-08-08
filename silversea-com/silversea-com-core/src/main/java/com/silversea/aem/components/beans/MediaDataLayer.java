package com.silversea.aem.components.beans;

/**
 * TODO clean
 */
public class MediaDataLayer {

    private String user_geo_adwords = "";

    private String track_adwords_account = "";

    private String track_77TrackingTransaction = "";

    private String Adwords_conversion_id = "";

    private String Adwords_conversion_label = "";

    private String Adwords_format = "";

    private String Adwords_value = "";

    private String Google_fw_num_Id = "";

    private String Google_fw_num_label = "";

    private String Google_tr_num_id = "";

    private String Google_tr_num_label = "";

    public MediaDataLayer(String user_geo_adwords, String track_adwords_account, String track_77TrackingTransaction,
            String adwords_conversion_id, String adwords_conversion_label, String adwords_format, String adwords_value,
            String google_fw_num_Id, String google_fw_num_label, String google_tr_num_id, String google_tr_num_label,
            String yahoo_project_id, String yahoo_pixel_id) {
        super();
        this.user_geo_adwords = user_geo_adwords;
        this.track_adwords_account = track_adwords_account;
        this.track_77TrackingTransaction = track_77TrackingTransaction;
        Adwords_conversion_id = adwords_conversion_id;
        Adwords_conversion_label = adwords_conversion_label;
        Adwords_format = adwords_format;
        Adwords_value = adwords_value;
        Google_fw_num_Id = google_fw_num_Id;
        Google_fw_num_label = google_fw_num_label;
        Google_tr_num_id = google_tr_num_id;
        Google_tr_num_label = google_tr_num_label;
    }

    public String getUser_geo_adwords() {
        return user_geo_adwords;
    }

    public void setUser_geo_adwords(String user_geo_adwords) {
        this.user_geo_adwords = user_geo_adwords;
    }

    public String getTrack_adwords_account() {
        return track_adwords_account;
    }

    public void setTrack_adwords_account(String track_adwords_account) {
        this.track_adwords_account = track_adwords_account;
    }

    public String getTrack_77TrackingTransaction() {
        return track_77TrackingTransaction;
    }

    public void setTrack_77TrackingTransaction(String track_77TrackingTransaction) {
        this.track_77TrackingTransaction = track_77TrackingTransaction;
    }

    public String getAdwords_conversion_id() {
        return Adwords_conversion_id;
    }

    public void setAdwords_conversion_id(String adwords_conversion_id) {
        Adwords_conversion_id = adwords_conversion_id;
    }

    public String getAdwords_conversion_label() {
        return Adwords_conversion_label;
    }

    public void setAdwords_conversion_label(String adwords_conversion_label) {
        Adwords_conversion_label = adwords_conversion_label;
    }

    public String getAdwords_format() {
        return Adwords_format;
    }

    public void setAdwords_format(String adwords_format) {
        Adwords_format = adwords_format;
    }

    public String getAdwords_value() {
        return Adwords_value;
    }

    public void setAdwords_value(String adwords_value) {
        Adwords_value = adwords_value;
    }

    public String getGoogle_fw_num_Id() {
        return Google_fw_num_Id;
    }

    public void setGoogle_fw_num_Id(String google_fw_num_Id) {
        Google_fw_num_Id = google_fw_num_Id;
    }

    public String getGoogle_fw_num_label() {
        return Google_fw_num_label;
    }

    public void setGoogle_fw_num_label(String google_fw_num_label) {
        Google_fw_num_label = google_fw_num_label;
    }

    public String getGoogle_tr_num_id() {
        return Google_tr_num_id;
    }

    public void setGoogle_tr_num_id(String google_tr_num_id) {
        Google_tr_num_id = google_tr_num_id;
    }

    public String getGoogle_tr_num_label() {
        return Google_tr_num_label;
    }

    public void setGoogle_tr_num_label(String google_tr_num_label) {
        Google_tr_num_label = google_tr_num_label;
    }

}
