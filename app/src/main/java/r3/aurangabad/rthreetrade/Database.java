package r3.aurangabad.rthreetrade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class Database extends SQLiteOpenHelper {
    final static String DBName = "main";
    final static int version = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    Model model;
    double totalrecamount=0;

    public Database(Context context) {
        super(context, DBName, null, version);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void createTables(SQLiteDatabase sampleDB)
    {
        try
        {
            int id=1;
            String value="";
            //Log.e("Tables ","Creating");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_loginmanagement(ID integer NOT NULL PRIMARY KEY AUTOINCREMENT,user_name TEXT,contact_no TEXT,user_type TEXT,imei_no TEXT,date TEXT,status TEXT,tally_name TEXT,url_address TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_order_det(Id INTEGER PRIMARY KEY AUTOINCREMENT,Order_id NUMERIC,Product_id INTEGER,Rate NUMERIC,Amount NUMERIC,Scheme TEXT,Quantity NUMERIC,Salesman TEXT,Description TEXT,Description_Amount TEXT,Status TEXT,Bottle NUMERIC,Extra_scheme NUMERIC,Tally_ID TEXT,Ins_Date TEXT,Price_Level TEXT,Is_Uploaded TEXT,Scheme_Desc TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_outstanding(Billno Text,Billdate date,Party text,Amount decimal(20,3),Salesman text,Opening decimal(20,3),Onaccount decimal(20,3))");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt(Rec_No TEXT,Rec_Date TEXT,Party TEXT,Salesman TEXT, Amount decimal(20,3),Paymode TEXT,Is_Uploaded TEXT,Cur_Date TEXT,Instrument_Number TEXT,Instrument_Date TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt_all(Rec_No TEXT,Rec_Date TEXT,Party TEXT,Salesman TEXT, Amount decimal(20,3),Paymode TEXT,Is_Uploaded TEXT,Instrument_Number TEXT,Instrument_Date TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_customer(Id INTEGER,Salesman text,Name text,Plevel decimal(20,3),Opening_Balance decimal(20,3),Closing_Balance decimal(20,3),Freight decimal(20,3),Serial TEXT,Lic_Number TEXT,City TEXT,Contact_Number TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_login(Id text,Name text,Password text,Status text,Serial TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_order(Id integer NOT NULL PRIMARY KEY AUTOINCREMENT,Customer_name text,Cdate TEXT,Salesman text,Total_amount decimal(20,3),Status text,Ins_Date text,Freight decimal(20,3),TCS decimal(20,3),Tally_ID TEXT,Approve TEXT,Approve_Main TEXT,Is_Uploaded TEXT,Remark TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_product(Id text,Ratea integer,Rateb integer,Name TEXT,Catagiry TEXT,Sequence INTEGER,Priority INTEGER,Brand Text,Price_Level Text)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_rebate(Id INTEGER,Party TEXT,Brand_Name TEXT,Cur_Date TEXT,Cases NUMERIC,Bottle NUMERIC,Rebate decimal(20,3),Company TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_update_date(Id INTEGER PRIMARY KEY,Update_Date TEXT)");
            sampleDB.execSQL("Create TABLE IF NOT EXISTS tbl_ledger(Doc_Number TEXT,Doc_Date date,Doc_Type TEXT,Party_Name TEXT,Amount decimal(20,4),Salesman TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_fcm_details(Notification_token Text,Name Text)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_stock_details(ID INTEGER PRIMARY KEY AUTOINCREMENT,Customer_Name TEXT,Product_Name TEXT,CDate TEXT,ClosingBalanceCase NUMERIC,ClosingBalancePcs NUMERIC,Salesman TEXT,Is_Uploaded TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_stock_details_temp(ID INTEGER PRIMARY KEY AUTOINCREMENT,Customer_Name TEXT,Product_Name TEXT,CDate TEXT,ClosingBalanceCase NUMERIC,ClosingBalancePcs NUMERIC,Salesman TEXT,Is_Uploaded TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_salesman_code(ID INTEGER PRIMARY KEY AUTOINCREMENT,Salesman_Name TEXT,Code TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt_sequence(ID INTEGER PRIMARY KEY AUTOINCREMENT,Rec_Date TEXT,Sequence TEXT,Is_Uploaded TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_scheme_master(ID INTEGER,Scheme_Name TEXT,Scheme_Type TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_company_details(ID INTEGER,Company_Name1 TEXT,Company_Name2 TEXT,Company_Address TEXT,Stamp_Duty TEXT,Stamp_Value TEXT,Prefix TEXT,Is_BT_Print TEXT,Is_SMS_Send TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_location_details(ID INTEGER PRIMARY KEY AUTOINCREMENT,Voucher_No Text,Voucher_Type Text,Location Text,Is_Uploaded TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt_references(ID INTEGER PRIMARY KEY AUTOINCREMENT,Party_Name Text,Amount decimal(20,3),Salesman Text,Rec_Number Text,Agst_Ref_1 Text,Agst_Ref_2 Text,Agst_Ref_3 Text,Agst_Ref_4 Text,Agst_Ref_5 Text,Agst_Amount_1 decimal(20,3),Agst_Amount_2 decimal(20,3),Agst_Amount_3 decimal(20,3),Agst_Amount_4 decimal(20,3),Agst_Amount_5 decimal(20,3),Is_Uploaded TEXT)");
            //  sampleDB.execSQL("update tbl_receipt set Is_Uploaded='NO' where Cur_Date='27/02/2020'");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt_details(ID INTEGER PRIMARY KEY AUTOINCREMENT,Party_Name TEXT,Cur_Date TEXT,Rec_No TEXT,Salesman TEXT,Is_Uploaded TEXT)");
           // sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_selected_salesman(ID Text,SalesmanName Text)");
            sampleDB.close();
            Log.i("Database","Created");
        }
        catch(Exception ex)
        {
            Log.e("Tables ","Error "+ex.getMessage());
        }
    }
    public boolean addLoginDetailsNew(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();

            ContentValues data = new ContentValues();
            //data.put("Id", v.elementAt(0).toString());
            data.put("user_name", v.elementAt(0).toString());
            data.put("contact_no", v.elementAt(1).toString());
            data.put("user_type", v.elementAt(2).toString());
            data.put("imei_no", v.elementAt(3).toString().trim());
            data.put("date", v.elementAt(4).toString().trim());
            data.put("status", v.elementAt(5).toString().trim());
            data.put("tally_name", v.elementAt(6).toString().trim());
            data.put("url_address", v.elementAt(7).toString().trim());
            mydb.insert("tbl_loginmanagement", null, data);
            Log.e("User","Added");
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("User","Not Added");
            return false;
        }

    }
    public Vector  getUserLogin() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al;
        try {

            String cols[] = { "user_name","contact_no","user_type","imei_no","date","status","tally_name","url_address"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_loginmanagement",cols,"Status=?",
                    new String[] { "YES" }, null,
                    null, null);
            al=new Vector();
            while (c.moveToNext()) {
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                al.addElement(c.getString(6).trim());
                al.addElement(c.getString(7).trim());
            }

            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public String getTodaysSaleTotal() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        try {
            Calendar cal=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            String formattedDate = df.format(cal.getTime());
            Cursor c = mydb.rawQuery("SELECT SUM(Total_amount) FROM tbl_order where Status='YES' and CDate='"+formattedDate+"'" ,null);
            while (c.moveToNext()) {
                return (c.getString(0).trim());
            }
            return "0";
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return "0";
        } finally {
            mydb.close();
        }
    }
    public String getTodaysReceiptTotal() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        try {
            Calendar cal=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy");
            String formattedDate = df.format(cal.getTime());
            Cursor c = mydb.rawQuery("SELECT SUM(Amount) FROM tbl_receipt_all where Rec_Date='"+formattedDate+"'" ,null);
            while (c.moveToNext()) {
                return (c.getString(0).trim());
            }
            return "0";
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return "0";
        } finally {
            mydb.close();
        }
    }
    public String getGroupOutstandingTotal(String group) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        try {
            Cursor c = mydb.rawQuery("SELECT SUM(Closing_Balance) FROM tbl_customer where Salesman='"+group+"'",null);
            while (c.moveToNext()) {
                return (c.getString(0).trim());
            }
            return "0";
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return "0";
        } finally {
            mydb.close();
        }
    }
    public boolean deleteLogin()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_login",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addLoginDetails(Vector v) {
        SQLiteDatabase mydb = this.getWritableDatabase();

        try {
            // // mydb.beginTransaction();
            ContentValues data = new ContentValues();
            data.put("Id", v.elementAt(0).toString());
            data.put("Name", v.elementAt(1).toString());
            data.put("Password", v.elementAt(2).toString());
            data.put("Status", v.elementAt(3).toString());
            data.put("Serial", v.elementAt(4).toString().trim());
            mydb.insert("tbl_login", null, data);
            //  // mydb.setTransactionSuccessful();
            //  // mydb.endTransaction();
            mydb.close();
            Log.i("Red det","Saved");
            return true;
        } catch (Exception e) {
            Log.i("Red det","Not Saved");
            // // mydb.setTransactionSuccessful();
            //  // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            //   // mydb.endTransaction();
            mydb.close();

        }

    }
    public boolean deleteCustomer()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_customer",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteLedger()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_ledger",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteOutstanding()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_outstanding",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteRebate()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_rebate",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteSalesmanCode()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_salesman_code",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteSchemeMaster()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_scheme_master",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addCustomerDetails(JSONArray jsonArray) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try {
            JSONObject json_data = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues data = new ContentValues();
                json_data = jsonArray.getJSONObject(i);
                data.put("Id", json_data.getString("ID"));
                data.put("Salesman",json_data.getString("salesman"));
                data.put("Name", json_data.getString("name"));
                data.put("Plevel", json_data.getString("plevel"));
                data.put("Opening_Balance", json_data.getString("opening_balance"));
                data.put("Closing_Balance", json_data.getString("closing_balance"));
                data.put("Freight", json_data.getString("frt"));
                data.put("Serial", json_data.getString("serial"));
                data.put("Lic_Number",json_data.getString("Lic_Number"));
                data.put("City",json_data.getString("City"));
                data.put("Contact_Number",json_data.getString("Contact_Number"));

                mydb.insert("tbl_customer", null, data);
            }
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean deleteProduct()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_product",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addProduct(Vector v) {


        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try {
            ContentValues data = new ContentValues();
            data.put("Id", v.elementAt(0).toString());
            data.put("Ratea", v.elementAt(1).toString());
            data.put("Rateb", v.elementAt(2).toString());
            data.put("Name", v.elementAt(3).toString());
            data.put("Catagiry", v.elementAt(4).toString().trim());
            data.put("Sequence", v.elementAt(5).toString().trim());
            data.put("Priority", v.elementAt(6).toString().trim());
            data.put("Brand", v.elementAt(7).toString().trim());
            data.put("Price_Level", v.elementAt(8).toString().trim());
            mydb.insert("tbl_product", null, data);
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            //  Log.e("Log in Product","Sucess");
            return true;
        } catch (Exception e) {
            Log.e("Error in Product",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean addOutstandingDetails(JSONArray jsonArray) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try {
            JSONObject json_data = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                ContentValues data = new ContentValues();
                json_data = jsonArray.getJSONObject(i);
                // Log.i("Outs",""+json_data.toString());
                Vector outs = new Vector();
                data.put("Billno",json_data.getString("billno"));
                data.put("Billdate", json_data.getString("billdate"));
                data.put("Party",json_data.getString("party"));
                data.put("Amount",json_data.getString("amount"));
                data.put("Salesman",json_data.getString("salesman"));
                data.put("Opening",json_data.getString("opening"));
                data.put("Onaccount", json_data.getString("onaccount"));
                mydb.insert("tbl_outstanding", null, data);

            }
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean addRebate(Vector v) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try {
            ContentValues data = new ContentValues();
            data.put("Id", v.elementAt(0).toString());
            data.put("Party", v.elementAt(1).toString());
            data.put("Brand_Name", v.elementAt(2).toString());
            data.put("Cur_Date", v.elementAt(3).toString());
            data.put("Cases", v.elementAt(4).toString().trim());
            data.put("Bottle", v.elementAt(5).toString().trim());
            data.put("Rebate", v.elementAt(6).toString().trim());
            data.put("Company", v.elementAt(7).toString().trim());
            mydb.insert("tbl_rebate", null, data);
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean addRebateDetails(JSONArray jsonArray) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try {
            JSONObject json_data = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                ContentValues data = new ContentValues();
                json_data = jsonArray.getJSONObject(i);
                // Log.i("Outs",""+json_data.toString());
                data.put("Id", json_data.getString("ID"));
                data.put("Party",json_data.getString("Party"));
                data.put("Brand_Name", json_data.getString("Brand_Name"));
                data.put("Cur_Date", json_data.getString("Cur_Date"));
                data.put("Cases", json_data.getString("Cases"));
                data.put("Bottle", json_data.getString("Bottle"));
                data.put("Rebate", json_data.getString("Rebate"));
                data.put("Company", json_data.getString("Company"));
                mydb.insert("tbl_rebate", null, data);
            }
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean addSalesmanCode(JSONArray jsonArray) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        // mydb.beginTransaction();
        try
        {
            JSONObject json_data = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                ContentValues data = new ContentValues();
                json_data = jsonArray.getJSONObject(i);
                Vector outs = new Vector();
                data.put("Salesman_Name",json_data.getString("Salesman_Name"));
                data.put("Code", json_data.getString("Code"));
                mydb.insert("tbl_salesman_code", null, data);
                Log.i("Data is",""+data.toString());
            }
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public String getSalesmanCode() {
        SQLiteDatabase mydb = null;
        try {
            String[] cols = {"Code"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_salesman_code",cols,null,null, null,
                    null, null);
            while (c.moveToNext()) {
        //        Log.i("Code",""+c.getString(0));
                return c.getString(0).trim();
        }
            return "Rec";
        } catch (Exception e) {
            return "Rec";
        } finally {
            mydb.close();
        }
    }
    public String getRecSequenceByDate(String Date) {
        SQLiteDatabase mydb = null;
        //Log.i("Date is",""+Date);
        try {
            String[] cols = {"Sequence"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_sequence",cols,"Rec_Date=?",
                    new String[] { ""+Date }, null,
                    null, null);
            while (c.moveToNext()) {
                //  Log.i("Num is",""+c.getString(0).trim());
                return c.getString(0).trim();
            }
            return "1";
        } catch (Exception e) {
            Log.e("Error",""+e.getLocalizedMessage());
            return "1";
        } finally {
            mydb.close();
        }
    }
    public boolean addAllReceiptSequence(Vector v) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        try{
            // mydb.beginTransaction();
            ContentValues data = new ContentValues();
            data.put("Rec_Date",""+v.elementAt(2));
            data.put("Sequence", ""+v.elementAt(3));
            data.put("Is_Uploaded", "YES");
            mydb.insert("tbl_receipt_sequence", null, data);
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean addSchemeMaster(JSONArray jsonArray) {

        SQLiteDatabase mydb = this.getWritableDatabase();
        try
        {
            // mydb.beginTransaction();
            JSONObject json_data = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                ContentValues data = new ContentValues();
                json_data = jsonArray.getJSONObject(i);
                Vector outs = new Vector();
                data.put("ID",json_data.getString("ID"));
                data.put("Scheme_Name", json_data.getString("Scheme_Name"));
                data.put("Scheme_Type", json_data.getString("Scheme_Type"));
                mydb.insert("tbl_scheme_master", null, data);
            }
            // mydb.setTransactionSuccessful();
            // mydb.endTransaction();
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            // mydb.endTransaction();
            mydb.close();
            return false;
        }
        finally {
            // mydb.endTransaction();
            mydb.close();

        }
    }
    public boolean deleteCompanyDetails()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_company_details",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addCompanyDetails(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("ID", v.elementAt(0).toString());
            data.put("Company_Name1", v.elementAt(1).toString());
            data.put("Company_Name2", v.elementAt(2).toString());
            data.put("Company_Address", v.elementAt(3).toString());
            data.put("Stamp_Duty", v.elementAt(4).toString().trim());
            data.put("Stamp_Value", v.elementAt(5).toString());
            data.put("Prefix", v.elementAt(6).toString());
            data.put("Is_BT_Print", v.elementAt(7).toString());
            data.put("Is_SMS_Send", v.elementAt(8).toString().trim());
            mydb.insert("tbl_company_details", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error ","In company save "+e.getLocalizedMessage());
            return false;
        }

    }
    public String[] getAllCustomer() {
       SQLiteDatabase mydb = null;
       Log.i("Fuction ","DB "+this.getReadableDatabase());
       model = Model.getInstance();
       String[] al;int i=0;
        if(model.getUserType().equals("Admin"))
        {
            try {
                String[] cols = {"Name"};
                mydb = this.getReadableDatabase();
               /* Cursor c = mydb.rawQuery("select * from tbl_customer",null);*/
                Cursor c = mydb.query("tbl_customer",cols,null,
                        null, null,
                        null, null);
                al = new String[c.getCount()];
                while (c.moveToNext()) {
                    al[i]=c.getString(0).trim();
                    i++;
                }
                return al;
            } catch (Exception e) {
                Log.e("Name : Error ",""+e.getMessage());
                return null;
            } finally {
                mydb.close();
            }
        }
        else
        {
            try {
                String[] cols = {"Name"};
                Log.i("Fuction ","Calling Salesman");
                mydb = this.getReadableDatabase();
                Cursor c = mydb.query("tbl_customer",cols,"Salesman=?",
                        new String[] { "" + model.getSalesman() }, null,
                        null, null);
                al = new String[c.getCount()];
                while (c.moveToNext()) {
                    al[i]=c.getString(0).trim();
                    i++;
                }
                return al;
            } catch (Exception e) {
                Log.e("Name : Error ",""+e.getMessage());
                return null;
            } finally {
                mydb.close();
            }
        }
    }
    public Vector  getCompanyDetails() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector v=new Vector();
        try {
            String[] cols = {"ID","Company_Name1","Company_Name2","Company_Address","Stamp_Duty","Stamp_Value","Prefix","Is_BT_Print","Is_SMS_Send"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_company_details",cols,null,
                    null, null,
                    null, null);
            while (c.moveToNext()) {
                v.addElement(c.getString(0));
                v.addElement(c.getString(1));
                v.addElement(c.getString(2));
                v.addElement(c.getString(3));
                v.addElement(c.getString(4));
                v.addElement(c.getString(5));
                v.addElement(c.getString(6));
                v.addElement(c.getString(7));
                v.addElement(c.getString(8));
            }
           // Log.i("Company is ",""+v.toString());
            return v;
        } catch (Exception e) {
           // Log.e("Company Error",""+e.getLocalizedMessage());
            return new Vector();
        } finally {
            mydb.close();
        }
    }
    public Vector getPartyReceipt() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode"};
            mydb = this.getReadableDatabase();
            Log.i("Party",""+model.getPartyName());
           // Log.i("Date",""+model.getTodaysDate());
            Cursor c = mydb.query("tbl_receipt",cols, "Rec_Date=? and Party=?",new String[]{""+model.getTodaysDate(),""+model.getPartyName()},null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                al.addElement(row);
                Log.i("Rec Checking",""+row.toString());
            }
            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public boolean addReceiptAll(Vector v) {
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Rec_No", v.elementAt(0).toString());
            data.put("Rec_Date", v.elementAt(1).toString());
            data.put("Party", v.elementAt(2).toString());
            data.put("Salesman", v.elementAt(3).toString().trim());
            data.put("Amount", v.elementAt(4).toString());
            data.put("Paymode", v.elementAt(5).toString());
            data.put("Is_Uploaded", v.elementAt(6).toString());
            data.put("Instrument_Number", v.elementAt(7).toString());
            data.put("Instrument_Date", v.elementAt(8).toString());
            mydb.insert("tbl_receipt_all", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Rec All Add Error",""+e.getLocalizedMessage());
            return false;
        }
    }
    public boolean addReceipt(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Rec_No", v.elementAt(0).toString());
            data.put("Rec_Date", v.elementAt(1).toString());
            data.put("Party", v.elementAt(2).toString());
            data.put("Salesman", v.elementAt(3).toString().trim());
            data.put("Amount", v.elementAt(4).toString());
            data.put("Paymode", v.elementAt(5).toString());
            data.put("Is_Uploaded", v.elementAt(6).toString());
            data.put("Cur_Date", v.elementAt(7).toString());
            data.put("Instrument_Number", v.elementAt(8).toString());
            data.put("Instrument_Date", v.elementAt(9).toString());
            mydb.insert("tbl_receipt", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean addReceiptDetails(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Party_Name", v.elementAt(0).toString());
            data.put("Cur_Date", v.elementAt(1).toString());
            data.put("Rec_No", v.elementAt(2).toString());
            data.put("Salesman", v.elementAt(3).toString().trim());
            data.put("Is_Uploaded","No");
            mydb.insert("tbl_receipt_details", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Vector getAllReceipt() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt",cols, null,null,null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                al.addElement(row);
            }
            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public boolean deleteRecSequence(String date)
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_receipt_sequence","Rec_Date=?",new String[]{date});
            //Log.i("RecSeq","Deleted");
            return true;
        }
        catch (Exception ex)
        {
            Log.e("Delete err",""+ex.getLocalizedMessage());
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addReceiptSequence(int seq,String date) {
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Rec_Date",date);
            data.put("Sequence", (seq+1));
            data.put("Is_Uploaded", "NO");
            mydb.insert("tbl_receipt_sequence", null, data);
             Log.i("RecSeq "+seq,"Added "+date);

            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("Error",""+e.getMessage());
            return false;
        }
    }
    public Vector getTodaysReceiptAll() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_all",cols, "Rec_Date=? and Salesman=?",new String[]{""+model.getTodaysDate(),model.getSalesman()},null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());

                al.addElement(row);
            }
            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getReceiptAllByRecNo() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode","Instrument_Number","Instrument_Date"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_all",cols, "Rec_No=? and Party=?",new String[]{""+model.getRecNumber(),""+model.getPartyName()},null,null,null);
            row=new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                row.addElement(c.getString(6).trim());
                row.addElement(c.getString(7).trim());
            }
            return row;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getReceiptByRecNo() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode","Cur_Date","Instrument_Number","Instrument_Date"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt",cols, "Rec_No=?",new String[]{""+model.getRecNumber()},null,null,null);
            al = new Vector();
            row=new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                row.addElement(c.getString(6).trim());
                row.addElement(c.getString(7).trim());
                row.addElement(c.getString(8).trim());

                al.addElement(row);
            }
            return row;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getReceiptDetailsForSync() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector row;
        try {
            String[] cols = {"ID","Party_Name","Cur_Date","Rec_No","Salesman","Is_Uploaded"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_details",cols, "Rec_No=?",new String[]{model.getRecNumber()},null,null,null,null);
            row = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());

            }
            return row;
        } catch (Exception e) {
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getReceiptSequenceByDate(String Recdate) {
        SQLiteDatabase mydb = null;
        //Log.i("Date is",""+Date);
        Vector rec=new Vector();
        try {

            String[] cols = {"ID","Rec_Date","Sequence"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_sequence",cols,"Rec_Date=?",
                    new String[]{Recdate}, null,
                    null, null);
            while (c.moveToNext()) {
                rec.addElement(c.getString(0));
                rec.addElement(c.getString(1));
                rec.addElement(c.getString(2));

            }
            return rec;
        } catch (Exception e) {
            Log.e("Error",""+e.getLocalizedMessage());
            return rec;
        } finally {
            mydb.close();
        }
    }
    public Vector getReceiptDetails() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"ID", "Party_Name", "Cur_Date", "Rec_No", "Salesman"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_details",cols, "Cur_Date=? and Salesman=? and Party_Name=?",new String[]{""+model.getTodaysDate(),model.getSalesman(),model.getPartyName()},null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                al.addElement(row);
                //   Log.i("AL",""+row.toString());
            }
            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getCustomerDetails(String name) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector v=new Vector();
        try {
            String[] cols = {"Id", "Salesman", "Name", "Plevel", "Opening_Balance", "Closing_Balance", "Freight", "Serial","Lic_Number","City","Contact_Number"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_customer",cols,"Name=?",
                    new String[] { ""+model.getPartyName()}, null,
                    null, null);
            while (c.moveToNext()) {
                v.addElement(c.getString(0));
                v.addElement(c.getString(1));
                v.addElement(c.getString(2));
                v.addElement(c.getString(3));
                v.addElement(c.getString(4));
                v.addElement(c.getString(5));
                v.addElement(c.getString(6));
                v.addElement(c.getString(7));
                v.addElement(c.getString(8));
                v.addElement(c.getString(9));
                v.addElement(c.getString(10));

            }
            Log.i("Customer is ",""+v.toString());
            return v;
        } catch (Exception e) {
            Log.e("Customer Error",""+e.getLocalizedMessage());
            return new Vector();
        } finally {
            mydb.close();
        }
    }
    public String[]  getAllLogin(String imeino) {
        SQLiteDatabase mydb = null;
        String[] users;
        try {

            String[] cols = {"Id", "Name", "Password", "Status", "Serial"};
            mydb = this.getReadableDatabase();
            //Remove IMEI Login feature temp
            Cursor c = mydb.query("tbl_login",cols,"Status=? AND Serial=? ",
                    new String[] { "YES",""+imeino }, null,
                    null, "Name");
            //for without imei login
            /*Cursor c = mydb.query("tbl_login",cols,"Status=?",
                    new String[] { "YES"}, null,
                    null, "Name");*/
            users=new String[c.getCount()];
            int i=0;
            while (c.moveToNext()) {
                users[i]=c.getString(1).trim();
                i++;
                }
            return users;
        } catch (Exception e) {Log.e("Login Error",""+e.getLocalizedMessage());

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAllOutstanding() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  Salesman,count(Billno) FROM tbl_outstanding GROUP BY Salesman",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAllOutstandingMonthCount() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al;
        al = new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c =null;
                if(i<10) {
                    c = mydb.rawQuery("SELECT Count(BillNo) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '0" + i + "'", null);
                }
                else
                {
                    c = mydb.rawQuery("SELECT Count(BillNo) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '" + i + "'", null);
                }
               while (c.moveToNext()) {

                    al.addElement(c.getString(0).trim());
                 //   Log.i("Month ", "" + al.toString());
                }
            }
            return al;
        } catch (Exception e) {
            Log.e("Error :- ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getSalesmanOutstandingMonthCount(String salesman) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al;
        al = new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c =null;
                if(i<10) {
                    c = mydb.rawQuery("SELECT SUM(amount),SUM(opening) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '0" + i + "' and Salesman='"+salesman+"'", null);
                }
                else
                {
                    c = mydb.rawQuery("SELECT  SUM(amount),SUM(opening) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '" + i + "'and Salesman='"+salesman+"'", null);
                }
                while (c.moveToNext()) {
                    double amt=0;
                    try{amt=(Double.parseDouble(c.getString(1).trim())-Double.parseDouble(c.getString(0).trim()));}catch (Exception ex){}
                    try {
                        if (amt == 0) {
                            al.addElement(c.getString(0).trim());
                        } else {
                            al.addElement(amt);
                        }
                    }
                    catch (Exception ex){al.addElement(0);}
                }
            }
            return al;
        } catch (Exception e) {
            Log.e("Error :- ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getPartyOutstandingMonthCount(String party) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al;
        al = new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c =null;
                if(i<10) {
                    c = mydb.rawQuery("SELECT SUM(amount),SUM(opening) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '0" + i + "' and Party='"+party+"'", null);
                }
                else
                {
                    c = mydb.rawQuery("SELECT SUM(amount),SUM(opening) FROM tbl_outstanding WHERE strftime('%m', BillDate) = '" + i + "'and Party='"+party+"'", null);
                }
                while (c.moveToNext()) {
                    double amt=0;
                    try{amt=(Double.parseDouble(c.getString(1).trim())-Double.parseDouble(c.getString(0).trim()));}catch (Exception ex){}
                    try {
                        if (amt == 0) {
                            al.addElement(c.getString(0).trim());
                        } else {
                            al.addElement(amt);
                        }
                    }
                    catch (Exception ex){al.addElement(0);}
                }
            }
            return al;
        } catch (Exception e) {
            Log.e("Error :- ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getSalesmanOutstanding(String salesman) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT o.Party,count(o.Billno)as cl,c.Closing_Balance FROM tbl_outstanding o,tbl_customer c where o.Salesman='"+salesman+"' and o.Party=c.Name GROUP BY Party",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());

                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public long getDays(String date) {
        SQLiteDatabase mydb = null;
        long v = 0;
        try {
            mydb = this.getReadableDatabase();
            // Vector ov = viewAllOrderById(id);
            String q = " SELECT julianday('now') - julianday('" + date + "');";

            Cursor c1 = mydb.rawQuery(q, null);

            if (c1.moveToNext()) {

                v = (c1.getLong(0));
            }

            return v;
        } catch (Exception e) {

            return 0;
        } finally {
            mydb.close();
        }
    }
    public String getPartyClosingBalance(String party) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al=new Vector();
        Vector row;
        try {
            String[] cols = {"Closing_Balance"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_customer",cols, "Name=?",new String[]{party},null,null,null);

            while (c.moveToNext()) {
                Log.i("","");
                return c.getString(0).trim();
            }
            Log.i("PL","Not Found");
            return "Primary";
        } catch (Exception e) {
            Log.e("PL Error",""+e.getMessage());
            return "Primary";
        } finally {
            mydb.close();
        }
    }
    public Vector  getPartyOutstanding(String party) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT * FROM tbl_outstanding where Party='"+party+"'",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                al.addElement(c.getString(6).trim());

                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public String  getSalesmanOutstandingTotal(String salesman) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        try {
            //Log.i("Query","SELECT  SUM(Closing_Balance) FROM tbl_customer where Salesman='"+salesman+"'");
            // Cursor c = mydb.rawQuery("SELECT  Abs(sum(CAST(Closing_Balance AS UNSIGNED))) FROM tbl_customer where Salesman='"+salesman+"' ",null);
            Cursor c = mydb.rawQuery("SELECT  sum(Closing_Balance) FROM tbl_customer where Salesman='"+salesman+"' ",null);
            while (c.moveToNext()){
                try
                {
                    BigDecimal b1=new BigDecimal(c.getDouble(0));
                    b1=b1.abs();
                    return ""+b1.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                }
                catch (Exception ex){
                    return c.getString(0).trim();
                }

            }
            return "0";
        } catch (Exception e) {
            Log.e("Error is closing ",""+e.getMessage());
            return "0";
        } finally {
            mydb.close();
        }
    }
    public ArrayList<String> getCount() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        ArrayList<String> al;
        try {
            String[] cols = {"Name"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_customer",cols,null,
                    null, null,
                    null, null);
            al = new ArrayList<String>();
            while (c.moveToNext()) {
                al.add(c.getString(0).trim());
            }

            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAllRebate() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  c.Salesman,sum(r.Rebate),sum(r.Cases),sum(r.Bottle),r.Party,c.Name FROM tbl_rebate r,tbl_customer c where c.Name=r.Party  GROUP BY c.Salesman",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAllRebateByMonth(String month) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  c.Salesman,sum(r.Rebate),sum(r.Cases),sum(r.Bottle),r.Party,c.Name FROM tbl_rebate r,tbl_customer c where c.Name=r.Party and Cur_Date like '%"+month+"%' GROUP BY c.Salesman",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getSalesmanRebate(String salesman) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  r.Party,sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where c.Salesman='"+salesman+"' and c.Name=r.Party GROUP BY r.Party",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());

                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getSalesmanRebateByMonth(String salesman,String month) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  r.Party,sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where c.Salesman='"+salesman+"' and c.Name=r.Party  and Cur_Date like '%"+month+"%' GROUP BY r.Party",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());

                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }

    public Vector  getPartyRebateByMonth(String partyname,String month) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            //Cursor c = mydb.rawQuery("SELECT  Brand_Name,sum(Rebate),sum(Cases),sum(Bottle),MONTH(Cur_Date) FROM tbl_rebate where Party='"+partyname+"' and MONTH(Cur_Date)='"+month+"'GROUP BY Brand_Name",null);
            Cursor c = mydb.rawQuery("SELECT Brand_Name,sum(Rebate),sum(Cases),sum(Bottle) FROM tbl_rebate where Party='"+partyname+"' and Cur_Date like '%"+month+"%' GROUP BY Brand_Name",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());

                Log.i("",""+al.toString());


                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getPartyRebate(String partyname) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
            Cursor c = mydb.rawQuery("SELECT  Brand_Name,sum(Rebate),sum(Cases),sum(Bottle) FROM tbl_rebate where Party='"+partyname+"' GROUP BY Brand_Name",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());


                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getPartyRebateByBrand(String partyname,String brandname) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,all;
        all=new Vector();
        try {
            Cursor c = mydb.rawQuery("SELECT * FROM tbl_rebate where Brand_Name='"+brandname+"' and Party='"+partyname+"'",null);
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                //   Log.i("0",c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                //   Log.i("1",c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                //   Log.i("2",c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                //   Log.i("3",c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                //   Log.i("4",c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                //   Log.i("5",c.getString(5).trim());
                if(c.getString(6).trim().equals(""))
                {
                    al.addElement(0);
                }
                else {
                    al.addElement(c.getString(6).trim());
                }
                //   Log.i("6",c.getString(6).trim());
                al.addElement(c.getString(7).trim());
                //   Log.i("7",c.getString(7).trim());
                all.addElement(al);

            }
            return all;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getRebateDetailsByCatagity(String PartyName,String BrandName)
    {
        try
        {
            SQLiteDatabase mydb = null;
            model = Model.getInstance();
            mydb = this.getReadableDatabase();
            Vector all;
            all=new Vector();
            try {
                Cursor c = mydb.rawQuery("SELECT SUM(Cases) as cs,SUM(Bottle)as btl,SUM(Rebate) as rbt FROM tbl_rebate where Brand_Name='"+BrandName+"' and Party='"+PartyName+"'",null);
                while (c.moveToNext()) {
                    all = new Vector();
                    all.addElement(c.getString(0).trim());
                    //   Log.i("0",c.getString(0).trim());
                    all.addElement(c.getString(1).trim());
                    //   Log.i("1",c.getString(1).trim());
                    all.addElement(c.getString(2).trim());

                }
                return all;
            } catch (Exception e) {
                Log.e("Error ",""+e.getMessage());
                return null;
            } finally {
                mydb.close();
            }
        }
        catch (Exception ex){return null;}
    }
    public Vector  getSalesmanRebateMonthCount(String salesman) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al=new Vector();
        Vector v=new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c = null;
                if (i < 10) {
                    c = mydb.rawQuery("SELECT  sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where c.Salesman='" + salesman + "' and c.Name=r.Party and strftime('%m', Cur_Date) = '0" + i + "'", null);
                } else {
                    c = mydb.rawQuery("SELECT  sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where c.Salesman='" + salesman + "' and c.Name=r.Party and strftime('%m', Cur_Date) = '0" + i + "'", null);
                }
                while (c.moveToNext()) {
                    al = new Vector();
                    al.addElement(c.getString(0).trim());
                    al.addElement(c.getString(1).trim());
                    al.addElement(c.getString(2).trim());
                    al.addElement(c.getString(3).trim());
                    al.addElement(c.getString(4).trim());
                    al.addElement(c.getString(5).trim());

                    v.addElement(al);
                }
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAdminRebateMonthCount() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al=new Vector();
        Vector v=new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c = null;
                if (i < 10) {
                    c = mydb.rawQuery("SELECT  sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where  c.Name=r.Party and strftime('%m', Cur_Date) = '0" + i + "'", null);
                } else {
                    c = mydb.rawQuery("SELECT  sum(r.Rebate),sum(r.Cases),sum(r.Bottle),c.Salesman,c.Name FROM tbl_rebate r,tbl_customer c where  c.Name=r.Party and strftime('%m', Cur_Date) = '0" + i + "'", null);
                }
                while (c.moveToNext()) {
                    al = new Vector();
                    al.addElement(c.getString(0).trim());
                    al.addElement(c.getString(1).trim());
                    al.addElement(c.getString(2).trim());
                    al.addElement(c.getString(3).trim());
                    al.addElement(c.getString(4).trim());
                    al.addElement(c.getString(5).trim());

                    v.addElement(al);
                }
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getPartyRebateMonthCount(String party) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al=new Vector();
        Vector v=new Vector();
        try {
            for(int i=1;i<=12;i++) {
                Cursor c = null;
                if (i < 10) {
                    c = mydb.rawQuery("SELECT  sum(Rebate),sum(Cases),sum(Bottle),Party FROM tbl_rebate where Party='"+party+"' and strftime('%m', Cur_Date) = '0" + i + "'", null);
                } else {
                    c = mydb.rawQuery("SELECT  sum(Rebate),sum(Cases),sum(Bottle),Party FROM tbl_rebate where Party='"+party+"' and strftime('%m', Cur_Date) = '" + i + "'", null);
                }
                while (c.moveToNext()) {
                    al = new Vector();
                    al.addElement(c.getString(0).trim());
                    al.addElement(c.getString(1).trim());
                    al.addElement(c.getString(2).trim());
                    al.addElement(c.getString(3).trim());
                    al.addElement(c.getString(4).trim());
                    al.addElement(c.getString(5).trim());

                    v.addElement(al);
                }
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getAllRebateByPartyName(String party) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        mydb = this.getReadableDatabase();
        Vector al,v;
        try {
          /*  String cols[] = { "Id","Party","Brand_Name","Cur_Date","Cases","Bottle","Rebate","Company"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_rebate",cols,"Party=?",
                    new String[] { party }, null,
                    null, null);*/
            Cursor c = mydb.rawQuery("select Id,Party,Brand_Name,Cur_Date,Cases,Bottle,Rebate,Company  from tbl_rebate where Party='"+party+"'",null);
            v=new Vector();
            while (c.moveToNext()) {
                al = new Vector();
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                al.addElement(c.getString(6).trim());
                al.addElement(c.getString(7).trim());
                v.addElement(al);
            }
            return v;
        } catch (Exception e) {
            Log.e("Error ",""+e.getMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
    public boolean updateOrderUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                Vector v1=(Vector)v.elementAt(i);
                String ID=v1.elementAt(0).toString();
                // Log.e("Record "+(i+1),""+ID);
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_order", data, "Id=?", new String[]{""+ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            Log.e("Error Update ",""+e.getMessage());
            return false;
        }
    }
    public boolean updateLocationUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                Vector v1=(Vector)v.elementAt(i);
                String ID=v1.elementAt(0).toString();
                // Log.e("Record "+(i+1),""+ID);
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_location_details", data, "Voucher_No=?", new String[]{""+ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            Log.e("Error Update ",""+e.getMessage());
            return false;
        }
    }
    public boolean updateRefDetUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                Vector v1=(Vector)v.elementAt(i);
                String ID=v1.elementAt(3).toString();
                // Log.e("Record "+(i+1),""+ID);
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_receipt_references", data, "Rec_Number=?", new String[]{""+ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            Log.e("Error Update ",""+e.getMessage());
            return false;
        }
    }
    public boolean updateSingleOrderUpload(String id) {
        try {
            // Log.e("Record "+(i+1),""+ID);
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Is_Uploaded", "YES");
            mydb.update("tbl_order", data, "Id=?", new String[]{""+id});
            mydb.close();

            return true;
        } catch (Exception e) {
            Log.e("Error Update ",""+e.getMessage());
            return false;
        }
    }
    public void updateStockUpload(Vector v) {
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Is_Uploaded", "YES");
            mydb.update("tbl_stock_details", data, null, null);
            mydb.close();

        } catch (Exception e) {
            Log.e("Error Update ",""+e.getMessage());

        }
    }
    public boolean updateOrderDetUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                Vector v1=(Vector)v.elementAt(i);
                String ID=v1.elementAt(0).toString();
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_order_det", data, "Id=" + ID, null);
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateSingleOrderDetUpload(String id) {
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Is_Uploaded", "YES");
            mydb.update("tbl_order_det", data, "Id=" + id, null);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateRecSequenceUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                String ID=v.elementAt(i).toString();
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_receipt_sequence", data, "Rec_Date=?", new String[]{ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateReceiptUpload(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                String ID=v.elementAt(i).toString();
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_receipt", data, "Rec_No=?", new String[]{ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateSingleReceiptUpload(String recno) {
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("Is_Uploaded", "YES");
            mydb.update("tbl_receipt", data, "Rec_No=?", new String[]{recno});
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateReceiptUploadAll(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                String ID=v.elementAt(i).toString();
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_receipt_all", data, "Rec_No=?", new String[]{ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateReceiptUploadDetails(Vector v) {
        try {
            for(int i=0;i<v.size();i++) {
                String ID=v.elementAt(i).toString();
                SQLiteDatabase mydb = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("Is_Uploaded", "YES");
                mydb.update("tbl_receipt_details", data, "Rec_No=?", new String[]{ID});
                mydb.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Vector getNotUploadedReceipt() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode", "Is_Uploaded","Cur_Date","Instrument_Number","Instrument_Date"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt",cols, "Is_Uploaded=?",new String[]{"No"},null,null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                row.addElement(c.getString(6).trim());
                row.addElement(c.getString(7).trim());
                row.addElement(c.getString(8).trim());
                row.addElement(c.getString(9).trim());
                al.addElement(row);
            }
           return al;
        } catch (Exception e) {
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getAllNotUploadedReceipt() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Rec_No", "Rec_Date", "Party", "Salesman", "Amount", "Paymode", "Is_Uploaded","Instrument_Number","Instrument_Date"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_all",cols, "Is_Uploaded=?",new String[]{"No"},null,null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                row.addElement(c.getString(6).trim());
                row.addElement(c.getString(7).trim());
                row.addElement(c.getString(8).trim());
                al.addElement(row);
            }
            return al;
        } catch (Exception e) {
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getAllNotUploadedReceiptDetails() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"ID","Party_Name","Cur_Date","Rec_No","Salesman","Is_Uploaded"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_details",cols, "Is_Uploaded=?",new String[]{"No"},null,null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                row.addElement(c.getString(4).trim());
                row.addElement(c.getString(5).trim());
                al.addElement(row);

            }
            return al;
        } catch (Exception e) {
            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector getAllRecSequence() {
        SQLiteDatabase mydb = null;
        //Log.i("Date is",""+Date);
        Vector rec=new Vector();
        try {

            String[] cols = {"ID","Rec_Date","Sequence"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_receipt_sequence",cols,"Is_Uploaded=?",
                    new String[]{"NO"}, null,
                    null, null);
            while (c.moveToNext()) {
                Vector data=new Vector();
                data.addElement(c.getString(0));
                data.addElement(c.getString(1));
                data.addElement(c.getString(2));
                rec.addElement(data);
            }
            return rec;
        } catch (Exception e) {
            Log.e("Error",""+e.getLocalizedMessage());
            return rec;
        } finally {
            mydb.close();
        }
    }
    public Vector getAllNotUploadedLocationDetails() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al,row;
        try {
            String[] cols = {"Voucher_No","Voucher_Type","Location","Is_Uploaded"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_location_details",cols, "Is_Uploaded=?",new String[]{"No"},null,null,null,null);
            al = new Vector();
            while (c.moveToNext()) {
                row=new Vector();
                row.addElement(c.getString(0).trim());
                row.addElement(c.getString(1).trim());
                row.addElement(c.getString(2).trim());
                row.addElement(c.getString(3).trim());
                al.addElement(row);

            }
            return al;
        } catch (Exception e) {
            Log.e("Loc sel error",""+e.getLocalizedMessage());
            return null;
        } finally {
            mydb.close();
        }
    }
}
