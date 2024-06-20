package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Assignments {

    private String assignmentId;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private String personnelPersonnelId;

    public Assignments(String assignmentId, String role, LocalDate startDate, LocalDate endDate, String personnelPersonnelId) {
        this.assignmentId = assignmentId;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.personnelPersonnelId = personnelPersonnelId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPersonnelPersonnelId() {
        return personnelPersonnelId;
    }

    public void setPersonnelPersonnelId(String personnelPersonnelId) {
        this.personnelPersonnelId = personnelPersonnelId;
    }

    public static VBox getAssignmentsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label assignmentIdLabel = new Label("Assignment ID:");
        TextField assignmentIdText = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleText = new TextField();
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();
        Label personnelPersonnelIdLabel = new Label("Personnel ID:");
        TextField personnelPersonnelIdText = new TextField();

        TableView<Assignments> tableView = new TableView<>();
        TableColumn<Assignments, String> assignmentIdCol = new TableColumn<>("Assignment ID");
        assignmentIdCol.setCellValueFactory(new PropertyValueFactory<>("assignmentId"));
        TableColumn<Assignments, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<Assignments, LocalDate> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        TableColumn<Assignments, LocalDate> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        TableColumn<Assignments, String> personnelPersonnelIdCol = new TableColumn<>("Personnel ID");
        personnelPersonnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelPersonnelId"));

        tableView.getColumns().addAll(assignmentIdCol, roleCol, startDateCol, endDateCol, personnelPersonnelIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String assignmentId = assignmentIdText.getText();
            String role = roleText.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String personnelPersonnelId = personnelPersonnelIdText.getText();

            Assignments assignment = new Assignments(assignmentId, role, startDate, endDate, personnelPersonnelId);
            System.out.println("Assignment Created: " + assignment.getAssignmentId());

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS (ASSIGNMENT_ID, ROLE, START_DATE, END_DATE, PERSONNEL_PERSONNEL_ID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, assignmentId);
                pstmt.setString(2, role);
                pstmt.setDate(3, java.sql.Date.valueOf(startDate));
                pstmt.setDate(4, java.sql.Date.valueOf(endDate));
                pstmt.setString(5, personnelPersonnelId);
                pstmt.executeUpdate();
                System.out.println("Assignment saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Add new assignment to the table view
            tableView.getItems().add(assignment);

            // Clear input fields after adding assignment
            assignmentIdText.clear();
            roleText.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            personnelPersonnelIdText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<Assignments> assignmentList = fetchAssignmentsFromDatabase();
        tableView.setItems(assignmentList);

        vbox.getChildren().addAll(
                assignmentIdLabel, assignmentIdText, roleLabel, roleText,
                startDateLabel, startDatePicker, endDateLabel, endDatePicker,
                personnelPersonnelIdLabel, personnelPersonnelIdText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<Assignments> fetchAssignmentsFromDatabase() {
        ObservableList<Assignments> assignmentList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT ASSIGNMENT_ID, ROLE, START_DATE, END_DATE, PERSONNEL_PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String assignmentId = rs.getString("ASSIGNMENT_ID");
                String role = rs.getString("ROLE");
                LocalDate startDate = rs.getDate("START_DATE").toLocalDate();
                LocalDate endDate = rs.getDate("END_DATE").toLocalDate();
                String personnelPersonnelId = rs.getString("PERSONNEL_PERSONNEL_ID");

                Assignments assignment = new Assignments(assignmentId, role, startDate, endDate, personnelPersonnelId);
                assignmentList.add(assignment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return assignmentList;
    }
}
