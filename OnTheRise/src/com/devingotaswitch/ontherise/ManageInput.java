package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * A little class that should help with making user input
 * into searches...etc a little cooler, doing nicer things.
 * @author Jeff
 *
 */
public class ManageInput 
{
	
	/**
	 * Handles the filter quantity dialog
	 * @param cont
	 */
	public static void filterQuantity(final Context cont, final int listSize) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.filter_quantity);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		int filterSize = ReadFromFile.readFilterQuantitySize(cont);
		final SeekBar selector = (SeekBar)dialog.findViewById(R.id.seekBar_quantity);
		final TextView display = (TextView)dialog.findViewById(R.id.quantity_display);
		if(filterSize == 0) {
			display.setText("None of the players");
		}
		else if(filterSize == 100) {
			display.setText("All of the players");
		}
		else {
			display.setText(filterSize + "% of the players");
		}
		selector.setProgress(filterSize);
		selector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    	    	int prog = selector.getProgress();
    	    	String size = "";
    	    	if(prog == 0) {
    	    		size = "None of the players";
    	    	}
    	    	else if(prog == 100) {
    	    		size = "All of the players";
    	    	}
    	    	else {
    	    		size = prog + "% of the players";
    	    	}
    	    	display.setText(size);	
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do not care
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Do not care here either
			}
    	});
		Button cancel = (Button)dialog.findViewById(R.id.filter_size_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button submit = (Button)dialog.findViewById(R.id.filter_size_submit);
		submit.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {
				WriteToFile.writeFilterSize(cont, selector.getProgress());
				dialog.dismiss();
				((Trending)cont).resetTrendingList(ReadFromFile.readLastFilter(cont), cont);
	    	}	
		});

	}
	
	/**
	 * Sees if there is an internet connection, true if yes, false if no
	 * @param cont
	 * @return
	 */
	public static boolean confirmInternet(Context cont)
	{
		ConnectivityManager connectivityManager 
	        = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/**
	 * Just to eek out tiny bits of performance improvements
	 * @param s
	 * @param key
	 * @param keyLen
	 * @return
	 */
	public static String[] tokenize(String s, char key, int keyLen)
	{
		char[] c = s.toCharArray();
		LinkedList<String> ll = new LinkedList<String>();
		int index = 0;
		for(int i = 0; i < c.length; i++)
		{
			if(c[i] == key && ((keyLen > 1 && c[i+1] == key) || keyLen == 1) || i+1 == c.length)
			{
				if(i+1 == c.length)
				{
					ll.add(s.substring(index, i+1));
				}
				else
					{
					ll.add(s.substring(index, i));
				}
				index = i + keyLen;
				i += keyLen - 1;
			}
		}
		String[] allData = new String[ll.size()];
		Iterator<String> iter = ll.iterator();
		for(index = 0; iter.hasNext(); index++)
		{
			allData[index] = iter.next();
		}
		return allData;
	}
}
