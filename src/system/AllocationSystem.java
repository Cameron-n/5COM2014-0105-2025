package fairview.system;

import fairview.talks.TalkSubmission;
import fairview.users.Reviewer;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AllocationService {

    private final Random random = new Random();

    public void allocate(Conference conference) {
        if (!conference.areSubmissionsClosed()) {
            throw new IllegalStateException("Cannot allocate before submissions close.");
        }

        List<Reviewer> reviewers = conference.getReviewers();

        for (TalkSubmission talk : conference.getSubmissions()) {
            String applicantAffiliation = talk.getApplicant().getAffiliation();

            //filter reviewers with different affiliation
            List<Reviewer> eligible = reviewers.stream()
                    .filter(r -> !r.getAffiliation().equals(applicantAffiliation))
                    .toList();

            if (eligible.size() < 2) {
                throw new IllegalStateException("Not enough eligible reviewers for talk: " + talk.getTitle());
            }

            //randomly pick two reviewers
            List<Reviewer> shuffled = new java.util.ArrayList<>(eligible);
            Collections.shuffle(shuffled, random);

            Reviewer r1 = shuffled.get(0);
            Reviewer r2 = shuffled.get(1);

            r1.assignTalk(talk);
            r2.assignTalk(talk);
        }
    }
}