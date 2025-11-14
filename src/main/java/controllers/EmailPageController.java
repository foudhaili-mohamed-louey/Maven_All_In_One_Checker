package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import services.EmailCleaningServiceImpl;

public class EmailPageController {

    // ========== FXML Components ==========
    
    // TableView and Column
    @FXML
    private TableView<EmailRow> emailTableView;
    
    @FXML
    private TableColumn<EmailRow, String> emailColumn;
    
    // Action Buttons
    @FXML
    private Button btnImport;
    
    @FXML
    private Button btnRemoveImported;
    
    @FXML
    private Button btnExport;
    
    @FXML
    private Button btnRemoveDuplicates;
    
    @FXML
    private Button btnRemoveEmptyLines;
    
    @FXML
    private Button btnTrimNormalize;
    
    @FXML
    private Button btnRemoveWithoutAt;
    
    @FXML
    private Button btnRemoveMultipleAt;
    
    @FXML
    private Button btnRemoveInvalidChars;
    
    @FXML
    private Button btnRemoveRoleBased;
    
    @FXML
    private Button btnRemoveBotAdmin;
    
    @FXML
    private Button btnRemoveDisposable;
    
    @FXML
    private Button btnRemoveInvalidTLDs;
    
    @FXML
    private Button btnRemoveNonExistentDomains;
    
    @FXML
    private Button btnRemoveInvalidDomainFormat;
    
    @FXML
    private Button btnRemoveTooShortLong;
    
    @FXML
    private Button btnPassToChecking;
    
    // Statistics Text Fields
    @FXML
    private Text TXTEmailTotal;
    
    @FXML
    private Text TXTEmailDuplicated;
    
    @FXML
    private Text TXTEmptyLines;
    
    @FXML
    private Text TXTRoleBasedEmails;
    
    @FXML
    private Text TXTTrimmedNormalizedEmails;
    
    @FXML
    private Text TXTInvalidFakeTLDs;
    
    @FXML
    private Text TXTEmailsWithoutAtt;
    
    @FXML
    private Text TXTEmailMoreThanOneAtt;
    
    @FXML
    private Text TXTEmailWithInvalidCharacter;
    
    @FXML
    private Text TXTLongShortEmails;
    
    @FXML
    private Text TXTEmailsDisposable;
    
    @FXML
    private Text TXTInvalidDomainFormat;
    
    @FXML
    private Text TXTBotAdminEmails;
    
    @FXML
    private Text TXTNonExistantDomains;
    
    // Progress Components
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Text TXTprogressBar;
    
    @FXML
    private Pane LeftBar;
    
    // ========== Service and Data ==========
    
    private EmailCleaningServiceImpl emailService;
    private ObservableList<EmailRow> emailData;
    
    // ========== Initialization ==========
    
    @FXML
    public void initialize() throws IOException {
        emailService = new EmailCleaningServiceImpl();
        emailData = FXCollections.observableArrayList();
        
        // Configure TableView
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailTableView.setItems(emailData);
        
        // Initialize all statistics to 0
        resetStatistics();
        
        // Set initial progress bar state
        progressBar.setProgress(0.0);
        TXTprogressBar.setText("Ready");
        
        // Setup button actions
        setupButtonActions();
        
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
        AnchorPane leftbar = loader1.load();
        LeftBar.getChildren().add(leftbar);
    }
    
    // ========== Button Actions Setup ==========
    
    private void setupButtonActions() {
        btnImport.setOnAction(e -> handleImport());
        btnRemoveImported.setOnAction(e -> handleRemoveImported());
        btnExport.setOnAction(e -> handleExport());
        
        btnRemoveDuplicates.setOnAction(e -> handleRemoveDuplicates());
        btnRemoveEmptyLines.setOnAction(e -> handleRemoveEmptyLines());
        btnTrimNormalize.setOnAction(e -> handleTrimNormalize());
        btnRemoveWithoutAt.setOnAction(e -> handleRemoveWithoutAt());
        btnRemoveMultipleAt.setOnAction(e -> handleRemoveMultipleAt());
        btnRemoveInvalidChars.setOnAction(e -> handleRemoveInvalidChars());
        btnRemoveRoleBased.setOnAction(e -> handleRemoveRoleBased());
        btnRemoveBotAdmin.setOnAction(e -> handleRemoveBotAdmin());
        btnRemoveDisposable.setOnAction(e -> handleRemoveDisposable());
        btnRemoveInvalidTLDs.setOnAction(e -> handleRemoveInvalidTLDs());
        btnRemoveNonExistentDomains.setOnAction(e -> handleRemoveNonExistentDomains());
        btnRemoveInvalidDomainFormat.setOnAction(e -> handleRemoveInvalidDomainFormat());
        btnRemoveTooShortLong.setOnAction(e -> handleRemoveTooShortLong());
    }
    
