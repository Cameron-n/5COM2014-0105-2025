package main.java.fairview.users;

import main.java.fairview.database.FairviewData;
import main.java.fairview.talks.TalkSubmission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Applicant extends User {
    private final List<TalkSubmission> talkSubmissions = new ArrayList<>();
    
    public Applicant(String name, String affiliation) {
        super(name, affiliation);
    }

    public boolean addSubmission(TalkSubmission submission) {
        String sql_get = "SELECT userID FROM User"
                         + " WHERE name=" 
                         + "'" + this.getName() + "';";
        List<String> sql_col = Arrays.asList("userID");
        String userID = FairviewData.getData(sql_get, sql_col).get(0).get(0);
        String sql = "INSERT INTO Talk(title, description, applicantID) VALUES("
                     + "'" + submission.getTitle() + "'" + "," 
                     + "'" + submission.getDescription() + "'" + "," 
                     + "'" + userID + "'"
                     + ")";
        boolean success = FairviewData.addData(sql);
        if (success) {
            talkSubmissions.add(submission);
        }
        return success;
    }
    
    public void setSubmission(String title, String description) {
        TalkSubmission s = new TalkSubmission(title, description, this);
        talkSubmissions.add(s);
    }

    @Override
    public String toString() {
        return getName();
    }

    public List<TalkSubmission> getTalkSubmissions() {
        return List.copyOf(talkSubmissions);
    }
}