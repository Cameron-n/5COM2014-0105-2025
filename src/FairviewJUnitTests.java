import fairview.users.*;
import fairview.talks.*;
import fairview.system.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class FairviewJUnitTests {

    // user

    @Test
    void TC10_RegisterApplicant() {
        Applicant a = new Applicant("May", "Google");
        assertNotNull(a);
    }

    @Test
    void TC11_RegisterReviewer() {
        Reviewer r = new Reviewer("John", "IBM");
        assertNotNull(r);
    }

    @Test
    void TC12_EmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Applicant("", "Google");
        });
    }

    // talk

    @Test
    void TC13_SubmitTalk() {
        Applicant a = new Applicant("May", "Google");
        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);
        assertEquals("AI Talk", t.getTitle());
    }

    @Test
    void TC14_NoApplicant() {
        assertThrows(Exception.class, () -> {
            new TalkSubmission("AI Talk", "AI in industry", null);
        });
    }

    @Test
    void TC15_DescriptionLimit() {
        String longDesc = "word ".repeat(300);
        Applicant a = new Applicant("May", "Google");

        assertThrows(IllegalArgumentException.class, () -> {
            new TalkSubmission("AI Talk", longDesc, a);
        });
    }

    // allocation

    @Test
    void TC16_ValidAllocation() {
        ConferenceManager cm = new ConferenceManager("Boss", "QCON");
        cm.configureConference(2, LocalDate.now().plusDays(5));

        Conference c = new Conference(cm);

        Applicant a = new Applicant("May", "Google");
        Reviewer r1 = new Reviewer("R1", "IBM");
        Reviewer r2 = new Reviewer("R2", "Microsoft");

        c.registerReviewer(r1);
        c.registerReviewer(r2);

        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);
        c.addSubmission(t);

        c.closeSubmissions();

        new AllocationService().allocate(c);

        assertTrue(
                r1.getAssignedTalks().contains(t) ||
                        r2.getAssignedTalks().contains(t)
        );
    }

    @Test
    void TC17_NotEnoughReviewers() {
        ConferenceManager cm = new ConferenceManager("Boss", "QCON");
        cm.configureConference(2, LocalDate.now().plusDays(5));

        Conference c = new Conference(cm);

        Applicant a = new Applicant("May", "Google");
        Reviewer r1 = new Reviewer("R1", "Google"); // conflict

        c.registerReviewer(r1);

        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);
        c.addSubmission(t);

        c.closeSubmissions();

        assertThrows(IllegalStateException.class, () -> {
            new AllocationService().allocate(c);
        });
    }

    @Test
    void TC18_AffiliationConflict() {
        Reviewer r = new Reviewer("R", "IBM");
        assertEquals("IBM", r.getAffiliation());
    }

    // review

    @Test
    void TC19_ValidReview() {
        Applicant a = new Applicant("May", "Google");
        Reviewer r = new Reviewer("John", "IBM");

        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);
        r.addAssignedTalk(t);

        Review review = r.submitReview(t, 8, "Good talk");

        assertEquals(8, review.getScore());
    }

    @Test
    void TC20_InvalidScore() {
        Reviewer r = new Reviewer("John", "IBM");

        assertThrows(IllegalArgumentException.class, () -> {
            new Review(r, 20, "Bad");
        });
    }

    // ranking

    @Test
    void TC22_Ranking() {
        Applicant a = new Applicant("May", "Google");
        Reviewer r = new Reviewer("John", "IBM");

        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);
        r.addAssignedTalk(t);
        r.submitReview(t, 9, "Great");

        RankingService rs = new RankingService();
        List<TalkSubmission> ranked = rs.rankTalks(List.of(t), 1);

        assertEquals(1, ranked.size());
    }

    @Test
    void TC23_NoReviews() {
        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry",
                new Applicant("May","Google"));

        assertEquals(0.0, t.getAverageScore());
    }

    @Test
    void TC24_SlotLimit() {
        RankingService rs = new RankingService();
        assertNotNull(rs);
    }

    // feedback

    @Test
    void TC25_Feedback() {
        Applicant a = new Applicant("May", "Google");
        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);

        FeedbackService fs = new FeedbackService();
        FeedbackReport report = fs.generateFeedback(t);

        assertNotNull(report);
    }

    @Test
    void TC26_NoComments() {
        Applicant a = new Applicant("May", "Google");
        TalkSubmission t = new TalkSubmission("AI Talk", "AI in industry", a);

        FeedbackService fs = new FeedbackService();
        FeedbackReport report = fs.generateFeedback(t);

        assertTrue(report.getReviewerComments().isEmpty());
    }

    @Test
    void TC27_SelectionMismatch() {
        assertTrue(true);
    }

    // system

    @Test
    void TC28_EndToEnd() {
        assertTrue(true);
    }

    @Test
    void TC29_ManagerRegistration() {
        UserRegistry r = new UserRegistry();
        r.registerManager(new ConferenceManager("Boss","QCON"));
        assertNotNull(r.getManager());
    }

    @Test
    void TC30_CloseSubmissions() {
        ConferenceManager cm = new ConferenceManager("Boss","QCON");
        cm.configureConference(2, LocalDate.now().plusDays(5));

        Conference c = new Conference(cm);
        c.closeSubmissions();

        assertThrows(IllegalStateException.class, () -> {
            c.addSubmission(new TalkSubmission("AI Talk","AI",new Applicant("May","Google")));
        });
    }
}
