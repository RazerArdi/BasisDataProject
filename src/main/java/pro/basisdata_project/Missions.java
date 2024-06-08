package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Missions {

    private int missionId;
    private String name;
    private String objective;
    private String startDate;
    private String endDate;

    public Missions(int missionId, String name, String objective, String startDate, String endDate) {
        this.missionId = missionId;
        this.name = name;
        this.objective = objective;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
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

    public static VBox getMissionsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label missionIdLabel = new Label("Mission ID:");
        TextField missionIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label objectiveLabel = new Label("Objective:");
        TextField objectiveText = new TextField();
        Label startDateLabel = new Label("Start Date:");
        TextField startDateText = new TextField();
        Label endDateLabel = new Label("End Date:");
        TextField endDateText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                int missionId = Integer.parseInt(missionIdText.getText());
                String name = nameText.getText();
                String objective = objectiveText.getText();
                String startDate = startDateText.getText();
                String endDate = endDateText.getText();

                Missions mission = new Missions(missionId, name, objective, startDate, endDate);
                System.out.println("Mission Created: " + mission.getMissionId());

                // Save to Database.txt
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                    writer.write(String.format("Missions,%d,%s,%s,%s,%s%n", missionId, name, objective, startDate, endDate));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid input: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(missionIdLabel, missionIdText, nameLabel, nameText, objectiveLabel, objectiveText, startDateLabel, startDateText, endDateLabel, endDateText, createButton);

        return vbox;
    }
}
