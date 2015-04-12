package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class TrendingAsyncTask {
	/**
	 * In the back-end fetches the posts
	 * @author Jeff
	 *
	 */
	public class ReadPosts extends AsyncTask<Object, Void, Storage> 
	{
		private Context cont;
	    public ReadPosts() 
	    {
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Storage result){
			Trending.setHolder(result);	
		}
		
	    protected Storage doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	cont = (Context) data[1];
	    	holder.posts.clear();
	   		//Get the aggregate rankings
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			String checkExists = prefs.getString("Posts", "Not Set");
			String[] perPost = checkExists.split("@@@");//ManageInput.tokenize(checkExists, '@', 3);
			String[][] split = new String[perPost.length][];
			for(int i = 0; i < perPost.length; i++)
			{
				split[i] = ManageInput.tokenize(perPost[i], '~', 3);
				Post newPost = new Post(split[i][0], split[i][1]);
				holder.posts.add(newPost);
			}
			return holder;
	    }
	  }
	
	  /**
     * Fetches the names list from file in the background
     * @author Jeff
     *
     */
	public class ReadNamesList extends AsyncTask<Object, Void, Storage> 
	{
		@Override
		protected void onPostExecute(Storage result){
			Trending.setHolder(result);	
		}
		
	    protected Storage doInBackground(Object... data){
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			holder.playerNames = (HashSet<String>) prefs.getStringSet("Player Names", null); 
    		return holder;
	    }
	  }
	
	/**
	 * Writes the posts to file 
	 * @author Jeff
	 *
	 */
	public class WritePostsListAsync extends AsyncTask<Object, Void, Void> {
		
		private List<String> trendingPlayers;
		private Activity act;
		private ProgressDialog pdia;
		
	    public WritePostsListAsync(Activity activity, List<String> trend) 
	    {
	    	act = activity;
	    	 pdia = new ProgressDialog(activity);
		     pdia.setCancelable(false);
	    	trendingPlayers = trend;
	    }

	    @Override
		protected void onPreExecute(){ 
		   super.onPreExecute();   
		   pdia.setMessage("Please wait, saving the posts, this should only take a minute...");
	       pdia.show(); 
		}
	    
		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}

	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	SharedPreferences.Editor editor = act.getSharedPreferences("FFR", 0).edit();
	    	StringBuilder posts = new StringBuilder(10000);
	    	for(String post : trendingPlayers){
	    		posts.append(post).append("##");
	    	}
	    	editor.putString("Posted Players", posts.toString());
	    	editor.apply();
			return null;
	    }
	}
}
