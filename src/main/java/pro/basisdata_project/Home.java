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
        TableColumn<DataItem, String> col1 = new TableColumn<>("Column 1");
        TableColumn<DataItem, String> col2 = new TableColumn<>("Column 2");

        col1.setCellValueFactory(new PropertyValueFactory<>("data1"));
        col2.setCellValueFactory(new PropertyValueFactory<>("data2"));

        tableView.getColumns().addAll(col1, col2);

        Button loadDataButton = new Button("Load Data from Database");
        loadDataButton.setOnAction(event -> {
            try {
                List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
                ObservableList<DataItem> data = FXCollections.observableArrayList();

                for (String line : lines) {
                    String[] parts = line.split(",");
                    data.add(new DataItem(parts[0], parts[1])); // Misalnya, kolom pertama dan kedua adalah data1 dan data2
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

                        return dataItem.getData1().toLowerCase().contains(lowerCaseFilter) ||
                                dataItem.getData2().toLowerCase().contains(lowerCaseFilter);
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
        private final String data1;
        private final String data2;

        public DataItem(String data1, String data2) {
            this.data1 = data1;
            this.data2 = data2;
        }

        public String getData1() {
            return data1;
        }

        public String getData2() {
            return data2;
        }
    }
}
