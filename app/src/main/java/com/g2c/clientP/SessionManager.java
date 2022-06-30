package com.g2c.clientP;



import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static  final String IS_LOGIN = "IsLoggedIn";
    public static  final String KEY_FULLNAME = "fullName";
    public static  final String KEY_UID = "uid";
    public static  final String KEY_EMAIL = "email";
    public static  final String KEY_TOKEN = "token";
    public static  final String KEY_LOCATION_Lat = "location_lat";
    public static  final String KEY_LOCATION_Lng = "location_lng";
    public static  final String KEY_LOCATION_Txt = "location_Txt";
    public static  final String KEY_PHONE = "phone";




    public SessionManager(Context _context){
        context = _context;
        usersSession = _context.getSharedPreferences("usersloginSession",Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public void createLoginSession(String UID, String fullName, String phone, String email, String location_lat, String loation_lng, String location_txt){
        editor.putBoolean(IS_LOGIN,true);

        /* Personal*/
        editor.putString(KEY_FULLNAME,fullName);
        editor.putString(KEY_UID,UID);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_LOCATION_Lat,location_lat);
        editor.putString(KEY_LOCATION_Lng,loation_lng);
        editor.putString(KEY_LOCATION_Txt,location_txt);








        editor.commit();
    }

    public HashMap<String,String> getUsersDetailsFromSessions(){
        HashMap<String,String> userData = new HashMap<String,String>();

        userData.put(KEY_FULLNAME,usersSession.getString(KEY_FULLNAME,null));
        userData.put(KEY_UID,usersSession.getString(KEY_UID,null));
        userData.put(KEY_PHONE,usersSession.getString(KEY_PHONE,null));
        userData.put(KEY_EMAIL,usersSession.getString(KEY_EMAIL,null));
        userData.put(KEY_LOCATION_Lat,usersSession.getString(KEY_LOCATION_Lat,null));
        userData.put(KEY_LOCATION_Lng,usersSession.getString(KEY_LOCATION_Lng,null));
        userData.put(KEY_LOCATION_Txt,usersSession.getString(KEY_LOCATION_Txt,null));
        userData.put(KEY_TOKEN,usersSession.getString(KEY_TOKEN,null));





        return  userData;
    }






    public Boolean checkLogin(){
        if(usersSession.getBoolean(IS_LOGIN,false)){
            return true;
        }else {
            return false;
        }

    }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }


}
