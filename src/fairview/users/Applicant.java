package fairview.users;

import fairview.talks.TalkSubmission;
import java.util.ArrayList;
import java.util.List;

public class Applicant extends User {
    private final List<TalkSubmission> talkSubmissions = new ArrayList<>();
    
    public Applicant(String name, String affiliation) {
        super(name, affiliation);
    }

    public void addSubmission(TalkSubmission submission) {
        talkSubmissions.add(submission);
    }

    public List<TalkSubmission> getTalkSubmissions() {
        return List.copyOf(talkSubmissions);
    }
}