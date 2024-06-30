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

public class ContractorsCivOfficers {

    private String personnelId;
    private String officerId;
    private String personnelName;
    private String rank;
    private String contractDate;
    private String activeDate;
    private String personnelType;

    public ContractorsCivOfficers(String personnelId, String officerId, String personnelName, String rank, String contractDate, String activeDate, String personnelType) {
        this.personnelId = personnelId;
        this.officerId = officerId;
        this.personnelName = personnelName;
        this.rank = rank;
        this.contractDate = contractDate;
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

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
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
        Label contractDateLabel = new Label("Contract Date:");
        DatePicker contractDatePicker = new DatePicker();
        Label activeDateLabel = new Label("Active Date:");
        DatePicker activeDateDatePicker = new DatePicker();

        typeChoiceBox.setOnAction(e -> {
            String selectedType = typeChoiceBox.getValue();
            if ("Contractors".equals(selectedType)) {
                personnelIdLabel.setText("Personnel ID:");
                officerIdLabel.setText("Officer ID:");
                personnelNameLabel.setText("Personnel Name:");
                rankLabel.setText("Rank:");
                contractDateLabel.setText("Contract Date:");
                activeDateLabel.setText("Active Date:");
            } else if ("Officer".equals(selectedType)) {
                personnelIdLabel.setText("Personnel ID:");
                officerIdLabel.setText("Officer ID:");
                personnelNameLabel.setText("Personnel Name:");
                rankLabel.setText("Rank:");
                contractDateLabel.setText("Contract Date:");
                activeDateLabel.setText("Active Date:");
            } else if ("Enlisted".equals(selectedType)) {
                personnelIdLabel.setText("Personnel ID:");
                officerIdLabel.setText("Enlist ID:");
                personnelNameLabel.setText("Personnel Name:");
                rankLabel.setText("Rank:");
                contractDateLabel.setText("Contract Date:");
                activeDateLabel.setText("Active Date:");
            }
        });

        TableView<ContractorsCivOfficers> tableView = new TableView<>();
        TableColumn<ContractorsCivOfficers, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        TableColumn<ContractorsCivOfficers, String> officerIdCol = new TableColumn<>("Officer ID");
        officerIdCol.setCellValueFactory(new PropertyValueFactory<>("officerId"));
        TableColumn<ContractorsCivOfficers, String> personnelNameCol = new TableColumn<>("Personnel Name");
        personnelNameCol.setCellValueFactory(new PropertyValueFactory<>("personnelName"));
        TableColumn<ContractorsCivOfficers, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<ContractorsCivOfficers, String> contractDateCol = new TableColumn<>("Contract Date");
        contractDateCol.setCellValueFactory(new PropertyValueFactory<>("contractDate"));
        TableColumn<ContractorsCivOfficers, String> activeDateCol = new TableColumn<>("Active Date");
        activeDateCol.setCellValueFactory(new PropertyValueFactory<>("activeDate"));

        tableView.getColumns().addAll(personnelIdCol, officerIdCol, personnelNameCol, rankCol, contractDateCol, activeDateCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String personnelId = personnelIdText.getText();
            String officerId = officerIdText.getText();
            String personnelName = personnelNameText.getText();
            String rank = rankText.getText();
            String contractDate = contractDatePicker.getValue().toString();
            String activeDate = activeDateDatePicker.getValue().toString();
            String personnelType = typeChoiceBox.getValue();

            ContractorsCivOfficers personnel = new ContractorsCivOfficers(personnelId, officerId, personnelName, rank, contractDate, activeDate, personnelType);

            if (personnelId.isEmpty() || personnelName.isEmpty() || rank.isEmpty() || contractDate.isEmpty() || activeDate.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Incomplete Personnel Data", "Please fill in all fields.");
                return;
            }

            savePersonnelToDatabase(personnel);

            ObservableList<ContractorsCivOfficers> updatedList = fetchPersonnelByTypeFromDatabase(personnelType);
            tableView.setItems(updatedList);

            personnelIdText.clear();
            officerIdText.clear();
            personnelNameText.clear();
            rankText.clear();
            contractDatePicker.getEditor().clear();
            activeDateDatePicker.getEditor().clear();
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            ContractorsCivOfficers selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                String personnelId = personnelIdText.getText();
                String officerId = officerIdText.getText();
                String personnelName = personnelNameText.getText();
                String rank = rankText.getText();
                String contractDate = contractDatePicker.getValue().toString();
                String activeDate = activeDateDatePicker.getValue().toString();
                String personnelType = selectedPersonnel.getPersonnelType();

                selectedPersonnel.setPersonnelId(personnelId);
                selectedPersonnel.setOfficerId(officerId);
                selectedPersonnel.setPersonnelName(personnelName);
                selectedPersonnel.setRank(rank);
                selectedPersonnel.setContractDate(contractDate);
                selectedPersonnel.setActiveDate(activeDate);

                updatePersonnelInDatabase(selectedPersonnel);

                ObservableList<ContractorsCivOfficers> updatedList = fetchPersonnelByTypeFromDatabase(personnelType);
                tableView.setItems(updatedList);

                personnelIdText.clear();
                officerIdText.clear();
                personnelNameText.clear();
                rankText.clear();
                contractDatePicker.getEditor().clear();
                activeDateDatePicker.getEditor().clear();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            ContractorsCivOfficers selectedPersonnel = tableView.getSelectionModel().getSelectedItem();
            if (selectedPersonnel != null) {
                deletePersonnelFromDatabase(selectedPersonnel);

                String personnelType = selectedPersonnel.getPersonnelType();
                ObservableList<ContractorsCivOfficers> updatedList = fetchPersonnelByTypeFromDatabase(personnelType);
                tableView.setItems(updatedList);
            }
        });

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                typeLabel, typeChoiceBox,
                personnelIdLabel, personnelIdText,
                officerIdLabel, officerIdText,
                personnelNameLabel, personnelNameText,
                rankLabel, rankText,
                contractDateLabel, contractDatePicker,
                activeDateLabel, activeDateDatePicker,
                buttonBox,
                tableView
        );

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void savePersonnelToDatabase(ContractorsCivOfficers personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String tableName;
            String insertColumns;
            String sequenceName;
            if ("Contractors".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".\"contractors\"";
                insertColumns = "(PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ContractDate, ACTIVE_DATE)";
                sequenceName = "contractors_seq";
            } else if ("Officer".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".officer";
                insertColumns = "(PERSONNEL_ID, OFFICER_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE)";
                sequenceName = "officer_seq";
            } else {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".enlisted";
                insertColumns = "(PERSONNEL_ID, ENLIST_ID, PERSONNEL_NAME, RANK, ACTIVE_DATE)";
                sequenceName = "enlisted_seq";
            }

            String sql = "INSERT INTO " + tableName + insertColumns + " VALUES (" +
                    "?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelId());

            if ("Contractors".equals(personnel.getPersonnelType())) {
                pstmt.setString(2, personnel.getOfficerId());
            } else {
                pstmt.setString(2, personnel.getPersonnelId());
            }

            pstmt.setString(3, personnel.getPersonnelName());
            pstmt.setString(4, personnel.getRank());

            if ("Contractors".equals(personnel.getPersonnelType())) {
                pstmt.setString(5, personnel.getContractDate());
            } else {
                pstmt.setString(5, personnel.getActiveDate());
            }

            pstmt.setString(6, personnel.getActiveDate());

            pstmt.executeUpdate();
            System.out.println("Personnel saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updatePersonnelInDatabase(ContractorsCivOfficers personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String tableName;
            String updateColumns;
            if ("Contractors".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".\"contractors\"";
                updateColumns = "OFFICER_ID = ?, PERSONNEL_NAME = ?, RANK = ?, ContractDate = ?, ACTIVE_DATE = ?";
            } else if ("Officer".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".officer";
                updateColumns = "OFFICER_ID = ?, PERSONNEL_NAME = ?, RANK = ?, ACTIVE_DATE = ?";
            } else {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".enlisted";
                updateColumns = "ENLIST_ID = ?, PERSONNEL_NAME = ?, RANK = ?, ACTIVE_DATE = ?";
            }

            String sql = "UPDATE " + tableName + " SET " + updateColumns + " WHERE PERSONNEL_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getOfficerId());
            pstmt.setString(2, personnel.getPersonnelName());
            pstmt.setString(3, personnel.getRank());

            if ("Contractors".equals(personnel.getPersonnelType())) {
                pstmt.setString(4, personnel.getContractDate());
            } else {
                pstmt.setString(4, personnel.getActiveDate());
            }

            pstmt.setString(5, personnel.getActiveDate());
            pstmt.setString(6, personnel.getPersonnelId());

            pstmt.executeUpdate();
            System.out.println("Personnel updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deletePersonnelFromDatabase(ContractorsCivOfficers personnel) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String tableName;
            if ("Contractors".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".\"contractors\"";
            } else if ("Officer".equals(personnel.getPersonnelType())) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".officer";
            } else {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".enlisted";
            }

            String sql = "DELETE FROM " + tableName + " WHERE PERSONNEL_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelId());
            pstmt.executeUpdate();
            System.out.println("Personnel deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<ContractorsCivOfficers> fetchPersonnelByTypeFromDatabase(String personnelType) {
        ObservableList<ContractorsCivOfficers> personnelList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String tableName;
            String idColumn;
            String officerIdColumn = "";
            if ("Contractors".equals(personnelType)) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".\"contractors\"";
                idColumn = "PERSONNEL_ID";
                officerIdColumn = "OFFICER_ID";
            } else if ("Officer".equals(personnelType)) {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".officer";
                idColumn = "PERSONNEL_ID";
            } else {
                tableName = "\"C4ISR PROJECT (BASIC) V2\".enlisted";
                idColumn = "PERSONNEL_ID";
                officerIdColumn = "ENLIST_ID";
            }

            String sql = "SELECT * FROM " + tableName;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String personnelId = rs.getString(idColumn);
                String officerId = "";
                if (!officerIdColumn.isEmpty()) {
                    officerId = rs.getString(officerIdColumn);
                }
                String personnelName = rs.getString("PERSONNEL_NAME");
                String rank = rs.getString("RANK");
                String contractDate = "";
                if ("Contractors".equals(personnelType)) {
                    contractDate = rs.getString("ContractDate");
                }
                String activeDate = rs.getString("ACTIVE_DATE");

                ContractorsCivOfficers personnel = new ContractorsCivOfficers(personnelId, officerId, personnelName, rank, contractDate, activeDate, personnelType);
                personnelList.add(personnel);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return personnelList;
    }
}
