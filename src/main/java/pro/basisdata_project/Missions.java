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

public class Missions {

    private int missionId;
    private String missionName;
    private String description;
    private String startDate;
    private String endDate;
    private String status;

    public Missions(int missionId, String missionName, String description, String startDate, String endDate, String status) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static VBox getMissionsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label missionIdLabel = new Label("Mission ID:");
        TextField missionIdText = new TextField();
        Label missionNameLabel = new Label("Mission Name:");
        TextField missionNameText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionText = new TextField();
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Submission", "Parliamentary Approval", "In Progress", "Completed");
        TableView<Missions> tableView = new TableView<>();

        TableColumn<Missions, Integer> missionIdCol = new TableColumn<>("Mission ID");
        missionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));
        TableColumn<Missions, String> missionNameCol = new TableColumn<>("Mission Name");
        missionNameCol.setCellValueFactory(new PropertyValueFactory<>("missionName"));
        TableColumn<Missions, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Missions, String> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        TableColumn<Missions, String> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        TableColumn<Missions, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(missionIdCol, missionNameCol, descriptionCol, startDateCol, endDateCol, statusCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Missions selectedMission = tableView.getSelectionModel().getSelectedItem();
            if (selectedMission != null) {
                missionIdText.setText(String.valueOf(selectedMission.getMissionId()));
                missionNameText.setText(selectedMission.getMissionName());
                descriptionText.setText(selectedMission.getDescription());
                statusLabel.setText(selectedMission.getStartDate());
                endDateLabel.setText(selectedMission.getEndDate());
                startDateLabel.setText(selectedMission.getStatus());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Mission Selected", "Please select a mission to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Missions selectedMission = tableView.getSelectionModel().getSelectedItem();
            if (selectedMission != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".MISSIONS WHERE MISSION_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, selectedMission.getMissionId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Mission Deleted", "Mission with ID " + selectedMission.getMissionId() + " has been deleted.");
                        tableView.getItems().remove(selectedMission);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete mission from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Mission Selected", "Please select a mission to delete.");
            }
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int missionId = Integer.parseInt(missionIdText.getText());
            String missionName = missionNameText.getText();
            String description = descriptionText.getText();
            String startDate = startDatePicker.getValue().toString(); // Get selected date as string
            String endDate = endDatePicker.getValue().toString(); // Get selected date as string
            String status = statusComboBox.getValue(); // Get selected status from ComboBox

            Missions mission = new Missions(missionId, missionName, description, startDate, endDate, status);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".MISSIONS (MISSION_ID, MISSION_NAME, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, missionId);
                pstmt.setString(2, missionName);
                pstmt.setString(3, description);
                pstmt.setString(4, startDate);
                pstmt.setString(5, endDate);
                pstmt.setString(6, status);
                pstmt.executeUpdate();
                System.out.println("Mission saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(mission);

            missionIdText.clear();
            missionNameText.clear();
            descriptionText.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            statusComboBox.setValue(null);
        });

        ObservableList<Missions> missionList = fetchMissionsFromDatabase();
        tableView.setItems(missionList);

        vbox.getChildren().addAll(
                missionIdLabel, missionIdText, missionNameLabel, missionNameText,
                descriptionLabel, descriptionText, startDateLabel, startDatePicker,
                endDateLabel, endDatePicker, statusLabel, statusComboBox,
                tableView, buttonBox, createButton);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static ObservableList<Missions> fetchMissionsFromDatabase() {
        ObservableList<Missions> missionList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT MISSION_ID, MISSION_NAME, DESCRIPTION, START_DATE, END_DATE, STATUS FROM \"C4ISR PROJECT (BASIC) V2\".MISSIONS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int missionId = rs.getInt("MISSION_ID");
                String missionName = rs.getString("MISSION_NAME");
                String description = rs.getString("DESCRIPTION");
                String startDate = rs.getString("START_DATE");
                String endDate = rs.getString("END_DATE");
                String status = rs.getString("STATUS");

                Missions mission = new Missions(missionId, missionName, description, startDate, endDate, status);
                missionList.add(mission);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return missionList;
    }
}
