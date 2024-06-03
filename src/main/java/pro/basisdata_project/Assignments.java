package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class Assignments {

    private String assignmentId;
    private String role;
    private String startDate;
    private String endDate;
    private String personnelId;

    public Assignments(String assignmentId, String role, String startDate, String endDate, String personnelId) {
        this.assignmentId = assignmentId;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.personnelId = personnelId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
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
        TextField startDateText = new TextField();
        Label endDateLabel = new Label("End Date:");
        TextField endDateText = new TextField();
        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String assignmentId = assignmentIdText.getText();
            String role = roleText.getText();
            String startDate = startDateText.getText();
            String endDate = endDateText.getText();
            String personnelId = personnelIdText.getText();

            Assignments assignment = new Assignments(assignmentId, role, startDate, endDate, personnelId);
            System.out.println("Assignment Created: " + assignment.getAssignmentId());
        });

        vbox.getChildren().addAll(assignmentIdLabel, assignmentIdText, roleLabel, roleText, startDateLabel, startDateText, endDateLabel, endDateText, personnelIdLabel, personnelIdText, createButton);

        return vbox;
    }
}
