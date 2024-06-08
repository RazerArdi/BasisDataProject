package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Home {

    public static VBox getHomeUI() {
        VBox homeContent = new VBox();
        homeContent.setAlignment(Pos.CENTER);
        homeContent.setSpacing(20);
        homeContent.setPadding(new Insets(20));

        // TextField for search
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        TableView<DataItem> tableView = new TableView<>();
        TableColumn<DataItem, String> colAnalysisId = new TableColumn<>("Analysis ID");
        TableColumn<DataItem, String> colDescription = new TableColumn<>("Description");
        TableColumn<DataItem, String> colDate = new TableColumn<>("Date");
        TableColumn<DataItem, String> colUserId = new TableColumn<>("User ID");
        TableColumn<DataItem, String> colPlatformId = new TableColumn<>("Platform ID");

        colAnalysisId.setCellValueFactory(new PropertyValueFactory<>("analysisId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colPlatformId.setCellValueFactory(new PropertyValueFactory<>("platformId"));

        tableView.getColumns().addAll(colAnalysisId, colDescription, colDate, colUserId, colPlatformId);

        Button loadDataButton = new Button("Load Data from Database");
        loadDataButton.setOnAction(event -> {
            try {
                List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
                ObservableList<DataItem> data = FXCollections.observableArrayList();

                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        data.add(new DataItem(parts[0], parts[1], parts[2], parts[3], parts[4]));
                    } else {
                        System.out.println("Invalid data format in line: " + line);
                    }
                }

                FilteredList<DataItem> filteredData = new FilteredList<>(data);
                tableView.setItems(filteredData);

                // Adding search functionality
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(dataItem -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        return dataItem.getDescription().toLowerCase().contains(lowerCaseFilter);
                    });
                });

                HBox searchBox = new HBox(searchField);
                searchBox.setAlignment(Pos.CENTER);
                searchBox.setPadding(new Insets(10));

                homeContent.getChildren().clear();
                homeContent.getChildren().addAll(searchBox, tableView);
            } catch (IOException e) {
                System.out.println("Error loading data from Database.txt.");
            }
        });



        homeContent.getChildren().addAll(loadDataButton);

        return homeContent;
    }

    public static class DataItem {
        private final String analysisId;
        private final String description;
        private final String date;
        private final String userId;
        private final String platformId;

        public DataItem(String analysisId, String description, String date, String userId, String platformId) {
            this.analysisId = analysisId;
            this.description = description;
            this.date = date;
            this.userId = userId;
            this.platformId = platformId;
        }

        public String getAnalysisId() {
            return analysisId;
        }

        public String getDescription() {
            return description;
        }

        public String getDate() {
            return date;
        }

        public String getUserId() {
            return userId;
        }

        public String getPlatformId() {
            return platformId;
        }
    }
}
