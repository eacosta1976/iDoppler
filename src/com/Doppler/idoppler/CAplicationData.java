/*****************************************************************************************
 * MODULE:  CApplicationData.java
 * Class: CApplicationData
 * Implements:  The Methods to manage using Shared Preferences the user data of the application
 * these data is:    
 *  
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/21/2014
 *  
 ********************************************************************************************/



package com.Doppler.idoppler;

import android.content.Context;
import android.content.SharedPreferences;

public class CAplicationData {

	private SharedPreferences prefs;
	private Context ctx;
	private boolean bolFirstTimeRun;
	private String PREF_APPDATA = "AppData";
	
	//Preferences
	private final String FIRST_TIME_RUN = "FIRST_TIME_RUN";
    private final String IDIOM = "IDIOM";
	 
	/*
	 * Constructor
	 */
	public CAplicationData(Context currentContext)
	{
		ctx = currentContext;
	}
	
	/*
	 * isFirstTimeRun 
	 */
	public boolean isFirstTimeRun()
	{
		return bolFirstTimeRun();
	}
	
	//=======================================================================
	// private interface
	//======================================================================
	
	/*
	 * Method: bolFirstTimeRun
	 * @return: true if first time run false otherwise
	 */
	
	private boolean bolFirstTimeRun()
	{
		prefs = ctx.getSharedPreferences(PREF_APPDATA, ctx.MODE_PRIVATE);
		bolFirstTimeRun = prefs.getBoolean(FIRST_TIME_RUN, true);
		
		if(bolFirstTimeRun == true)
		{
			
			SharedPreferences.Editor editor = prefs.edit();    // Get Editor and set First Time Run to false forever
			editor.putBoolean(FIRST_TIME_RUN, false);         // Set to First Time Run to False
			editor.commit();
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
