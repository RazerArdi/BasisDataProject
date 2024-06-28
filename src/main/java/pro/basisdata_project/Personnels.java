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

public class Personnels {

    private String personnelId;
    private String personnelName;
    private String rank;
    private String specialty;
    private String currentAssignment;
    private String contactInfo;

    public Personnels(String personnelId, String personnelName, String rank, String specialty, String currentAssignment, String contactInfo) {
        this.personnelId = personnelId;
        this.personnelName = personnelName;
        this.rank = rank;
        this.specialty = specialty;
        this.currentAssignment = currentAssignment;
        this.contactInfo = contactInfo;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(String currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public static VBox getPersonnelsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);
        personnelIdText.setDisable(true);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            personnelIdText.setDisable(newValue);
            if (newValue) {
                personnelIdText.setText("Auto Generated");
            } else {
                personnelIdText.clear();
            }
        });

        Label personnelNameLabel = new Label("Personnel Name *:");
        TextField personnelNameText = new TextField();
        Label rankLabel = new Label("Rank *:");
        TextField rankText = new TextField();
        Label specialtyLabel = new Label("Specialty *:");
        TextField specialtyText = new TextField();
        Label currentAssignmentLabel = new Label("Current Assignment *:");
        TextField currentAssignmentText = new TextField();
        Label contactInfoLabel = new Label("Contact Info *:");
        TextField contactInfoText = new TextField();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        TableView<Personnels> tableView = new TableView<>();
        TableColumn<Personnels, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        TableColumn<Personnels, String> personnelNameCol = new TableColumn<>("Personnel Name");
        personnelNameCol.setCellValueFactory(new PropertyValueFactory<>("personnelName"));
        TableColumn<Personnels, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<Personnels, String> specialtyCol = new TableColumn<>("Specialty");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        TableColumn<Personnels, String> currentAssignmentCol = new TableColumn<>("Current Assignment");
        currentAssignmentCol.setCellValueFactory(new PropertyValueFactory<>("currentAssignment"));
        TableColumn<Personnels, String> contactInfoCol = new TableColumn<>("Contact Info");
        contactInfoCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        tableView.getColumns().addAll(personnelIdCol, personnelNameCol, rankCol, specialtyCol, currentAssignmentCol, contactInfoCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Personnels selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                personnelIdText.setText(selectedPersonnel.getPersonnelId());
                personnelNameText.setText(selectedPersonnel.getPersonnelName());
                rankText.setText(selectedPersonnel.getRank());
                specialtyText.setText(selectedPersonnel.getSpecialty());
                currentAssignmentText.setText(selectedPersonnel.getCurrentAssignment());
                contactInfoText.setText(selectedPersonnel.getContactInfo());
                autoGenerateIdCheckBox.setSelected(false);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Personnel Selected", "Please select a personnel to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Personnels selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL WHERE PERSONNEL_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedPersonnel.getPersonnelId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Personnel Deleted", "Personnel with ID " + selectedPersonnel.getPersonnelId() + " has been deleted.");
                        tableView.getItems().remove(selectedPersonnel);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete personnel from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Personnel Selected", "Please select a personnel to delete.");
            }
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = autoGenerateIdCheckBox.isSelected() ? null : personnelIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String specialty = specialtyText.getText();
            String currentAssignment = currentAssignmentText.getText();
            String contactInfo = contactInfoText.getText();

            if (personnelName.isEmpty() || rank.isEmpty() || specialty.isEmpty() || currentAssignment.isEmpty() || contactInfo.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql;
                if (autoGenerateIdCheckBox.isSelected()) {
                    sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PERSONNEL (Personnel_Name, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO) VALUES (?, ?, ?, ?, ?)";
                } else {
                    sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PERSONNEL (PERSONNEL_ID, Personnel_Name, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO) VALUES (?, ?, ?, ?, ?, ?)";
                }
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"PERSONNEL_ID"});
                int parameterIndex = 1;
                if (!autoGenerateIdCheckBox.isSelected()) {
                    pstmt.setString(parameterIndex++, personnelId);
                }
                pstmt.setString(parameterIndex++, personnelName);
                pstmt.setString(parameterIndex++, rank);
                pstmt.setString(parameterIndex++, specialty);
                pstmt.setString(parameterIndex++, currentAssignment);
                pstmt.setString(parameterIndex++, contactInfo);
                pstmt.executeUpdate();

                if (autoGenerateIdCheckBox.isSelected()) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        personnelId = rs.getString(1);
                    }
                }

                Personnels personnel = new Personnels(personnelId, personnelName, rank, specialty, currentAssignment, contactInfo);
                tableView.getItems().add(personnel);

                personnelIdText.clear();
                personnelNameText.clear();
                rankText.clear();
                specialtyText.clear();
                currentAssignmentText.clear();
                contactInfoText.clear();
                autoGenerateIdCheckBox.setSelected(true);
                errorLabel.setText("");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        ObservableList<Personnels> personnelList = fetchPersonnelsFromDatabase();
        tableView.setItems(personnelList);

        vbox.getChildren().addAll(
                personnelIdLabel, personnelIdText, autoGenerateIdCheckBox,
                personnelNameLabel, personnelNameText,
                rankLabel, rankText,
                specialtyLabel, specialtyText,
                currentAssignmentLabel, currentAssignmentText,
                contactInfoLabel, contactInfoText,
                errorLabel, createButton, buttonBox, tableView

        );

        return vbox;
    }

    private static ObservableList<Personnels> fetchPersonnelsFromDatabase() {
        ObservableList<Personnels> personnelList = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT * FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                String personnelName = rs.getString("Personnel_Name");
                String rank = rs.getString("RANK");
                String specialty = rs.getString("SPECIALTY");
                String currentAssignment = rs.getString("CURRENT_ASSIGNMENT");
                String contactInfo = rs.getString("CONTACT_INFO");
                Personnels personnel = new Personnels(personnelId, personnelName, rank, specialty, currentAssignment, contactInfo);
                personnelList.add(personnel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return personnelList;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
