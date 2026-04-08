package fairview.system;

import fairview.talks.FeedbackReport;
import fairview.talks.TalkSubmission;

public class FeedbackService {

    public FeedbackReport generateReport(TalkSubmission talk, boolean selected) {
        return new FeedbackReport(
                talk.getTitle(),
                talk.getReviews().stream().map(r -> r.getFeedback()).toList(),
                selected
        );
    }
}