package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import services.EmailCleaningService;
import services.EmailCleaningServiceImpl;
import services.CleaningResult;

public class EmailPageController {
    
    // Service instance
    private EmailCleaningService emailService = new EmailCleaningServiceImpl();
    
    // Store the imported file
    private File importedFile;
    
    // Current email list being worked on
    private ObservableList<String> currentEmails = FXCollections.observableArrayList();
    
    // FXML Components
    @FXML
    private Pane LeftBar;
    
    @FXML
    private TableView<String> emailTableView;
    
    @FXML
    private TableColumn<String, String> emailColumn;
    
    @FXML
    private Button importButton;
    
    @FXML
    private Button removeAllButton;
    
    @FXML
    private Button cleanListButton;
    
    @FXML
    private Button RemoveInvalidCharacters;
    
    @FXML
    private Button RemoveRoleBasedAdminBotEmails;
    
    @FXML
    private Button RemoveDisposableEmails;
    
    @FXML
    private Button RemoveInvalidTLDs;
    
    @FXML
    private Button RemoveNonExistantDomains;
    
    @FXML
    private Button exportButton;
    
    @FXML
    private Button passToCheckingButton;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Text progressText;
    
    // Statistics Text Fields
    @FXML
    private Text ResultatTotal;
    
    @FXML
    private Text ResultatEmailUncleaned;
    
    @FXML
    private Text ResultatEmailWithInvalidCharacters;
    
    @FXML
    private Text ResultatRoleBasedEmails;
    
    @FXML
    private Text ResultatDisposableEmails;
    
    @FXML
    private Text ResultatEmailsWithInvalidTLDs;
    
    @FXML
    private Text ResultatEmailsWithInvalidDomains;
    
    @FXML
    public void initialize() throws IOException {
        // Import left bar
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
        AnchorPane leftbar = loader1.load();
        LeftBar.getChildren().add(leftbar);
        
        setupTableView();
        setupButtonActions();
        resetAllStatistics();
    }
    
