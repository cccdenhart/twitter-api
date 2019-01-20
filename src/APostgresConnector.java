import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// abstract class for connecting to a postgres database
public class APostgresConnector {

  public Connection connect (String username, String password, String database) {
    Connection c = null;
    System.out.println("Trying to connect to database...");
    try {
      c = DriverManager
          .getConnection("jdbc:postgresql://localhost:5432/" + database,
              username, password);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
    return c;
  }

  public void insert (Connection c, String sql) {
    try {
      Statement stmt = c.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public ResultSet query (Connection c, String sql) {
    ResultSet rs = null;
    try {
      Statement stmt = c.createStatement();
      rs = stmt.executeQuery(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    return rs;
  }
}
