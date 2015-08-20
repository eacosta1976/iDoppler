/*****************************************************************************************
 * MODULE:  MainDoppler
 * Class: MainDoppler
 * Implements:  Activity Main Doppler
 *  
 * Objective: Iplements the Main GUI to Get the Audio Baby Beats. it starts the AudioClipRecorder 
 * thread to manage the Audio Signal
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/15/2014
 *  
 ********************************************************************************************/



package com.Doppler.idoppler;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import android.os.Bundle;
import java.io.IOException;
import android.util.Log;

/************************************************************************
 * Class: MainDoppler
 * @author Edgar Acosta
 * Description: Main Activity to Start Reading and recording the Baby HeartBeat
 * Date:  05/20/2014 
 ************************************************************************/

public class MainDoppler extends Activity 
{
	//Audio Port Broadcast receiver
	AudioJackStatus headsetReceiver;
	
	//GUI Objects 
	private TextView cmdStart;
    private TextView cmdStop;
	private TextView cmdReproduce;
	private ImageView imgHearthPulse;
	private Button cmdEdit;
	private Button cmdIdiom;
	
	// Thread, Handlers and Bundles
	private Thread audioThread = null;
	Bundle b;
	private Handler hd;
	
	// Audio Objects
	LoudNoiseDetector NoiseDetector;
	AudioClipRecorder AudioRecorder;
	
	// Status Variables
	private boolean bolRecording = false;
	
	/**************************************************************
	 * Handler to Receive Messages from Audio Thread.
	 *************************************************************/
	final Handler audioHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			Bundle b;
	        if(msg.what == 1)
	        {
	            b=msg.getData();
	            int intBeat  = b.getInt("beat");
	            updateUI(intBeat);    
	        }
	       
