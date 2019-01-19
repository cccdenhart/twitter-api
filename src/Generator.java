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

    // store all of the words to be used in an ArrayList
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
        float elapsedTime = (stopTime - startTime) / 1000;
        System.out.println("CSV files generated in: " + elapsedTime + " seconds");
    }

    // writes the tweetOutput generated to a CSV file
    // used some ideas from: https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file
    private void genTweetCSV() throws FileNotFoundException {
        System.out.println("Generating tweets CSV file...");
        PrintWriter pw = new PrintWriter(new File("./tweets.csv"));
        pw.write(this.tweetOutput.toString());
        pw.close();
        System.out.println("Done");
    }

    // writes the followersOutput generated to a CSV file
    private void genFollowersCSV() throws FileNotFoundException {
        System.out.println("Generating followers CSV file...");
        PrintWriter pw = new PrintWriter(new File("./followers.csv"));
        pw.write(this.followersOutput.toString());
        pw.close();
        System.out.println("Done");
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

    // generates a random timestamp
    private String makeTimestamp() {
        int year = this.START_YEAR + new Random().nextInt(this.END_YEAR - this.START_YEAR);
        String month = Integer.toString(1 + new Random().nextInt(12));
        String day = Integer.toString(1 + new Random().nextInt(30)); // assumes only 30 days in a month for simplicity
        String hour = Integer.toString(new Random().nextInt(24));
        String minute = Integer.toString(new Random().nextInt(60));
        String second = Integer.toString(1 + new Random().nextInt(60));
        ArrayList<String> items = new ArrayList<>(Arrays.asList(month, day, hour, minute, second));
        // insert a '0' in front of every single digit time item
        for (String item : items) {
            if (item.length() == 1) {
                item = "0" + item;
            }
        }
        return month + "-" + day + "-" + year + " " + hour + ":" + minute + ":" + second;
    }

    // randomly generates a tweet
    private void makeTweet(int tweet_id) {
        int user_id = new Random().nextInt(this.NUM_USERS);
        String ts = this.makeTimestamp();
        String text = this.makeTweetText();
        String tweet = tweet_id + "," + user_id + "," + ts + "," + text;
        this.tweetOutput.append(tweet);
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
}
