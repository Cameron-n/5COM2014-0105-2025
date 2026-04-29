package main.java.fairview.database;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FairviewData {
    private static String url = "jdbc:sqlite:fairview.db";

    public static boolean addData(String sql) {
        try (var conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
                return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    public static ArrayList<ArrayList<String>> getData(String sql, List<String> columns) {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                var temp = new ArrayList<String>();
                for (String i: columns) {
                    temp.add(rs.getString(i));
                }
                data.add(temp);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return data;
    }
}
