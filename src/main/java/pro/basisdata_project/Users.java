package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Users {

    private String userId;
    private String name;
    private String role;
    private int accessLevel;
    private long lastLogin;

    public Users(String userId, String name, String role, int accessLevel, long lastLogin) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.accessLevel = accessLevel;
        this.lastLogin = lastLogin;
    }

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
        ComboBox<Integer> accessLevelComboBox = new ComboBox<>();
        accessLevelComboBox.getItems().addAll(1, 2, 3, 4, 5);
        accessLevelComboBox.setValue(1); // Default value
        Label lastLoginLabel = new Label("Last Login:");
        DatePicker lastLoginPicker = new DatePicker();

        TableView<Users> tableView = new TableView<>();
        TableColumn<Users, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<Users, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Users, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<Users, Integer> accessLevelCol = new TableColumn<>("Access Level");
        accessLevelCol.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));
        TableColumn<Users, Long> lastLoginCol = new TableColumn<>("Last Login");
        lastLoginCol.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));

        tableView.getColumns().addAll(userIdCol, nameCol, roleCol, accessLevelCol, lastLoginCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String userId = userIdText.getText();
            String name = nameText.getText();
            String role = roleText.getText();
            int accessLevel = accessLevelComboBox.getValue();
            long lastLogin = lastLoginPicker.getValue().toEpochDay();

            Users user = new Users(userId, name, role, accessLevel, lastLogin);
            System.out.println("User Created: " + user.getUserId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%d,%d%n", userId, name, role, accessLevel, lastLogin));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new user to the table view
            tableView.getItems().add(user);

            // Clear input fields after adding user
            userIdText.clear();
            nameText.clear();
            roleText.clear();
            accessLevelComboBox.setValue(1); // Reset to default value
            lastLoginPicker.getEditor().clear();
        });

        vbox.getChildren().addAll(
                userIdLabel, userIdText, nameLabel, nameText,
                roleLabel, roleText, accessLevelLabel, accessLevelComboBox,
                lastLoginLabel, lastLoginPicker,
                tableView, createButton);

        return vbox;
    }
}
