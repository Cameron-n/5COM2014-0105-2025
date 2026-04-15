package fairview.talks;

import fairview.users.Applicant;
import java.util.ArrayList;
import java.util.List;

public class TalkSubmission {
    private final String title;
    private final String description; // up to 250 words
    private final Applicant applicant;
    private final List<String> reviews = new ArrayList<>(); //up to 2 reviews

    public TalkSubmission(String title, String description, Applicant applicant) {
        if (description.split("\\s+").length > 250){
            throw new IllegalArgumentException("Description must be 250 words or less.");
        }

        this.title = title;
        this.description = description;
        this.applicant = applicant;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void addReview(Review review) {
        if(reviews.size() >= 2){
            throw new IllegalStateException("A talk cannot have more than two reviews.");
        }
        reviews.add(review);
    }

    public List<String> getReviews() {
        return List.copyOf(reviews);
    }

    public double getAverageScore() {
        return reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
    }
}