    // ========== Import/Export Handlers ==========
    
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Email List");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(btnImport.getScene().getWindow());
        
        if (file != null) {
            executeTask(() -> {
                ObservableList<String> imported = emailService.importData(file);
                
                Platform.runLater(() -> {
                    emailData.clear();
                    for (String email : imported) {
                        emailData.add(new EmailRow(email));
                    }
                    updateTotalCount();
                    TXTprogressBar.setText("Import completed: " + emailData.size() + " emails");
                });
            });
        }
    }
    
    private void handleRemoveImported() {
        emailService.removeDataImported();
        emailData.clear();
        resetStatistics();
        TXTprogressBar.setText("Data cleared");
    }
    
    private void handleExport() {
        if (emailData.isEmpty()) {
            showAlert("No Data", "There are no emails to export!");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Cleaned Emails");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());
        
        if (file != null) {
            executeTask(() -> {
                try {
                    List<String> emailList = emailData.stream()
                        .map(EmailRow::getEmail)
                        .toList();
                    
                    boolean success = emailService.exportCleanedEmails(emailList, file);
                    
                    Platform.runLater(() -> {
                        if (success) {
                            TXTprogressBar.setText("Export successful!");
                            showAlert("Success", "Emails exported successfully to: " + file.getName());
                        } else {
                            TXTprogressBar.setText("Export failed!");
                            showAlert("Error", "Failed to export emails. Check file format.");
                        }
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        TXTprogressBar.setText("Export error!");
                        showAlert("Error", "Error exporting emails: " + ex.getMessage());
                    });
                }
            });
        }
    }
    
    // ========== Cleaning Function Handlers ==========
    
    private void handleRemoveDuplicates() {
        executeCleaningTask(
            emailService::removeDuplicates,
            TXTEmailDuplicated,
            "Duplicates removed"
        );
    }
    
    private void handleRemoveEmptyLines() {
        executeCleaningTask(
            emailService::removeEmptyLines,
            TXTEmptyLines,
            "Empty lines removed"
        );
    }
    
    private void handleTrimNormalize() {
        executeCleaningTask(
            emailService::trimAndNormalize,
            TXTTrimmedNormalizedEmails,
            "Emails normalized"
        );
    }
    
    private void handleRemoveWithoutAt() {
        executeCleaningTask(
            emailService::removeEmailsWithoutAt,
            TXTEmailsWithoutAtt,
            "Emails without @ removed"
        );
    }
    
    private void handleRemoveMultipleAt() {
        executeCleaningTask(
            emailService::removeEmailsWithMultipleAt,
            TXTEmailMoreThanOneAtt,
            "Emails with multiple @ removed"
        );
    }
    
    private void handleRemoveInvalidChars() {
        executeCleaningTask(
            emailService::removeInvalidCharacters,
            TXTEmailWithInvalidCharacter,
            "Invalid characters removed"
        );
    }
    
    private void handleRemoveRoleBased() {
        executeCleaningTask(
            emailService::removeRoleBasedEmails,
            TXTRoleBasedEmails,
            "Role-based emails removed"
        );
    }
    
    private void handleRemoveBotAdmin() {
        executeCleaningTask(
            emailService::removeAdminOrBotEmails,
            TXTBotAdminEmails,
            "Bot/Admin emails removed"
        );
    }
    
    private void handleRemoveDisposable() {
        executeCleaningTask(
            emailService::removeDisposableEmails,
            TXTEmailsDisposable,
            "Disposable emails removed"
        );
    }
    
    private void handleRemoveInvalidTLDs() {
        executeCleaningTask(
            emailService::removeInvalidOrFakeTLDs,
            TXTInvalidFakeTLDs,
            "Invalid TLDs removed"
        );
    }
    
    private void handleRemoveNonExistentDomains() {
        executeCleaningTask(
            emailService::removeNonExistentDomains,
            TXTNonExistantDomains,
            "Non-existent domains removed"
        );
    }
    
    private void handleRemoveInvalidDomainFormat() {
        executeCleaningTask(
            emailService::removeInvalidDomainFormat,
            TXTInvalidDomainFormat,
            "Invalid domain format removed"
        );
    }
    
    private void handleRemoveTooShortLong() {
        executeCleaningTask(
            emailService::removeTooShortOrTooLongEmails,
            TXTLongShortEmails,
            "Too short/long emails removed"
        );
    }
    
    // ========== Task Execution ==========
    
    private void executeCleaningTask(
        java.util.function.Function<List<String>, List<String>> cleaningFunction,
        Text statisticText,
        String completionMessage
    ) {
        executeTask(() -> {
            int beforeCount = emailData.size();
            
            List<String> currentEmails = emailData.stream()
                .map(EmailRow::getEmail)
                .toList();
            
            List<String> cleanedEmails = cleaningFunction.apply(currentEmails);
            
            int afterCount = cleanedEmails.size();
            int removedCount = beforeCount - afterCount;
            
            Platform.runLater(() -> {
                emailData.clear();
                for (String email : cleanedEmails) {
                    emailData.add(new EmailRow(email));
                }
                
                updateStatistic(statisticText, removedCount);
                updateTotalCount();
                TXTprogressBar.setText(completionMessage + ": " + removedCount);
            });
        });
    }
    
    private void executeTask(Runnable taskLogic) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                taskLogic.run();
                return null;
            }
        };
        
        // Disable all buttons during task execution
        task.setOnRunning(e -> {
            disableAllButtons(true);
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            TXTprogressBar.setText("Processing...");
        });
        
        // Re-enable buttons when task completes and STOP the progress bar animation
        task.setOnSucceeded(e -> {
            disableAllButtons(false);
            // Stop the indeterminate animation by setting a specific value
            progressBar.setProgress(1.0);
        });
        
        task.setOnFailed(e -> {
            disableAllButtons(false);
            // Stop the indeterminate animation by setting to 0
            progressBar.setProgress(0.0);
            TXTprogressBar.setText("Error occurred!");
            showAlert("Error", "An error occurred during processing.");
        });
        
        // Run task in background thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    // ========== Utility Methods ==========
    
    private void disableAllButtons(boolean disable) {
        btnImport.setDisable(disable);
        btnRemoveImported.setDisable(disable);
        btnExport.setDisable(disable);
        btnRemoveDuplicates.setDisable(disable);
        btnRemoveEmptyLines.setDisable(disable);
        btnTrimNormalize.setDisable(disable);
        btnRemoveWithoutAt.setDisable(disable);
        btnRemoveMultipleAt.setDisable(disable);
        btnRemoveInvalidChars.setDisable(disable);
        btnRemoveRoleBased.setDisable(disable);
        btnRemoveBotAdmin.setDisable(disable);
        btnRemoveDisposable.setDisable(disable);
        btnRemoveInvalidTLDs.setDisable(disable);
        btnRemoveNonExistentDomains.setDisable(disable);
        btnRemoveInvalidDomainFormat.setDisable(disable);
        btnRemoveTooShortLong.setDisable(disable);
        btnPassToChecking.setDisable(disable);
    }
    
    private void updateTotalCount() {
        TXTEmailTotal.setText(String.valueOf(emailData.size()));
    }
    
    private void updateStatistic(Text text, int value) {
        int currentValue = Integer.parseInt(text.getText());
        text.setText(String.valueOf(currentValue + value));
    }
    
    private void resetStatistics() {
        TXTEmailTotal.setText("0");
        TXTEmailDuplicated.setText("0");
        TXTEmptyLines.setText("0");
        TXTRoleBasedEmails.setText("0");
        TXTTrimmedNormalizedEmails.setText("0");
        TXTInvalidFakeTLDs.setText("0");
        TXTEmailsWithoutAtt.setText("0");
        TXTEmailMoreThanOneAtt.setText("0");
        TXTEmailWithInvalidCharacter.setText("0");
        TXTLongShortEmails.setText("0");
        TXTEmailsDisposable.setText("0");
        TXTInvalidDomainFormat.setText("0");
        TXTBotAdminEmails.setText("0");
        TXTNonExistantDomains.setText("0");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========== Email Row Class ==========
    
    public static class EmailRow {
        private String email;
        
        public EmailRow(String email) {
            this.email = email;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }
}
