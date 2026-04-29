package fairview.gui;

import fairview.database.FairviewData;
import fairview.system.*;
import fairview.users.*;
import fairview.talks.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class FairviewGUI extends JFrame {

    private final Conference conference;
    private final UserRegistry registry;

    private JTabbedPane tabs;

    // LOGIN PANEL
    private JTable loginTable;
    private DefaultTableModel loginModel;

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

        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Users", createUsersPanel());

        add(tabs);
    }
    
    private void loadUserData(boolean all, String username) {
        String sql = "SELECT name, affiliation, userType FROM User";
        if (!all) {
            sql = sql + " WHERE name=" + "'" + username + "'";
        }
        List<String> columns = Arrays.asList("name", "affiliation", "userType");
        var userlist = FairviewData.getData(sql, columns);
        for (int i=0; i < userlist.size(); i++) {
            if (userlist.get(i).get(2).equals("Applicant")) {
                registry.setApplicant(userlist.get(i).get(0), userlist.get(i).get(1));
            } else if (userlist.get(i).get(2).equals("Reviewer")) {
                Reviewer r = registry.setReviewer(userlist.get(i).get(0), userlist.get(i).get(1));
                conference.registerReviewer(r);
            }
            usersModel.addRow(new Object[]{userlist.get(i).get(0), userlist.get(i).get(1), userlist.get(i).get(2)});
        }
    }
    
    private void loadTalkData(boolean all, String username) {
        String sql = "SELECT title, applicantID, description FROM Talk";
        if (!all) {
            String sql_name = "SELECT userID FROM User"
                              + " WHERE name=" + "'" + username + "'";
            List<String> sql_cols = Arrays.asList("userID");
            String applicantid = FairviewData.getData(sql_name, sql_cols).get(0).get(0);
            sql = sql + " WHERE applicantID=" + applicantid;
        }
        List<String> columns = Arrays.asList("title", "applicantID", "description");
        var talklist = FairviewData.getData(sql, columns);
        for (int i=0; i < talklist.size(); i++) {
            final int index = i;
            String sql_name = "SELECT name FROM User"
                              + " WHERE userID="
                              + "'" + talklist.get(i).get(1) + "';";
            List<String> name_col = Arrays.asList("name");
            String applicantName = FairviewData.getData(sql_name, name_col).get(0).get(0);
            registry.getApplicants().stream().filter(o -> o.getName().equals(applicantName)).forEach(
                o -> {
                    o.setSubmission(talklist.get(index).get(0), talklist.get(index).get(2));
                    List<TalkSubmission> talks = o.getTalkSubmissions();
                    conference.addSubmission(talks.getLast());
                }
            );
            submissionsModel.addRow(new Object[]{talklist.get(i).get(0), talklist.get(i).get(1), talklist.get(i).get(2)});
        }
    }
    
    private void loadReviewData(boolean all, String name) {
        String sql = "SELECT score, feedback, talkID, reviewerID FROM Review;";
        List<String> columns = Arrays.asList("score", "feedback", "talkID", "reviewerID");
        var reviewlist = FairviewData.getData(sql, columns);
        if (reviewlist.size() > 0) {
            conference.closeSubmissions();
            if (name == "Applicant") {
                return;
            } else if (name == "Manager") {
                AllocationService service = new AllocationService();
                service.allocate(conference);
                
                String sql_alloc = "SELECT talkID, title FROM Talk";
                List<String> col_alloc = Arrays.asList("talkID", "title");
                var alloclist = FairviewData.getData(sql_alloc, col_alloc);
                for (int i=0; i < alloclist.size(); i++) {
                    String sql_talk = "SELECT name FROM User JOIN Review ON userID=reviewerID"
                                    + " WHERE talkID=" + "'" + alloclist.get(i).get(0) + "'";                
                    List<String> col_talk = Arrays.asList("name");
                    var reviewerlist = FairviewData.getData(sql_talk, col_talk);
                    allocationModel.addRow(new Object[]{
                        alloclist.get(i).get(1),
                        reviewerlist.get(0).get(0),
                        reviewerlist.get(1).get(0)
                    });
                }
            } else {
                String sql_talks = "SELECT title, description, applicantID, score, feedback FROM Talk"
                                   + " JOIN Review ON Talk.talkID = Review.talkID"
                                   + " JOIN User on reviewerID = userID"
                                   + " WHERE name=" + "'" + name + "'";
                List<String> cols_talks = Arrays.asList("title", "description", "applicantID", "score", "feedback");
                ArrayList<ArrayList<String>> talkslist = FairviewData.getData(sql_talks, cols_talks);
                for (int i=0; i < talkslist.size(); i++) {
                    String sql_app = "SELECT name, affiliation FROM User"
                                     + " WHERE userID=" + "'" + talkslist.get(i).get(2) + "'";
                    List<String> cols_app = Arrays.asList("name", "affiliation");
                    ArrayList<String> app = FairviewData.getData(sql_app, cols_app).get(0);
                    registry.setApplicant(app.get(0), app.get(1));
                    Applicant a = registry.getApplicants().getLast();
                    a.setSubmission(talkslist.get(i).get(0), talkslist.get(i).get(1));
                    TalkSubmission t = a.getTalkSubmissions().getLast();
                    Review r = new Review(registry.getReviewers().getFirst(), Integer.parseInt(talkslist.get(i).get(3)), talkslist.get(i).get(4));
                    t.addReview(r);
                    registry.getReviewers().getFirst().addAssignedTalk(t);
                }
            }
        }
    }
    
    // ---------------------------------------------------------
    // LOGIN PANEL
    // ---------------------------------------------------------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Name", "Password"};
        loginModel = new DefaultTableModel(cols, 0);

        JButton addUserBtn = new JButton("Login");
        addUserBtn.addActionListener(e -> showLoginDialog());

        JPanel bottom = new JPanel();
        bottom.add(addUserBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void showLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField passwordField = new JTextField();

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton save = new JButton("Login");
        save.addActionListener(e -> {
            String name = nameField.getText();
            String pass = passwordField.getText();

            String sql = "SELECT password, userType FROM User"
                         + " WHERE name="
                         + "'" + name + "'";
            List<String> cols = Arrays.asList("password", "userType");
            
            String role;
            String password = " ";
            try {
                ArrayList<ArrayList<String>> data = FairviewData.getData(sql, cols);
                role = data.get(0).get(1);
                password = data.get(0).get(0);
            } catch(Exception err) {
                role = "";
            }
            
            if (pass.equals(password)) {
                if (role.equals("Manager")) {
                    tabs.removeAll();

                    tabs.addTab("Users", createUsersPanel());
                    loadUserData(true, name);

                    tabs.addTab("Talk Submissions", createSubmissionsPanel());
                    loadTalkData(true, name);

                    tabs.addTab("Allocation", createAllocationPanel());
                    tabs.addTab("Reviews", createReviewsPanel());
                    loadReviewData(true, "Manager");
                    
                    tabs.addTab("Ranking", createRankingPanel());
                    tabs.addTab("Feedback Reports", createFeedbackPanel());
                } else if (role.equals("Applicant")) {
                    tabs.removeAll();

                    loadUserData(false, name);

                    tabs.addTab("Talk Submissions", createSubmissionsPanel());
                    loadTalkData(false, name);

                    loadReviewData(false, "Applicant");

                    tabs.addTab("Feedback Reports", createFeedbackPanel());
                } else if (role.equals("Reviewer")) {
                    tabs.removeAll();
                    loadUserData(false, name);

                    tabs.addTab("Reviews", createReviewsPanel());
                    loadReviewData(false, name);

                    tabs.addTab("Feedback Reports", createFeedbackPanel());
                }
            }

            dialog.dispose();
        });

        form.add(save);
        dialog.add(form);
        dialog.setVisible(true);
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

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        FairviewTheme.styleCard(form);

        JTextField nameField = new JTextField();
        JTextField loginField = new JTextField();
        JTextField affiliationField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Applicant", "Reviewer"});

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Password:"));
        form.add(loginField);
        form.add(new JLabel("Affiliation:"));
        form.add(affiliationField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        JButton save = new JButton("Save");
        FairviewTheme.styleButton(save);
        save.addActionListener(e -> {
            String name = nameField.getText();
            String pass = loginField.getText();
            String aff = affiliationField.getText();
            String role = (String) roleBox.getSelectedItem();

            var success = true;
            if (role.equals("Applicant")) {
                Applicant a = new Applicant(name, aff);
                success = registry.registerApplicant(a, pass);
            } else if (role.equals("Reviewer")) {
                Reviewer r = new Reviewer(name, aff);
                success = registry.registerReviewer(r, pass);
                if (success) {
                    conference.registerReviewer(r);
                }
            }

            if (success) {
                usersModel.addRow(new Object[]{name, aff, role});
            }
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
            boolean success = a.addSubmission(talk);
            if (success) {
                submissionsModel.addRow(new Object[]{talk.getTitle(), a.getName(), talk.getDescription()});
            }

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