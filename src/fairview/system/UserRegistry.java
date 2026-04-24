package fairview.system;

import fairview.users.*;
import fairview.database.FairviewData;

import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO User(name, affiliation, userType) VALUES("
                     + "'" + applicant.getName() + "'" + "," 
                     + "'" + applicant.getAffiliation() + "'" + "," 
                     + "'" + "Applicant" + "'"
                     + ")";
        FairviewData.addData(sql);
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
    
    public void setApplicant(String name, String aff) {
        Applicant a = new Applicant(name, aff);
        applicants.add(a);
    }
}