/*****************************************************************************************
 * MODULE:  AudioClipRecorder.java
 * Class: AudioClipRecorder 
 * Implements:  Thread
 *  
 * Objective: The AudioClipRecorder class is a Thread that implements the methods to Adquire an Audio 
 * Signal from AudioJackPort, call the class to process the signal, call the methods to record the signal
 * and call the Thread ReproduceBeatRT to reproduce the audio in Real Time 
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/21/2014
 *  
 ********************************************************************************************/

package com.Doppler.idoppler;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContextWrapper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/****************************************************************************************
 * Class: AudioClipRecorder
 * @author Edgar Acosta
 * Implements a THREAD and the methods to :
 *     - Read Audio Signal from microphone in a separate thread
 *     - Pass the signal to AudioClipListener for Audio Signal processing
 *     - Record for 20 seconds a Wav File
 * Date: 05/20/2014
 ****************************************************************************************/

public class AudioClipRecorder extends Thread
{
	// Class and Activity Objects
	private Context ctxCurrent;
	private Intent audioIntent;

	// Audio Objects
	private AudioRecord recorder;             // Android Audio Object
	private AudioClipListener clipListener;   // AudioClipListener Audio Signal Processing
	
	// Audio Settings
	public static final int RECORDER_SAMPLERATE_CD = 44100;
	public static final int RECORDER_SAMPLERATE_8000 = 8000;
	private static final int DEFAULT_BUFFER_INCREASE_FACTOR = 3;
	private boolean heard;
	int bufferResult;
	int sampleRate;
	int encoding;
	int bufferSize;
	int increaseRecordingBufferSize;
	short[] readBuffer = null;
		
	private boolean bolstart = false;
	int intb = 0;
	
	// Thread GUI Communication Objects and variables
	Intent intent;
	private Handler hd;
	Bundle b;
	 
	
	//Process variables
	static boolean bolHearing = false;
	static String filePath = "/Android/com.example.dopplerdemo/hbeat.pcm";
	FileOutputStream os = null;
	int BytesPerElement = 2; // 2 bytes in 16bit format
	byte bData[];
	
	//WAV file Settings
	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final String BABY_BEATS_FILE_NAME = "baby_beats";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_IDOPPLER_RECORDER_FOLDER = "iDoppler";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.pcm";
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    
    // WAV FILE Time (20 Sec)
    private long lngStartTimeStamp = 0;
	private long lngStopTimeStamp =  0;
	
	//Clock variables
	private boolean bolFlag15Secs = false;
	private final long startTime = 15 * 1000;
	private final long interval = 1 * 1000;
	
	
	/**************************************************************
     * DEFAULT EMPTY CONSTRUCTOR  	
     * @param clipListener
     ***************************************************************/
	public AudioClipRecorder()
	{
		// DEFAULT EMPTY CONSTRUCTOR
	}
	
	/*******************************************************************************
	 * CONSTRUCTOR WITH PARAMETERS
	 * @param clipListener AudioClipListener object that process the Audio Signals
	 * @param msgHandler  Thread Handler to send Notification to Main GUI Thread
	 *******************************************************************************/
	
