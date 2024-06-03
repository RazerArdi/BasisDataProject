package pro.basisdata_project;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class Missions {

    private String missionId;
    private String missionName;
    private String missionType;
    private String startDate;
    private String endDate;
    private String status;

    public Missions(String missionId, String missionName, String missionType, String startDate, String endDate, String status) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.missionType = missionType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
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
        Label missionTypeLabel = new Label("Mission Type:");
        TextField missionTypeText = new TextField();
        Label startDateLabel = new Label("Start Date:");
        TextField startDateText = new TextField();
        Label endDateLabel = new Label("End Date:");
        TextField endDateText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String missionId = missionIdText.getText();
            String missionName = missionNameText.getText();
            String missionType = missionTypeText.getText();
            String startDate = startDateText.getText();
            String endDate = endDateText.getText();
            String status = statusText.getText();

            Missions mission = new Missions(missionId, missionName, missionType, startDate, endDate, status);
            System.out.println("Mission Created: " + mission.getMissionId());
        });

        vbox.getChildren().addAll(missionIdLabel, missionIdText, missionNameLabel, missionNameText, missionTypeLabel, missionTypeText, startDateLabel, startDateText, endDateLabel, endDateText, statusLabel, statusText, createButton);

        return vbox;
    }
}

