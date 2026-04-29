package main.java.fairview.system;

import java.util.ArrayList;
import java.util.List;

import main.java.fairview.talks.TalkSubmission;
import main.java.fairview.users.ConferenceManager;
import main.java.fairview.users.Reviewer;

public class Conference {

    private final ConferenceManager manager;
    private final List<TalkSubmission> submissions = new ArrayList<>();
    private final List<Reviewer> reviewers = new ArrayList<>();
    private boolean submissionsClosed = false;

    public Conference(ConferenceManager manager) {
        this.manager = manager;
    }

    public void addSubmission(TalkSubmission submission) {
        if (submissionsClosed) {
            throw new IllegalStateException("Submissions are closed.");
        }
        submissions.add(submission);
    }

    public void registerReviewer(Reviewer reviewer) {
        reviewers.add(reviewer);
    }

    public void closeSubmissions() {
        submissionsClosed = true;
    }

    public boolean areSubmissionsClosed() {
        return submissionsClosed;
    }

    public List<TalkSubmission> getTalkSubmissions() {
        return List.copyOf(submissions);
    }

    public List<Reviewer> getReviewers() {
        return List.copyOf(reviewers);
    }

    public ConferenceManager getManager() {
        return manager;
    }
}