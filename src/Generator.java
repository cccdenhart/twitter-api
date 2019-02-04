import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.sql.Timestamp;

public class Generator {

    // arbitrary list of words used to generate a tweet
    private final ArrayList<String> WORDS = new ArrayList<>(Arrays.asList("hello", "goodbye", "something", "word", "other", "thing", "big", "small", "ordinary", "snow", "weather", "rain", "summer", "winter", "fall", "spring", "falling", "rising", "up", "down", "wall", "brick", "trash", "recycling", "sky", "ground", "carpet", "house", "furniture", "chair", "sofa", "tv", "phone", "sink", "fridge", "water", "chicken", "cow"));

    // store basic assumptions of the simulation as immutable constants
    private final int NUM_TWEETS = 1000000;
    private final int CHAR_COUNT = 140;
    private final int NUM_USERS = 50;
    private final int NUM_FOLLOWERS = 10;
    private final int START_YEAR = 2010;
    private final int END_YEAR = 2019;

    // store the tweets and followers output to be written to the CSV files
    private StringBuilder tweetOutput;
    private StringBuilder followersOutput;

    public Generator() {
        this.tweetOutput = new StringBuilder();
        this.followersOutput = new StringBuilder();
        this.makeManyTweets();
        this.makeManyFollowers();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Generator gen = new Generator();
        try {
            gen.genTweetCSV();
            gen.genFollowersCSV();
        } catch (Exception e) {
            System.out.println(e);
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("CSV files generated in: " + elapsedTime + " milliseconds");
    }

    // writes the tweetOutput generated to a CSV file
    // used some ideas from: https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file
    private void genTweetCSV() throws FileNotFoundException {
        System.out.println("Generating tweets CSV file...");
        PrintWriter pw = new PrintWriter(new File("./data/tweets.csv"));
        pw.write(this.tweetOutput.toString());
        pw.close();
        System.out.println("Done");
    }

    // writes the followersOutput generated to a CSV file
    private void genFollowersCSV() throws FileNotFoundException {
        System.out.println("Generating followers CSV file...");
        PrintWriter pw = new PrintWriter(new File("./data/followers.csv"));
        pw.write(this.followersOutput.toString());
        pw.close();
        System.out.println("Done");
    }

    // generate the number of tweets that are required
    private void makeManyTweets() {
        System.out.println("Making tweets...");
        for (int i = 0; i < this.NUM_TWEETS; i++) {
            this.makeTweet(i);
            if (i < this.NUM_TWEETS - 1) {
                this.tweetOutput.append("\n");
            }
        }
        System.out.println("Done");
    }

    // randomly generates a tweet
    private void makeTweet(int tweet_id) {
        int user_id = new Random().nextInt(this.NUM_USERS);
        Timestamp ts = this.makeTimestamp();
        String text = this.makeTweetText();
        String tweet = tweet_id + "," + user_id + "," + ts + "," + text;
        this.tweetOutput.append(tweet);
    }

    // generates a random timestamp
    private Timestamp makeTimestamp() {
        long offset = Timestamp.valueOf(this.START_YEAR + "-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf(this.END_YEAR + "-01-01 00:00:00").getTime();
        long diff = end - offset + 1;
        Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
        return rand;
    }

    // randomly generates text for a tweet
    private String makeTweetText() {
        String text = "";
        String maxWord = Collections.max(this.WORDS, Comparator.comparing(s -> s.length()));
        while (text.length() < (this.CHAR_COUNT - maxWord.length()) - 1) {
            int index = new Random().nextInt(this.WORDS.size());
            String word = WORDS.get(index);
            text += word + " ";
        }
        return text;
    }

    // generate followers for every user
    private void makeManyFollowers() {
        System.out.println("Making followers...");
        for (int i = 0; i < this.NUM_USERS; i++) {
            if (i < this.NUM_USERS - 1) {
                this.makeFollowers(i, false);
            } else {
                this.makeFollowers(i, true);
            }
        }
        System.out.println("Done");
    }

    // randomly generate followers for a given user
    private void makeFollowers(int user_id, boolean isLast) {
        for (int i = 0; i < this.NUM_FOLLOWERS; i++) {
            int follower = 1 + new Random()
                .nextInt(this.NUM_USERS); // assume it is possible for a user to follow themselves
            this.followersOutput.append(user_id);
            this.followersOutput.append(",");
            this.followersOutput.append(follower);
            if (!isLast) {
                this.followersOutput.append("\n");
            } else {
                if (i < this.NUM_FOLLOWERS - 1) {
                    this.followersOutput.append("\n");
                }
            }
        }
    }
}
