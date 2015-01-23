package com.kahl.twitteranalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.base.Strings;
import com.kahl.twitteranalyzer.Application.TwitterConfig;

@Configuration
@Import({
	TwitterConfig.class,
	TwitterClient.class,
	Main.class
	})
@EnableAutoConfiguration
public class Application {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		String twitterUser = context.getEnvironment().getProperty("twitterUser");
		
		String consumerKey = context.getEnvironment().getProperty("OAuthConsumerKey");
		String consumerSecret = context.getEnvironment().getProperty("OAuthConsumerSecret");
		String accessToken = context.getEnvironment().getProperty("OAuthAccessToken");
		String accessTokenSecret = context.getEnvironment().getProperty("OAuthAccessTokenSecret");
		
		
		if (Strings.isNullOrEmpty(twitterUser)) {
			System.out.println("########################################################\n" +
					           "#  Missing Params:  \n" +
					           "#  --twitterUser=\"xxx\" \n" +
					           "#  --fileOutputBasePath=\"/tmp/\" \n" +
					           "########################################################");
		} else if (Strings.isNullOrEmpty(consumerKey) || Strings.isNullOrEmpty(consumerSecret)
				|| Strings.isNullOrEmpty(accessToken) || Strings.isNullOrEmpty(accessTokenSecret)){
			System.out.println("########################################################\n" +
			           "#  Missing TwitterAuthData: \n" + 
					   "#  Create a file application.properties either in src/main/resources (if you build the source yourself) or in the same folder where the jar file is located (if you only run the built jar file via java -jar ... ) \n" +
					   "#  Include following properties: \n" +
					   "#     OAuthConsumerKey=YOUR_DATA \n" + 
					   "#     OAuthConsumerSecret=YOUR_DATA \n" + 
					   "#     OAuthAccessToken=YOUR_DATA \n" + 
					   "#     OAuthAccessTokenSecret=YOUR_DATA \n" +  
			           "########################################################");
		} else {
			Main main = context.getBean(Main.class);
			main.analyze();
		}
		
		
	}
	
	@Configuration
	protected static class TwitterConfig {
		
		@Value("${OAuthConsumerKey:}")	private String consumerKey;
		@Value("${OAuthConsumerSecret:}") private String consumerSecret;
		@Value("${OAuthAccessToken:}") private String accessToken;
		@Value("${OAuthAccessTokenSecret:}") private String accessTokenSecret;
		
		@Bean
		public TwitterFactory twitterFactory() {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(consumerKey)
			  .setOAuthConsumerSecret(consumerSecret)
			  .setOAuthAccessToken(accessToken)
			  .setOAuthAccessTokenSecret(accessTokenSecret);
			TwitterFactory tf = new TwitterFactory(cb.build());
			return tf;
		}
		
		@Bean
		public Twitter twitter() {
			return twitterFactory().getInstance();
		}
	}

}
