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
        TextField startDateText = new TextField();
        Label endDateLabel = new Label("End Date:");
        TextField endDateText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();

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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int missionId = Integer.parseInt(missionIdText.getText());
            String missionName = missionNameText.getText();
            String description = descriptionText.getText();
            String startDate = startDateText.getText();
            String endDate = endDateText.getText();
            String status = statusText.getText();

            Missions mission = new Missions(missionId, missionName, description, startDate, endDate, status);
            System.out.println("Mission Created: " + mission.getMissionId());

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC)\".MISSIONS (MISSION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
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

            // Add new mission to the table view
            tableView.getItems().add(mission);

            // Clear input fields after adding mission
            missionIdText.clear();
            missionNameText.clear();
            descriptionText.clear();
            startDateText.clear();
            endDateText.clear();
            statusText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<Missions> missionList = fetchMissionsFromDatabase();
        tableView.setItems(missionList);

        vbox.getChildren().addAll(
                missionIdLabel, missionIdText, missionNameLabel, missionNameText,
                descriptionLabel, descriptionText, startDateLabel, startDateText,
                endDateLabel, endDateText, statusLabel, statusText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<Missions> fetchMissionsFromDatabase() {
        ObservableList<Missions> missionList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT MISSION_ID, NAME, DESCRIPTION, START_DATE, END_DATE, STATUS FROM \"C4ISR PROJECT (BASIC)\".MISSIONS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int missionId = rs.getInt("MISSION_ID");
                String missionName = rs.getString("NAME");
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
