package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Date;

public class Users {

    private String userId;
    private String name;
    private String role;
    private int accessLevel;
    private long lastLogin;

    // Constructor
    public Users(String userId, String name, String role, int accessLevel, long lastLogin) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.accessLevel = accessLevel;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Method to display the Users GUI
    public static VBox getUsersUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleText = new TextField();
        Label accessLevelLabel = new Label("Access Level:");
        TextField accessLevelText = new TextField();
        Label lastLoginLabel = new Label("Last Login:");
        TextField lastLoginText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String userId = userIdText.getText();
            String name = nameText.getText();
            String role = roleText.getText();
            int accessLevel = Integer.parseInt(accessLevelText.getText());
            long lastLogin = Date.parse(lastLoginText.getText());

            Users user = new Users(userId, name, role, accessLevel, lastLogin);
            System.out.println("User Created: " + user.getName());
        });

        vbox.getChildren().addAll(userIdLabel, userIdText, nameLabel, nameText, roleLabel, roleText, accessLevelLabel, accessLevelText, lastLoginLabel, lastLoginText, createButton);

        return vbox;
    }
}
