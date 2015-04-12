package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Sets up the trending page
 * @author Jeff
 * 
 */ 
public class Trending extends Activity {
	//Some globals, the dialog and the context
	private Context cont;
	private Button day;
	private Button week;
	private Button month;
	private Button all;
	private static OutBounceListView listview;
	private static boolean refreshed = false;
	private int lastFilter;
	private static SimpleAdapter mAdapter;
	private static List<Map<String, String>> data;
	private static Storage holder;
	private final int color = 0XFF26a69a;
	/**
	 * Sets up the dialog to show up immediately
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		cont = this;
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	listview = (OutBounceListView)findViewById(R.id.listview_trending);
		initialLoad(prefs);
	}

	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trending, menu);
		return true;
	}
 
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.filter_quantity_menu:
				if(holder.posts == null || holder.posts.size() == 0){
					Toast.makeText(cont, "You have to fetch the posts (press filter) to do this", Toast.LENGTH_SHORT).show(); 
				}
				else {
					filterQuantity();
				}
				return true;
			case R.id.filter_topics_menu:
				topicalTrending(holder);
				return true;
			case R.id.help:
				helpDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	/**
	 * Handles the help dialog
	 */
	public void helpDialog() {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.help_trending);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.help_trending_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
	    });
	}
	
	/**
	 * Handles the initial load of trending players on creation
	 * @param result 
	 * @param prefs
	 */
	public void initialLoad(SharedPreferences prefs) {
    	//Get the posts, if they're not set, fetch them. Otherwise, get from storage.
    	if(prefs.contains("Posts")) {
    		Toast.makeText(cont, "Please select Filter Topics from the menu to see trending players", Toast.LENGTH_SHORT).show();
    	}
    	else {
    		ReadFromFile.fetchPostsLocal(cont, holder);
    	}
		if(holder.playerNames.size() < 19) {
			ReadFromFile.fetchNamesBackEnd(holder, cont);
		} 
   		getFilterForPosts(holder); 
   		
		String storedPosts = prefs.getString("Posted Players", "Not Posted");
		Boolean lastEmpty = prefs.getBoolean("Last Empty", false);

		if(!storedPosts.equals("Not Posted")) {  
			List<String>postsList = new ArrayList<String>();
			data = new ArrayList<Map<String, String>>();
			if(lastEmpty) {
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("name", "No posts were found with the selection you made.");
				datum.put("count", "It's possible that thread is not yet available.");
				data.add(datum);
			}
			else {
				String[] posts = ManageInput.tokenize(storedPosts, '#', 2);
				mAdapter = new SimpleAdapter(cont, data, 
			    		R.layout.bold_header_elem, 
			    		new String[] {"name", "count"}, 
			    		new int[] {R.id.text1, 
			    			R.id.text2});
				for(String post : posts) {
					try{
						String[] nameSet = post.split(": mentioned ");
						Map<String, String> datum = new HashMap<String, String>(2);
						if(nameSet[0].contains("No players mentioned")){
							datum.put("name", "No players mentioned in this timeframe");
							datum.put("count", "Please try something else");
							data.add(datum);
						}
						else{
							final String name = nameSet[0];
							final StringBuilder sec = new StringBuilder();
							sec.append(nameSet[1].split("//")[1]);
							datum.put("name", name + "");
							datum.put("count", sec.toString());
							data.add(datum);
							postsList.add(post);
						}
					} catch(ArrayIndexOutOfBoundsException e)
					{
						continue;
					}
				}
			}
   		    listview.setAdapter(mAdapter); 
    		SwipeDismissListViewTouchListener touchListener =
    				new SwipeDismissListViewTouchListener(
    						true, "Trending", listview,
	                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
    							@Override
	                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
    								String name = "";
	                                for (int position : reverseSortedPositions) {
	                                   	Map<String, String> datum = new HashMap<String, String>(2);
	                                   	datum = data.get(position);
	                                   	name = datum.get("name");
	                                    data.remove(mAdapter.getItem(position));
	                                }
	                                mAdapter.notifyDataSetChanged();
	                                Toast.makeText(cont, "Temporarily hiding " + name, Toast.LENGTH_SHORT).show();
	                            }
	                        });
	        listview.setOnTouchListener(touchListener);
	        listview.setOnScrollListener(touchListener.makeScrollListener()); 
	    }
		else{
			if(holder.posts.size() != 0){
				Toast.makeText(cont, "The posts are saved, but not the players. Select a time frame above", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * Handles the topical trending, once it's done it 
	 * refreshes the rankings based on the input here
	 * @param holder
	 */ 
	public void topicalTrending(final Storage holder)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trending_topic_filter);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		final CheckBox value = (CheckBox)dialog.findViewById(R.id.value_box);
		final CheckBox rookie = (CheckBox)dialog.findViewById(R.id.rookies);
		final CheckBox want = (CheckBox)dialog.findViewById(R.id.must_haves);
		final CheckBox dontWant = (CheckBox)dialog.findViewById(R.id.dont_want);
		if(holder.isRegularSeason) {
			value.setText("Buy Low/Sell High");
			rookie.setText("Dynasty/Keepers");
			want.setText("Contract Year Guys");
			dontWant.setText("Mid-Season Trade Targets");
		}
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		boolean valueSet =  prefs.getBoolean("Value Topic", true);
		boolean mustHaveSet = prefs.getBoolean("Good Topic", true);
		boolean rookieSet = prefs.getBoolean("Rookie Topic", true);
		boolean dontWantSet = prefs.getBoolean("Bad Topic", false);
		value.setChecked(valueSet);
		want.setChecked(mustHaveSet);
		rookie.setChecked(rookieSet);
		dontWant.setChecked(dontWantSet);
    	final SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		Button submit = (Button)dialog.findViewById(R.id.trending_filter_submit);
		submit.setOnClickListener(new View.OnClickListener() {	
            @Override
            public void onClick(View v) {
            	day.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	week.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	month.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	all.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
				Boolean valueChecked = value.isChecked();
				Boolean rookieChecked = rookie.isChecked();
				Boolean wantChecked = want.isChecked();
				Boolean dontWantChecked = dontWant.isChecked();
				editor.putBoolean("Value Topic", valueChecked);
				editor.putBoolean("Rookie Topic", rookieChecked);
				editor.putBoolean("Good Topic", wantChecked);
				editor.putBoolean("Bad Topic", dontWantChecked);
				editor.apply();
				dialog.dismiss();
				if(data == null) {
					data = new ArrayList<Map<String, String>>();
				}
				else {
					data.clear();
				}
				holder.posts.clear();
				if(ManageInput.confirmInternet(cont)) {
					fetchTrending(holder);
				}
				else {
					Toast.makeText(cont, "No Internet Connection", Toast.LENGTH_SHORT).show();
				}
				listview.setAdapter(null);
            }
		});
		Button cancel = (Button)dialog.findViewById(R.id.trending_filter_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {	
            @Override
            public void onClick(View v) {
				dialog.dismiss();
            }
		});
	} 
	
	/**
	 * Just because this has to be called twice, abstracted.
	 * Makes the loading dialog, and in a side thread calls the back
	 * function so both could happen. 
	 * @param holder
	 */
	public void fetchTrending(final Storage holder)
	{
		try {
			ParseTrending.trendingPlayers(holder, cont);
			listview.setAdapter(null);
		} catch (IOException e) {
			
		}	
	}
	
	/**
	 * Calls the function that handles filtering 
	 * quantity size
	 */
	public void filterQuantity() {
		ManageInput.filterQuantity(cont, holder.posts.size());	
	}
	
	/**
	 * Waits for a click of a button to get a filter
	 * timeframe
	 * @param holder
	 */
	private void getFilterForPosts(final Storage holder) {
        day = (Button)findViewById(R.id.filter_day);
        setOnClicks(day, 1);     
        week = (Button)findViewById(R.id.filter_week);
        setOnClicks(week, 7);   
        month = (Button)findViewById(R.id.filter_month);
        setOnClicks(month, 30);
        all = (Button)findViewById(R.id.filter_all);
        setOnClicks(all, 365);
		lastFilter = ReadFromFile.readLastFilter(cont);
		if(lastFilter == 1) {
			day.setBackgroundColor(color);
		}
		if(lastFilter == 7) {
			week.setBackgroundColor(color);
		}		
		if(lastFilter == 30) {
			month.setBackgroundColor(color);
		}		
		if(lastFilter == 365)  {
			all.setBackgroundColor(color);
		}
	}
	
	/**
	 * Handles setting of onclick listeners
	 * @param button
	 * @param filterSize
	 */
	public void setOnClicks(final Button button, final int filterSize) {
		button.setOnClickListener(new View.OnClickListener() {	
            @Override
            public void onClick(View v) {
            	if(holder.posts.size() > 0) {
            		lastFilter = filterSize;
	            	day.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	week.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	month.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	all.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	button.setBackgroundColor(color);
					WriteToFile.writeLastFilter(cont, filterSize);
					resetTrendingList(filterSize, cont);
					day.setClickable(false);
					month.setClickable(false);
					week.setClickable(false);
					all.setClickable(false);
            	}
            	else { 
            		Toast.makeText(cont, "Select filter topics to get the posts before you can do this", Toast.LENGTH_SHORT).show();
            	}
            }
		});
	}
	
	/**
	 * Refreshes the list based on the selected timeframe
	 * @param filterSize
	 * @param cont
	 * @throws ParseException
	 * @throws IOException
	 */
	public void resetTrendingList(int filterSize, Context cont) {
		refreshed = true;
		if(data != null){
			data.clear();
		}
		else{
			data = new ArrayList<Map<String, String>>();
		}
		holder.postedPlayers.clear();
		ParseTrending.setUpLists(holder, filterSize, cont);
	}
	
	
	/**
	 * Handles the middle ground before setting the listView
	 * @param holder
	 * @param cont
	 */
	public void intermediateHandleTrending(Storage holder, Activity cont) {
		day.setClickable(true);
		week.setClickable(true);
		month.setClickable(true);
		all.setClickable(true);
		int maxSize = ReadFromFile.readFilterQuantitySize((Context)cont);
		PriorityQueue<PostedPlayer>finalList = new PriorityQueue<PostedPlayer>(300, new Comparator<PostedPlayer>()  {
			@Override
			public int compare(PostedPlayer a, PostedPlayer b) 
			{
				if (a.count > b.count)
			    {
			        return -1;
			    }
			    if (a.count < b.count)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		int total = holder.postedPlayers.size();
		double fraction = (double)maxSize * 0.01;
		double newSize = total * fraction;
		for(int i = 0; i < newSize; i++) {
			finalList.add(holder.postedPlayers.poll());
		}
		holder.postedPlayers.clear();
		handleParsed(finalList, holder, cont);
	}
	
	/**
	 * Does sexy things
	 * @param holder
	 */
	public void handleParsed(PriorityQueue<PostedPlayer> playersTrending, Storage holder, final Activity cont) {
	    listview = (OutBounceListView) cont.findViewById(R.id.listview_trending);
	    listview.setAdapter(null);
	    data = new ArrayList<Map<String, String>>();
	    List<String> trendingPlayers = new ArrayList<String>(350);
	    while(!playersTrending.isEmpty()) {
	    	final PostedPlayer elem = playersTrending.poll();
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", elem.name + "");
	    	StringBuilder count = new StringBuilder(1000);
	    	count.append(elem.count + " times");
	    	final StringBuilder sub = new StringBuilder(1000);
	    	boolean lastTrue = false;
	    	if(lastFilter >= 7 && elem.lastTime(1) > 0) {
	    		int lastDay = elem.lastTime(1);
	    		sub.append(lastDay + "% in the last day");
	    		lastTrue = true;
	    	}
	    	boolean lastWeekTrue = false;
	    	if(lastFilter >= 28 && elem.lastTime(8) > 0) {
	    		if(lastTrue) {
	    			sub.append("\n");
	    		}
	    		int lastWeek = elem.lastTime(8);
	    		sub.append(lastWeek + "% in the last week");
	    		lastWeekTrue = true;
	    	}
	    	if(lastFilter >= 45 && elem.lastTime(32) > 0) {
	    		if(lastWeekTrue) {
	    			sub.append("\n");
	    		}
	    		int lastMonth = elem.lastTime(32);
	    		sub.append(lastMonth + "% in the last month");
	    	}
	    	StringBuilder sb = new StringBuilder(1000);
	    	sb.append(count.toString());
	    	if(sub.toString().length() > 4) {
	    		sb.append("\n" + sub.toString());
	    	}
	    	datum.put("count", sb.toString());
	    }
	    if(data.size() == 0) {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", "No players mentioned in this timeframe");
	    	datum.put("count", "Please try something else");
	    	trendingPlayers.add("No players mentioned in this timeframe" + ": mentioned " + "Please try something else" + " times//");
	    	data.add(datum);
	    }
	    if(refreshed) {
	    	WriteToFile.writePostsList(trendingPlayers, cont);
	    	refreshed = false;
	    } 

	    mAdapter = new SimpleAdapter(cont, data, 
	    		R.layout.bold_header_elem, 
	    		new String[] {"name", "count"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listview.setAdapter(mAdapter);
	    SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        true, "Trending", listview,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            	String name = "";
                                for (int position : reverseSortedPositions) {
                                	Map<String, String> datum = new HashMap<String, String>(2);
                                	datum = data.get(position);
                                	name = datum.get("name");
                                    data.remove(mAdapter.getItem(position));
                                }
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(cont, "Temporarily hiding " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
	}

	/**
	 * Sets the listview to say the posts are fetched
	 * @param act
	 */
	public static void setContent(Activity act, boolean flag) {
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    	Map<String, String> datum = new HashMap<String, String>(2);
    	if(flag)
    	{
    		datum.put("name", "No posts were found with the selection you made.");
			datum.put("count", "It's possible that thread is not yet available.");
			datum.put("freq", ""); 
    	}
    	else
    	{
	    	datum.put("name", "The posts are fetched\n");
	    	datum.put("count", "Select a timeframe from above");
    	}
    	data.add(datum);
    	mAdapter = new SimpleAdapter(act, data, 
	    		R.layout.bold_header_elem, 
	    		new String[] {"name", "count"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listview.setAdapter(mAdapter);
	}

	public static void setHolder(Storage result) {
		holder = result;
	}
}