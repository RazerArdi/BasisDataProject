package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
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

        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        Label nameLabel = new Label("Personnel Name:");
        TextField nameText = new TextField();
        Label positionLabel = new Label("Position:");
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

        // Handle the selection of the rank
        rankChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            Position selectedPosition = rankChoice.getItems().get(newValue.intValue());
            VBox additionalFields = getAdditionalFieldsForPosition(selectedPosition, activeDateLabel, activeDateText);
            vbox.getChildren().addAll(additionalFields.getChildren());
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
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
                writer.write(String.format("Personnel,%s,%s,%s,%s,%s,%s,%s%n", personnelId, personnelName, rank.name(), speciality, currentAssignment, contactInfo, activeDate));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(personnelIdLabel, personnelIdText, nameLabel, nameText, positionLabel, rankChoice, specialityLabel, specialityText, assignmentLabel, assignmentText, contactLabel, contactText, activeDateLabel, activeDateText, createButton);

        return vbox;
    }

    private static VBox getAdditionalFieldsForPosition(Position position, Label activeDateLabel, TextField activeDateText) {
        VBox additionalFields = new VBox();
        additionalFields.setSpacing(10);

        switch (position) {
            case OFFICER:
                additionalFields.getChildren().addAll(activeDateLabel, activeDateText);
                Button editActiveDateButtonOfficer = new Button("Edit Active Date");
                additionalFields.getChildren().add(editActiveDateButtonOfficer);
                editActiveDateButtonOfficer.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog(activeDateText.getText());
                    dialog.setTitle("Edit Active Date");
                    dialog.setHeaderText("Edit Active Date for Officer");
                    dialog.setContentText("New Active Date:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newActiveDate -> activeDateText.setText(newActiveDate));
                });
                break;

            case CIVILIAN_CONTRACTOR:
                additionalFields.getChildren().addAll(activeDateLabel, activeDateText);
                Button editActiveDateButtonCivilian = new Button("Edit Active Date");
                additionalFields.getChildren().add(editActiveDateButtonCivilian);
                editActiveDateButtonCivilian.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog(activeDateText.getText());
                    dialog.setTitle("Edit Active Date");
                    dialog.setHeaderText("Edit Active Date for Civilian/Contractor");
                    dialog.setContentText("New Active Date:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newActiveDate -> activeDateText.setText(newActiveDate));
                });
                break;

            case ENLISTED:
                additionalFields.getChildren().addAll(activeDateLabel, activeDateText);
                // No additional fields for enlisted personnel
                break;
        }

        return additionalFields;
    }
}
