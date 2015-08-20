/*****************************************************************************************
 * MODULE: CIdiom.java
 * Class: CIdiom
 * Implements: The operations to manage the Localization (English, Spanish) of the application messages
 *  
 * Software Engineer:  Edgar Acosta Davila
 * Date:  06/15/2014
 *  
 ********************************************************************************************/



package com.Doppler.idoppler;
import java.util.Locale;
import android.content.Context; 
import android.content.res.Configuration;

/*
 * Class: CIDiom 
 * To manage App Languge
 */

public class CIdiom 
{
	  private String strCurrentLanguage;
	  private Context ctx;
	   
	  /*
	   * Constructor, get the current language
	   */
      public CIdiom(Context ctxApp)
      {
    	  ctx = ctxApp;
    	  // Get Current Language from the System
    	  strCurrentLanguage = ctx.getResources().getConfiguration().locale.getDisplayName();
      }
     
      /*
       * Set Language
       * Configure into the Android System the current language
       */
      public void setLanguage(String strIdiom)
      {
    	   Locale locale; 
    	   locale = new Locale(strIdiom);
    	   Locale.setDefault(locale);
    	   
    	   Configuration conf = new Configuration();    // Configuration Object
    	   conf.locale = locale;
    	   ctx.getApplicationContext().getResources().updateConfiguration(conf, null);
    	   strCurrentLanguage = ctx.getResources().getConfiguration().locale.getDisplayName();
    	   
    	   conf = null;
    	   locale = null;
      }
      
      /*
       * getCurrentlanguage
       */
      public String getCurrentLanguage()
      {
    	  return strCurrentLanguage;
      }
     
      /*
       * setDefaultLanguage  (English)
       */
      public void setDefaultLanguage()
      {
    	  setLanguage("en_US");
      }
      
} //***************************** END CLASS **************************************************
