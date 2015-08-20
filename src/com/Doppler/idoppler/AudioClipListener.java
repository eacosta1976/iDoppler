/*****************************************************************************************
 * MODULE:  AudioClipListener.java
 * Interface AudioClipListener
 * Implements:  An abstract interface with the heard method, this method is used to implement the
 * Signal Processing for Audio
 *  
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/20/2014
 *  
 ********************************************************************************************/


package com.Doppler.idoppler;

/**************************************************************
 * Interface:  AudioClipListener
 * @author Edgar Acosta
 * It Implements AudioClipListener Interface with heard method
 *************************************************************/
public interface AudioClipListener
{     
	  /***********************************************
	  * Method : heard
	  * @param data  Audiobuffer from AudioRecord
	  * @param maxAplitude: maxAmplitud Threshold
	  * @return boolean True/False
	  * 
	  ************************************************/
	  public boolean heard(short[] data, int maxAplitude);
}
