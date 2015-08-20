/*****************************************************************************************
 * MODULE:  ReproduceBeatRT.java
 * Interface: ReproduceBeatRT.class 
 * Implements:  An Intent Service (Thread) That receives from AudioClipRecorder thread
 * an Audio Buffer in raw bytes to reproduce the Audio Signal in real time. This thread has
 * an Audio Priority
 *
 * Software Engineer:  Edgar Acosta Davila
 * Date:  07/10/2014
 *  
 ********************************************************************************************/

package com.Doppler.idoppler;

import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;


/**
 * Class: ReproduceBeatRT
 * @author edgaracosta
 * This class is an Intent Service class Thread, only to PlayBack the Hearth Beat Buffer on the doppler speaker
 */

public class ReproduceBeatRT  extends IntentService
{
	AudioTrack mAudioTrack = null;
	private static final int RECORDER_SAMPLERATE =  44100;
	int bufferSize = 0;
	
	/*
	 * OnCreate Event
	 * Intialize AudioManager to play the file with the proper settings for real time playing
	 * 
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		
		AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		//audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        
		bufferSize = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		bufferSize *= 3;
	    mAudioTrack = new AudioTrack(audioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
	}


	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	
	}

	// Constructor
	public ReproduceBeatRT() 
	{
		super(ReproduceBeatRT.class.getName());
		setIntentRedelivery(false);
	}

	/*
	 * onHandleIntent Event
	 * Here we reproduce the Buffer in Real time and record the file
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);    // Set Priority of Thread to 10
		Bundle extras = intent.getExtras();  
		short[] arrBeats = extras.getShortArray("BEATS");
		
	
		/*  for debuging purposes
		if(mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED)
		{
	
	 	        Log.i("NO INICIALIZADO ", Integer.toString(bufferSize));
		}
		else
		{
			
			   Log.i("OK YA INICIALIZO ", Integer.toString(bufferSize));
		}
		*/
			
	    if(mAudioTrack != null  &&  mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED)
	    {
	   		if(mAudioTrack.getPlaybackRate() != AudioTrack.PLAYSTATE_PLAYING)
	   		{
	   	
				mAudioTrack.play();
	 		}
			mAudioTrack.write(arrBeats, 0, arrBeats.length);
	  	}
	}// *** END METHOD *************************************

}  // End Class
