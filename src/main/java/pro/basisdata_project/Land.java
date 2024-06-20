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

public class Land {
    private String landId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Land(String landId, String task, String location, String communicationLogCommId) {
        this.landId = landId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public String getLandId() {
        return landId;
    }

    public String getTask() {
        return task;
    }

    public String getLocation() {
        return location;
    }

    public String getCommunicationLogCommId() {
        return communicationLogCommId;
    }

    public static VBox getLandUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label landIdLabel = new Label("Land ID:");
        TextField landIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID:");
        TextField commIdText = new TextField();

        TableView<Land> tableView = new TableView<>();
        TableColumn<Land, String> landIdCol = new TableColumn<>("Land ID");
        landIdCol.setCellValueFactory(new PropertyValueFactory<>("landId"));
        TableColumn<Land, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Land, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Land, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(landIdCol, taskCol, locationCol, commIdCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Land selectedLand = tableView.getSelectionModel().getSelectedItem();
            if (selectedLand != null) {
                landIdText.setText(selectedLand.getLandId());
                taskText.setText(selectedLand.getTask());
                locationText.setText(selectedLand.getLocation());
                commIdText.setText(selectedLand.getCommunicationLogCommId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Land Selected", "Please select a land to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Land selectedLand = tableView.getSelectionModel().getSelectedItem();
            if (selectedLand != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".LAND WHERE LAND_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedLand.getLandId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Land Deleted", "Land with ID " + selectedLand.getLandId() + " has been deleted.");
                        tableView.getItems().remove(selectedLand);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete land from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Land Selected", "Please select a land to delete.");
            }
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String landId = landIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdText.getText();

            Land land = new Land(landId, task, location, commId);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".LAND (LAND_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, landId);
                pstmt.setString(2, task);
                pstmt.setString(3, location);
                pstmt.setString(4, commId);
                pstmt.executeUpdate();
                System.out.println("Land saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(land);

            landIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdText.clear();
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton, createButton);

        ObservableList<Land> landList = fetchLandFromDatabase();
        tableView.setItems(landList);

        vbox.getChildren().addAll(
                landIdLabel, landIdText, taskLabel, taskText,
                locationLabel, locationText, commIdLabel, commIdText,
                tableView, buttonBox);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static ObservableList<Land> fetchLandFromDatabase() {
        ObservableList<Land> landList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT LAND_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".LAND";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String landId = rs.getString("LAND_ID");
                String task = rs.getString("TASK");
                String location = rs.getString("LOCATION");
                String commId = rs.getString("COMMUNICATION_LOG_COMM_ID");

                Land land = new Land(landId, task, location, commId);
                landList.add(land);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return landList;
    }
}
