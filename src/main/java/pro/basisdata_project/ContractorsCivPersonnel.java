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

public class ContractorsCivPersonnel {

    private String personnelId;
    private String officerId;
    private String personnelName;
    private String rank;
    private String activeDate;

    public ContractorsCivPersonnel(String personnelId, String officerId, String personnelName, String rank, String activeDate) {
        this.personnelId = personnelId;
        this.officerId = officerId;
        this.personnelName = personnelName;
        this.rank = rank;
        this.activeDate = activeDate;
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

    public static VBox getContractorsCivPersonnelUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

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

        TableView<ContractorsCivPersonnel> tableView = new TableView<>();
        TableColumn<ContractorsCivPersonnel, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        TableColumn<ContractorsCivPersonnel, String> officerIdCol = new TableColumn<>("Officer ID");
        officerIdCol.setCellValueFactory(new PropertyValueFactory<>("officerId"));
        TableColumn<ContractorsCivPersonnel, String> personnelNameCol = new TableColumn<>("Personnel Name");
        personnelNameCol.setCellValueFactory(new PropertyValueFactory<>("personnelName"));
        TableColumn<ContractorsCivPersonnel, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<ContractorsCivPersonnel, String> activeDateCol = new TableColumn<>("Active Date");
        activeDateCol.setCellValueFactory(new PropertyValueFactory<>("activeDate"));

        tableView.getColumns().addAll(personnelIdCol, officerIdCol, personnelNameCol, rankCol, activeDateCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = personnelIdText.getText();
            String officerId = officerIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String activeDate = activeDateText.getText();

            ContractorsCivPersonnel personnel = new ContractorsCivPersonnel(personnelId, officerId, personnelName, rank, activeDate);

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC)\".CONTRACTORS_CIV_PERSONNEL (PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE) VALUES (?, ?, ?, ?, ?)";
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

            // Add new personnel to the table view
            tableView.getItems().add(personnel);

            // Clear input fields after adding personnel
            personnelIdText.clear();
            officerIdText.clear();
            personnelNameText.clear();
            rankText.clear();
            activeDateText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<ContractorsCivPersonnel> personnelList = fetchPersonnelFromDatabase();
        tableView.setItems(personnelList);

        vbox.getChildren().addAll(
                personnelIdLabel, personnelIdText, officerIdLabel, officerIdText,
                personnelNameLabel, personnelNameText, rankLabel, rankText,
                activeDateLabel, activeDateText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<ContractorsCivPersonnel> fetchPersonnelFromDatabase() {
        ObservableList<ContractorsCivPersonnel> personnelList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE FROM \"C4ISR PROJECT (BASIC)\".CONTRACTORS_CIV_PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString("PERSONNEL_ID");
                String officerId = rs.getString("OFFICER_ID");
                String personnelName = rs.getString("PERSONNEL_NAME");
                String rank = rs.getString("RANK");
                String activeDate = rs.getString("ACTIVE_DATE");

                ContractorsCivPersonnel personnel = new ContractorsCivPersonnel(personnelId, officerId, personnelName, rank, activeDate);
                personnelList.add(personnel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return personnelList;
    }
}
