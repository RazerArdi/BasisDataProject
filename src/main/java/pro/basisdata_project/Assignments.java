package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
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

        Label assignmentIdLabel = new Label("Assignment ID *:");
        TextField assignmentIdText = new TextField();
        Label roleLabel = new Label("Role *:");
        TextField roleText = new TextField();
        Label startDateLabel = new Label("Start Date *:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date *:");
        DatePicker endDatePicker = new DatePicker();
        Label personnelPersonnelIdLabel = new Label("Personnel ID *:");
        ComboBox<String> personnelPersonnelIdComboBox = new ComboBox<>();
        ObservableList<String> personnelIds = fetchPersonnelIdsFromDatabase();
        personnelPersonnelIdComboBox.setItems(personnelIds);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Generated");
        autoIncrementCheckBox.setSelected(true);
        autoIncrementCheckBox.setOnAction(e -> {
            if (autoIncrementCheckBox.isSelected()) {
                assignmentIdText.setDisable(true);
                assignmentIdText.clear();
            } else {
                assignmentIdText.setDisable(false);
            }
        });

        HBox assignmentIdBox = new HBox(5, assignmentIdText, autoIncrementCheckBox);

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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                String assignmentId;
                if (autoIncrementCheckBox.isSelected()) {
                    assignmentId = getNextAssignmentIdFromDatabase();
                } else {
                    assignmentId = assignmentIdText.getText().trim();
                    if (assignmentId.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Missing ID", "Assignment ID Required", "Please enter an assignment ID.");
                        return;
                    }
                }

                String role = roleText.getText().trim();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String personnelPersonnelId = personnelPersonnelIdComboBox.getValue();

                if (role.isEmpty() || startDate == null || endDate == null || personnelPersonnelId == null) {
                    showAlert(Alert.AlertType.ERROR, "Missing Fields", "Required Fields", "Please fill in all required fields.");
                    return;
                }

                Assignments assignment = new Assignments(assignmentId, role, startDate, endDate, personnelPersonnelId);
                saveAssignmentToDatabase(assignment);
                tableView.getItems().add(assignment);

                showAlert(Alert.AlertType.INFORMATION, "Assignment Created", "Success", "Assignment has been created successfully.");

                assignmentIdText.clear();
                roleText.clear();
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                personnelPersonnelIdComboBox.setValue(null);
                errorLabel.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Create Assignment", "An error occurred while creating the assignment.");
            }
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Assignments selectedAssignment = tableView.getSelectionModel().getSelectedItem();
            if (selectedAssignment != null) {
                try {
                    String assignmentId;
                    if (autoIncrementCheckBox.isSelected()) {
                        assignmentId = getNextAssignmentIdFromDatabase();
                    } else {
                        assignmentId = assignmentIdText.getText().trim();
                        if (assignmentId.isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "Missing ID", "Assignment ID Required", "Please enter an assignment ID.");
                            return;
                        }
                    }

                    String role = roleText.getText().trim();
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    String personnelPersonnelId = personnelPersonnelIdComboBox.getValue();

                    selectedAssignment.setAssignmentId(assignmentId);
                    selectedAssignment.setRole(role);
                    selectedAssignment.setStartDate(startDate);
                    selectedAssignment.setEndDate(endDate);
                    selectedAssignment.setPersonnelPersonnelId(personnelPersonnelId);

                    updateAssignmentInDatabase(selectedAssignment);

                    tableView.refresh();

                    assignmentIdText.clear();
                    roleText.clear();
                    startDatePicker.setValue(null);
                    endDatePicker.setValue(null);
                    personnelPersonnelIdComboBox.setValue(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Edit Assignment", "An error occurred while editing the assignment.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Assignment Selected", "Please select an assignment to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Assignments selectedAssignment = tableView.getSelectionModel().getSelectedItem();
            if (selectedAssignment != null) {
                deleteAssignmentFromDatabase(selectedAssignment);
                tableView.getItems().remove(selectedAssignment);

                showAlert(Alert.AlertType.INFORMATION, "Assignment Deleted", "Success", "Assignment has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Assignment Selected", "Please select an assignment to delete.");
            }
        });

        ObservableList<Assignments> assignmentList = fetchAssignmentsFromDatabase();
        tableView.setItems(assignmentList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);
        vbox.getChildren().addAll(
                assignmentIdLabel, assignmentIdBox,
                roleLabel, roleText,
                startDateLabel, startDatePicker,
                endDateLabel, endDatePicker,
                personnelPersonnelIdLabel, personnelPersonnelIdComboBox,
                errorLabel, tableView, buttonBox
        );

        return vbox;
    }

    private static ObservableList<String> fetchPersonnelIdsFromDatabase() {
        ObservableList<String> personnelIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                personnelIds.add(rs.getString("PERSONNEL_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch personnel IDs from database.");
        }
        return personnelIds;
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
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch assignments from database.");
        }
        return assignmentList;
    }

    private static void saveAssignmentToDatabase(Assignments assignment) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS (ASSIGNMENT_ID, ROLE, START_DATE, END_DATE, PERSONNEL_PERSONNEL_ID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, assignment.getAssignmentId());
            pstmt.setString(2, assignment.getRole());
            pstmt.setDate(3, java.sql.Date.valueOf(assignment.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(assignment.getEndDate()));
            pstmt.setString(5, assignment.getPersonnelPersonnelId());
            pstmt.executeUpdate();
            System.out.println("Assignment saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Save Operation Failed", "Failed to save assignment to database.");
        }
    }

    private static void updateAssignmentInDatabase(Assignments assignment) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS SET ROLE = ?, START_DATE = ?, END_DATE = ?, PERSONNEL_PERSONNEL_ID = ? WHERE ASSIGNMENT_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, assignment.getRole());
            pstmt.setDate(2, java.sql.Date.valueOf(assignment.getStartDate()));
            pstmt.setDate(3, java.sql.Date.valueOf(assignment.getEndDate()));
            pstmt.setString(4, assignment.getPersonnelPersonnelId());
            pstmt.setString(5, assignment.getAssignmentId());
            pstmt.executeUpdate();
            System.out.println("Assignment updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Error", "Update Operation Failed", "Failed to update assignment in database.");
        }
    }

    private static void deleteAssignmentFromDatabase(Assignments assignment) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS WHERE ASSIGNMENT_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, assignment.getAssignmentId());
            pstmt.executeUpdate();
            System.out.println("Assignment deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Delete Error", "Delete Operation Failed", "Failed to delete assignment from database.");
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static String getNextAssignmentIdFromDatabase() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "A" + rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ID Generation Error", "Failed to Generate ID", "Failed to generate next assignment ID.");
        }
        return null;
    }
}
