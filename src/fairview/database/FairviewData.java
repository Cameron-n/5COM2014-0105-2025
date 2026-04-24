package fairview.database;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FairviewData {
    private static String url = "jdbc:sqlite:fairview.db";

    public static void addData(String sql) {
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static ArrayList<ArrayList<String>> getData(String sql) {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                var temp = new ArrayList<String>();
                temp.add(rs.getString("name"));
                temp.add(rs.getString("affiliation"));
                data.add(temp);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return data;
    }
}
