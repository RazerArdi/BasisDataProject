package pro.basisdata_project;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

public class Login {

    private Button loginButton;
    private Scene loginScene, registerScene, forgotPasswordScene, changePasswordScene;
    private TextField emailField, passwordField;
    private Main mainApp;

    public Login(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initializeScenes(Stage stage) {
        createLoginScene(stage);
        createRegisterScene(stage);
        createForgotPasswordScene(stage);
        createChangePasswordScene(stage);
        stage.setScene(loginScene);
    }

    private void createLoginScene(Stage stage) {
        Image logo = new Image(Login.class.getResourceAsStream("Database.jpg"));
        ImageView gtradeLogoView = new ImageView(logo);
        gtradeLogoView.setFitWidth(200);
        gtradeLogoView.setFitHeight(150);
        VBox logo1 = new VBox();
        logo1.setAlignment(Pos.TOP_CENTER);
        logo1.getChildren().add(gtradeLogoView);

        Label helloLabel = new Label("Hello");
        helloLabel.setStyle("-fx-font-size: 50; -fx-text-fill: linear-gradient(to right, blue, red)");

        Label signInLabel = new Label("Sign in to your account");
        signInLabel.setStyle("-fx-font-size: 10");

        emailField = new TextField();
        emailField.setPromptText("Email/Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14; -fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(e -> handleLogin(stage, emailField.getText(), passwordField.getText()));

        Hyperlink registerLink = new Hyperlink("Register here!");
        registerLink.setOnAction(e -> stage.setScene(registerScene));

        Hyperlink forgotPasswordLink = new Hyperlink("Click Here!");
        forgotPasswordLink.setOnAction(e -> stage.setScene(forgotPasswordScene));

        TextFlow textFlow = new TextFlow(
                new Text("\t\t\t\t\t"),
                new Text("Don't have an Account? "),
                registerLink,
                new Text("\n"),
                new Text("\t\t\t\t\t"),
                new Text("Forgot your password? "),
                forgotPasswordLink
        );

        VBox loginLayout = new VBox(10);
        loginLayout.getChildren().addAll(logo1, helloLabel, signInLabel, emailField, passwordField, loginButton, textFlow);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setStyle("-fx-background-color: #EEF5FF; -fx-padding: 20;");

        loginButton.setPrefSize(150, 30);

        loginScene = new Scene(loginLayout, 600, 700);
    }

    private void createRegisterScene(Stage stage) {
        Label registerHelloLabel = new Label("Hello");
        registerHelloLabel.setStyle("-fx-font-size: 50; -fx-text-fill: linear-gradient(to right, blue, red)");

        Label registerLabel = new Label("Register for a new account");
        registerLabel.setStyle("-fx-font-size: 10");

        TextField registerNameField = new TextField();
        registerNameField.setPromptText("Name");

        TextField registerUsernameField = new TextField();
        registerUsernameField.setPromptText("Username");

        TextField registerEmailField = new TextField();
        registerEmailField.setPromptText("Email");

        PasswordField registerPasswordField = new PasswordField();
        registerPasswordField.setPromptText("Password");

        PasswordField confirmRegisterPasswordField = new PasswordField();
        confirmRegisterPasswordField.setPromptText("Confirm Password");

        Button registerSubmitButton = new Button("Register");
        registerSubmitButton.setOnAction(e -> handleRegistration(stage, registerNameField.getText(), registerUsernameField.getText(),
                registerEmailField.getText(), registerPasswordField.getText(), confirmRegisterPasswordField.getText()));

        Hyperlink backToLoginLink = new Hyperlink("Back to Log In");
        backToLoginLink.setOnAction(e -> stage.setScene(loginScene));

        VBox registerLayout = new VBox(20);
        registerLayout.getChildren().addAll(registerHelloLabel, registerLabel, registerNameField, registerUsernameField,
                registerEmailField, registerPasswordField, confirmRegisterPasswordField, registerSubmitButton, backToLoginLink);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.setStyle("-fx-background-color: #EEF5FF; -fx-padding: 20;");
        registerScene = new Scene(registerLayout, 600, 700);
    }

    private void createForgotPasswordScene(Stage stage) {
        Hyperlink backToLoginLink = new Hyperlink("Back to Log In");
        backToLoginLink.setOnAction(e -> stage.setScene(loginScene));

        Label forgotPasswordLabel = new Label("Enter Email Address");
        TextField forgotPasswordEmailField = new TextField();
        forgotPasswordEmailField.setPromptText("Email");

        Button submitForgotPasswordButton = new Button("Submit");
        submitForgotPasswordButton.setOnAction(e -> handleForgotPassword(stage, forgotPasswordEmailField.getText()));

        VBox forgotPasswordLayout = new VBox(5);
        forgotPasswordLayout.getChildren().addAll(backToLoginLink, forgotPasswordLabel, forgotPasswordEmailField, submitForgotPasswordButton);
        forgotPasswordLayout.setAlignment(Pos.CENTER);
        forgotPasswordLayout.setStyle("-fx-background-color: #EEF5FF; -fx-padding: 20;");

        forgotPasswordScene = new Scene(forgotPasswordLayout, 600, 700);
    }

    private void createChangePasswordScene(Stage stage) {
        Label changePasswordLabel = new Label("Change Password");
        changePasswordLabel.setStyle("-fx-font-size: 20");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmNewPasswordField = new PasswordField();
        confirmNewPasswordField.setPromptText("Confirm New Password");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> handleChangePassword(stage, newPasswordField.getText(), confirmNewPasswordField.getText()));

        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.getChildren().addAll(changePasswordLabel, newPasswordField, confirmNewPasswordField, changePasswordButton);
        changePasswordLayout.setAlignment(Pos.CENTER);
        changePasswordLayout.setStyle("-fx-background-color: #EEF5FF; -fx-padding: 20;");

        changePasswordScene = new Scene(changePasswordLayout, 600, 700);
    }

    private void handleLogin(Stage stage, String emailOrUsername, String password) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT NAME, EMAIL, USERNAME, PASSWORD FROM \"C4ISR PROJECT (BASIC) V2\".LOGIN WHERE (EMAIL = ? OR USERNAME = ?) AND PASSWORD = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedName = rs.getString("name");
                showSuccessDialog("Login successful!");

                mainApp.showMainScene(storedName, loginScene);
            } else {
                showErrorDialog("Invalid email/username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Error accessing the database.");
        }
    }

    private void handleRegistration(Stage stage, String fullName, String username, String email, String password, String confirmPassword) {
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Passwords do not match.");
            return;
        }

        if (!isValidEmail(email)) {
            showErrorDialog("Invalid email address.");
            return;
        }

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String fetchMaxIdSql = "SELECT NVL(MAX(LOGIN_ID), 0) + 1 AS NEW_ID FROM \"C4ISR PROJECT (BASIC) V2\".LOGIN";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(fetchMaxIdSql);
            rs.next();
            int newLoginId = rs.getInt("NEW_ID");

            String insertSql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".LOGIN (LOGIN_ID, NAME, USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, newLoginId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, username);
            pstmt.setString(4, email);
            pstmt.setString(5, password);

            pstmt.executeUpdate();
            showSuccessDialog("Registration successful!");
            stage.setScene(loginScene);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Error saving user data.");
        }
    }

    private void handleForgotPassword(Stage stage, String email) {
        if (isValidEmail(email)) {
            saveRequestData(email);
            showSuccessDialog("Request submitted. You will be contacted by customer service.");

            stage.setScene(changePasswordScene);
        } else {
            showErrorDialog("Invalid email address.");
        }
    }

    private void handleChangePassword(Stage stage, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            showErrorDialog("Passwords do not match.");
            return;
        }

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String updatePasswordSql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".LOGIN SET PASSWORD = ? WHERE EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(updatePasswordSql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, emailField.getText());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                showSuccessDialog("Password changed successfully!");
                stage.setScene(loginScene);
            } else {
                showErrorDialog("Failed to change password. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Error updating password.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private void saveRequestData(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("PermintaanPerubahanData.txt", true))) {
            writer.write(email);
            writer.newLine();
        } catch (IOException e) {
            showErrorDialog("Error saving request data.");
        }
    }

    private static void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
