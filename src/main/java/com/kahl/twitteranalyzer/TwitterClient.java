package com.kahl.twitteranalyzer;
 
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
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
	
	
//    public void demoServiceMethod()
//    {
//    	log.info("Getting Tweets ...  Current time is :: "+ new Date());
//    	try {
////			ResponseList<Status> homeTimeline = twitter.getHomeTimeline();
//			String queryString = "#tngtech OR tngtech";
//			FriendsFollowersResources friendsFollowers = twitter.friendsFollowers();
//			PagableResponseList<User> followersList = twitter.getFollowersList("alexJojomay", -1);
//			QueryResult result = twitter.search(new Query(queryString));
//			System.out.println("RateLimit" + result.getRateLimitStatus());
//			for (Status t : result.getTweets()) {
//				log.info(t+"");
//				Tweet tweet = new Tweet(t.getId(), t.getText(), t.getUser().getName());
//				tweet.setAuthorImageUrl(t.getUser().getProfileImageURL());
//				tweet.setNrOfRetweets(t.getRetweetCount());
//				MediaEntity[] mediaEntities = t.getMediaEntities();
//				System.out.println("Ex:" + mediaEntities.length);
//				if (!t.isRetweet()) {
//					tweetRepository.save(tweet);
//				}
//			}
//		} catch (TwitterException e) {
//			log.error("An exception occured retreiving tweets: " + e.getMessage());
//		}
////    	tweetRepository.save(new Tweet("a","b","c"));
//    }
}