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

    public String getTask() {
        return task;
    }

    public String getLocation() {
        return location;
    }

    public String getCommunicationLogCommId() {
        return communicationLogCommId;
    }

    public void setAirId(String airId) {
        this.airId = airId;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setLocation(String location) {
        this.location = location;
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
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID *:");
        ComboBox<String> commIdComboBox = new ComboBox<>();
        ObservableList<String> commIdList = fetchCommIdsFromDatabase();
        commIdComboBox.setItems(commIdList);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);
        HBox airIdBox = new HBox();
        airIdBox.getChildren().addAll(
                airIdText,
                new Label("Auto Generated:"),
                createAutoGenerateCheckBox(airIdText)
        );
        airIdBox.setSpacing(5);

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
            String airId = airIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdComboBox.getValue();

            if (airId.isEmpty() || commId.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!isAutoGenerateChecked(airIdText)) {
                airId = airIdText.getText();
            } else {
                // Mode auto-generate, tandai sebagai "AUTO_GENERATED"
                airId = "AUTO_GENERATED";
            }

            Air air = new Air(airId, task, location, commId);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".AIR (AIR_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, airId);
                pstmt.setString(2, task);
                pstmt.setString(3, location);
                pstmt.setString(4, commId);
                pstmt.executeUpdate();
                System.out.println("Air saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

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
                String airId = airIdText.getText();
                String task = taskText.getText();
                String location = locationText.getText();
                String commId = commIdComboBox.getValue();

                selectedAir.setAirId(airId);
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
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                airIdText.clear();
                taskText.clear();
                locationText.clear();
                commIdComboBox.setValue(null);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Air selectedAir = tableView.getSelectionModel().getSelectedItem();
            if (selectedAir != null) {
                String airId = selectedAir.getAirId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".AIR WHERE AIR_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, airId);
                    pstmt.executeUpdate();
                    System.out.println("Air deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedAir);
            }
        });

        ObservableList<Air> airList = fetchAirFromDatabase();
        tableView.setItems(airList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                airIdLabel, airIdBox,
                taskLabel, taskText,
                locationLabel, locationText,
                commIdLabel, commIdComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField airIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                airIdText.setDisable(true);
                airIdText.clear();
            } else {
                airIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField airIdText) {
        CheckBox checkBox = (CheckBox) airIdText.getParent().getChildrenUnmodifiable().get(2);
        return checkBox.isSelected();
    }

    private static ObservableList<Air> fetchAirFromDatabase() {
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

    private static ObservableList<String> fetchCommIdsFromDatabase() {
        ObservableList<String> commIdList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String commId = rs.getString("COMM_ID");
                commIdList.add(commId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return commIdList;
    }
}
