package main.java.fairview.users;

import java.util.ArrayList;
import java.util.List;

import main.java.fairview.talks.TalkSubmission;

public class Applicant extends User {
    private final List<TalkSubmission> talkSubmissions = new ArrayList<>();
    
    public Applicant(String name, String affiliation) {
        super(name, affiliation);
    }

    @Override
    public String toString() {
        return getName();
    }


    public void addSubmission(TalkSubmission submission) {
        talkSubmissions.add(submission);
    }

    public List<TalkSubmission> getTalkSubmissions() {
        return List.copyOf(talkSubmissions);
    }
}