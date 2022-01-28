package r3.aurangabad.rthreetrade;

import android.app.Activity;

public class Model {
    private static Model model = null;
    private String Url_address="";
    private String Salesman="";
    private String userType="";
    private String todaysDate="",recNumber="",tallyName="";
    Activity activity=null;
    public String getTallyName() {
        return tallyName;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setTallyName(String tallyName) {
        this.tallyName = tallyName;
    }

    public String getRecNumber() {
        return recNumber;
    }

    public void setRecNumber(String recNumber) {
        this.recNumber = recNumber;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    private String partyName="";
    public Model() {

    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUrl_address() {
        return Url_address;
    }

    public String getSalesman() {
        return Salesman;
    }

    public void setSalesman(String salesman) {
        Salesman = salesman;
    }

    public void setUrl_address(String url_address) {
        Url_address = url_address;
    }

    public static Model getInstance() {
         if (model != null) {
            // Log.i("Old M","Model");
            return model;
        }
        model = new Model();
      //  Log.i("New M","Model");
        return model;
    }
}
