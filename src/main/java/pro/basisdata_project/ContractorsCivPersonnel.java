package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
            System.out.println("Personnel Created: " + personnel.getPersonnelId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s%n", personnelId, officerId, personnelName, rank, activeDate));
            } catch (IOException ex) {
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

        vbox.getChildren().addAll(
                personnelIdLabel, personnelIdText, officerIdLabel, officerIdText,
                personnelNameLabel, personnelNameText, rankLabel, rankText,
                activeDateLabel, activeDateText,
                tableView, createButton);

        return vbox;
    }
}

