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

    public void setLandId(String landId) {
        this.landId = landId;
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

    public static VBox getLandUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label landIdLabel = new Label("Land ID *:");
        TextField landIdText = new TextField();
        CheckBox autoGenerateCheckbox = new CheckBox("Auto Generate");
        autoGenerateCheckbox.setSelected(true); // Default to auto generate
        autoGenerateCheckbox.setOnAction(e -> {
            landIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                landIdText.clear();
            }
        });

        Label taskLabel = new Label("Task *:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID *:");
        ComboBox<String> commIdComboBox = new ComboBox<>();

        // Populate the ComboBox with COMM_IDs from the Communication Log table
        loadCommIdsIntoComboBox(commIdComboBox);

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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String landId;
            if (autoGenerateCheckbox.isSelected()) {
                landId = "AUTO_GENERATED";
            } else {
                landId = landIdText.getText();
            }
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdComboBox.getValue();

            if (task.isEmpty() || commId.isEmpty() || (landId.isEmpty() && !autoGenerateCheckbox.isSelected())) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

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
            commIdComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Land selectedLand = tableView.getSelectionModel().getSelectedItem();
            if (selectedLand != null) {
                String landId = landIdText.getText();
                String task = taskText.getText();
                String location = locationText.getText();
                String commId = commIdComboBox.getValue();

                if (task.isEmpty() || commId.isEmpty() || (landId.isEmpty() && !autoGenerateCheckbox.isSelected())) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }

                selectedLand.setLandId(landId);
                selectedLand.setTask(task);
                selectedLand.setLocation(location);
                selectedLand.setCommunicationLogCommId(commId);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".LAND SET TASK = ?, LOCATION = ?, COMMUNICATION_LOG_COMM_ID = ? WHERE LAND_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, task);
                    pstmt.setString(2, location);
                    pstmt.setString(3, commId);
                    pstmt.setString(4, landId);
                    pstmt.executeUpdate();
                    System.out.println("Land updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                landIdText.clear();
                taskText.clear();
                locationText.clear();
                commIdComboBox.setValue(null);
                errorLabel.setText("");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Land selectedLand = tableView.getSelectionModel().getSelectedItem();
            if (selectedLand != null) {
                String landId = selectedLand.getLandId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".LAND WHERE LAND_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, landId);
                    pstmt.executeUpdate();
                    System.out.println("Land deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedLand);
            }
        });

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        ObservableList<Land> landList = fetchLandFromDatabase();
        tableView.setItems(landList);

        vbox.getChildren().addAll(
                landIdLabel, new HBox(10, landIdText, autoGenerateCheckbox),
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

    private static void loadCommIdsIntoComboBox(ComboBox<String> commIdComboBox) {
        ObservableList<String> commIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String commId = rs.getString("COMM_ID");
                commIds.add(commId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        commIdComboBox.setItems(commIds);
    }
}
