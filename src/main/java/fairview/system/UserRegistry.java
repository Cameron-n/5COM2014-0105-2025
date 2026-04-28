package fairview.system;

import java.util.ArrayList;
import java.util.List;

import fairview.users.*;

public class UserRegistry {

    private ConferenceManager manager;
    private final List<Applicant> applicants = new ArrayList<>();
    private final List<Reviewer> reviewers = new ArrayList<>();

    public void registerManager(ConferenceManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager already registered.");
        }
        this.manager = manager;
    }

    public void registerApplicant(Applicant applicant) {
        applicants.add(applicant);
    }

    public void registerReviewer(Reviewer reviewer) {
        reviewers.add(reviewer);
    }

    public ConferenceManager getManager() {
        return manager;
    }

    public List<Applicant> getApplicants() {
        return List.copyOf(applicants);
    }

    public List<Reviewer> getReviewers() {
        return List.copyOf(reviewers);
    }
}