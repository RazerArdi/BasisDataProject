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

        Label missionIdLabel = new Label("Mission ID *:");
        TextField missionIdText = new TextField();
        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");

        autoGenerateIdCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                missionIdText.setEditable(false);
                missionIdText.setText("Auto Generated");
            } else {
                missionIdText.setEditable(true);
                missionIdText.clear();
            }
        });

        Label missionNameLabel = new Label("Mission Name *:");
        TextField missionNameText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionText = new TextArea();
        descriptionText.setWrapText(true);
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();
        Label statusLabel = new Label("Status *:");
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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int missionId;
            if (autoGenerateIdCheckBox.isSelected()) {
                missionId = -1; // Set to -1 or any negative value to indicate auto generation
            } else {
                try {
                    missionId = Integer.parseInt(missionIdText.getText());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }
            }
            String missionName = missionNameText.getText();
            String description = descriptionText.getText();
            String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : null;
            String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : null;
            String status = statusComboBox.getValue();

            if (missionName.isEmpty() || status == null) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            Missions mission = new Missions(missionId, missionName, description, startDate, endDate, status);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".MISSIONS (MISSION_ID, MISSION_NAME, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                if (autoGenerateIdCheckBox.isSelected()) {
                    pstmt.setNull(1, java.sql.Types.INTEGER); // Set to NULL for auto increment in database
                } else {
                    pstmt.setInt(1, missionId);
                }
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
            autoGenerateIdCheckBox.setSelected(false); // Reset checkbox after creation
            errorLabel.setText("");
        });

        ObservableList<Missions> missionList = fetchMissionsFromDatabase();
        tableView.setItems(missionList);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(createButton);

        vbox.getChildren().addAll(
                missionIdLabel, missionIdText, autoGenerateIdCheckBox, missionNameLabel, missionNameText,
                descriptionLabel, descriptionText, startDateLabel, startDatePicker,
                endDateLabel, endDatePicker, statusLabel, statusComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
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
