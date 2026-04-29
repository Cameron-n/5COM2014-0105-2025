package fairview.users;

import fairview.talks.TalkSubmission;
import fairview.database.FairviewData;
import fairview.talks.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reviewer extends User {
    private final List<TalkSubmission> assignedTalks = new ArrayList<>();

    public Reviewer(String name, String affiliation) {
        super(name, affiliation);
    }

    public void addAssignedTalk(TalkSubmission talk) {
        String sql_talk = "SELECT talkID FROM Talk"
                         + " WHERE title=" 
                         + "'" + talk.getTitle() + "';";
        List<String> sql_talk_col = Arrays.asList("talkID");
        String talkID = FairviewData.getData(sql_talk, sql_talk_col).get(0).get(0);
        String sql_reviewer = "SELECT userID FROM User"
                              + " WHERE name="
                              + "'" + this.getName() + "';";
        List<String> sql_reviewer_col = Arrays.asList("userID");
        String reviewerID = FairviewData.getData(sql_reviewer, sql_reviewer_col).get(0).get(0);
        String sql = "INSERT INTO Review(reviewerID, talkID) VALUES("
                     + "'" + reviewerID + "',"
                     + "'" + talkID + "'"
                     + ")";
        assignedTalks.add(talk);
        FairviewData.addData(sql);
    }

    public List<TalkSubmission> getAssignedTalks() {
        return List.copyOf(assignedTalks);
    }

    public Review submitReview(TalkSubmission talk, int score, String feedback) {
        if (!assignedTalks.contains(talk)) {
            throw new IllegalArgumentException("Reviewer is not assigned to this talk.");
        }
        Review review = new Review(this, score, feedback);
        
        String sql_reviewerid = "SELECT userID FROM User"
                                + " WHERE name=" + "'" + this.getName() + "'";
        List<String> col_reviewerid = Arrays.asList("userID");
        String reviewerid = FairviewData.getData(sql_reviewerid, col_reviewerid).get(0).get(0);

        String sql_talkid = "SELECT talkID FROM Talk"
                            + " WHERE title=" + "'" + talk.getTitle() + "'";
        List<String>  col_talkid = Arrays.asList("talkID");
        String talkid = FairviewData.getData(sql_talkid, col_talkid).get(0).get(0);

        String sql = "UPDATE Review"
                     + " SET score=" + score + ","
                     + " feedback=" + "'" + feedback + "'"
                     + " WHERE reviewerID=" + "'" + reviewerid + "'"
                     + " AND talkID=" + "'" + talkid + "'";
        boolean success = FairviewData.addData(sql);
        if (success) {
            talk.addReview(review);
        }
        return review;
    }

}