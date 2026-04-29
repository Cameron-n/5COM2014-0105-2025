package main.java.fairview.system;

import java.util.ArrayList;
import java.util.List;

import main.java.fairview.talks.FeedbackReport;
import main.java.fairview.talks.Review;
import main.java.fairview.talks.TalkSubmission;

public class FeedbackService {

    public FeedbackReport generateFeedback(TalkSubmission talk) {

        //Extract reviewer comments from Review objects
        List<String> comments = new ArrayList<>();
        for (Review r : talk.getReviews()) {
            comments.add(r.getComment()); 
        }

        boolean selected = talk.getAverageScore() >= 3.0;

        //Build and return the FeedbackReport
        return new FeedbackReport(
                talk,   
                comments, 
                selected   
        );
    }
}