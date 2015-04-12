package com.devingotaswitch.ontherise;

import java.util.List;

import com.devingotaswitch.ontherise.TrendingAsyncTask.WritePostsListAsync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class WriteToFile {
	
	private static TrendingAsyncTask asyncObj = new TrendingAsyncTask();
	
	/**
	 * Writes the last filter to file for trending
	 * @param cont
	 * @param lastFilter
	 */
	public static void writeLastFilter(Context cont, int lastFilter) {
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	editor.putInt("Last Filter", lastFilter).apply();
	}
	
	/**
	 * Just writes the filter size to file for later usage
	 * @param cont
	 * @param size
	 */
	public static void writeFilterSize(Context cont, int size){
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
   		editor.putInt("Filter Quantity Size", size).apply();
	}
	
	/**
	 * Writes the list of trending players to file
	 * @param trendingPlayers
	 * @param cont
	 */
	public static void writePostsList(List<String> trendingPlayers, Activity cont) {
	    WritePostsListAsync draftTask = asyncObj.new WritePostsListAsync(cont, trendingPlayers);
	    draftTask.execute();
	}

}
