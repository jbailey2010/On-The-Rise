package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
	    	Storage holder = (Storage) data[1];
	    	cont = (Context) data[0];
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
	
	/**
	 * Parses the posts from the forums
	 * @author Jeff
	 *
	 */
	public class FetchTrends extends AsyncTask<Object, String, Void> 
	{
		private Activity act;
		private ProgressDialog pdia;
		private Storage holder;
	    public FetchTrends(Activity activity, Storage hold) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        holder = hold;
	    }
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia = new ProgressDialog(act);
		        pdia.setCancelable(false);
		        pdia.setMessage("Please wait, parsing the forums. This could take a few minutes...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   boolean flag = false;
		   if(holder.posts.size() == 0)
		   {
			   SharedPreferences.Editor editor = act.getSharedPreferences("FFR", 0).edit();
			   editor.putBoolean("Last Empty", true).apply();
			   flag = true;
		   }
		   Trending.setContent(act, flag);
		}
		
		@Override
	    public void onProgressUpdate(String... values)
	    {
	    	super.onProgressUpdate(values);
	    	pdia.setMessage((String) values[0]);
	    }
    	
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage hold = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			boolean value =  prefs.getBoolean("Value Topic", true);
			boolean mustHave = prefs.getBoolean("Good Topic", true);
			boolean rookie = prefs.getBoolean("Rookie Topic", true);
			boolean dontWant = prefs.getBoolean("Bad Topic", false);

	    	holder = hold;
			try {
				if(!holder.isRegularSeason)
				{
					if(mustHave)
					{
						//Wish List
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=419610&st=");
						//Rounds 1 and 2
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=504148=");
				    	// Top 30
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=497589&st=");
						//RB rankings
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=421811&st=");
						//QB rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=502668&st=");
						//WR rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=505526&st=");
						//TE rankings
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=424362&st=");
					}
					if(value)
					{
						//Bounce backs
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=418111&st=");
						//Value picks
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=501750&st=");
						//2014 sleepers
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=418103&st=");
						//adp steals
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=354905&st=");
						// 2015 Bench Value Picks
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=516474&st=");
						// Injury gems
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=507532&st=");
					}
					if(rookie)
			 		{
						//Draft eligible players
						//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=378836&st=");
				 		//Rookie rankings
			 			//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=331665&st=");
			 			//Draft thread
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=518365&st=");
			 		}
			 		if(dontWant)
			 		{
			 			//LVP
			 			//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=418626&st=");
			 			//Overvalued
			 			//ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=334675&st=");
			 			//Don't draft
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=501797&st=");
			 			//Busts
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=509208&st=");
			 		}
				} 
				else
				{
					if(mustHave)
					{ 
						//Interesting contract years/free agents
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=419241&st=");
					}
					if(value)
					{
						//Buy low/sell high
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=377921&st=");
					}
					if(rookie)
					{
						//Keepers/dynasty central
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=324465&st=");
					}
					if(dontWant)
					{
						//Trade targets/completed trades
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=365820&st=");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(holder.posts.size() > 1)
			{
				publishProgress("Please wait, saving the posts...");
				WriteToFile.writePosts(holder, act);
			}
			return null;
	    }
	  }
	
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	public class ParseNames extends AsyncTask<Object, Void, Void>  {
		private ProgressDialog pdia;
		
	    public ParseNames(Activity activity, boolean iff) {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the player names list...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   Trending.finishInitialConfig();
		}

	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Context cont = (Context) data[0];
	    	try {
				ParsePlayerNames.fetchPlayerNames(cont);
			} catch (IOException e) {
			}
			return null;
	    }
	  }
}
