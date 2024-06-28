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

public class Space {
    private int spaceId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Space(int spaceId, String task, String location, String communicationLogCommId) {
        this.spaceId = spaceId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
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

    public static VBox getSpaceUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label spaceIdLabel = new Label("Space ID *:");
        TextField spaceIdText = new TextField();
        Label taskLabel = new Label("Task *:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location (Optional):");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID *:");
        ComboBox<String> commIdComboBox = new ComboBox<>();
        ObservableList<String> commLogIds = fetchCommunicationLogIdsFromDatabase();
        commIdComboBox.setItems(commLogIds);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);

        HBox spaceIdBox = new HBox();
        spaceIdBox.getChildren().addAll(
                spaceIdText,
                new Label("* Auto Generated:"),
                createAutoGenerateCheckBox(spaceIdText)
        );
        spaceIdBox.setSpacing(5);

        TableView<Space> tableView = new TableView<>();
        TableColumn<Space, Integer> spaceIdCol = new TableColumn<>("Space ID");
        spaceIdCol.setCellValueFactory(new PropertyValueFactory<>("spaceId"));
        TableColumn<Space, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Space, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Space, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(spaceIdCol, taskCol, locationCol, commIdCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int spaceId = -1; // default value
            if (!isAutoGenerateChecked(spaceIdText)) {
                try {
                    spaceId = Integer.parseInt(spaceIdText.getText());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Space ID must be a valid number!");
                    return;
                }
            }

            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdComboBox.getValue();

            if (task.isEmpty() || commId.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!isAutoGenerateChecked(spaceIdText) && spaceIdText.getText().isEmpty()) {
                errorLabel.setText("Space ID must be provided or auto-generated!");
                return;
            }

            Space space = new Space(spaceId, task, location, commId);
            saveSpaceToDatabase(space);
            tableView.getItems().add(space);

            spaceIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            // Logika untuk tombol Edit
            Space selectedSpace = tableView.getSelectionModel().getSelectedItem();
            if (selectedSpace != null) {
                spaceIdText.setText(String.valueOf(selectedSpace.getSpaceId()));
                taskText.setText(selectedSpace.getTask());
                locationText.setText(selectedSpace.getLocation());
                commIdComboBox.setValue(selectedSpace.getCommunicationLogCommId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Space Selected", "Please select a space to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            // Logika untuk tombol Delete
            Space selectedSpace = tableView.getSelectionModel().getSelectedItem();
            if (selectedSpace != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".SPACE WHERE SPACE_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, selectedSpace.getSpaceId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Space Deleted", "Space with ID " + selectedSpace.getSpaceId() + " has been deleted.");
                        tableView.getItems().remove(selectedSpace);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete space from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Space Selected", "Please select a space to delete.");
            }
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton, createButton);

        vbox.getChildren().addAll(
                spaceIdLabel, spaceIdBox,
                taskLabel, taskText,
                locationLabel, locationText,
                commIdLabel, commIdComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField spaceIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                spaceIdText.setDisable(true);
                spaceIdText.clear();
            } else {
                spaceIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField spaceIdText) {
        CheckBox checkBox = (CheckBox) spaceIdText.getParent().getChildrenUnmodifiable().get(2);
        return checkBox.isSelected();
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static void saveSpaceToDatabase(Space space) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".SPACE (SPACE_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, space.getSpaceId());
            pstmt.setString(2, space.getTask());
            pstmt.setString(3, space.getLocation());
            pstmt.setString(4, space.getCommunicationLogCommId());
            pstmt.executeUpdate();
            System.out.println("Space saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Space> fetchSpacesFromDatabase() {
        ObservableList<Space> spaceList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SPACE_ID, TASK, LOCATION, COMMUNICATION_LOG_COMM_ID FROM \"C4ISR PROJECT (BASIC) V2\".SPACE";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int spaceId = rs.getInt("SPACE_ID");
                String task = rs.getString("TASK");
                String location = rs.getString("LOCATION");
                String commId = rs.getString("COMMUNICATION_LOG_COMM_ID");

                Space space = new Space(spaceId, task, location, commId);
                spaceList.add(space);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return spaceList;
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