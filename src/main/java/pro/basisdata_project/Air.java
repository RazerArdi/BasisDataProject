package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    public static VBox getAirUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label airIdLabel = new Label("Air ID:");
        TextField airIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID:");
        TextField commIdText = new TextField();

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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String airId = airIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdText.getText();

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
            commIdText.clear();
        });

        ObservableList<Air> airList = fetchAirFromDatabase();
        tableView.setItems(airList);

        vbox.getChildren().addAll(
                airIdLabel, airIdText, taskLabel, taskText,
                locationLabel, locationText, commIdLabel, commIdText,
                tableView, createButton);

        return vbox;
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
}