    private void setupTableView() {
        emailColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue())
        );
        emailTableView.setItems(currentEmails);
    }
    
    private void setupButtonActions() {
        importButton.setOnAction(event -> handleImport());
        removeAllButton.setOnAction(event -> handleRemoveAll());
        cleanListButton.setOnAction(event -> handleCleanList());
        RemoveInvalidCharacters.setOnAction(event -> handleRemoveInvalidCharacters());
        RemoveRoleBasedAdminBotEmails.setOnAction(event -> handleRemoveRoleBasedAdminBotEmails());
        RemoveDisposableEmails.setOnAction(event -> handleRemoveDisposableEmails());
        RemoveInvalidTLDs.setOnAction(event -> handleRemoveInvalidTLDs());
        RemoveNonExistantDomains.setOnAction(event -> handleRemoveNonExistantDomains());
        exportButton.setOnAction(event -> handleExport());
        passToCheckingButton.setOnAction(event -> handlePassToChecking());
    }
    
    // ================== BUTTON HANDLERS ==================
    
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Email List");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv")
        );
        
        importedFile = fileChooser.showOpenDialog(importButton.getScene().getWindow());
        
        if (importedFile != null) {
            Task<ObservableList<String>> importTask = new Task<ObservableList<String>>() {
                @Override
                protected ObservableList<String> call() throws Exception {
                    updateMessage("Importing...");
                    updateProgress(0, 100);
                    
                    ObservableList<String> imported = emailService.importData(importedFile);
                    
                    updateProgress(100, 100);
                    updateMessage("Import Complete");
                    return imported;
                }
            };
            
            importTask.setOnSucceeded(e -> {
                currentEmails.setAll(importTask.getValue());
                resetAllStatistics();
                updateTotalCount();    
                resetProgressBar();
            });
            
            importTask.setOnFailed(e -> {
                resetProgressBar();
            });
            
            progressBar.progressProperty().bind(importTask.progressProperty());
            progressText.textProperty().bind(importTask.messageProperty());
            
            new Thread(importTask).start();
        }
    }

    
    private void handleRemoveAll() {
        emailService.removeDataImported();
        currentEmails.clear();
        importedFile = null;
        resetAllStatistics();
        resetProgressBar();
    }
    
    private void handleCleanList() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> cleanTask = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                List<String> emails = currentEmails;
                int totalSteps = 5;
                int currentStep = 0;
                int totalRemoved = 0;
                
                int beforeCount = emails.size();
                
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing duplicates...");
                emails = emailService.removeDuplicates(emails);
                totalRemoved += (beforeCount - emails.size());
                beforeCount = emails.size();
                Thread.sleep(300);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing empty lines...");
                emails = emailService.removeEmptyLines(emails);
                totalRemoved += (beforeCount - emails.size());
                beforeCount = emails.size();
                Thread.sleep(300);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Trimming and normalizing...");
                emails = emailService.trimAndNormalize(emails);
                totalRemoved += (beforeCount - emails.size());
                beforeCount = emails.size();
                Thread.sleep(300);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing emails without @...");
                emails = emailService.removeEmailsWithoutAt(emails);
                totalRemoved += (beforeCount - emails.size());
                beforeCount = emails.size();
                Thread.sleep(300);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing emails with multiple @...");
                emails = emailService.removeEmailsWithMultipleAt(emails);
                totalRemoved += (beforeCount - emails.size());
                Thread.sleep(300);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Cleaning complete!");
                
                return new CleaningResult(emails, totalRemoved);
            }
        };
        
        cleanTask.setOnSucceeded(e -> {
            CleaningResult result = cleanTask.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatEmailUncleaned, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        cleanTask.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(cleanTask.progressProperty());
        progressText.textProperty().bind(cleanTask.messageProperty());
        
        new Thread(cleanTask).start();
    }
    
    private void handleRemoveInvalidCharacters() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> task = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                updateProgress(0, 1);
                updateMessage("Removing invalid characters...");
                
                int beforeCount = currentEmails.size();
                List<String> emails = emailService.removeInvalidCharacters(currentEmails);
                int removed = beforeCount - emails.size();
                Thread.sleep(300);
                
                updateProgress(1, 1);
                updateMessage("Complete!");
                
                return new CleaningResult(emails, removed);
            }
        };
        
        task.setOnSucceeded(e -> {
            CleaningResult result = task.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatEmailWithInvalidCharacters, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        task.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        
        new Thread(task).start();
    }
    
    private void handleRemoveRoleBasedAdminBotEmails() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> filterTask = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                List<String> emails = currentEmails;
                int totalSteps = 2;
                int currentStep = 0;
                int totalRemoved = 0;
                
                int beforeCount = emails.size();
                
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing role-based emails...");
                emails = emailService.removeRoleBasedEmails(emails);
                totalRemoved += (beforeCount - emails.size());
                beforeCount = emails.size();
                Thread.sleep(400);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Removing admin/bot emails...");
                emails = emailService.removeAdminOrBotEmails(emails);
                totalRemoved += (beforeCount - emails.size());
                Thread.sleep(400);
                
                currentStep++;
                updateProgress(currentStep, totalSteps);
                updateMessage("Filtering complete!");
                
                return new CleaningResult(emails, totalRemoved);
            }
        };
        
        filterTask.setOnSucceeded(e -> {
            CleaningResult result = filterTask.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatRoleBasedEmails, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        filterTask.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(filterTask.progressProperty());
        progressText.textProperty().bind(filterTask.messageProperty());
        
        new Thread(filterTask).start();
    }
    
    private void handleRemoveDisposableEmails() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> task = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                updateProgress(0, 1);
                updateMessage("Removing disposable emails...");
                
                int beforeCount = currentEmails.size();
                List<String> emails = emailService.removeDisposableEmails(currentEmails);
                int removed = beforeCount - emails.size();
                Thread.sleep(400);
                
                updateProgress(1, 1);
                updateMessage("Complete!");
                
                return new CleaningResult(emails, removed);
            }
        };
        
        task.setOnSucceeded(e -> {
            CleaningResult result = task.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatDisposableEmails, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        task.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        
        new Thread(task).start();
    }
    
    private void handleRemoveInvalidTLDs() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> task = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                updateProgress(0, 1);
                updateMessage("Removing invalid TLDs...");
                
                int beforeCount = currentEmails.size();
                List<String> emails = emailService.removeInvalidOrFakeTLDs(currentEmails);
                int removed = beforeCount - emails.size();
                Thread.sleep(500);
                
                updateProgress(1, 1);
                updateMessage("Complete!");
                
                return new CleaningResult(emails, removed);
            }
        };
        
        task.setOnSucceeded(e -> {
            CleaningResult result = task.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatEmailsWithInvalidTLDs, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        task.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        
        new Thread(task).start();
    }
    
    private void handleRemoveNonExistantDomains() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        disableButtons(true);
        
        Task<CleaningResult> task = new Task<CleaningResult>() {
            @Override
            protected CleaningResult call() throws Exception {
                updateProgress(0, 1);
                updateMessage("Checking domain existence...");
                
                int beforeCount = currentEmails.size();
                List<String> emails = emailService.removeNonExistentDomains(currentEmails);
                int removed = beforeCount - emails.size();
                
                updateProgress(1, 1);
                updateMessage("Complete!");
                
                return new CleaningResult(emails, removed);
            }
        };
        
        task.setOnSucceeded(e -> {
            CleaningResult result = task.getValue();
            currentEmails.setAll(result.getCleanedEmails());
            
            // Update statistics
            updateStatistic(ResultatEmailsWithInvalidDomains, result.getRemovedCount());
            updateTotalCount();
            
            disableButtons(false);
            resetProgressBar();
        });
        
        task.setOnFailed(e -> {
            disableButtons(false);
            resetProgressBar();
        });
        
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        
        new Thread(task).start();
    }
    
    private void handleExport() {
        if (currentEmails.isEmpty()) {
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Email List");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("cleaned_emails.txt");
        
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        
        if (file != null) {
            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Exporting...");
                    updateProgress(0, currentEmails.size());
                    
                    try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
                        int count = 0;
                        for (String email : currentEmails) {
                            writer.write(email);
                            writer.newLine();
                            count++;
                            updateProgress(count, currentEmails.size());
                        }
                    }
                    
                    updateMessage("Export complete!");
                    return null;
                }
            };
            
            exportTask.setOnSucceeded(e -> {
                resetProgressBar();
            });
            
            exportTask.setOnFailed(e -> {
                resetProgressBar();
            });
            
            progressBar.progressProperty().bind(exportTask.progressProperty());
            progressText.textProperty().bind(exportTask.messageProperty());
            
            new Thread(exportTask).start();
        }
    }
    
    private void handlePassToChecking() {
        System.out.println("Passing " + currentEmails.size() + " emails to checking...");
        // TODO: Implement navigation to checking page
    }
    
    // ================== HELPER METHODS ==================
    
    private void disableButtons(boolean disable) {
        importButton.setDisable(disable);
        removeAllButton.setDisable(disable);
        cleanListButton.setDisable(disable);
        RemoveInvalidCharacters.setDisable(disable);
        RemoveRoleBasedAdminBotEmails.setDisable(disable);
        RemoveDisposableEmails.setDisable(disable);
        RemoveInvalidTLDs.setDisable(disable);
        RemoveNonExistantDomains.setDisable(disable);
        exportButton.setDisable(disable);
        passToCheckingButton.setDisable(disable);
    }
    
    /**
     * Resets the progress bar to initial state.
     * IMPORTANT: Unbinds any existing bindings before resetting.
     */
    private void resetProgressBar() {
        // Unbind first to avoid "bound value cannot be set" error
        progressBar.progressProperty().unbind();
        progressText.textProperty().unbind();
        
        // Now safe to set values
        progressBar.setProgress(0);
        progressText.setText("Ready");
    }
    
    // ================== STATISTICS METHODS ==================
    
    /**
     * Updates the total count display (ResultatTotal)
     */
    private void updateTotalCount() {
        Platform.runLater(() -> {
            ResultatTotal.setText(String.valueOf(currentEmails.size()));
        });
    }
    
    /**
     * Updates a specific statistic text field by adding to its current value
     * 
     * @param textField The Text field to update
     * @param removedCount The number to add to the current value
     */
    private void updateStatistic(Text textField, int removedCount) {
        Platform.runLater(() -> {
            int currentValue = Integer.parseInt(textField.getText());
            int newValue = currentValue + removedCount;
            textField.setText(String.valueOf(newValue));
        });
    }
    
    /**
     * Resets all statistics to 0
     */
    private void resetAllStatistics() {
        Platform.runLater(() -> {
            ResultatEmailUncleaned.setText("0");
            ResultatEmailWithInvalidCharacters.setText("0");
            ResultatRoleBasedEmails.setText("0");
            ResultatDisposableEmails.setText("0");
            ResultatEmailsWithInvalidTLDs.setText("0");
            ResultatEmailsWithInvalidDomains.setText("0");
            ResultatTotal.setText("0");
        });
    }
}
