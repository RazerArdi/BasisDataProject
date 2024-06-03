package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class Personnels {

    private int personnelId;
    private String name;
    private String rank;
    private String role;
    private String status;

    public Personnels(int personnelId, String name, String rank, String role, String status) {
        this.personnelId = personnelId;
        this.name = name;
        this.rank = rank;
        this.role = role;
        this.status = status;
    }

    public int getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(int personnelId) {
        this.personnelId = personnelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static VBox getPersonnelsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label rankLabel = new Label("Rank:");
        TextField rankText = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int personnelId = Integer.parseInt(personnelIdText.getText());
            String name = nameText.getText();
            String rank = rankText.getText();
            String role = roleText.getText();
            String status = statusText.getText();

            Personnels personnel = new Personnels(personnelId, name, rank, role, status);
            System.out.println("Personnel Created: " + personnel.getPersonnelId());
        });

        vbox.getChildren().addAll(personnelIdLabel, personnelIdText, nameLabel, nameText, rankLabel, rankText, roleLabel, roleText, statusLabel, statusText, createButton);

        return vbox;
    }
}
