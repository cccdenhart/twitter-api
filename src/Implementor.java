import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Implementor {

  public static void main(String[] args) {

    // filenames
    final String TWEET_FILE = "./data/tweets.csv";
    final String FOLLOWERS_FILE = "./data/followers.csv";
    final int NUM_TIMELINES = 1000;

    // get database information from program arguments
    String username = args[0];
    String password = args[1];
    String database = args[2];

    // allow user to choose whether to use 'postgres' or 'redis'
    Scanner keyboard = new Scanner(System.in);
    System.out.println("Enter 'postgres' or 'redis' to select a database: ");
    String db = keyboard.next();
    while (!db.equals("postgres") && !db.equals("redis")) {
      System.out
          .println("Invalid input! Please enter 'postgres' or 'redis' to select a database: ");
      db = keyboard.next();
    }
    System.out.println();

    // instantiate the proper database API
    ITwitterAPI api;
    if (db.equals("postgres")) {
      api = new PostgresTwitterAPI(username, password, database);
    } else {
      System.out.println("Enter 't' if using broadcasting and 'f' if not:");
      String broadcasting = keyboard.next();
      while (!broadcasting.toLowerCase().equals("t") && !broadcasting.toLowerCase().equals("f")) {
        System.out.println("Invalid input.  Please enter 't' or 'f':");
        broadcasting = keyboard.next();
      }
      Boolean broadcast;
      if (broadcasting.equals("t")) {
        api = new RedisBroadcastTwitterAPI();
      } else {
        api = new RedisTwitterAPI();
      }
    }

    keyboard.close();

    Extractor extractor = new Extractor();
    List<Tweet> tweets = extractor.getTweets(TWEET_FILE);
    HashMap<Integer, ArrayList<Integer>> followers = extractor.getFollowers(FOLLOWERS_FILE);


    // add followers
    System.out.println("Adding followers to database...");
    for (int user : followers.keySet()) {
      for (int follower : followers.get(user)) {
        api.addFollower(user, follower);
      }
    }
    System.out.println("Done");

    // post tweets
    long startTweetTime = System.currentTimeMillis();
    System.out.println("Posting tweets...");
    for (Tweet t : tweets) {
      api.postTweet(t);
    }
    long stopTweetTime = System.currentTimeMillis();
    long elapsedTweetTime = stopTweetTime - startTweetTime;
    System.out.println("Tweets posted in: " + elapsedTweetTime + " milliseconds");

    // generate users to retrieve home timelines from
    System.out.println("generating random users to retrieve home timelines from");
    ArrayList<Integer> user_ids = new ArrayList<>();
    for (int i = 0; i < NUM_TIMELINES; i++) {
      user_ids.add(api.findRandUser());
    }

    // post home timelines
    long startHTTime = System.currentTimeMillis();
    System.out.println("Retrieving home timelines...");
    for (int user_id : user_ids) {
      System.out.println("User " + user_id + " timeline: ");
      List<Tweet> timelines = api.getTimeline(user_id);
      for (Tweet t : timelines) {
        System.out.println(t.toString());
      }
      System.out.println();
    }
    long stopHTTime = System.currentTimeMillis();
    long elapsedHTTime = stopHTTime - startHTTime;
    System.out.println("Home timelines retrieved in: " + elapsedHTTime + " milliseconds");

  }

}
