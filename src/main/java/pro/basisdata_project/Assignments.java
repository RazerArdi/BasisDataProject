package pro.basisdata_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Assignments extends Application {
    private static TableView<Assignments> tableView = new TableView<>();
    private int assignmentId;
    private String task;
    private String description;
    private int personnelId;
    private int missionId;

    public Assignments(int assignmentId, String task, String description, int personnelId, int missionId) {
        this.assignmentId = assignmentId;
        this.task = task;
        this.description = description;
        this.personnelId = personnelId;
        this.missionId = missionId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public String getTask() {
        return task;
    }

    public String getDescription() {
        return description;
    }

    public int getPersonnelId() {
        return personnelId;
    }

    public int getMissionId() {
        return missionId;
    }

    public static VBox getAssignmentsUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<Assignments> tableView = new TableView<>();
        ObservableList<Assignments> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Assignments, Integer> assignmentIdCol = new TableColumn<>("Assignment ID");
        assignmentIdCol.setCellValueFactory(new PropertyValueFactory<>("assignmentId"));

        TableColumn<Assignments, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));

        TableColumn<Assignments, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Assignments, Integer> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));

        TableColumn<Assignments, Integer> missionIdCol = new TableColumn<>("Mission ID");
        missionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));

        tableView.getColumns().addAll(assignmentIdCol, taskCol, descriptionCol, personnelIdCol, missionIdCol);
        tableView.setItems(data);

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            result.ifPresent(analysisIdText::setText);
        });

        Button refreshButton = new Button("Refresh Data");
        refreshButton.setOnAction(e -> {
            tableView.getItems().clear(); // Clear existing data
            loadData(); // Load data from Database.txt
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Data Refreshed");
            alert.setHeaderText(null);
            alert.setContentText("Data has been refreshed from the database.");
            alert.showAndWait();
        });

        Label assignmentIdLabel = new Label("Assignment ID:");
        TextField assignmentIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionText = new TextArea();
        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        Label missionIdLabel = new Label("Mission ID:");
        TextField missionIdText = new TextField();

        Button insertButton = new Button("Insert Data");
        insertButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int assignmentId = Integer.parseInt(assignmentIdText.getText());
            String task = taskText.getText();
            String description = descriptionText.getText();
            int personnelId = Integer.parseInt(personnelIdText.getText());
            int missionId = Integer.parseInt(missionIdText.getText());

            Assignments assignment = new Assignments(assignmentId, task, description, personnelId, missionId);
            data.add(assignment);
            saveToDatabase(analysisId, assignment);
            clearForm(analysisIdText, assignmentIdText, taskText, descriptionText, personnelIdText, missionIdText);
        });

        Button deleteButton = new Button("Delete Selected Data");
        deleteButton.setOnAction(e -> {
            Assignments selectedAssignment = tableView.getSelectionModel().getSelectedItem();
            if (selectedAssignment != null) {
                data.remove(selectedAssignment);
                deleteFromDatabase(selectedAssignment);
            }
        });

        formLayout.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, assignmentIdLabel, assignmentIdText, taskLabel, taskText, descriptionLabel, descriptionText, personnelIdLabel, personnelIdText, missionIdLabel, missionIdText, insertButton);

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().addAll(contentLayout, deleteButton, refreshButton);
        return mainLayout;
    }

    private static void saveToDatabase(String analysisId, Assignments assignment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Analysis ID,%s,Assignments,%d,%s,%s,%d,%d%n", analysisId, assignment.getAssignmentId(), assignment.getTask(), assignment.getDescription(), assignment.getPersonnelId(), assignment.getMissionId()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteFromDatabase(Assignments assignment) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Assignments,%d,%s,%s,%d,%d",
                            assignment.getAssignmentId(), assignment.getTask(), assignment.getDescription(), assignment.getPersonnelId(), assignment.getMissionId())))
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

    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Database.txt"))) {
            ObservableList<Assignments> dataList = FXCollections.observableArrayList();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7 && parts[2].equals("Assignments")) {
                    try {
                        int assignmentId = Integer.parseInt(parts[3].trim());
                        String task = parts[4].trim();
                        String description = parts[5].trim();
                        int personnelId = Integer.parseInt(parts[6].trim());
                        int missionId = Integer.parseInt(parts[7].trim());
                        dataList.add(new Assignments(assignmentId, task, description, personnelId, missionId));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in line: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in line: " + line);
                }
            }
            tableView.setItems(dataList);
        } catch (IOException e) {
            System.out.println("Error loading data from Database.txt.");
        }
    }


    private static void clearForm(TextField analysisIdText, TextField assignmentIdText, TextField taskText, TextArea descriptionText, TextField personnelIdText, TextField missionIdText) {
        analysisIdText.clear();
        assignmentIdText.clear();
        taskText.clear();
        descriptionText.clear();
        personnelIdText.clear();
        missionIdText.clear();
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Assignments Management");
        primaryStage.setScene(new Scene(getAssignmentsUI(), 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
