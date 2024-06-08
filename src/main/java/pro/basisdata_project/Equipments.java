package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Equipments {

    private int equipmentId;
    private String name;
    private String type;
    private String status;
    private int missionId;

    public Equipments(int equipmentId, String name, String type, String status, int missionId) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.missionId = missionId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getMissionId() {
        return missionId;
    }

    public static VBox getEquipmentsUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<Equipments> tableView = new TableView<>();
        ObservableList<Equipments> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Equipments, Integer> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));

        TableColumn<Equipments, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Equipments, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Equipments, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Equipments, Integer> missionIdCol = new TableColumn<>("Mission ID");
        missionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));

        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol, missionIdCol);
        tableView.setItems(data);

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label missionIdLabel = new Label("Mission ID:");
        TextField missionIdText = new TextField();

        Button insertButton = new Button("Insert Data");
        insertButton.setOnAction(e -> {
            int equipmentId = Integer.parseInt(equipmentIdText.getText());
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            int missionId = Integer.parseInt(missionIdText.getText());

            Equipments equipment = new Equipments(equipmentId, name, type, status, missionId);
            data.add(equipment);
            saveToDatabase(equipment);
            clearForm(equipmentIdText, nameText, typeText, statusText, missionIdText);
        });

        formLayout.getChildren().addAll(equipmentIdLabel, equipmentIdText, nameLabel, nameText, typeLabel, typeText, statusLabel, statusText, missionIdLabel, missionIdText, insertButton);

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().add(contentLayout);

        return mainLayout;
    }

    private static void saveToDatabase(Equipments equipment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Equipments,%d,%s,%s,%s,%d%n", equipment.getEquipmentId(), equipment.getName(), equipment.getType(), equipment.getStatus(), equipment.getMissionId()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearForm(TextField equipmentIdText, TextField nameText, TextField typeText, TextField statusText, TextField missionIdText) {
        equipmentIdText.clear();
        nameText.clear();
        typeText.clear();
        statusText.clear();
        missionIdText.clear();
    }

    public static void main(String[] args) {
        // Create a JavaFX application to display the UI
        javafx.application.Application.launch(EquipmentsApp.class, args);
    }

    public static class EquipmentsApp extends javafx.application.Application {
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Equipments Management");
            primaryStage.setScene(new Scene(getEquipmentsUI(), 800, 600));
            primaryStage.show();
        }
    }
}
