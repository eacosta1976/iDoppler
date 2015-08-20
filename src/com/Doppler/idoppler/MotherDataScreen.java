/*****************************************************************************************
 * MODULE:    MotherDataScreen.java
 * Class:     MotherDataScreen
 * Implements:  GUI to manage the Mother Data (Record and Read)
 *  
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/06/2014
 *  
 ********************************************************************************************/


package com.Doppler.idoppler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.Dialog;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;   // get access to the input system (to hide soft keyboard)


/*
 * Class: MotherDataScreen
 * It defines the Mother Data Screen Input
 */
public class MotherDataScreen extends Activity 
{
	//Widgets
	private EditText txtMotherName ;
	private EditText txtBabyName;
	private EditText txtDueDate ;
	private EditText txtEmail;
	private Button cmdNext;
	
	// Objects
	CAplicationData appData = null;   //Application Data
	CMotherData MotherData = null;
	CMData MDATA = null;  
	Calendar now;
	DateDialogFragment frag;
	
	
	/*************************************************
	 * On Create Event
	 *************************************************/
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.motherdatascreen);
    	
    	
    	txtMotherName = (EditText)findViewById(R.id.txtMotherName);
    	txtBabyName = (EditText)findViewById(R.id.txtBabyName);
    	txtDueDate = (EditText)findViewById(R.id.txtDueDate);
    	txtEmail = (EditText)findViewById(R.id.txtEmail);
    	cmdNext = (Button)findViewById(R.id.cmdMotherDataNext);
    	
    	appData = new CAplicationData(this);   //App Data Settings
    	
    	
    	now = Calendar.getInstance();
    	MotherData = new CMotherData(this);    // Mother data Settings
    	MDATA = new CMData();
    	
    	// A mother data exist in the Settings
    	if(!appData.isFirstTimeRun())
    	{
    		  // Edit Mother Data
    		 MDATA = MotherData.getMotherData(); 
    		 txtMotherName.setText(MDATA.MotherName);
    		 txtDueDate.setText(MDATA.DueDate);
    		 txtBabyName.setText(MDATA.BabyName);
    		 txtEmail.setText(MDATA.Email);
    	}
    	
    
    	/*****************************************************
    	 * txtDueDate click event , show the Date Picker Dialog Box
    	 *******************************************************/
        txtDueDate.setOnClickListener(new OnClickListener()
    	{

			@Override
			public void onClick(View v)
			{
				showDatePickerDialog();    
			}
    		
    	});
        
        /*
         * SetOnTouchListener event (On Focus)
         * remove the soft keyboard
         */
        
        txtDueDate.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				v.onTouchEvent(event);
				hideKeyboard(v);
				return true;
			}
        	
        	
        });
        
        /***********************************************
         * cmdNext Button Click Event
         **********************************************/
        cmdNext.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
			     saveMotherDataSettings();	
			     goToDopplerMain_Screen();
			}
        	
        });
    }  // End On Create

	/*******************************************************************
	 * Method: showDatePickerDialog
	 * It Shows a Date picker Dialog
	 ******************************************************************/
	public void showDatePickerDialog() 
	{
		
		FragmentTransaction ft = getFragmentManager().beginTransaction(); //get the fragment
    	frag = DateDialogFragment.newInstance(this, new DateDialogFragmentListener()
    	{
    		public void updateChangedDate(int year, int month, int day)
    		{
    			txtDueDate.setText("");
    			txtDueDate.setText(String.valueOf(month+1)+"-"+String.valueOf(day)+"-"+String.valueOf(year));
    			now.set(year, month, day);
    		}
    	}, now);
    	
    	frag.show(ft, "DateDialogFragment");
    	
    }
	
	
	/*************************************************************
	 * Method saveMotherDataSettings()
	 * It saves Mother Data Settings 
	 ************************************************************/
	public void saveMotherDataSettings()
	{
		String strMotherName = txtMotherName.getText().toString();
		String strBabyName = txtBabyName.getText().toString();
		String strEmail = txtEmail.getText().toString();
		String strDueDate = txtDueDate.getText().toString();
		
		if(MotherData.registerMotherData(strMotherName, strBabyName, strDueDate, strEmail) == true)
			Toast.makeText(MotherDataScreen.this, getString(R.string.motherdatareg_succes) , Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(MotherDataScreen.this, getString(R.string.motherdatareg_error) , Toast.LENGTH_SHORT).show();
	   
	}
	
	/**********************************************************
	 * goToMain_Screen
	 ********************************************************/
	public void goToDopplerMain_Screen()
	{
		Intent i = new Intent(MotherDataScreen.this,MainDoppler.class);
		startActivity(i);
	}
	
	/**********************************************************
	 * Hide the Soft Keyboard
	 *********************************************************/
	private void hideKeyboard(View v) 
	{   
		InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }                
	}
	
	
}  //*********************** End Class *******************************
