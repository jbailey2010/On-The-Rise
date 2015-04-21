package com.devingotaswitch.ontherise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;


/**
 * A small library to help manage parsing of player
 * names from cbs to local storage
 * @author Jeff
 *
 */
public class ParsePlayerNames {

	/**
	 * This fetches/parses the player names, then 
	 * calls another function to write it to file
	 * @param holder holds the array to be written to and written
	 * @param cont the context used to write to file in the called function
	 * @throws IOException 
	 */
	public static void fetchPlayerNames(final Context cont) throws IOException
	{
		//holder.playerNames.clear();
		HashSet<String> names = new HashSet<String>();
		String[] defenses = {"Bengals D/ST", "Steelers D/ST", "Browns D/ST", "Ravens D/ST", 
	   		"Patriots D/ST", "Dolphins D/ST", "Bills D/ST", "Jets D/ST", "Texans D/ST",
	   		"Colts D/ST", "Jaguars D/ST", "Titans D/ST", "Broncos D/ST", "Raiders D/ST",
	   		"Chiefs D/ST", "Chargers D/ST", "Vikings D/ST", "Lions D/ST", "Packers D/ST",
	   		"Bears D/ST", "Giants D/ST", "Eagles D/ST", "Cowboys D/ST", "Redskins D/ST",
	   		"Falcons D/ST", "Saints D/ST", "Panthers D/ST", "Buccaneers D/ST", "Seahawks D/ST",
	   		"49ers D/ST", "Rams D/ST", "Cardinals D/ST"	};
	   	for(int i = 0; i < defenses.length; i++)
	   	{
			names.add(defenses[i]);
	   	}
		//The baseline URL and the endings to be attached for the sake of getting all data easily
		String url = "http://www.cbssports.com/nfl/playersearch?POSITION=";
		String[] concat = {"QB&print_rows=9999", "RB&print_rows=9999", "FB&print_rows=9999",
				"WR&print_rows=9999", "TE&print_rows=9999", "K&print_rows=9999"};
		for(int i = 0; i < concat.length; i++)
		{
			final String full = url + concat[i];
			try {
				Document doc = Jsoup.connect(full).timeout(0).get();
				ParsePlayerNames.fetchPlayersHelp(doc, names, cont, full, "row2");
				ParsePlayerNames.fetchPlayersHelp(doc, names, cont, full, "row1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(names.size() > 200)
		{
			WriteToFile.storePlayerNames(names, cont);
		}
	}

	/**
	 * Abstracted as cbs is stupid, same code twice
	 * @param holder the storage to write to
	 * @param cont the context because idk
	 * @param full the url to work with
	 * @param params the id to parse from
	 * @throws IOException
	 */
	public static void fetchPlayersHelp(Document doc, HashSet<String> names, Context cont, String full, String params) throws IOException
	{
		List<String> perRow = HandleBasicQueries.handleTablesMulti(doc, full, params);
		for(int j = 0; j < perRow.size(); j++)
		{
			String[][] all = new String[perRow.size()][];
			all[j] = perRow.get(j).split(" ");
			if(all[j][0].contains(","))
			{
				String lastName = all[j][0].substring(0, all[j][0].length() - 1);
				String name = all[j][1] + " " + lastName;
				names.add(name);
			}
		}
	}

}
