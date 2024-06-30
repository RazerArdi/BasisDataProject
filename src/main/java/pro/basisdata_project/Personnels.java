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
        personnelIdText.setDisable(true);

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

        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);

        HBox personnelIdBox = new HBox();
        personnelIdBox.getChildren().addAll(personnelIdText, new Label("*"), autoGenerateIdCheckBox);
        personnelIdBox.setSpacing(10);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                personnelIdText.setDisable(true);
                personnelIdText.clear();
            } else {
                personnelIdText.setDisable(false);
            }
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = autoGenerateIdCheckBox.isSelected() ? generatePersonnelId() : personnelIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String specialty = specialtyText.getText();
            String currentAssignment = currentAssignmentText.getText();
            String contactInfo = contactInfoText.getText();

            if (personnelName.isEmpty() || rank.isEmpty() || specialty.isEmpty() || currentAssignment.isEmpty() || contactInfo.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!autoGenerateIdCheckBox.isSelected() && personnelId.isEmpty()) {
                errorLabel.setText("Personnel ID is required!");
                return;
            }

            if (!autoGenerateIdCheckBox.isSelected() && personnelIdExists(personnelId)) {
                errorLabel.setText("Personnel ID already exists!");
                return;
            }

            Personnels personnel = new Personnels(personnelId, personnelName, rank, specialty, currentAssignment, contactInfo);

            savePersonnelToDatabase(personnel);

            tableView.getItems().add(personnel);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Personnel Created", "Personnel has been created successfully.");

            clearFields(personnelIdText, personnelNameText, rankText, specialtyText, currentAssignmentText, contactInfoText);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Personnels selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                String personnelId = selectedPersonnel.getPersonnelId();
                String personnelName = personnelNameText.getText();
                String rank = rankText.getText();
                String specialty = specialtyText.getText();
                String currentAssignment = currentAssignmentText.getText();
                String contactInfo = contactInfoText.getText();

                if (personnelId.isEmpty()) {
                    errorLabel.setText("Personnel ID is required!");
                    return;
                }

                if (personnelName.isEmpty() || rank.isEmpty() || specialty.isEmpty() || currentAssignment.isEmpty() || contactInfo.isEmpty()) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }

                selectedPersonnel.setPersonnelName(personnelName);
                selectedPersonnel.setRank(rank);
                selectedPersonnel.setSpecialty(specialty);
                selectedPersonnel.setCurrentAssignment(currentAssignment);
                selectedPersonnel.setContactInfo(contactInfo);

                updatePersonnelInDatabase(selectedPersonnel);

                tableView.refresh();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Personnel Updated", "Personnel details have been updated successfully.");

                clearFields(personnelIdText, personnelNameText, rankText, specialtyText, currentAssignmentText, contactInfoText);
                errorLabel.setText("");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Personnel Selected", "Please select a personnel to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Personnels selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                deletePersonnelFromDatabase(selectedPersonnel);

                tableView.getItems().remove(selectedPersonnel);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Personnel Deleted", "Personnel has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Personnel Selected", "Please select a personnel to delete.");
            }
        });

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        ObservableList<Personnels> personnelList = fetchPersonnelsFromDatabase();
        tableView.setItems(personnelList);

        vbox.getChildren().addAll(
                personnelIdLabel, personnelIdBox,
                personnelNameLabel, personnelNameText,
                rankLabel, rankText,
                specialtyLabel, specialtyText,
                currentAssignmentLabel, currentAssignmentText,
                contactInfoLabel, contactInfoText,
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

    private static boolean personnelIdExists(String personnelId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL WHERE PERSONNEL_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String generatePersonnelId() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".PERSONNEL_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "AUTO_GENERATED";
    }

    private static void savePersonnelToDatabase(Personnels personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PERSONNEL (PERSONNEL_ID, PERSONNEL_NAME, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelId());
            pstmt.setString(2, personnel.getPersonnelName());
            pstmt.setString(3, personnel.getRank());
            pstmt.setString(4, personnel.getSpecialty());
            pstmt.setString(5, personnel.getCurrentAssignment());
            pstmt.setString(6, personnel.getContactInfo());
            pstmt.executeUpdate();
            System.out.println("Personnel saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updatePersonnelInDatabase(Personnels personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".PERSONNEL " +
                    "SET PERSONNEL_NAME = ?, RANK = ?, SPECIALTY = ?, CURRENT_ASSIGNMENT = ?, CONTACT_INFO = ? " +
                    "WHERE PERSONNEL_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelName());
            pstmt.setString(2, personnel.getRank());
            pstmt.setString(3, personnel.getSpecialty());
            pstmt.setString(4, personnel.getCurrentAssignment());
            pstmt.setString(5, personnel.getContactInfo());
            pstmt.setString(6, personnel.getPersonnelId());
            pstmt.executeUpdate();
            System.out.println("Personnel updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deletePersonnelFromDatabase(Personnels personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL WHERE PERSONNEL_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelId());
            pstmt.executeUpdate();
            System.out.println("Personnel deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Personnels> fetchPersonnelsFromDatabase() {
        ObservableList<Personnels> personnelList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID, PERSONNEL_NAME, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                String personnelName = rs.getString("PERSONNEL_NAME");
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

    private static void clearFields(TextField personnelIdText, TextField personnelNameText, TextField rankText, TextField specialtyText, TextField currentAssignmentText, TextField contactInfoText) {
        personnelIdText.clear();
        personnelNameText.clear();
        rankText.clear();
        specialtyText.clear();
        currentAssignmentText.clear();
        contactInfoText.clear();
    }
}
