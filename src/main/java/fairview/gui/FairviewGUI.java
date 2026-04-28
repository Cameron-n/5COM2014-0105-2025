package fairview.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import fairview.system.*;
import fairview.talks.*;
import fairview.users.*;

import java.awt.*;
import java.util.List;

public class FairviewGUI extends JFrame {

    private final Conference conference;
    private final UserRegistry registry;

    private JTabbedPane tabs;

    // USERS PANEL
    private JTable usersTable;
    private DefaultTableModel usersModel;

    // SUBMISSIONS PANEL
    private JTable submissionsTable;
    private DefaultTableModel submissionsModel;

    // ALLOCATION PANEL
    private JTable allocationTable;
    private DefaultTableModel allocationModel;

    // REVIEWS PANEL
    private JTable reviewsTable;
    private DefaultTableModel reviewsModel;
    private JComboBox<Reviewer> reviewerDropdown;

    // RANKING PANEL
    private JTable rankingTable;
    private DefaultTableModel rankingModel;

    // FEEDBACK PANEL
    private JTable feedbackTable;
    private DefaultTableModel feedbackModel;
    private JComboBox<Applicant> applicantDropdown;

    public FairviewGUI(ConferenceManager manager) {
        this.registry = new UserRegistry();
        registry.registerManager(manager);

        this.conference = new Conference(manager);

        setTitle("F@irview Conference System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        tabs = new JTabbedPane();

        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Talk Submissions", createSubmissionsPanel());
        tabs.addTab("Allocation", createAllocationPanel());
        tabs.addTab("Reviews", createReviewsPanel());
        tabs.addTab("Ranking", createRankingPanel());
        tabs.addTab("Feedback Reports", createFeedbackPanel());

        add(tabs);
    }

    // ---------------------------------------------------------
    // USERS PANEL
    // ---------------------------------------------------------
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("User Registration");
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Name", "Affiliation", "Role"};
        usersModel = new DefaultTableModel(cols, 0);
        usersTable = new JTable(usersModel);
        FairviewTheme.styleTable(usersTable);

        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JButton addUserBtn = new JButton("Register User");
        FairviewTheme.styleButton(addUserBtn);
        addUserBtn.addActionListener(e -> showAddUserDialog());

        JPanel bottom = new JPanel();
        bottom.setBackground(FairviewTheme.BACKGROUND);
        bottom.add(addUserBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Register User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        FairviewTheme.styleCard(form);

        JTextField nameField = new JTextField();
        JTextField affiliationField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Applicant", "Reviewer"});

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Affiliation:"));
        form.add(affiliationField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        JButton save = new JButton("Save");
        FairviewTheme.styleButton(save);
        save.addActionListener(e -> {
            String name = nameField.getText();
            String aff = affiliationField.getText();
            String role = (String) roleBox.getSelectedItem();

            if (role.equals("Applicant")) {
                Applicant a = new Applicant(name, aff);
                registry.registerApplicant(a);
            } else {
                Reviewer r = new Reviewer(name, aff);
                registry.registerReviewer(r);
                conference.registerReviewer(r);
            }

            usersModel.addRow(new Object[]{name, aff, role});
            dialog.dispose();
        });

        form.add(save);
        dialog.add(form);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------
    // SUBMISSIONS PANEL
    // ---------------------------------------------------------
    private JPanel createSubmissionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("Talk Submissions");
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Title", "Applicant", "Description"};
        submissionsModel = new DefaultTableModel(cols, 0);
        submissionsTable = new JTable(submissionsModel);
        FairviewTheme.styleTable(submissionsTable);

        panel.add(new JScrollPane(submissionsTable), BorderLayout.CENTER);

        JButton submitBtn = new JButton("Submit Talk");
        FairviewTheme.styleButton(submitBtn);

        submitBtn.addActionListener(e -> {

            // VALIDATION 1: No applicants
            if (registry.getApplicants().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No applicants have been registered yet.\nPlease register at least one applicant first.",
                        "No Applicants Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            showSubmitTalkDialog();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(FairviewTheme.BACKGROUND);
        bottom.add(submitBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void showSubmitTalkDialog() {
        JDialog dialog = new JDialog(this, "Submit Talk", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        FairviewTheme.styleCard(form);

        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea();
        JComboBox<Applicant> applicantBox = new JComboBox<>(registry.getApplicants().toArray(new Applicant[0]));

        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Description (250 words):"));
        form.add(new JScrollPane(descArea));
        form.add(new JLabel("Applicant:"));
        form.add(applicantBox);

        JButton save = new JButton("Submit");
        FairviewTheme.styleButton(save);
        save.addActionListener(e -> {
            Applicant a = (Applicant) applicantBox.getSelectedItem();
            TalkSubmission talk = new TalkSubmission(titleField.getText(), descArea.getText(), a);

            conference.addSubmission(talk);
            a.addSubmission(talk);

            submissionsModel.addRow(new Object[]{talk.getTitle(), a.getName(), talk.getDescription()});
            dialog.dispose();
        });

        form.add(save);
        dialog.add(form);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------
    // ALLOCATION PANEL
    // ---------------------------------------------------------
    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("Review Allocation");
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Talk", "Reviewer 1", "Reviewer 2"};
        allocationModel = new DefaultTableModel(cols, 0);
        allocationTable = new JTable(allocationModel);
        FairviewTheme.styleTable(allocationTable);

        panel.add(new JScrollPane(allocationTable), BorderLayout.CENTER);

        JButton allocateBtn = new JButton("Allocate Reviews");
        FairviewTheme.styleButton(allocateBtn);

        allocateBtn.addActionListener(e -> {

            // VALIDATION 2: No submissions
            if (conference.getTalkSubmissions().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No talks have been submitted yet.\nPlease submit at least one talk before allocating reviews.",
                        "No Submissions Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            allocateReviews();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(FairviewTheme.BACKGROUND);
        bottom.add(allocateBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void allocateReviews() {
        AllocationService service = new AllocationService();
        service.allocate(conference);

        allocationModel.setRowCount(0);

        for (TalkSubmission t : conference.getTalkSubmissions()) {
            List<Reviewer> assigned = conference.getReviewers().stream()
                    .filter(r -> r.getAssignedTalks().contains(t))
                    .toList();

            allocationModel.addRow(new Object[]{
                    t.getTitle(),
                    assigned.get(0).getName(),
                    assigned.get(1).getName()
            });
        }
    }

    // ---------------------------------------------------------
    // REVIEWS PANEL
    // ---------------------------------------------------------
    private JPanel createReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("Submit Reviews");
        panel.add(title, BorderLayout.NORTH);

        reviewerDropdown = new JComboBox<>(registry.getReviewers().toArray(new Reviewer[0]));
        reviewerDropdown.addActionListener(e -> refreshReviewerTalks());
        panel.add(reviewerDropdown, BorderLayout.NORTH);

        String[] cols = {"Talk Title", "Description"};
        reviewsModel = new DefaultTableModel(cols, 0);
        reviewsTable = new JTable(reviewsModel);
        FairviewTheme.styleTable(reviewsTable);

        panel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);

        JButton reviewBtn = new JButton("Submit Review");
        FairviewTheme.styleButton(reviewBtn);

        reviewBtn.addActionListener(e -> {

            // VALIDATION 3: No reviewers
            if (registry.getReviewers().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No reviewers have been registered yet.\nPlease register at least one reviewer first.",
                        "No Reviewers Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            showReviewDialog();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(FairviewTheme.BACKGROUND);
        bottom.add(reviewBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshReviewerTalks() {
        reviewsModel.setRowCount(0);

        Reviewer r = (Reviewer) reviewerDropdown.getSelectedItem();
        if (r == null) return;

        for (TalkSubmission t : r.getAssignedTalks()) {
            reviewsModel.addRow(new Object[]{t.getTitle(), t.getDescription()});
        }
    }

    private void showReviewDialog() {
        Reviewer r = (Reviewer) reviewerDropdown.getSelectedItem();
        int row = reviewsTable.getSelectedRow();
        if (row == -1) return;

        TalkSubmission talk = r.getAssignedTalks().get(row);

        JDialog dialog = new JDialog(this, "Submit Review", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        FairviewTheme.styleCard(form);

        JTextField scoreField = new JTextField();
        JTextArea feedbackArea = new JTextArea();

        form.add(new JLabel("Score (1–10):"));
        form.add(scoreField);
        form.add(new JLabel("Feedback (250 chars):"));
        form.add(new JScrollPane(feedbackArea));

        JButton save = new JButton("Submit");
        FairviewTheme.styleButton(save);
        save.addActionListener(e -> {
            int score = Integer.parseInt(scoreField.getText());
            String feedback = feedbackArea.getText();

            r.submitReview(talk, score, feedback);
            dialog.dispose();
        });

        form.add(save);
        dialog.add(form);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------
    // RANKING PANEL
    // ---------------------------------------------------------
    private JPanel createRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("Ranking");
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Talk", "Average Score", "Selected"};
        rankingModel = new DefaultTableModel(cols, 0);
        rankingTable = new JTable(rankingModel);
        FairviewTheme.styleTable(rankingTable);

        panel.add(new JScrollPane(rankingTable), BorderLayout.CENTER);

        JButton rankBtn = new JButton("Generate Ranking");
        FairviewTheme.styleButton(rankBtn);

        rankBtn.addActionListener(e -> {

            // VALIDATION 4: No reviews
            boolean noReviews = conference.getTalkSubmissions().stream()
                    .allMatch(t -> t.getAverageScore() == 0);

            if (noReviews) {
                JOptionPane.showMessageDialog(
                        this,
                        "No reviews have been submitted yet.\nPlease ensure reviewers submit reviews before generating rankings.",
                        "No Reviews Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            generateRanking();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(FairviewTheme.BACKGROUND);
        bottom.add(rankBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void generateRanking() {
        RankingService service = new RankingService();
        List<TalkSubmission> ranked = service.rankTalks(
                conference.getTalkSubmissions(),
                conference.getManager().getNumberOfSlots()
        );

        rankingModel.setRowCount(0);

        for (TalkSubmission t : conference.getTalkSubmissions()) {
            boolean selected = ranked.contains(t);
            rankingModel.addRow(new Object[]{
                    t.getTitle(),
                    t.getAverageScore(),
                    selected ? "YES" : "NO"
            });
        }
    }

    // ---------------------------------------------------------
    // FEEDBACK PANEL
    // ---------------------------------------------------------
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        FairviewTheme.stylePanel(panel);

        JLabel title = FairviewTheme.title("Feedback Reports");
        panel.add(title, BorderLayout.NORTH);

        applicantDropdown = new JComboBox<>(registry.getApplicants().toArray(new Applicant[0]));
        panel.add(applicantDropdown, BorderLayout.NORTH);

        applicantDropdown.addActionListener(e -> refreshFeedbackTable());

        String[] cols = {"Talk", "Selected", "Reviewer Comments"};
        feedbackModel = new DefaultTableModel(cols, 0);
        feedbackTable = new JTable(feedbackModel);
        FairviewTheme.styleTable(feedbackTable);

        panel.add(new JScrollPane(feedbackTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshFeedbackTable() {
        feedbackModel.setRowCount(0);

        Applicant a = (Applicant) applicantDropdown.getSelectedItem();
        if (a == null) return;

        FeedbackService service = new FeedbackService();

        for (TalkSubmission t : a.getTalkSubmissions()) {
            boolean selected = t.getAverageScore() > 0;

            FeedbackReport report = service.generateFeedback(t);

            feedbackModel.addRow(new Object[]{
                    t.getTitle(),
                    selected ? "YES" : "NO",
                    String.join(" | ", report.getReviewerComments())
            });
        }
    }
}