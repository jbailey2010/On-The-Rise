package com.devingotaswitch.ontherise;

/**
 * Holds all the data of a post.
 * This is parsed later to get postedplayers.
 * @author Jeff
 *
 */
public class Post 
{
	public String text;
	public String date;
	
	/**
	 * Creates the very simple post object
	 * @param post
	 * @param datePosted
	 */
	public Post(String post, String datePosted)
	{
		text = post;
		date = datePosted;
	}
}
