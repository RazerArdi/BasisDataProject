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
        Label personnelNameLabel = new Label("Personnel Name:");
        TextField personnelNameText = new TextField();
        Label rankLabel = new Label("Rank:");
        TextField rankText = new TextField();
        Label specialtyLabel = new Label("Specialty:");
        TextField specialtyText = new TextField();
        Label currentAssignmentLabel = new Label("Current Assignment:");
        TextField currentAssignmentText = new TextField();
        Label contactInfoLabel = new Label("Contact Info:");
        TextField contactInfoText = new TextField();

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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = personnelIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String specialty = specialtyText.getText();
            String currentAssignment = currentAssignmentText.getText();
            String contactInfo = contactInfoText.getText();

            Personnels personnel = new Personnels(personnelId, personnelName, rank, specialty, currentAssignment, contactInfo);
            System.out.println("Personnel Created: " + personnel.getPersonnelId());

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC)\".PERSONNEL (PERSONNEL_ID, NAME, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, personnelId);
                pstmt.setString(2, personnelName);
                pstmt.setString(3, rank);
                pstmt.setString(4, specialty);
                pstmt.setString(5, currentAssignment);
                pstmt.setString(6, contactInfo);
                pstmt.executeUpdate();
                System.out.println("Personnel saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Add new personnel to the table view
            tableView.getItems().add(personnel);

            // Clear input fields after adding personnel
            personnelIdText.clear();
            personnelNameText.clear();
            rankText.clear();
            specialtyText.clear();
            currentAssignmentText.clear();
            contactInfoText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<Personnels> personnelList = fetchPersonnelsFromDatabase();
        tableView.setItems(personnelList);

        vbox.getChildren().addAll(
                personnelIdLabel, personnelIdText, personnelNameLabel, personnelNameText,
                rankLabel, rankText, specialtyLabel, specialtyText,
                currentAssignmentLabel, currentAssignmentText, contactInfoLabel, contactInfoText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<Personnels> fetchPersonnelsFromDatabase() {
        ObservableList<Personnels> personnelList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID, NAME, RANK, SPECIALTY, CURRENT_ASSIGNMENT, CONTACT_INFO FROM \"C4ISR PROJECT (BASIC)\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                String personnelName = rs.getString("NAME");
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
}
