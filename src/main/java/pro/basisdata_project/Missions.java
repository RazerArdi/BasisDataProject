package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            result.ifPresent(analysisIdText::setText);
        });

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

        TableView<Missions> tableView = new TableView<>();
        ObservableList<Missions> data = FXCollections.observableArrayList();

        TableColumn<Missions, Integer> missionIdCol = new TableColumn<>("Mission ID");
        missionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));
        TableColumn<Missions, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Missions, String> objectiveCol = new TableColumn<>("Objective");
        objectiveCol.setCellValueFactory(new PropertyValueFactory<>("objective"));
        TableColumn<Missions, String> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        TableColumn<Missions, String> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        tableView.getColumns().addAll(missionIdCol, nameCol, objectiveCol, startDateCol, endDateCol);
        tableView.setItems(data);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int missionId = Integer.parseInt(missionIdText.getText());
            String name = nameText.getText();
            String objective = objectiveText.getText();
            String startDate = startDateText.getText();
            String endDate = endDateText.getText();

            Missions mission = new Missions(missionId, name, objective, startDate, endDate);
            data.add(mission);

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Missions,%d,%s,%s,%s,%s%n", analysisId, missionId, name, objective, startDate, endDate));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteButton = new Button("Delete Selected Mission");
        deleteButton.setOnAction(e -> {
            Missions selectedMission = tableView.getSelectionModel().getSelectedItem();
            if (selectedMission != null) {
                data.remove(selectedMission);
                deleteFromDatabase(selectedMission);
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, missionIdLabel, missionIdText, nameLabel, nameText, objectiveLabel, objectiveText, startDateLabel, startDateText, endDateLabel, endDateText, createButton, deleteButton, tableView);

        return vbox;
    }

    private static void deleteFromDatabase(Missions mission) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Missions,%d,%s,%s,%s,%s",
                            mission.getMissionId(), mission.getName(), mission.getObjective(), mission.getStartDate(), mission.getEndDate())))
                    .collect(Collectors.toList());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt"))) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }
}
