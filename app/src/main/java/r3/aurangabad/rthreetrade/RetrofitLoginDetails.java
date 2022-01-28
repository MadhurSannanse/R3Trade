package r3.aurangabad.rthreetrade;

public class RetrofitLoginDetails {
    private String ID="";
    private String user_name="";
    private String contact_no="";
    private String user_type="";
    private String imei_no="";
    private String date="";
    private String status="";
    private String tally_name="";
    private String url_address="";

    public RetrofitLoginDetails(String ID, String user_name, String contact_no, String user_type, String imei_no, String date, String status, String tally_name, String url_address) {
        this.ID = ID;
        this.user_name = user_name;
        this.contact_no = contact_no;
        this.user_type = user_type;
        this.imei_no = imei_no;
        this.date = date;
        this.status = status;
        this.tally_name = tally_name;
        this.url_address = url_address;
    }

    public String getID() {
        return ID;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getImei_no() {
        return imei_no;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getTally_name() {
        return tally_name;
    }

    public String getUrl_address() {
        return url_address;
    }
}
