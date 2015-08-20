
/*****************************************************************************************
 * MODULE:  DateDialogFragment.java
 * Class: DateDialogFragment
 * Implements:  A DateDialogFragment type of DialogFragment to create a Dialog Box that displays
 * dates to select (Calendar)
 *
 * Software Engineer:  Edgar Acosta Davila
 * Date:  07/6/2014
 *  
 ********************************************************************************************/

package com.Doppler.idoppler;

import android.app.Dialog;
import android.app.DialogFragment;
import java.util.Calendar;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

/*
 * Class: DateDialogFragment
 * A Dialog Fragment to implement a Calendar Dialog
 * 
 */
public class DateDialogFragment  extends DialogFragment 
{
	// Date Private Data
	public static String TAG = "DateDialogFragment";
	static Context mContext; //I guess hold the context that called it. Needed when making a DatePickerDialog. I guess its needed when conncting the fragment with the context
	static int mYear;
	static int mMonth;
	static int mDay;
	static DateDialogFragmentListener mListener;
	
	public static DateDialogFragment newInstance(Context context, DateDialogFragmentListener listener, Calendar now) {
		DateDialogFragment dialog = new DateDialogFragment();
		mContext = context;
		mListener = listener;
		
		mYear = now.get(Calendar.YEAR);
		mMonth = now.get(Calendar.MONTH);
		mDay = now.get(Calendar.DAY_OF_MONTH);
		/*I dont really see the purpose of the below*/
		Bundle args = new Bundle();
		args.putString("title", "Set Date");
		dialog.setArguments(args);//setArguments can only be called before fragment is attached to an activity, so right after the fragment is created
		
		
		return dialog;
	}
	
	
	/*
	 * onCreateDialog Event
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new DatePickerDialog(mContext, mDateSetListener, mYear, mMonth, mDay);
	}
	
	/*
	 * OnDateSetListener event
	 * Set the data selected by the user
	 */
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			mListener.updateChangedDate(year, monthOfYear, dayOfMonth);
		}
	};
		
		 
}
