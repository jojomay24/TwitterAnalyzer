# TwitterAnalyzer
Keeps track of your twitter followers and visualizes as a nice chart. 
See demo here: http://alexxx.eltanin.uberspace.de/twitter/charts.html  and enter alexjojomay 

### Technology
 java 7 (spring boot,twitter4j), html, highcharts.js

### Instructions:
... clone it!

... build it:
gradle clean build

... create application.properties with twitter authorization data (get auth-data here: https://apps.twitter.com/app/new ) 
vim application.properties
OAuthConsumerKey=YOUR_DATA 
OAuthConsumerSecret=YOUR_DATA 
OAuthAccessToken=YOUR_DATA 
OAuthAccessTokenSecret=YOUR_DATA 

... run it:
java -jar build/libs/twitterAnalyzer-0.1.0.jar --twitterUser="alexjojomay"  --fileOutputBasePath="/tmp/"

... view your chart
open charts.html with any modern browser and specify your used twitterName

... for creating a real chart, run this app on a regular basis (like once a day). All changes will be tracked in local csv-files (Format: ."twitterUser"-followerIDs.current, ."twitterUser"-followerIDs.delta). The webpage (charts.html) uses these files as input-data.
