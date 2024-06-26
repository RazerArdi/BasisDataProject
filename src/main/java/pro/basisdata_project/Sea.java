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

public class Sea {
    private String seaPlatformId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Sea(String seaPlatformId, String task, String location, String communicationLogCommId) {
        this.seaPlatformId = seaPlatformId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public String getSeaPlatformId() {
        return seaPlatformId;
    }

    public void setSeaPlatformId(String seaPlatformId) {
        this.seaPlatformId = seaPlatformId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCommunicationLogCommId() {
        return communicationLogCommId;
    }

    public void setCommunicationLogCommId(String communicationLogCommId) {
        this.communicationLogCommId = communicationLogCommId;
    }

    public static VBox getSeaUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID:");
        ComboBox<String> commIdComboBox = new ComboBox<>();
        ObservableList<String> commLogIds = fetchCommunicationLogIdsFromDatabase();
        commIdComboBox.setItems(commLogIds);

        TableView<Sea> tableView = new TableView<>();
        TableColumn<Sea, String> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("seaPlatformId"));
        TableColumn<Sea, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Sea, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Sea, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(platformIdCol, taskCol, locationCol, commIdCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Sea selectedSea = tableView.getSelectionModel().getSelectedItem();
            if (selectedSea != null) {
                platformIdText.setText(selectedSea.getSeaPlatformId());
                taskText.setText(selectedSea.getTask());
                locationText.setText(selectedSea.getLocation());
                commIdComboBox.setValue(selectedSea.getCommunicationLogCommId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Sea Platform Selected", "Please select a sea platform to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Sea selectedSea = tableView.getSelectionModel().getSelectedItem();
            if (selectedSea != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".SEA WHERE SEA_PLATFORM_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedSea.getSeaPlatformId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Sea Platform Deleted", "Sea Platform with ID " + selectedSea.getSeaPlatformId() + " has been deleted.");
                        tableView.getItems().remove(selectedSea);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete sea platform from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Sea Platform Selected", "Please select a sea platform to delete.");
            }
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String platformId = platformIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdComboBox.getValue();

            Sea sea = new Sea(platformId, task, location, commId);
            System.out.println("Sea Platform Created: " + sea.getSeaPlatformId());

            saveSeaPlatformToDatabase(sea);

            tableView.getItems().add(sea);

            platformIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdComboBox.setValue(null);
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton, createButton);

        ObservableList<Sea> seaList = fetchSeaPlatformsFromDatabase();
        tableView.setItems(seaList);

        vbox.getChildren().addAll(
                platformIdLabel, platformIdText, taskLabel, taskText,
                locationLabel, locationText, commIdLabel, commIdComboBox,
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

    private static void saveSeaPlatformToDatabase(Sea sea) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".SEA (SEA_PLATFORM_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sea.getSeaPlatformId());
            pstmt.setString(2, sea.getTask());
            pstmt.setString(3, sea.getLocation());
            pstmt.setString(4, sea.getCommunicationLogCommId());
            pstmt.executeUpdate();
            System.out.println("Sea platform saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Sea> fetchSeaPlatformsFromDatabase() {
        ObservableList<Sea> seaList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SEA_PLATFORM_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".SEA";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String platformId = rs.getString("SEA_PLATFORM_ID");
                String task = rs.getString("TASK");
                String location = rs.getString("LOCATION");
                String commId = rs.getString("COMMUNICATION_LOG_COMM_ID");

                Sea sea = new Sea(platformId, task, location, commId);
                seaList.add(sea);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return seaList;
    }

    private static ObservableList<String> fetchCommunicationLogIdsFromDatabase() {
        ObservableList<String> commLogIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String commId = rs.getString("COMM_ID");
                commLogIds.add(commId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return commLogIds;
    }
}