	public AudioClipRecorder(AudioClipListener clipListener, Handler msgHandler, Context appContext)
	{
		 this.clipListener = clipListener;
		 heard = false;
	     hd = msgHandler;
	 	 b = new Bundle(4);
	 	 bolHearing = false;
	 	 ctxCurrent = appContext;
	 	 audioIntent  = new Intent(ctxCurrent,ReproduceBeatRT.class);

	 	 // Initialize Buffers and Settings
	 	 initializeAudioSettings();
	}
	
	
    /**********************************************************************************
     * Method: initializeAudioSettings()
     * It Initializes : sampleRate, encoding, bufferSize, AudioRecord Object
     *********************************************************************************/
	public void initializeAudioSettings()
	{
		  bufferSize = determineMinimumBufferSize(RECORDER_SAMPLERATE,RECORDER_AUDIO_ENCODING);
	  	  
          //give it extra space to prevent overflow
          increaseRecordingBufferSize = bufferSize * DEFAULT_BUFFER_INCREASE_FACTOR;

          recorder = new AudioRecord(AudioSource.MIC, RECORDER_SAMPLERATE,RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, increaseRecordingBufferSize);
          readBuffer = new short[bufferSize];  //Create Audio Input Buffer
          
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

	//==============================================================================================
	/** Audio Thread Main Program  
	 * Method: run()
	 * Audio Thread Main Execution
	 * It Gets Audio Samples from AudioJack, and process the signal detecting high amplitude and call
	 * methods to record the signal and reproduce the signal in real time
	 **/
	//=============================================================================================
	public void run()
	{
		lngStartTimeStamp  = System.currentTimeMillis();  // Get Time Stamp
		
		// ********* Start Thread Cycle   ***********************************
		while(!Thread.currentThread().isInterrupted())
		{
		
			//Note: possible IllegalStateException
            //if audio recording is already recording or otherwise not available
            //AudioRecord.getState() will be AudioRecord.STATE_UNINITIALIZED
			
		     if(recorder.getState() == recorder.STATE_UNINITIALIZED)
             {
            	  b.putInt("beat", -1);    //ERROR -1 : AudioRecorder not Initialized
                  Message msg = hd.obtainMessage();
                  msg.what = 1;
                  msg.setData(b);
                  hd.sendMessage(msg);
                  Thread.interrupted();
                  break;
             }
             else if(bufferSize == AudioRecord.ERROR_BAD_VALUE)
             {
                   b.putInt("beat", -3);    //ERROR -3 : BAD ENCODING VALUE
                   Message msg = hd.obtainMessage();
                   msg.what = 1;
                   msg.setData(b);
                   hd.sendMessage(msg);
                   Thread.interrupted();
                   break;
             }
             else if (bufferSize == AudioRecord.ERROR)
             {
                   b.putInt("beat", -4);    //ERROR -4 : ERROR CREATING BUFFER SIZE
                   Message msg = hd.obtainMessage();
                   msg.what = 1;
                   msg.setData(b);
                   hd.sendMessage(msg);
                   Thread.interrupted();
                   break;
             }
             // NO ERROR START RECORDING
             else if(recorder.getState() == recorder.STATE_INITIALIZED)
             {
            	  bolHearing = true;
            	  recorder.startRecording();   // Start Recording HERE
             }
       
		      //*********** HERE WE GET THE AUDIO SAMPLE***********************
              bufferResult = recorder.read(readBuffer,0,bufferSize);  //READ AUDIO SAMPLE
      	   
      	      // check for error conditions
              if (bufferResult == AudioRecord.ERROR_INVALID_OPERATION)
              {
            	  b.putInt("beat", -2);    //ERROR  -2 : ERROR INVALID OPERATION
                  Message msg = hd.obtainMessage();
                  msg.what = 1;
                  msg.setData(b);
                  hd.sendMessage(msg);
                  Thread.interrupted();
                  break;
              }
              else if (bufferResult == AudioRecord.ERROR_BAD_VALUE)
              {
            	  b.putInt("beat", -3);  //ERROR -3:  BAD VALUE  
                  Message msg = hd.obtainMessage();
                  msg.what = 1;
                  msg.setData(b);
                  hd.sendMessage(msg);
                  Thread.interrupted();
                  break;
              }
              else  // no errors, do Audio Signal processing
              {   
            	   // HERE WE DETECT HIGH AMPLITUDE OR NOT
            	   heard = clipListener.heard(readBuffer, sampleRate); 
            	 
            	
                   if (heard) // Hearth Beat UP
                   {   
               	        b.putInt("beat", 1);
                        Message msg = hd.obtainMessage();
                        msg.what = 1;
                        msg.setData(b);
                        hd.sendMessage(msg);
                   }
                   else
                   {
               	       b.putInt("beat", 0);
                       Message msg = hd.obtainMessage();
                       msg.what = 1;
                       msg.setData(b);
                       hd.sendMessage(msg);
                   }
                
                   //Send Audio to Intent Service to Play It in Real Time
                   audioIntent.putExtra("BEATS", readBuffer);
                   ctxCurrent.startService(audioIntent);
                }
              
                // HERE WE CREATE THE AUDIO FILE
                try
                {
                  lngStopTimeStamp = getTimeFrame();   
                  
             	  if(lngStopTimeStamp <= ctxCurrent.getResources().getInteger(R.integer.RecordingTimeFrame))
            	  {
            	       bData = short2byte(readBuffer);  //Convert Buffer Short to Byte 
       	               os.write(bData, 0, bufferSize * BytesPerElement);
            	  }
            	  else
            	  {
            		  os.close();  
            	  }
              }
              catch (IOException e) 
              {
             	               
              }
                   	  
             
    	} //************* End Thread Cycle **************************
		
	    done();  // Free Memory
	    
	    // Close File if it still Open
        try 
        {
        	if(os != null)
                os.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        
        generate_WAV_File();
        
	}  // End Method 
	
	/***********************************************************
	 * Generate the Wav File from the Raw File created in thread
	 ************************************************************/
	private void generate_WAV_File()
	{
		 // Generate WAV File
        copyWaveFile(getTempFilename(),getFilename());
        // Delete temp file
        deleteTempFile();
	}
	
	/**********************************************************
	 * Get Time Frame in Seconds since time zero(start thread)
	 *********************************************************/
	
	private long getTimeFrame()
	{
		long lngTimeFrame;
		lngTimeFrame =  (System.currentTimeMillis()-lngStartTimeStamp)/1000; 
		return lngTimeFrame;
	}
	
	
	
	
	
	/***************************************************************************
	 * Method: getTempFilename()
	 * Get the Path + temporal file name to be used in the raw audio file
	 * @return File Path
	 ***************************************************************************/
	
	private String getTempFilename()
    {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath,AUDIO_IDOPPLER_RECORDER_FOLDER);
            
            if(!file.exists()){
                    file.mkdirs();
            }
            
            File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
            
            if(tempFile.exists())
                    tempFile.delete();
            
            return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }
	
    /****************************************************************************************
     * Method: getFileName
     * It Creates the WAV File
     * @return Path + WAV File Name
     ***************************************************************************************/
    private String getFilename()
    {
            File file = get_WAV_File_Path();
            
            if(!file.exists()){
                    file.mkdirs();
            }
            
            return (file.getAbsolutePath() + "/" + BABY_BEATS_FILE_NAME + AUDIO_RECORDER_FILE_EXT_WAV);
    }
    
    /************************************************************************************
     * Get Wav File Path
     ***********************************************************************************/
    
    private static File get_WAV_File_Path()
    {
    	 String filepath = Environment.getExternalStorageDirectory().getPath();
         File file = new File(filepath, AUDIO_IDOPPLER_RECORDER_FOLDER);
         return file;
    }
	
    /*****************************************************************************************
     * Method: deleteTempFile
     * It removes the temporal filename 
     *****************************************************************************************/
    private void deleteTempFile()
    {
        File file = new File(getTempFilename());
        file.delete();
    } 
	
	/*****************************************************************************************
	 * Method: copyWaveFile
	 * It Transforms the Audio RAW file to an Audio WAV file
	 * @param 	infileName:    Raw Audio Signals file
	 * @param   outfileName:   WAV File to generate  
	 * @return  void
	 ****************************************************************************************/
	
	private void copyWaveFile(String inFilename,String outFilename)
	{
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
        
        byte[] data = new byte[bufferSize];
        
        try
        {
                in = new FileInputStream(inFilename);
                out = new FileOutputStream(outFilename);
                totalAudioLen = in.getChannel().size();
                totalDataLen = totalAudioLen + 36;
                
                                
                WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                longSampleRate, channels, byteRate);
                
                while(in.read(data) != -1)
                {
                        out.write(data);
                }
                
                in.close();
                out.close();
          }
          catch (FileNotFoundException e)
          {
                e.printStackTrace();
          } 
          catch (IOException e) 
          {
                e.printStackTrace();
          }
    }
	/***************************************************************************************************
	 * Method: WriteWaveFileHeader
	 * It creates the Header to the WAV File
	 * @param totalAudioLen:  Length of Audio File
	 * @param totalDataLen:   Length of the Data
	 * @param longSampleRate: Sample Rate
	 * @param channels:       channels
	 * @param byterate:       byteRate
	 * @return void       
	 ***************************************************************************************************/
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
			long longSampleRate, int channels, long byteRate) throws IOException 
    {
    
         byte[] header = new byte[44];
    
         header[0] = 'R';  // RIFF/WAVE header
         header[1] = 'I';
         header[2] = 'F';
         header[3] = 'F';
         header[4] = (byte) (totalDataLen & 0xff);
         header[5] = (byte) ((totalDataLen >> 8) & 0xff);
         header[6] = (byte) ((totalDataLen >> 16) & 0xff);
         header[7] = (byte) ((totalDataLen >> 24) & 0xff);
         header[8] = 'W';
         header[9] = 'A';
         header[10] = 'V';
         header[11] = 'E';
		 header[12] = 'f';  // 'fmt ' chunk
		 header[13] = 'm';
		 header[14] = 't';
		 header[15] = ' ';
		 header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		 header[17] = 0;
		 header[18] = 0;
		 header[19] = 0;
		 header[20] = 1;  // format = 1
		 header[21] = 0;
		 header[22] = (byte) channels;
		 header[23] = 0;
		 header[24] = (byte) (longSampleRate & 0xff);
		 header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		 header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		 header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		 header[28] = (byte) (byteRate & 0xff);
		 header[29] = (byte) ((byteRate >> 8) & 0xff);
		 header[30] = (byte) ((byteRate >> 16) & 0xff);
		 header[31] = (byte) ((byteRate >> 24) & 0xff);
		 header[32] = (byte) (2 * 16 / 8);  // block align
		 header[33] = 0;
		 header[34] = RECORDER_BPP;  // bits per sample
         header[35] = 0;
         header[36] = 'd';
         header[37] = 'a';
         header[38] = 't';
         header[39] = 'a';
         header[40] = (byte) (totalAudioLen & 0xff);
         header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
         header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
         header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

         out.write(header, 0, 44);
    }
	
	
	/******************************************************************************************
	 * DetermineMinimumBufferSize
	 * Determines the Buffer memory minimum size
	 * @param sampleRate: Rate of Samplig Audio data
	 * @param encoding
	 * @return minimum buffer size
	 ******************************************************************************************/
	
    private int determineMinimumBufferSize(final int sampleRate, int encoding)
    {
    	int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,AudioFormat.ENCODING_PCM_16BIT,encoding);
    	return minBufferSize;
    }
    
   
     /****************************************************************************************
     * Method: done()
     * It stops the AudioRecord object, destroys the object memory
     ***************************************************************************************/
     public void done()
     {
          if (recorder != null)
          {
              recorder.stop();
              recorder.release();
              recorder = null;
              bolHearing = false;
          }
      }
     
     /******************************************************************************************
      * Method: short2byte
      * Converts a short buffer to Byte Array
      * @param sData
      * @return
      ******************************************************************************************/
     private byte[] short2byte(short[] sData) 
     {
         int shortArrsize = sData.length;
         byte[] bytes = new byte[shortArrsize * 2];
         int intIndex = 0;           
         
         for (int i = 0; i < shortArrsize; i++) 
         {
    	        bytes[i * 2] = (byte) (sData[i] & 0x00FF);
    	        bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
    	        sData[i] = 0;
    	 }
    	 return bytes;
     }
   
     /*******************************************************************************************
      * Method: hasMicrophone
      * Detects if the Phone has Microphone
      * @param context
      * @return true/false
      ******************************************************************************************/
       
     public static boolean hasMicrophone(Context context)
     {
    	 return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
     }
     
     /*****************************************************************************************
      * Method: isHearingHearthBeat
      * It returns true/false if the Thread is getting Audio Signal
      * @return true/false
      *****************************************************************************************/
     
     public static boolean isHearingHearthBeat()
     {
    	 return bolHearing;
     }
     
     /*****************************************************************************************
      * Method: playAudioFile
      * It Plays if the Audio Recorded
      * @param NONE
      * @return void
      ****************************************************************************************/
     public static int playAudioFile() throws IOException
     {
    	 if(isHearingHearthBeat() == false)
    	 {
    	       MediaPlayer mPlayer;
               mPlayer = new MediaPlayer();
               File file = get_WAV_File_Path();
               
               if(file.list().length > 0)
               {
                     String filepath = file.getAbsolutePath() + "/" + BABY_BEATS_FILE_NAME + AUDIO_RECORDER_FILE_EXT_WAV;
               
                     mPlayer.setDataSource(filepath);
                     mPlayer.prepare();
                     mPlayer.start();
                     
                     return 0;
               }
               else
               {
            	     return -1;
               }
              
    	 }
    	 else
    	 {
    		 return -2;
    	 }
      }
     
     /**********************************************************************************
      * Delete ALl Audio Files in a Directory
      *********************************************************************************/
     public static void cleanIDoppler_Directory()
     {
    	 File file = get_WAV_File_Path();
         
         if(file.exists())
         {
             if (file.isDirectory()) 
             {
    	         String[] children = file.list();
    	         for (int i = 0; i < children.length; i++) 
    	         {
    	             new File(file, children[i]).delete();
    	         }
    	     }
         }
     }
     
 } //************************* END CLASS *********************************************************