			super.handleMessage(msg);
		}
	};
	
	
       
	@Override
	/*******************************************************************
	 * Activity onCreate Method
	 ******************************************************************/
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_doppler);
		
		//Register Audio Jack Broadcast Receiver
		registerAudioJack_BroadcastReceiver();
		
		// Buttons
		cmdStart = (TextView)findViewById(R.id.cmdStartRecording);
		cmdStop = (TextView)findViewById(R.id.cmdStopRecording);
		cmdReproduce = (TextView)findViewById(R.id.cmdReproduce);
		cmdEdit = (Button)findViewById(R.id.cmdEditMotherData);
		cmdIdiom = (Button)findViewById(R.id.cmdLanguageData);

		
		// Intialize Image Hearth Not Pulse 
		imgHearthPulse = (ImageView)findViewById(R.id.imgHearth);
	   	imgHearthPulse.setImageResource(R.drawable.hearth0);
	   	
	   	// CREATE OBJECTS
	   	NoiseDetector = new LoudNoiseDetector();

	   	
	   	/*****************************************************************************
	   	 * METHOD: cmdStart
	   	 * It Starts the Thread to get the Hearth Signal and recording
	   	 *****************************************************************************/
	 	cmdStart.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
  			{
				if(headsetReceiver.getAudioJack_Status() == getResources().getInteger(R.integer.AUDIO_JACK_IN))
				{
				    Toast.makeText(getBaseContext(),getString(R.string.startDoppler) , Toast.LENGTH_SHORT).show();
				    bolRecording = true;
				    getHearthBeat();    // Starts Hearing Hearth Beat
				}
				else
				{
					Toast.makeText(getBaseContext(), getString(R.string.errorDopplerNotConnected), Toast.LENGTH_LONG).show();
				}
	   	    }
		});
		
		/*****************************************************************************
	   	 * METHOD: cmdStop
	   	 * It Stops the Audio Thread
	   	 *****************************************************************************/
		cmdStop.setOnClickListener(new OnClickListener()
		{
		    public void onClick(View v)
		    {
		    	if(bolRecording == true)    //If Audio is Recording
		    	{
		    	    stopHearthBeat();   // Stops Hearth beat
		     	    bolRecording = false;
		     	    updateUI(getResources().getInteger(R.integer.HEARTH_DOWN));  //Update GUI with Hearth Down      
		    	    Toast.makeText(getBaseContext(),getString(R.string.stopDoppler), Toast.LENGTH_SHORT).show();
		    	}
		    }
		});
		
		/*********************************************************************************
		 * METHOD: cmdReproduce
		 * Reproduce the Sound recorded
		 *********************************************************************************/
		cmdReproduce.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(headsetReceiver.getAudioJack_Status() == getResources().getInteger(R.integer.AUDIO_JACK_OUT))
				{
					if(bolRecording == false)
					{
						try
						{
                            Toast.makeText(getBaseContext(), getString(R.string.reproducingBabyBeats), Toast.LENGTH_SHORT).show();
             	            AudioClipRecorder A = new AudioClipRecorder();
				            int intPlayResp = A.playAudioFile();
				            
				            if(intPlayResp == -1)   // There is no file to play
				            	Toast.makeText(getBaseContext(), getString(R.string.noFileToPlay), Toast.LENGTH_SHORT).show();
						}
						catch(IOException e)  // File corrupted
						{
							Toast.makeText(getBaseContext(), getString(R.string.errorPlayingFile), Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(getBaseContext(), getString(R.string.errorStillRecordingBeats), Toast.LENGTH_SHORT).show();
					}
			    }
				else
				{
					Log.d("estado", "estado= " + headsetReceiver.getAudioJack_Status());
					Toast.makeText(getBaseContext(), getString(R.string.errorPlayingBabyBeats_DopplerConnected), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		/********************************************************************************
		 * Method: cmdEdit
		 * It call the motherDataScreen to edit the Mother data
		 *******************************************************************************/
		cmdEdit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(bolRecording == false)
				{
				    Intent i = new Intent(MainDoppler.this, MotherDataScreen.class);
				    startActivity(i);
				}
			}
	   		
		});
	
		/******************************************************************************
		 * Method: cmdIdiom
		 * It calls the selectIdiom activity
		 *****************************************************************************/
		cmdIdiom.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(bolRecording == false)
				{
				    Intent i = new Intent(MainDoppler.this, selectIdiom.class);
			    	i.putExtra("callForm", MainDoppler.class.toString());
				    startActivity(i);
				}
			}
			
		});
		
   
   } // END onCreate
	
	/*****************************************************
	 *  On Stop Event
	 * (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 ****************************************************/
  	@Override
	protected void onStop()
  	{
		// TODO Auto-generated method stub
		super.onStop();
		unregisterAudioJack_BoradcastReceiver();
	}
    
  	/*****************************************************
  	 * On Resume Event
  	 * (non-Javadoc)
  	 * @see android.app.Activity#onResume()
  	 *****************************************************/
  	
  	@Override
	protected void onResume() 
  	{
		// TODO Auto-generated method stub
		super.onResume();
		registerAudioJack_BroadcastReceiver();
	}
  	
  	
	/******************************************************************
  	 * Method: stopHearthBeat()
  	 * It Stops the Audio Thread
  	 *******************************************************************/
	private void stopHearthBeat()
	{
		if(audioThread != null)
		{
   		     audioThread.interrupt();
   		     audioThread = null;
   		     updateUI(0);  
   		     AudioManager auManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
  		     auManager.setMode(AudioManager.MODE_NORMAL);
		}
	}
	
	/*************************************************************************
	 * Method: 	getHearthBeat()
	 * It Starts the Audio Thread that gets the Sound from Audio Jack.
	 **********************************************************************/
	private void getHearthBeat()
	{
		 if(AudioClipRecorder.hasMicrophone(MainDoppler.this) == true)
		 {
			 
		    if(AudioClipRecorder.isHearingHearthBeat() == false)
		    {
		    	 AudioClipRecorder.cleanIDoppler_Directory();
				 audioThread = new AudioClipRecorder(NoiseDetector,audioHandler,this);
				 audioThread.start();
	        }
		 } 
	}
	
	/****************************************************************************
	 * Update Graphical User Interface 
	 * @param intBeat
	 * ************************************************************************/
	private void updateUI(int intBeat)
	{
        if(intBeat == getResources().getInteger(R.integer.HEARTH_UP))           // Hearth UP
        {
        	imgHearthPulse.setImageResource(R.drawable.hearth1); 
        }
        else if(intBeat == getResources().getInteger(R.integer.HEARTH_DOWN))     // Hearth DOWN
        {
        	imgHearthPulse.setImageResource(R.drawable.hearth0);	
        }
        else if(intBeat == getResources().getInteger(R.integer.ERROR_AUDIO_NOT_INITIALIZED))   // ERROR -1: ERROR Audio not Initialized
        {
        	Toast.makeText(this,getString(R.string.errorAudioNotInitialized) , Toast.LENGTH_LONG).show();
        }
        else if(intBeat == getResources().getInteger(R.integer.ERROR_INVALID_OPERATION))   // ERROR -2: ERROR Invalid Operation
        {
        	Toast.makeText(this,getString(R.string.errorAudioInvalidOperation) , Toast.LENGTH_LONG).show();
        }
        else if(intBeat == getResources().getInteger(R.integer.ERROR_BAD_VALUE))   // ERROR -3: ERROR Bad Value
        {
        	Toast.makeText(this,getString(R.string.errorAudioBadValue) , Toast.LENGTH_LONG).show();
        }
        else if(intBeat == getResources().getInteger(R.integer.ERROR_BUFFER_SIZE))   // ERROR -4: ERROR Creating Buffer Size
        {
        	Toast.makeText(this,getString(R.string.errorCreatingBufferSize), Toast.LENGTH_LONG).show();
        }
        else if(intBeat == getResources().getInteger(R.integer.ERROR_SD_CARD_PATH))   // ERROR -4: ERROR SD Card Path Error
        {
        	Toast.makeText(this,getString(R.string.errorSDCardPathError), Toast.LENGTH_LONG).show();
        }
	}
	
	/***********************************************************************
     * Register Action Headset Plug to detect if Headset port Change Status
     ***********************************************************************/
    public void registerAudioJack_BroadcastReceiver()
    {
       headsetReceiver = new AudioJackStatus(this);
       registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }
    
    /************************************************************************
     * Unregister Action Headset Plug to detect of headset port change status
     ************************************************************************/
    public void unregisterAudioJack_BoradcastReceiver()
    {
    	unregisterReceiver(headsetReceiver);
    }

} //*** End Class

