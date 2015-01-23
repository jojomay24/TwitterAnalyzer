package com.kahl.twitteranalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import twitter4j.TwitterException;
import twitter4j.User;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Component
public class Main {
	
	@Autowired
	private TwitterClient twitterClient;
	
	private final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);
	
	private static CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(';').withIgnoreEmptyLines();
	
	private String postfixFollowerCurrent = "-followerIDs.current";
	private String postfixFollowerDelta = "-followerIDs.delta";
	
	@Value("${twitterUser:tngtech}")
	private String twitterUser;

	@Value("${fileOutputBasePath:}")
	private String fileOutputBasePath;

	private Set<String> currentIDsFromTwitter;
	
	public void analyze() {
		log.info("Starting analyzing for user: " + twitterUser);
		
		//Create the CSVFormat object
		createFileIfNotExisting(filenameFollowerIDsDelta());
		createFileIfNotExisting(filenameFollowerIDsCurrent());
		
		// putting all known ids in a set
		Set<String> currentIDsFromFile = readAllIDsFromCurrentFile(); 
		currentIDsFromTwitter = getAllFollwerIDsFromTwitter();
		
		SetView<String> symmetricDifference = Sets.symmetricDifference(currentIDsFromFile, currentIDsFromTwitter);
		
		if (symmetricDifference.size() == 0) {
			log.info("No new followers were found.");
		} else {
			processSyncDifferences(currentIDsFromFile, symmetricDifference);
	
			deleteAndRecreateFile(filenameFollowerIDsCurrent());
			writeCurrentFollowerIDFile(currentIDsFromTwitter);
		}
	}

	private void processSyncDifferences(Set<String> currentIDsFromFile,
			SetView<String> symmetricDifference) {
		
		File file = new File(filenameFollowerIDsDelta());
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(file,true), csvFormat);){
		for (String id : symmetricDifference) {
			User userDetails = twitterClient.getUserDetails(id);
			if (currentIDsFromFile.contains(id)) {
				log.info("UNFOLLOW: Follower " + id + " unfollowed you!");
				printer.printRecord("DROP",
						DateTime.now().getMillis(),
						id,
						currentIDsFromTwitter.size(),
						(userDetails != null ) ? userDetails.getProfileImageURL() : "",
						(userDetails != null ) ? userDetails.getScreenName() : "",
						(userDetails != null ) ? userDetails.getName() : ""
						);
			} else {
				log.info("FOLLOW: Follower " + id + " followed you!");
				printer.printRecord("ADD",
						DateTime.now().getMillis(),
						id,
						currentIDsFromTwitter.size(),
						(userDetails != null ) ? userDetails.getProfileImageURL() : "",
						(userDetails != null ) ? userDetails.getScreenName() : "",
						(userDetails != null ) ? userDetails.getName() : ""
						);
			}
		}
		
	} catch (IOException e) {
		log.error(e.getMessage());
	}
		
	}

	private String filenameFollowerIDsCurrent() {
		return fileOutputBasePath + "." + twitterUser + postfixFollowerCurrent;
	}
	
	private String filenameFollowerIDsDelta() {
		return fileOutputBasePath + "." + twitterUser + postfixFollowerDelta;
	}

	private void writeCurrentFollowerIDFile(Set<String> currentIDsFromTwitter) {
		
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(new File(
				filenameFollowerIDsCurrent())), csvFormat);){
			for (String id : currentIDsFromTwitter) {
				printer.printRecord(id);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} 
		
	}

	private void deleteAndRecreateFile(String filename) {
		new File(filename).delete();
		try {
			new File(filename).createNewFile();
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
	}

	private Set<String> getAllFollwerIDsFromTwitter() {
		Set<String> result = new TreeSet<>();
		try {
			result = twitterClient.getFollowerInformation(twitterUser);
		} catch (TwitterException e) {
			log.error(e.getMessage());
		}
		log.info("found " +  result.size() + " followerIDs from twitter.");
		return result;
	}

	private Set<String> readAllIDsFromCurrentFile() {
		Set<String> result = new TreeSet<>();
		try (CSVParser parser = new CSVParser(new FileReader(filenameFollowerIDsCurrent()), csvFormat);){
			for(CSVRecord record : parser){
				result.add(record.get(0));
			}
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
		
		log.info("found " +  result.size() + " followerIDs in " + postfixFollowerCurrent + " file.");
		return result;
	}

	private boolean createFileIfNotExisting(String fileName) {
		File fCurrent = new File(fileName);
		try {
			return fCurrent.createNewFile();
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
		
		return false;
	}
	
	

}
