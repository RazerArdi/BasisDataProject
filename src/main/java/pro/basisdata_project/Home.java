package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
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
        TableColumn<DataItem, String> colType = new TableColumn<>("Type");
        TableColumn<DataItem, String> colId = new TableColumn<>("ID");
        TableColumn<DataItem, String> colName = new TableColumn<>("Name");

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableView.getColumns().addAll(colType, colId, colName);

        Button loadDataButton = new Button("Load Data from All Classes");
        loadDataButton.setOnAction(event -> {
            ObservableList<DataItem> data = FXCollections.observableArrayList();

            // Add data from Analysis
            data.addAll(getAnalysisData());

            // Add data from Data
            data.addAll(getDataData());

            // Add data from Users
            data.addAll(getUsersData());

            // Add data from Platforms
            data.addAll(getPlatformsData());

            // Add data from Personnels
            data.addAll(getPersonnelsData());

            // Add data from Equipments
            data.addAll(getEquipmentsData());

            // Add data from Missions
            data.addAll(getMissionsData());

            // Add data from Maintancelog
            data.addAll(getMaintancelogData());

            // Add data from Commlog
            data.addAll(getCommlogData());

            // Add data from Sensors
            data.addAll(getSensorsData());

            // Add data from Assignments
            data.addAll(getAssignmentsData());

            FilteredList<DataItem> filteredData = new FilteredList<>(data);
            tableView.setItems(filteredData);

            // Adding search functionality
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(dataItem -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    // Filter by type or name
                    return dataItem.getType().toLowerCase().contains(lowerCaseFilter)
                            || dataItem.getName().toLowerCase().contains(lowerCaseFilter);
                });
            });

            HBox searchBox = new HBox(searchField);
            searchBox.setAlignment(Pos.CENTER);
            searchBox.setPadding(new Insets(10));

            homeContent.getChildren().clear();
            homeContent.getChildren().addAll(searchBox, tableView);
        });

        homeContent.getChildren().addAll(loadDataButton);

        return homeContent;
    }

    private static List<DataItem> getAnalysisData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Analysis class
        return data;
    }

    private static List<DataItem> getDataData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Data class
        return data;
    }

    private static List<DataItem> getUsersData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Users class
        return data;
    }

    private static List<DataItem> getPlatformsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Platforms class
        return data;
    }

    private static List<DataItem> getPersonnelsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Personnels class
        return data;
    }

    private static List<DataItem> getEquipmentsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Equipments class
        return data;
    }

    private static List<DataItem> getMissionsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Missions class
        return data;
    }

    private static List<DataItem> getMaintancelogData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Maintancelog class
        return data;
    }

    private static List<DataItem> getCommlogData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Commlog class
        return data;
    }

    private static List<DataItem> getSensorsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Sensors class
        return data;
    }

    private static List<DataItem> getAssignmentsData() {
        List<DataItem> data = new ArrayList<>();
        // Add data from Assignments class
        return data;
    }

    public static class DataItem {
        private final String type;
        private final String id;
        private final String name;

        public DataItem(String type, String id, String name) {
            this.type = type;
            this.id = id;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
