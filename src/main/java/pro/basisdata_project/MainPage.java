package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainPage extends StackPane {

    private String accountName; // Menyimpan nama akun saat ini
    private Stage primaryStage;
    private Scene loginScene;

    public MainPage(String accountName, Stage primaryStage, Scene loginScene) {
        this.accountName = accountName;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;

        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #EEF5FF;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #DF591A;");
        topBar.setPadding(new Insets(10));

        Button homeButton = new Button("HOME");
        homeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 15;");

        // Add vertical separator next to the home button
        Separator homeSeparator = new Separator();
        homeSeparator.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Label systemLabel = new Label("SYSTEM");
        systemLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");

        Hyperlink accountLink = new Hyperlink(accountName); // Hyperlink for account name

        HBox.setMargin(homeButton, new Insets(0, 0, 0, 40));
        HBox.setMargin(homeSeparator, new Insets(0, 10, 0, 60));
        HBox.setMargin(accountLink, new Insets(0, 10, 0, 0));

        topBar.getChildren().addAll(homeButton, homeSeparator, systemLabel, accountLink);
        HBox.setHgrow(systemLabel, Priority.ALWAYS);
        systemLabel.setMaxWidth(Double.MAX_VALUE);
        systemLabel.setAlignment(Pos.CENTER);

        // Separator line below top bar
        Separator topSeparator = new Separator();

        // Sidebar
        VBox sidebar = new VBox();
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setStyle("-fx-background-color: #EEF5FF; -fx-font-size: 15;");
        sidebar.setPrefWidth(200);
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        // Create a ListView for the features
        ListView<String> featureList = new ListView<>();
        featureList.getItems().addAll(
                "Data", "Users", "Platforms", "Personnels", "Assignments",
                "Equipments", "Missions", "Maintenancelogs", "Analysis", "CommLog", "Sensors",
                "Land", "Sea", "Air", "Space"
        );

        // Set custom cell factory to adjust spacing between items
        featureList.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.setPadding(new Insets(10)); // Adjust padding as needed
                    setGraphic(label);
                }
            }
        });

        VBox.setVgrow(featureList, Priority.ALWAYS); // Ensure featureList expands vertically
        sidebar.getChildren().add(featureList);

        // Main content area (VBox instead of ListView)
        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 20;");
        VBox.setVgrow(mainContent, Priority.ALWAYS); // Ensure mainContent expands vertically

        Label welcomeLabel = new Label("Welcome to the Main Page");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: black;");
        mainContent.getChildren().add(welcomeLabel);

        // Add listener to feature list
        featureList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mainContent.getChildren().clear();
                switch (newValue) {
                    case "Users":
                        mainContent.getChildren().add(Users.getUsersUI());
                        break;
                    case "Sensors":
                        mainContent.getChildren().add(Sensors.getSensorsUI());
                        break;
                    case "Platforms":
                        mainContent.getChildren().add(Platforms.getPlatformsUI());
                        break;
                    case "Personnels":
                        mainContent.getChildren().add(Personnels.getPersonnelsUI());
                        break;
                    case "Maintenancelogs":
                        mainContent.getChildren().add(MaintenanceLogs.getMaintenanceLogsUI());
                        break;
                    case "CommLog":
                        mainContent.getChildren().add(CommunicationLog.getCommunicationLogUI());
                        break;
                    case "Analysis":
                        mainContent.getChildren().add(Analysis.getAnalysisUI());
                        break;
                    case "Assignments":
                        mainContent.getChildren().add(Assignments.getAssignmentsUI());
                        break;
                    case "Equipments":
                        mainContent.getChildren().add(Equipments.getEquipmentsUI());
                        break;
                    case "Missions":
                        mainContent.getChildren().add(Missions.getMissionsUI());
                        break;
                    case "Data":
                        mainContent.getChildren().add(Data.getDataUI());
                        break;
                    case "Land":
                        mainContent.getChildren().add(Land.getLandUI());
                        break;
                    case "Sea":
                        mainContent.getChildren().add(Sea.getSeaUI());
                        break;
                    case "Air":
                        mainContent.getChildren().add(Air.getAirUI());
                        break;
                    case "Space":
                        mainContent.getChildren().add(Space.getSpaceUI());
                        break;
                    default:
                        mainContent.getChildren().add(new Label("Select an option from the sidebar."));
                        break;
                }
            }
        });

        accountLink.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                // Create a context menu with options Profil and Log Out
                ContextMenu contextMenu = new ContextMenu();
                MenuItem profilItem = new MenuItem("Profil");
                MenuItem logOutItem = new MenuItem("Log Out");

                profilItem.setOnAction(event -> {
                    // Handle action for Profil option
                    // Load and display profile data for the current account from database
                    String profileData = loadProfileDataFromDatabase(accountName);
                    if (profileData != null) {
                        // Show profile data in a dialog or somewhere in the UI
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Profil");
                        alert.setHeaderText(null);
                        alert.setContentText(profileData);
                        alert.showAndWait();
                    } else {
                        showErrorDialog("Error loading profile data.");
                    }
                });

                logOutItem.setOnAction(event -> {
                    // Handle action for Log Out option
                    // Return to the login page
                    primaryStage.setScene(loginScene);
                });

                // Add menu items to the context menu
                contextMenu.getItems().addAll(profilItem, logOutItem);

                // Show context menu at the position of the mouse click relative to the accountLink
                contextMenu.show(accountLink, e.getScreenX(), e.getScreenY());
            }
        });


        homeButton.setOnAction(event -> {
            mainContent.getChildren().clear();
            Home home = new Home(); // Buat instance dari kelas Home
            mainContent.getChildren().add(home.getHomeUI()); // Gunakan instance untuk memanggil metode getHomeUI()
            featureList.getSelectionModel().clearSelection(); // Menghapus pemilihan dari ListView saat tombol HOME ditekan
        });

        // BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new VBox(topBar, topSeparator));
        borderPane.setLeft(sidebar);
        borderPane.setCenter(mainContent);

        // Set alignment constraints for the components
        BorderPane.setAlignment(topBar, Pos.TOP_CENTER);
        BorderPane.setAlignment(sidebar, Pos.TOP_LEFT);

        this.getChildren().add(borderPane); // Adding the BorderPane to the StackPane
    }

    // Method to load profile data from LOGIN table in the database
    private String loadProfileDataFromDatabase(String accountName) {
        String profileData = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = OracleAPEXConnection.getConnection(); // Get connection

            // SQL query to retrieve profile data based on accountName
            String sql = "SELECT NAME, EMAIL, USERNAME FROM \"C4ISR PROJECT (BASIC) V2\".LOGIN WHERE USERNAME = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("NAME");
                String email = rs.getString("EMAIL");
                String username = rs.getString("USERNAME");
                profileData = "Full Name: " + fullName + "\nUsername: " + username + "\nEmail: " + email;
            }
        } catch (SQLException ex) {
            showErrorDialog("Error loading profile data: " + ex.getMessage());
        } finally {
            // Close resources in a finally block to ensure they are always closed
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                showErrorDialog("Error closing database resources: " + ex.getMessage());
            }
        }

        return profileData;
    }

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

