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

    public boolean registerApplicant(Applicant applicant, String pass) {
        String sql = "INSERT INTO User(name, password, affiliation, userType) VALUES("
                     + "'" + applicant.getName() + "'" + "," 
                     + "'" + pass + "'" + ","
                     + "'" + applicant.getAffiliation() + "'" + "," 
                     + "'" + "Applicant" + "'"
                     + ")";
        boolean success = FairviewData.addData(sql);
        if (success) {
            applicants.add(applicant);
        }
        return success;
    }

    public boolean registerReviewer(Reviewer reviewer, String pass) {
        String sql = "INSERT INTO User(name, password, affiliation, userType) VALUES("
                     + "'" + reviewer.getName() + "'" + "," 
                     + "'" + pass + "'" + ","
                     + "'" + reviewer.getAffiliation() + "'" + "," 
                     + "'" + "Reviewer" + "'"
                     + ")";
        boolean success = FairviewData.addData(sql);
        if (success) {
            reviewers.add(reviewer);
        }
        return success;
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
    
    public Reviewer setReviewer(String name, String aff) {
        Reviewer r = new Reviewer(name, aff);
        reviewers.add(r);
        return r;
    }
}