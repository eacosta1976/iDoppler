/*****************************************************************************************
 * MODULE:  CMotherData.java
 * Class: CMotherData
 * Implements:  OPerations to record and get all the Mother Data using shared preferences
 *  
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  06/20/2014
 *  
 ********************************************************************************************/

package com.Doppler.idoppler;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class: CMotherData
 * It manages all data related to the mother (Mother name, baby name, due date, email)
 * @author Edgar Acosta
 *
 */

public class CMotherData
{
     private String strMotherName;
     private String strBabyName;
     private String strDueDate;
     private String strEmail;
     
     // Shared Preferences settings
     private SharedPreferences prefs;
     private final String prefName = "motherdata";
     private final String FIRST_TIME_RUN = "FIRST_TIME_RUN";
     private final String MOTHER_NAME = "MOTHER_NAME";
     private final String BABY_NAME = "BABY_NAME";
     private final String DUEDATE = "DUEDATE";
     private final String EMAIL = "EMAIL";
     
     // Context Object 
     private Context ctx;
     
     /**
      * Constructor
      */
     public CMotherData(Context currentContext)
     {
    	 ctx = currentContext;
     }
      
     /**
      * Register Mother Data in Shared Preferences
      */
     public boolean registerMotherData(String motherName, String babyName, String DueDate, String Email)
     {
    	 strMotherName = motherName;
         strBabyName = babyName;
         strDueDate = DueDate;
    	 strEmail = Email;
    	 
         prefs = ctx.getSharedPreferences(prefName, ctx.MODE_PRIVATE);
         SharedPreferences.Editor editor = prefs.edit();   // Get Editor
    	 
         // Record SharePreferences Settings Data
         editor.putString(MOTHER_NAME, strMotherName);
         editor.putString(BABY_NAME, strBabyName);
         editor.putString(DUEDATE, strDueDate);
         editor.putString(EMAIL,strEmail);
         if(editor.commit() == true)
        	 return true;
         else
        	 return false;
     }
     
     /**
      * Method: getMotherData
      * Read the Shared Preferences Mother Data and return the object CMData with data
      * @return
      */
     
     public CMData getMotherData()
     {
    	 CMData mdata = new CMData();
    	 
    	 prefs = ctx.getSharedPreferences(prefName, ctx.MODE_PRIVATE);
    	 strMotherName = prefs.getString(MOTHER_NAME, ""); 
    	 strBabyName = prefs.getString(BABY_NAME,"");
    	 strDueDate = prefs.getString(DUEDATE, "");
         strEmail = prefs.getString(EMAIL,"");
         
         mdata.MotherName = strMotherName;
         mdata.BabyName = strBabyName;
         mdata.DueDate = strDueDate;
         mdata.Email = strEmail;
         
         return mdata;
     }
     
     /*
      * Reset Mother Data
      * Reset the Mother Data Settings
      */
     public boolean resetMotherData()
     {
    	 strMotherName = "";
         strBabyName = "";
         strDueDate = "";
         strEmail = "";
    	 
         prefs = ctx.getSharedPreferences(prefName, ctx.MODE_PRIVATE);
         SharedPreferences.Editor editor = prefs.edit();   // Get Editor
    	 
         // Record SharePreferences Settings Data
         editor.putString(MOTHER_NAME, strMotherName);
         editor.putString(BABY_NAME, strBabyName);
         editor.putString(DUEDATE, strDueDate);
         editor.putString(EMAIL,strEmail);
          
         if(editor.commit() == true)
        	 return true;
         else
        	 return false;
    	 
     }
     
} // End Class
