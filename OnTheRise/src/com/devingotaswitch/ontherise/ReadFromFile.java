package com.devingotaswitch.ontherise;

import com.devingotaswitch.ontherise.TrendingAsyncTask.ReadNamesList;
import com.devingotaswitch.ontherise.TrendingAsyncTask.ReadPosts;

import android.content.Context;
import android.content.SharedPreferences;

public class ReadFromFile {
	private static TrendingAsyncTask readFromFileAsyncObj = new TrendingAsyncTask();

	/**
	 * Reads the filter quantity size from file
	 * @param cont the context from which it reads
	 * @return the size
	 */
	public static int readFilterQuantitySize(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getInt("Filter Quantity Size", 50);
	}
	
	/**
	 * Handles reading the last filter used from
	 * file (for trending)
	 * @param cont
	 * @return
	 */
	public static int readLastFilter(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getInt("Last Filter", 365);
	}
	
	/**
	 * Fetches the players from local to a local object
	 * @param holder
	 * @param cont
	 */
	public static void fetchPostsLocal(Context cont, Storage holder) {
	    ReadPosts values = readFromFileAsyncObj.new ReadPosts();
		values.execute(cont, holder);
	}
	
	/**
	 * Fetches the names list from file in the back end
	 * @param holder
	 * @param cont
	 */
    public static void fetchNamesBackEnd(Storage holder, Context cont) {
	    ReadNamesList values = readFromFileAsyncObj.new ReadNamesList();
		values.execute(holder, cont);
    }
}
