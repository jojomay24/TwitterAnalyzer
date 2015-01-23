package com.kahl.twitteranalyzer;
 
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
 
@Component
public class TwitterClient
{
	private final Logger log = LoggerFactory.getLogger(TwitterClient.class);
	
	@Autowired
	private Twitter twitter;
	
	public Set<String> getFollowerInformation(String twitterUser) throws TwitterException {
		Set<String> result = new TreeSet<>();
			IDs followerIDs = twitter.getFollowersIDs(twitterUser, -1l);
			for (Long id : followerIDs.getIDs()) {
				result.add(id + "");
			}
			return result;
	}
	
	public User getUserDetails(String user) {
		try {
			return twitter.showUser(Long.parseLong(user));
		} catch (TwitterException e) {
			log.error(e.getMessage());
		}
		
		return null;
	}

}