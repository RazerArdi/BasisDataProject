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

public class Air {
    private String airId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Air(String airId, String task, String location, String communicationLogCommId) {
        this.airId = airId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public String getAirId() {
        return airId;
    }

    public void setAirId(String airId) {
        this.airId = airId;
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

    public static VBox getAirUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label airIdLabel = new Label("Air ID *:");
        TextField airIdText = new TextField();
        CheckBox autoGenerateCheckbox = new CheckBox("Auto Generate");
        autoGenerateCheckbox.setSelected(true);
        autoGenerateCheckbox.setOnAction(e -> {
            airIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                airIdText.clear();
            }
        });

        Label taskLabel = new Label("Task *:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID *:");
        ComboBox<String> commIdComboBox = new ComboBox<>();

        loadCommIdsIntoComboBox(commIdComboBox);

        TableView<Air> tableView = new TableView<>();
        TableColumn<Air, String> airIdCol = new TableColumn<>("Air ID");
        airIdCol.setCellValueFactory(new PropertyValueFactory<>("airId"));
        TableColumn<Air, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Air, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Air, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(airIdCol, taskCol, locationCol, commIdCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String airId;
            if (autoGenerateCheckbox.isSelected()) {
                airId = getNextAirIdFromSequence();
            } else {
                airId = airIdText.getText();
            }
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdComboBox.getValue();

            if (task.isEmpty() || commId.isEmpty() || (!autoGenerateCheckbox.isSelected() && airId.isEmpty())) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            Air air = new Air(airId, task, location, commId);

            saveAirToDatabase(air);

            tableView.getItems().add(air);

            airIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Air selectedAir = tableView.getSelectionModel().getSelectedItem();
            if (selectedAir != null) {
                String airId = selectedAir.getAirId();
                String task = taskText.getText();
                String location = locationText.getText();
                String commId = commIdComboBox.getValue();

                if (task.isEmpty() || commId.isEmpty()) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }

                selectedAir.setTask(task);
                selectedAir.setLocation(location);
                selectedAir.setCommunicationLogCommId(commId);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".AIR SET TASK = ?, LOCATION = ?, COMMUNICATION_LOG_COMM_ID = ? WHERE AIR_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, task);
                    pstmt.setString(2, location);
                    pstmt.setString(3, commId);
                    pstmt.setString(4, airId);
                    pstmt.executeUpdate();
                    System.out.println("Air updated in database.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Air Updated", "Air with ID " + airId + " has been updated.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "Failed to update air in database.");
                }

                tableView.refresh();

                airIdText.clear();
                taskText.clear();
                locationText.clear();
                commIdComboBox.setValue(null);
                errorLabel.setText("");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Air Selected", "Please select an air platform to edit.");
            }
        });


        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Air selectedAir = tableView.getSelectionModel().getSelectedItem();
            if (selectedAir != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".AIR WHERE AIR_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedAir.getAirId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Air Deleted", "Air with ID " + selectedAir.getAirId() + " has been deleted.");
                        tableView.getItems().remove(selectedAir);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete air platform from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete air platform from database.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Air Selected", "Please select an air platform to delete.");
            }
        });

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        ObservableList<Air> airList = fetchAirPlatformsFromDatabase();
        tableView.setItems(airList);

        vbox.getChildren().addAll(
                airIdLabel, new HBox(10, airIdText, autoGenerateCheckbox),
                taskLabel, taskText,
                locationLabel, locationText,
                commIdLabel, commIdComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static void saveAirToDatabase(Air air) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".AIR (AIR_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, air.getAirId());
            pstmt.setString(2, air.getTask());
            pstmt.setString(3, air.getLocation());
            pstmt.setString(4, air.getCommunicationLogCommId());
            pstmt.executeUpdate();
            System.out.println("Air platform saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Air> fetchAirPlatformsFromDatabase() {
        ObservableList<Air> airList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT AIR_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".AIR";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String airId = rs.getString("AIR_ID");
                String task = rs.getString("TASK");
                String location = rs.getString("LOCATION");
                String commId = rs.getString("COMMUNICATION_LOG_COMM_ID");

                Air air = new Air(airId, task, location, commId);
                airList.add(air);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return airList;
    }

    private static void loadCommIdsIntoComboBox(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String commId = rs.getString("COMM_ID");
                comboBox.getItems().add(commId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static String getNextAirIdFromSequence() {
        String airId = null;
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".AIR_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                airId = "AIR-" + rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return airId;
    }
}
