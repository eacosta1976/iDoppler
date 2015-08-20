/*****************************************************************************************
 * MODULE:  AudioJackStatus.java
 * Class: AudioJackStatus 
 * Implements:  The class to manage the AudioJack Port Status (Busy, not Busy)
 *  
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/21/2014
 *  
 ********************************************************************************************/


package com.Doppler.idoppler;
import java.lang.Object;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/***************************************************************************************************
 * Class: AudioJackStatus  
 * Receives a Broadcast receiver when the AudioJack PORT changes Status (Connected, not Connected)
 ***************************************************************************************************/
		
public class AudioJackStatus extends BroadcastReceiver
{
     private int intAudioStatus = 0;   //Initialize as iDoppler not connected
     Context ctx;
     
     
     /*****************************************
      * Constructor
      *****************************************/
     public AudioJackStatus(Context ctx)
     {
    	 this.ctx = ctx;
     }
     
    /********************************************************************************************************* 
     * Receive from OS, and Audio Headset Plug intent
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     ********************************************************************************************************/
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		final String action = intent.getAction();
		
		if (Intent.ACTION_HEADSET_PLUG.equals(action))
		{
			Log.d("HeadSetPlugInTest", "state: " + intent.getIntExtra("state", -1));
            Log.d("HeadSetPlugInTest", "microphone: " + intent.getIntExtra("microphone", -1));
        	int state = intent.getIntExtra("state", -1);
			if(state == 0)   intAudioStatus = ctx.getResources().getInteger(R.integer.AUDIO_JACK_OUT);     //Audio Jack Out
			if(state == 1)   intAudioStatus = ctx.getResources().getInteger(R.integer.AUDIO_JACK_IN);      //Audio Jack In
			else intAudioStatus = ctx.getResources().getInteger(R.integer.AUDIO_JACK_OUT);               //Other State
		}
	}
	
	/*******************************************************
	 * Method: getAudioJack
	 * returns the Audio Jack Status Variable
	 ******************************************************/
	public int getAudioJack_Status()
	{
		return intAudioStatus;
	}
	
	/******************************************************
	 * Reset Status
	 ******************************************************/
    public void reset_AudioJack_Status()
    {
    	intAudioStatus = ctx.getResources().getInteger(R.integer.AUDIO_JACK_OTHER);
    }
    
} // End Class
