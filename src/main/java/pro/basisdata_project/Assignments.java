package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s%n", assignmentId, role, startDate, endDate, personnelPersonnelId));
            } catch (IOException ex) {
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

        vbox.getChildren().addAll(
                assignmentIdLabel, assignmentIdText, roleLabel, roleText,
                startDateLabel, startDatePicker, endDateLabel, endDatePicker,
                personnelPersonnelIdLabel, personnelPersonnelIdText,
                tableView, createButton);

        return vbox;
    }
}