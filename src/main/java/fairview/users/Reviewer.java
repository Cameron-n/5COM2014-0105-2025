package main.java.fairview.users;

import java.util.ArrayList;
import java.util.List;

import main.java.fairview.talks.Review;
import main.java.fairview.talks.TalkSubmission;

public class Reviewer extends User {
    private final List<TalkSubmission> assignedTalks = new ArrayList<>();

    public Reviewer(String name, String affiliation) {
        super(name, affiliation);
    }

    @Override
    public String toString() {
        return getName();
    }

    public void addAssignedTalk(TalkSubmission talk) {
        assignedTalks.add(talk);
    }

    public List<TalkSubmission> getAssignedTalks() {
        return List.copyOf(assignedTalks);
    }

    public Review submitReview(TalkSubmission talk, int score, String feedback) {
        if (!assignedTalks.contains(talk)) {
            throw new IllegalArgumentException("Reviewer is not assigned to this talk.");
        }
        Review review = new Review(this, score, feedback);
        talk.addReview(review);
        return review;
    }

}