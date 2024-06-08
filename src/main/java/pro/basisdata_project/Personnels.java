package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Personnels {

    public enum Position {
        OFFICER,
        ENLISTED,
        CIVILIAN_CONTRACTOR
    }

    private String personnelId;
    private String personnelName;
    private Position rank;
    private String speciality;
    private String currentAssignment;
    private String contactInfo;
    private String activeDate;

    public Personnels(String personnelId, String personnelName, Position rank, String speciality, String currentAssignment, String contactInfo, String activeDate) {
        this.personnelId = personnelId;
        this.personnelName = personnelName;
        this.rank = rank;
        this.speciality = speciality;
        this.currentAssignment = currentAssignment;
        this.contactInfo = contactInfo;
        this.activeDate = activeDate;
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

    public Position getRank() {
        return rank;
    }

    public void setRank(Position rank) {
        this.rank = rank;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
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

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public static VBox getPersonnelsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            result.ifPresent(analysisIdText::setText);
        });

        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        Label nameLabel = new Label("Personnel Name:");
        TextField nameText = new TextField();
        Label PositionLabel = new Label("Position:");
        ChoiceBox<Position> rankChoice = new ChoiceBox<>();
        rankChoice.getItems().addAll(Position.values());
        Label specialityLabel = new Label("Speciality:");
        TextField specialityText = new TextField();
        Label assignmentLabel = new Label("Current Assignment:");
        TextField assignmentText = new TextField();
        Label contactLabel = new Label("Contact Info:");
        TextField contactText = new TextField();
        Label activeDateLabel = new Label("Active Date:");
        TextField activeDateText = new TextField();

        TableView<Personnels> tableView = new TableView<>();
        TableColumn<Personnels, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        TableColumn<Personnels, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("personnelName"));
        TableColumn<Personnels, Position> rankCol = new TableColumn<>("Position");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<Personnels, String> specialityCol = new TableColumn<>("Speciality");
        specialityCol.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        TableColumn<Personnels, String> assignmentCol = new TableColumn<>("Current Assignment");
        assignmentCol.setCellValueFactory(new PropertyValueFactory<>("currentAssignment"));
        TableColumn<Personnels, String> contactCol = new TableColumn<>("Contact Info");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        TableColumn<Personnels, String> activeDateCol = new TableColumn<>("Active Date");
        activeDateCol.setCellValueFactory(new PropertyValueFactory<>("activeDate"));

        tableView.getColumns().addAll(personnelIdCol, nameCol, rankCol, specialityCol, assignmentCol, contactCol, activeDateCol);

        vbox.getChildren().add(tableView);

        rankChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == Position.OFFICER.ordinal()) {
                vbox.getChildren().add(createOfficerFields(activeDateLabel, activeDateText));
            } else if (newValue.intValue() == Position.CIVILIAN_CONTRACTOR.ordinal()) {
                vbox.getChildren().add(createCivilianContractorFields(activeDateLabel, activeDateText));
            } else {
                vbox.getChildren().removeAll(activeDateLabel, activeDateText);
            }
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            String personnelId = personnelIdText.getText();
            String personnelName = nameText.getText();
            Position rank = rankChoice.getValue();
            String speciality = specialityText.getText();
            String currentAssignment = assignmentText.getText();
            String contactInfo = contactText.getText();
            String activeDate = activeDateText.getText();

            Personnels personnel = new Personnels(personnelId, personnelName, rank, speciality, currentAssignment, contactInfo, activeDate);
            System.out.println("Personnel Created: " + personnel.getPersonnelId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Personnels,%s,%s,%s,%s,%s,%s,%s%n", analysisId, personnelId, personnelName, rank.name(), speciality, currentAssignment, contactInfo, activeDate));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, personnelIdLabel, personnelIdText, nameLabel, nameText, PositionLabel, rankChoice, specialityLabel, specialityText, assignmentLabel, assignmentText, contactLabel, contactText, activeDateLabel, activeDateText, createButton);

        return vbox;
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }

    private static VBox createOfficerFields(Label activeDateLabel, TextField activeDateText) {
        VBox officerFields = new VBox();
        officerFields.setSpacing(10);
        officerFields.getChildren().addAll(activeDateLabel, activeDateText);

        boolean isEditButtonAdded = false;

        Button editActiveDateButton = new Button("Edit Active Date");

        officerFields.getChildren().add(editActiveDateButton);
        isEditButtonAdded = true;

        editActiveDateButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(activeDateText.getText());
            dialog.setTitle("Edit Active Date");
            dialog.setHeaderText("Edit Active Date for Officer");
            dialog.setContentText("New Active Date:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newActiveDate -> activeDateText.setText(newActiveDate));
        });

        return officerFields;
    }

    private static VBox createCivilianContractorFields(Label activeDateLabel, TextField activeDateText) {
        VBox civilianContractorFields = new VBox();
        civilianContractorFields.setSpacing(10);
        civilianContractorFields.getChildren().addAll(activeDateLabel, activeDateText);

        boolean isEditButtonAdded = false;

        Button editActiveDateButton = new Button("Edit Active Date");

        civilianContractorFields.getChildren().add(editActiveDateButton);
        isEditButtonAdded = true;

        editActiveDateButton.setOnAction(event -> {
            String personelType = "Civilian/Contractor"; // Tambahkan variabel personelType
            TextInputDialog dialog = new TextInputDialog(activeDateText.getText());
            dialog.setTitle("Edit Active Date");
            dialog.setHeaderText("Edit Active Date for " + personelType);
            dialog.setContentText("New Active Date:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newActiveDate -> activeDateText.setText(newActiveDate));
        });

        return civilianContractorFields;
    }
}
