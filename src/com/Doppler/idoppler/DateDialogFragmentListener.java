/*****************************************************************************************
 * MODULE:  AudioClipRecorder
 * Interface: DateDialogFragmentListener 
 * Implements:  Abstract method to get the date from DateDialogFragment and stored in the Activity (GUI)
 *  
 * 
 * Software Engineer:  Edgar Acosta Davila
 * Date:  05/06/2014
 *  
 ********************************************************************************************/

package com.Doppler.idoppler;

import java.util.Calendar;

/*
 * This interface is a listener between the Date Dialog fragment and the activity to update the EditText date
 */
public interface DateDialogFragmentListener 
{
	public void updateChangedDate(int year, int month, int day);
}
