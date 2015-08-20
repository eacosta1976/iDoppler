/*****************************************************************************************
 * MODULE:    LoudNoiseDetector.java
 * Class: LoudNoiseDetector
 * Implements: (AUDIO SIGNAL PROCESSING)
 *  Implements the AudioClipListener interface, this class is responsible to process the Audio Signal
 *  using the RMS Algorithm (Root Mean Squares). it gets a sample of Audio and process the chunk using
 *  RMS and compare the results against a threshols to determine if there is a High Amplitud Signal
 *  (Beat Up) or Low Amplitud Signal (Beat Down)
 *  
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/22/2014
 *  
 ********************************************************************************************/



package com.Doppler.idoppler;

import android.util.Log;
 
/*****************************************************************
 * Class: LoudNoiseDetector
 * Implements Digital Audio Signal processing
 * @author Edgar Acosta
 * Interfazes: AudioClipListener
 * Date: 05/20/2014
 *****************************************************************/

public class LoudNoiseDetector implements AudioClipListener
{
    private double volumeThreshold;
    // Threshold to detect Amplitud
    public static final int DEFAULT_LOUDNESS_THRESHOLD = 130;
   
    /*****************************************************************
     * Method: LoudNoiseDetector
     * DEFAULT CONSTRUCTOR
     *****************************************************************/
    public LoudNoiseDetector() 
    {
    	volumeThreshold = DEFAULT_LOUDNESS_THRESHOLD;
    }
    
    /*****************************************************************
     * Method: LoudNoiseDetector
     * CONSTRUCTOR With Parameter
     * @param volumeThreshold : Threshold to detect High Amplitude
     *****************************************************************/
    public LoudNoiseDetector(double volumeThreshold)
    {
    	this.volumeThreshold = volumeThreshold;
    }
    
    /******************************************************************
     * Method: heard
     * @param data:  Audio Raw Buffer
     * @param sampleRate:  sampleRate
     ******************************************************************/
    
    //@override
    public boolean heard(short[] data, int sampleRate)
    {
    	boolean heard = false;
    	// use Root Mean Square to take the entire audio signal into account
        // and discount any one single high amplitude

        double currentVolume = rootMeanSquare(data);   
        
        if(currentVolume > volumeThreshold)
        {
            heard = true;
        }
        
        return heard;
    	
    }
    
    /****************************************************************************
     * Method: rootMeanSquared
     * @param nums:  Raw Audio Buffer
     * @return: Root Mean Square Value
     ***************************************************************************/
    private double rootMeanSquare(short[] nums)
    {
        double ms = 0;
        for (int i = 0; i < nums.length; i++)
        {
            ms += nums[i] * nums[i];
        }
        ms /= nums.length;
        return Math.sqrt(ms);
    }

    
}  //**************************************** END CLASS***************************************************
