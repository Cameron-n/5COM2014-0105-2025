package fairview.database;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FairviewDatabaseSetup {
    public static void main(String[] args) {
        //create database
        String url = "jdbc:sqlite:fairview.db";

        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        var sql = "";

        //create table: User
        sql = "CREATE TABLE IF NOT EXISTS User ("
            + "     userID INTEGER PRIMARY KEY,"
            + "     name CHAR(255) UNIQUE,"
            + "     email VARCHAR(255),"
            + "     password VARCHAR(255),"
            + "     affiliation CHAR(255),"
            + "     userType CHAR(255)"
            + ");";

        createTable(sql, url);

        //create table: Applicant
        sql = "CREATE TABLE IF NOT EXISTS Applicant ("
            + "     userID INTEGER PRIMARY KEY,"
            + "     FOREIGN KEY(userID) REFERENCES User(userID)"
            + ");";

        createTable(sql, url);

        //create table: Reviewer
        sql = "CREATE TABLE IF NOT EXISTS Reviewer ("
            + "     userID INTEGER PRIMARY KEY,"
            + "     reviewerCode VARCHAR(255),"
            + "     FOREIGN KEY(userID) REFERENCES User(userID)"
            + ");";

        createTable(sql, url);

        //create table: Talk
        sql = "CREATE TABLE IF NOT EXISTS Talk ("
            + "     talkID INTEGER PRIMARY KEY,"
            + "     title CHAR(255) UNIQUE,"
            + "     description CHAR(255),"
            + "     status CHAR(255),"
            + "     applicantID INTEGER,"
            + "     FOREIGN KEY(applicantID) REFERENCES Applicant(userID)"
            + ");";

        createTable(sql, url);

        //create table: Review
        sql = "CREATE TABLE IF NOT EXISTS Review ("
            + "     reviewID INTEGER PRIMARY KEY,"
            + "     score INTEGER,"
            + "     feedback VARCHAR(255),"
            + "     talkID INTEGER,"
            + "     reviewerID INTEGER,"
            + "     FOREIGN KEY(reviewerID) REFERENCES Reviewer(userID),"
            + "     FOREIGN KEY(talkID) REFERENCES Talk(talkID),"
            + "     UNIQUE(reviewerID, talkID)"
            + ");";

        createTable(sql, url);

        //create table: Timeslot
        sql = "CREATE TABLE IF NOT EXISTS Timeslot ("
            + "     timeslotID INTEGER PRIMARY KEY,"
            + "     startTime TIME,"
            + "     endTime TIME,"
            + "     talkID INTEGER,"
            + "     FOREIGN KEY(talkID) REFERENCES Talk(talkID)"
            + ");";

        createTable(sql, url);
        
        //add test data
        sql = "INSERT INTO User(name, password, affiliation, userType) VALUES("
            + "     'manager', 'manager', 'ConferenceOrg', 'Manager'"
            + "     ),("
            + "     'applicant1', 'applicant1', 'TESCO', 'Applicant'"
            + "     ),("
            + "     'applicant2', 'applicant2', 'ASDA', 'Applicant'"
            + "     ),("
            + "     'reviewer1', 'reviewer1', 'Waitrose', 'Reviewer'"
            + "     ),("
            + "     'reviewer2', 'reviewer2', 'Morrisons', 'Reviewer'"
            + "     )";
        addData(sql, url);

    }

    public static void createTable(String sql, String url) {
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement()) {
            //create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean addData(String sql, String url) {
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
                return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
