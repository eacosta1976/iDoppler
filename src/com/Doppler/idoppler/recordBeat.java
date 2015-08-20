
/*****************************************************************************************
 *THIS CLASS IS NOT USED
 *  
 ********************************************************************************************/


package com.Doppler.idoppler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Service;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;



public class recordBeat extends IntentService{

	 private static final String AUDIO_RECORDER_FOLDER = "BeatRecorder";
	 private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.pcm";
	 FileOutputStream os = null;
	 
	/**
	 * Constructor
	 * @param name
	 */
	public recordBeat(String name)
	{
		super(recordBeat.class.getName());
		// TODO Auto-generated constructor stub
	}
	

	/*
	 * OnCreate Event
	 * @see android.app.IntentService#onCreate()
	 */

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Create temporal file name
        String filename = getTempFilename();
        try 
        {
      	    os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }

	}




	@Override
	protected void onHandleIntent(Intent intent) 
	{
		
		 //os.write(bData, 0, bufferSize * BytesPerElement);
	}

	@Override
	public void onDestroy() 
	{
		
		super.onDestroy();
	}	
	
	/***************************************************************************
	 * Method: getTempFilename()
	 * Get the Path + temporal file name to be used in the raw audio file
	 * @return File Path
	 ***************************************************************************/
	
	private String getTempFilename()
    {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath,AUDIO_RECORDER_FOLDER);
            
            if(!file.exists()){
                    file.mkdirs();
            }
            
            File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
            
            if(tempFile.exists())
                    tempFile.delete();
            
            return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }
	
}
