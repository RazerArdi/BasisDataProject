package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Space {
    private int spaceId;
    private String task;
    private String location;

    public Space(int spaceId, String task, String location) {
        this.spaceId = spaceId;
        this.task = task;
        this.location = location;
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

    public static VBox getSpaceUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label spaceIdLabel = new Label("Space ID:");
        TextField spaceIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location (Optional):");
        TextField locationText = new TextField();

        TableView<Space> tableView = new TableView<>();
        TableColumn<Space, Integer> spaceIdCol = new TableColumn<>("Space ID");
        spaceIdCol.setCellValueFactory(new PropertyValueFactory<>("spaceId"));
        TableColumn<Space, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Space, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(spaceIdCol, taskCol, locationCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int spaceId = Integer.parseInt(spaceIdText.getText());
            String task = taskText.getText();
            String location = locationText.getText();

            Space space = new Space(spaceId, task, location);
            System.out.println("Space Created: " + space.getSpaceId());

            // Save to Oracle database
            saveSpaceToDatabase(space);

            // Add new space to the table view
            tableView.getItems().add(space);

            // Clear input fields after adding space
            spaceIdText.clear();
            taskText.clear();
            locationText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<Space> spaceList = fetchSpacesFromDatabase();
        tableView.setItems(spaceList);

        vbox.getChildren().addAll(
                spaceIdLabel, spaceIdText, taskLabel, taskText,
                locationLabel, locationText, tableView, createButton);

        return vbox;
    }

    private static void saveSpaceToDatabase(Space space) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO SPACES (SPACE_ID, TASK, LOCATION) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, space.getSpaceId());
            pstmt.setString(2, space.getTask());
            pstmt.setString(3, space.getLocation());
            pstmt.executeUpdate();
            System.out.println("Space saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Space> fetchSpacesFromDatabase() {
        ObservableList<Space> spaceList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SPACE_ID, TASK, LOCATION FROM SPACES";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int spaceId = rs.getInt("SPACE_ID");
                String task = rs.getString("TASK");
                String location = rs.getString("LOCATION");

                Space space = new Space(spaceId, task, location);
                spaceList.add(space);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return spaceList;
    }
}
