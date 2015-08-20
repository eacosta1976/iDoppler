/*****************************************************************************************
 * MODULE:  splashScreen.java
 * Class: splashScreen 
 * Implements: GUI Splash Screen of the application
 *  
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/06/2014
 *  
 ********************************************************************************************/



package com.Doppler.idoppler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.IntentFilter;

/*************************************************
 * Class: SpashScreen
 * It Implements the iDoppler welcome screen
 * @author Edgar Acosta 
 ************************************************/
public class SplashScreen extends Activity
{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4500;
    CAplicationData appData = new CAplicationData(this);   //Application Data
    boolean bolFirstTimeRun = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.splashscreen);
    	
    	bolFirstTimeRun = appData.isFirstTimeRun();    //Verify if First Time Run
    	
    	/* 
         * Showing splash screen with a timer. 
         */
         new Handler().postDelayed(new Runnable()
         {
        	 public void run()
        	 {
        		 // This method will be executed once the timer is over
                 // Start your app main activity
        		 if(bolFirstTimeRun == true)
        		 {
        			 Intent i = new Intent(SplashScreen.this, selectIdiom.class);
        			 i.putExtra("callForm", SplashScreen.class.toString());
        	         startActivity(i);
        		 }
        		 else
        		 {
        			 Intent i = new Intent(SplashScreen.this, MainDoppler.class);
                     startActivity(i);
        		 }
       
        		 // close this activity
                 finish();  		 
        	 }
         },SPLASH_TIME_OUT);
 
   } // End OnCreate
} // End Class
