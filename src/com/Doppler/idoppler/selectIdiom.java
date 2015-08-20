/*****************************************************************************************
 * MODULE:  selectIdiom.java
 * Class: selectIdiom 
 * Implements:  GUI to manage localization (Languages spanish - english)
 *
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/06/2014
 *  
 ********************************************************************************************/





package com.Doppler.idoppler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;

/*
 * Class selectIdiom
 * Implements the User Interface and methods to Select the Idiom
 */

public class selectIdiom extends Activity 
{
    private Button btnNext;
    private Spinner spinIdiom;
    private TextView lblIdiomSelected;
    private Intent I;
    private String currentIdiom;
    private int intForm = 0;
    
    CIdiom Idiom; 
	
	/*
	 * onCreate Event
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectidiom);
		
		//Detect the called form
		Bundle bundle = getIntent().getExtras();
		if(bundle.getString("callForm").equals(SplashScreen.class.toString()))
		{
			intForm = 1;
		}
		else if((bundle.getString("callForm").equals(MainDoppler.class.toString())))
		{
			intForm = 2;
		}
		
		// Get Widgets References
		btnNext = (Button)findViewById(R.id.cmdIdiomNext);
		spinIdiom = (Spinner)findViewById(R.id.spinIdiom);
		lblIdiomSelected = (TextView)findViewById(R.id.lblIdiomSelected);
		
		
		//Get current Language setup and displayed in Label
		Idiom = new CIdiom(this);   
		currentIdiom = Idiom.getCurrentLanguage();
		lblIdiomSelected.setText(currentIdiom);   
		
		// Update Spinner to default App Language
		if(currentIdiom.equals(getString(R.string.englishLan)))
		      spinIdiom.setSelection(this.getResources().getInteger(R.integer.POSENGLISH));
		
		else if(currentIdiom.equals(getString(R.string.espanolLan)))
			  spinIdiom.setSelection(this.getResources().getInteger(R.integer.POSESPANOL));
		
		/*
		 * Button Next Implementation
		 */
		btnNext.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
			     
				if(intForm == 1)        //Pass to Mother Data Activity
		   		      I = new Intent(selectIdiom.this,MotherDataScreen.class);	
				else if(intForm == 2)  // Pass to MainDoppler Activity
					  I = new Intent(selectIdiom.this,MainDoppler.class);
			    
				startActivity(I);
			}
	
	   });
		
		/*
		 * Spinner Idiom Implementation
		 */
	   spinIdiom.setOnItemSelectedListener(new OnItemSelectedListener()
	   {

		   String strSelected = "";
		   @Override
		    public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long arg3) 
		    {
			   strSelected = parent.getItemAtPosition(pos).toString();
	           
			   //Record App Language selected in App Settings
			   if(strSelected.equals(getString(R.string.english)))
	        	      Idiom.setLanguage(getString(R.string.englishLan));
			   else if(strSelected.equals(getString(R.string.espanol)))	   
				      Idiom.setLanguage(getString(R.string.espanolLan));
   	           
			   lblIdiomSelected.setText(getString(R.string.curLanguage) + " " + strSelected);
		    }

		   @Override
		   public void onNothingSelected(AdapterView<?> arg0) {
			     // TODO Auto-generated method stub
			
		   }
	   });	
		
	}
   
}
