package fairview.talks;

public class FeedbackReport {
    private final TalkSubmission talkSubmission;
    private final List<String> reviewerComments; 
    private final boolean selected;

    public FeedbackReport(TalkSubmission talkSubmission, List<String> reviewerComments, boolean selected) {
        this.talkSubmission = talkSubmission;
        this.reviewerComments = reviewerComments;
        this.selected = selected;
    }

    public TalkSubmission getTalkSubmission() {
        return talkSubmission;
    }

    public List<String> getReviewerComments() {
        return reviewerComments;
    }

    public boolean isSelected() {
        return selected;
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Talk Title: ").append(talkSubmission.getTitle()).append("\n");
        report.append("Description: ").append(talkSubmission.getDescription()).append("\n");
        report.append("Applicant: ").append(talkSubmission.getApplicant().getName()).append("\n");
        report.append("Average Score: ").append(String.format("%.2f", talkSubmission.getAverageScore())).append("\n");
        report.append("Reviewer Comments:\n");
        for (String comment : reviewerComments) {
            report.append("- ").append(comment).append("\n");
        }
        report.append("Selected for Conference: ").append(selected ? "Yes" : "No").append("\n");
        return report.toString();
    }
}