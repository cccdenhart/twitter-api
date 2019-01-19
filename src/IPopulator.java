import java.util.List;

// interface for populating any database given a CSV file of data
public interface IPopulator {

  // extracts the CSV data and stores it in a 2-D array
  List<String[]> extract(String file);

}
