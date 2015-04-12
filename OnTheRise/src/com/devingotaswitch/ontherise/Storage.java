package com.devingotaswitch.ontherise;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

import android.content.Context;

/**
 * Handles the storage of players and player names.
 * @author - Jeff
 */

/**
 *Stores various info as it's needed
 * @author Jeff
 *
 */
public class Storage 
{
	public HashSet<String> playerNames;
	public List<Post> posts;
	public HashSet<String> parsedPlayers;
	public PriorityQueue<PostedPlayer> postedPlayers;
	public final boolean isRegularSeason = false;
	/**
	 * This sets up the priority queue and it's subsequent comparator.
	 * No parameters are necessary, and the playerNames array doesn't need initialization.
	 */
	public Storage(Context cont)
	{
		postedPlayers = new PriorityQueue<PostedPlayer>(100, new Comparator<PostedPlayer>()
		{
			@Override
			public int compare(PostedPlayer a, PostedPlayer b)
			{
				if(a.count > b.count)
				{
					return -1;
				}
				if(a.count < b.count)
				{
					return 1;
				}
				return 0;
			}
		});
		playerNames = new HashSet<String>(400);
		posts = new ArrayList<Post>(500);
		parsedPlayers = new HashSet<String>(350);
	}
}
