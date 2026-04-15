package fairview.talks;

import fairview.users.Reviewer;

public class Review {
    private final Reviewer reviewer;
    private final int score; // 1-10
    private final String feedback; // up to 250 characters

    public Review(Reviewer reviewer, int score, String feedback) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("Score must be between 1 and 10.");
        }

        if (feedback.length() > 250) {
            throw new IllegalArgumentException("Feedback must be 250 characters or less.");
        }

        this.reviewer = reviewer;
        this.score = score;
        this.feedback = feedback;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public int getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getComment() {
        return feedback;
    }
}