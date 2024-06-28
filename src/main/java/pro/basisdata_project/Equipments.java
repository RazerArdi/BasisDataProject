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

public class Equipments {
    private String equipmentId;
    private String name;
    private String type;
    private String status;

    public Equipments(String equipmentId, String name, String type, String status) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static VBox getEquipmentsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label equipmentIdLabel = new Label("Equipment ID *:");
        TextField equipmentIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        ObservableList<String> statusList = FXCollections.observableArrayList("Active", "Inactive");
        statusComboBox.setItems(statusList);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);
        HBox equipmentIdBox = new HBox();
        equipmentIdBox.getChildren().addAll(
                equipmentIdText,
                new Label("Auto Generated:"),
                createAutoGenerateCheckBox(equipmentIdText)
        );
        equipmentIdBox.setSpacing(5);

        TableView<Equipments> tableView = new TableView<>();
        TableColumn<Equipments, String> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<Equipments, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Equipments, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Equipments, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String equipmentId = equipmentIdText.getText();
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusComboBox.getValue();

            if (equipmentId.isEmpty() || status.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!isAutoGenerateChecked(equipmentIdText)) {
                equipmentId = equipmentIdText.getText();
            } else {
                // Auto-generate mode, set as "AUTO_GENERATED"
                equipmentId = "AUTO_GENERATED";
            }

            Equipments equipment = new Equipments(equipmentId, name, type, status);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO Equipments (EQUIPMENT_ID, NAME, TYPE, STATUS) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, equipmentId);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setString(4, status);
                pstmt.executeUpdate();
                System.out.println("Equipment saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(equipment);

            equipmentIdText.clear();
            nameText.clear();
            typeText.clear();
            statusComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                String equipmentId = equipmentIdText.getText();
                String name = nameText.getText();
                String type = typeText.getText();
                String status = statusComboBox.getValue();

                selectedEquipment.setEquipmentId(equipmentId);
                selectedEquipment.setName(name);
                selectedEquipment.setType(type);
                selectedEquipment.setStatus(status);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE Equipments SET NAME = ?, TYPE = ?, STATUS = ? WHERE EQUIPMENT_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setString(2, type);
                    pstmt.setString(3, status);
                    pstmt.setString(4, equipmentId);
                    pstmt.executeUpdate();
                    System.out.println("Equipment updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                equipmentIdText.clear();
                nameText.clear();
                typeText.clear();
                statusComboBox.setValue(null);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                String equipmentId = selectedEquipment.getEquipmentId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM Equipments WHERE EQUIPMENT_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, equipmentId);
                    pstmt.executeUpdate();
                    System.out.println("Equipment deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedEquipment);
            }
        });

        ObservableList<Equipments> equipmentList = fetchEquipmentsFromDatabase();
        tableView.setItems(equipmentList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                equipmentIdLabel, equipmentIdBox,
                nameLabel, nameText,
                typeLabel, typeText,
                statusLabel, statusComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField equipmentIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                equipmentIdText.setDisable(true);
                equipmentIdText.clear();
            } else {
                equipmentIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField equipmentIdText) {
        CheckBox checkBox = (CheckBox) equipmentIdText.getParent().getChildrenUnmodifiable().get(2);
        return checkBox.isSelected();
    }

    private static ObservableList<Equipments> fetchEquipmentsFromDatabase() {
        ObservableList<Equipments> equipmentList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID, NAME, TYPE, STATUS FROM Equipments";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("EQUIPMENT_ID");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String status = rs.getString("STATUS");

                Equipments equipment = new Equipments(equipmentId, name, type, status);
                equipmentList.add(equipment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return equipmentList;
    }
}
