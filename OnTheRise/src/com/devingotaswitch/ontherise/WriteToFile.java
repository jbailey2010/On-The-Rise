package com.devingotaswitch.ontherise;

import java.util.HashSet;
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
	
	/**
	 * Stores the posts to file to avoid unnecessary calls
	 * @param holder
	 * @param cont
	 */
	public static void writePosts(Storage holder, Context cont) 
	{
		StringBuilder posts = new StringBuilder(10000);
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		for(Post post : holder.posts)
		{
			posts.append(post.text + "~~~" + post.date + "@@@");
		}
		editor.putString("Posts", posts.toString());
		editor.putBoolean("Last Empty", false);
		editor.apply();
	}
	
	/**
	 * Reads if it's the first usage of the app, defaulting to true
	 */
	public static void writeFirstOpen(Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putBoolean("First Open", false).apply();
	}

	public static void writeNewYear(Context cont, int yearKey) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putInt("Year Key", yearKey).apply();
	}
	
	/**
	 * This stores the player names to the SD card, it can 
	 * only be called by fetchPlayerNames to avoid unnecessary calls
	 * @param holder holds the array to be stored
	 * @param cont used to be allowed to write to file in android
	 */
	public static void storePlayerNames(HashSet<String> names, Context cont) {
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putStringSet("Player Names", names);
		editor.apply();
	}

}
