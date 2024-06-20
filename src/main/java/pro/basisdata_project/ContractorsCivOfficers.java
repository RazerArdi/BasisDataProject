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

public class ContractorsCivOfficers {

    private String personnelId;
    private String officerId;
    private String personnelName;
    private String rank;
    private String activeDate;
    private String personnelType; // New field for personnel type

    public ContractorsCivOfficers(String personnelId, String officerId, String personnelName, String rank, String activeDate, String personnelType) {
        this.personnelId = personnelId;
        this.officerId = officerId;
        this.personnelName = personnelName;
        this.rank = rank;
        this.activeDate = activeDate;
        this.personnelType = personnelType;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
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

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public String getPersonnelType() {
        return personnelType;
    }

    public void setPersonnelType(String personnelType) {
        this.personnelType = personnelType;
    }

    public static VBox getContractorsCivPersonnelUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label typeLabel = new Label("Select Personnel Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Contractors", "Officer", "Enlisted"));

        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        Label officerIdLabel = new Label("Officer ID:");
        TextField officerIdText = new TextField();
        Label personnelNameLabel = new Label("Personnel Name:");
        TextField personnelNameText = new TextField();
        Label rankLabel = new Label("Rank:");
        TextField rankText = new TextField();
        Label activeDateLabel = new Label("Active Date:");
        TextField activeDateText = new TextField();

        TableView<ContractorsCivOfficers> tableView = new TableView<>();
        TableColumn<ContractorsCivOfficers, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        TableColumn<ContractorsCivOfficers, String> officerIdCol = new TableColumn<>("Officer ID");
        officerIdCol.setCellValueFactory(new PropertyValueFactory<>("officerId"));
        TableColumn<ContractorsCivOfficers, String> personnelNameCol = new TableColumn<>("Personnel Name");
        personnelNameCol.setCellValueFactory(new PropertyValueFactory<>("personnelName"));
        TableColumn<ContractorsCivOfficers, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<ContractorsCivOfficers, String> activeDateCol = new TableColumn<>("Active Date");
        activeDateCol.setCellValueFactory(new PropertyValueFactory<>("activeDate"));

        tableView.getColumns().addAll(personnelIdCol, officerIdCol, personnelNameCol, rankCol, activeDateCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = personnelIdText.getText();
            String officerId = officerIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String activeDate = activeDateText.getText();
            String personnelType = typeChoiceBox.getValue(); // Get selected personnel type

            ContractorsCivOfficers personnel = new ContractorsCivOfficers(personnelId, officerId, personnelName, rank, activeDate, personnelType);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql;
                if ("Contractors".equals(personnelType)) {
                    sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".\"contractors\" (PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE) VALUES (?, ?, ?, ?, ?)";
                } else if ("Officer".equals(personnelType)) {
                    sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".officer (PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE) VALUES (?, ?, ?, ?, ?)";
                } else { // Enlisted
                    sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".enlisted (PERSONNEL_ID, ENLIST_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE) VALUES (?, ?, ?, ?, ?)";
                }
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, personnelId);
                pstmt.setString(2, officerId);
                pstmt.setString(3, personnelName);
                pstmt.setString(4, rank);
                pstmt.setString(5, activeDate);
                pstmt.executeUpdate();
                System.out.println("Personnel saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Update the table view based on the selected personnel type
            ObservableList<ContractorsCivOfficers> updatedList = fetchPersonnelByTypeFromDatabase(personnelType);
            tableView.setItems(updatedList);

            personnelIdText.clear();
            officerIdText.clear();
            personnelNameText.clear();
            rankText.clear();
            activeDateText.clear();
        });

        vbox.getChildren().addAll(
                typeLabel, typeChoiceBox,
                personnelIdLabel, personnelIdText, officerIdLabel, officerIdText,
                personnelNameLabel, personnelNameText, rankLabel, rankText,
                activeDateLabel, activeDateText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<ContractorsCivOfficers> fetchPersonnelByTypeFromDatabase(String personnelType) {
        ObservableList<ContractorsCivOfficers> personnelList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql;
            if ("Contractors".equals(personnelType)) {
                sql = "SELECT PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE FROM \"C4ISR PROJECT (BASIC) V2\".\"contractors\"";
            } else if ("Officer".equals(personnelType)) {
                sql = "SELECT PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE FROM \"C4ISR PROJECT (BASIC) V2\".officer";
            } else { // Enlisted
                sql = "SELECT PERSONNEL_ID, ENLIST_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE FROM \"C4ISR PROJECT (BASIC) V2\".enlisted";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                String officerId = rs.getString("OFFICER_ID");
                String personnelName = rs.getString("PERSONNEL_NAME");
                String rank = rs.getString("RANK");
                String activeDate = rs.getString("ACTIVE_DATE");

                ContractorsCivOfficers personnel = new ContractorsCivOfficers(personnelId, officerId, personnelName, rank, activeDate, personnelType);
                personnelList.add(personnel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return personnelList;
    }
}
