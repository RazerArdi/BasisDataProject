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
import java.time.format.DateTimeFormatter;

public class Missions {

    private Integer missionId;
    private String missionName;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private static DateTimeFormatter formatter;

    public Missions(Integer missionId, String missionName, String description, String startDate, String endDate, String status) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
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

        Label missionIdLabel = new Label("Mission ID *:");
        TextField missionIdText = new TextField();
        Label missionNameLabel = new Label("Mission Name *:");
        TextField missionNameText = new TextField();
        Label descriptionLabel = new Label("Description *:");
        TextField descriptionText = new TextField();
        Label startDateLabel = new Label("Start Date *:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date *:");
        DatePicker endDatePicker = new DatePicker();
        Label statusLabel = new Label("Status *:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Planning", "In Progress", "Completed", "Cancelled");
        statusComboBox.setValue("Planning");

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

        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);

        HBox missionIdBox = new HBox();
        missionIdBox.getChildren().addAll(missionIdText, new Label("*"), autoGenerateIdCheckBox);
        missionIdBox.setSpacing(10);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                missionIdText.setDisable(true);
                missionIdText.clear();
            } else {
                missionIdText.setDisable(false);
            }
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            Integer missionId = autoGenerateIdCheckBox.isSelected() ? generateMissionId() : Integer.parseInt(missionIdText.getText());
            String missionName = missionNameText.getText();
            String description = descriptionText.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String status = statusComboBox.getValue();

            if (missionName.isEmpty() || description.isEmpty() || startDate == null || endDate == null || status.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Required Fields", "Fields marked with * are required!");
                return;
            }

            Missions mission = new Missions(missionId, missionName, description, startDate.toString(), endDate.toString(), status);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".MISSIONS (MISSION_ID, MISSION_NAME, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, missionId);
                pstmt.setString(2, missionName);
                pstmt.setString(3, description);
                pstmt.setDate(4, java.sql.Date.valueOf(startDate));
                pstmt.setDate(5, java.sql.Date.valueOf(endDate));
                pstmt.setString(6, status);
                pstmt.executeUpdate();
                System.out.println("Mission saved to database.");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Mission Created", "Mission has been created successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error Occurred", "Failed to save mission to database.");
            }

            tableView.getItems().add(mission);

            missionIdText.clear();
            missionNameText.clear();
            descriptionText.clear();
            startDatePicker.getEditor().clear();
            endDatePicker.getEditor().clear();
            statusComboBox.setValue("Planning");
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Missions selectedMission = tableView.getSelectionModel().getSelectedItem();
            if (selectedMission != null) {
                Integer missionId = selectedMission.getMissionId();
                String missionName = missionNameText.getText();
                String description = descriptionText.getText();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String status = statusComboBox.getValue();

                if (missionName.isEmpty() || description.isEmpty() || startDate == null || endDate == null || status.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Required Fields", "Fields marked with * are required!");
                    return;
                }

                selectedMission.setMissionName(missionName);
                selectedMission.setDescription(description);
                selectedMission.setStartDate(startDate.toString());
                selectedMission.setEndDate(endDate.toString());
                selectedMission.setStatus(status);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".MISSIONS " +
                            "SET MISSION_NAME = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, STATUS = ? " +
                            "WHERE MISSION_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, missionName);
                    pstmt.setString(2, description);
                    pstmt.setDate(3, java.sql.Date.valueOf(startDate));
                    pstmt.setDate(4, java.sql.Date.valueOf(endDate));
                    pstmt.setString(5, status);
                    pstmt.setInt(6, missionId);
                    pstmt.executeUpdate();
                    System.out.println("Mission updated in database.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Mission Updated", "Mission has been updated successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Error Occurred", "Failed to update mission in database.");
                }

                tableView.refresh();

                missionIdText.clear();
                missionNameText.clear();
                descriptionText.clear();
                startDatePicker.getEditor().clear();
                endDatePicker.getEditor().clear();
                statusComboBox.setValue("Planning");
                errorLabel.setText("");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Missions selectedMission = tableView.getSelectionModel().getSelectedItem();
            if (selectedMission != null) {
                Integer missionId = selectedMission.getMissionId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".MISSIONS WHERE MISSION_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, missionId);
                    pstmt.executeUpdate();
                    System.out.println("Mission deleted from database.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Mission Deleted", "Mission has been deleted successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Error Occurred", "Failed to delete mission from database.");
                }

                tableView.getItems().remove(selectedMission);
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(createButton, editButton, deleteButton);

        vbox.getChildren().addAll(missionIdLabel, missionIdBox, missionNameLabel, missionNameText, descriptionLabel, descriptionText, startDateLabel, startDatePicker, endDateLabel, endDatePicker, statusLabel, statusComboBox, buttonBox, errorLabel, tableView);

        tableView.setItems(getMissions());

        return vbox;
    }

    private static Integer generateMissionId() {
        Integer nextId = null;

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".missions_seq.NEXTVAL AS NEXT_ID FROM dual";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                nextId = rs.getInt("NEXT_ID");
            } else {
                nextId = 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return nextId;
    }

    private static void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static ObservableList<Missions> getMissions() {
        ObservableList<Missions> missions = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT * FROM \"C4ISR PROJECT (BASIC) V2\".MISSIONS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer missionId = rs.getInt("MISSION_ID");
                String missionName = rs.getString("MISSION_NAME");
                String description = rs.getString("DESCRIPTION");
                String startDate = rs.getString("START_DATE");
                String endDate = rs.getString("END_DATE");
                String status = rs.getString("STATUS");
                missions.add(new Missions(missionId, missionName, description, startDate, endDate, status));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return missions;
    }
}
