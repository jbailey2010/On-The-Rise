package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.util.HashSet;

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
    
    /** 
	 * Designed to be the one call that's made to handle any fetching...etc of 
	 * the player names. If they're already written, it fetches them and stores.
	 * If not, it calls a function to fetch them and write to file. It's set up this way
	 * to minimize other calls such that only one function needs to be called, yet 
	 * running time is minimized this way.
	 * 
	 * @param holder holds the array to be written to
	 * @param cont holds the context to be used to write to file.
	 * @throws IOException 
	 */
	public static void fetchNames(Storage holder, Context cont) throws IOException
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		holder.playerNames = (HashSet<String>) prefs.getStringSet("Player Names", null);
	}
	
	/**
	 * Reads if it's the first usage of the app, defaulting to true
	 */
	public static boolean readFirstOpen(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		return prefs.getBoolean("First Open", true);
	}

	public static boolean isNewYear(Context cont, int yearKey) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		return prefs.getInt("Year Key", 2000) < yearKey;
	}
}
