package fairview.users;

import java.time.LocalDate;

public class ConferenceManager extends User {

    private int numberOfSlots;
    private LocalDate submissionDeadline;
    private boolean configured = false;

    public ConferenceManager(String name, String affiliation) {
        super(name, affiliation);
    }

    public void configureConference(int numberOfSlots, LocalDate submissionDeadline) {
        if (numberOfSlots <= 0) {
            throw new IllegalArgumentException("Number of slots must be positive.");
        }
        if (submissionDeadline == null || submissionDeadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Submission deadline must be a future date.");
        }

        this.numberOfSlots = numberOfSlots;
        this.submissionDeadline = submissionDeadline;
        this.configured = true;
    }

    public int getNumberOfSlots() {
        ensureConfigured();
        return numberOfSlots;
    }

    public LocalDate getSubmissionDeadline() {
        ensureConfigured();
        return submissionDeadline;
    }

    public boolean isConfigured() {
        return configured;
    }

    private void ensureConfigured() {
        if (!configured) {
            throw new IllegalStateException("Conference has not been configured yet.");
        }
    }
}