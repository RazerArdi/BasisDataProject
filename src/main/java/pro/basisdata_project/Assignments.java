package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
        ComboBox<String> personnelPersonnelIdComboBox = new ComboBox<>();
        ObservableList<String> personnelIds = fetchPersonnelIdsFromDatabaseAssignments();
        personnelPersonnelIdComboBox.setItems(personnelIds);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);
        HBox assignmentIdBox = new HBox();
        assignmentIdBox.getChildren().addAll(
                assignmentIdText,
                new Label("Auto Generated:"),
                createAutoGenerateCheckBox(assignmentIdText) // Membuat CheckBox untuk auto-generate
        );
        assignmentIdBox.setSpacing(5);

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
            String personnelPersonnelId = personnelPersonnelIdComboBox.getValue();

            if (!isAutoGenerateChecked(assignmentIdText)) {
                assignmentId = assignmentIdText.getText();
            } else {
                // Mode auto-generate, tandai sebagai "AUTO_GENERATED"
                assignmentId = "AUTO_GENERATED";
            }

            Assignments assignment = new Assignments(assignmentId, role, startDate, endDate, personnelPersonnelId);

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

            tableView.getItems().add(assignment);

            assignmentIdText.clear();
            roleText.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            personnelPersonnelIdComboBox.setValue(null);
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Assignments selectedAssignment = tableView.getSelectionModel().getSelectedItem();
            if (selectedAssignment != null) {
                String assignmentId = assignmentIdText.getText();
                String role = roleText.getText();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String personnelPersonnelId = personnelPersonnelIdComboBox.getValue();

                selectedAssignment.setAssignmentId(assignmentId);
                selectedAssignment.setRole(role);
                selectedAssignment.setStartDate(startDate);
                selectedAssignment.setEndDate(endDate);
                selectedAssignment.setPersonnelPersonnelId(personnelPersonnelId);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS SET ROLE = ?, START_DATE = ?, END_DATE = ?, PERSONNEL_PERSONNEL_ID = ? WHERE ASSIGNMENT_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, role);
                    pstmt.setDate(2, java.sql.Date.valueOf(startDate));
                    pstmt.setDate(3, java.sql.Date.valueOf(endDate));
                    pstmt.setString(4, personnelPersonnelId);
                    pstmt.setString(5, assignmentId);
                    pstmt.executeUpdate();
                    System.out.println("Assignment updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                assignmentIdText.clear();
                roleText.clear();
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                personnelPersonnelIdComboBox.setValue(null);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Assignments selectedAssignment = tableView.getSelectionModel().getSelectedItem();
            if (selectedAssignment != null) {
                String assignmentId = selectedAssignment.getAssignmentId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS WHERE ASSIGNMENT_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, assignmentId);
                    pstmt.executeUpdate();
                    System.out.println("Assignment deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedAssignment);
            }
        });

        ObservableList<Assignments> assignmentList = fetchAssignmentsFromDatabase();
        tableView.setItems(assignmentList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);
        vbox.getChildren().addAll(
                assignmentIdLabel, assignmentIdBox, roleLabel, roleText,
                startDateLabel, startDatePicker, endDateLabel, endDatePicker,
                personnelPersonnelIdLabel, personnelPersonnelIdComboBox,
                tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField assignmentIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Centang, nonaktifkan input manual
                assignmentIdText.setDisable(true);
                assignmentIdText.clear(); // Bersihkan nilai yang mungkin sudah diisi
            } else {
                // Tidak tercentang, aktifkan kembali input manual
                assignmentIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField assignmentIdText) {
        CheckBox checkBox = (CheckBox) assignmentIdText.getParent().getChildrenUnmodifiable().get(2); // Menyesuaikan indeks CheckBox
        return checkBox.isSelected();
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

    private static ObservableList<String> fetchPersonnelIdsFromDatabaseAssignments() {
        ObservableList<String> personnelIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                personnelIds.add(personnelId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return personnelIds;
    }
}
