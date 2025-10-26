package controllers;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import services.CheckerResult;
import services.CheckerService;
import services.CheckerServiceImpl;

public class CheckerPageController {
	
	@FXML
	private Pane LeftBar;
	
	@FXML
	private TextArea emailInputArea;
	
	@FXML
	private Button checkButton;
	
	@FXML
	private Button clearButton;
	
	@FXML
	private TableView<CheckerResult> resultsTableView;
	
	@FXML
	private TableColumn<CheckerResult, String> emailColumn;
	
	@FXML
	private TableColumn<CheckerResult, String> statusColumn;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Text progressText;
	
	@FXML
	private Text validCountText;
	
	@FXML
	private Text invalidCountText;
	
	private CheckerService checkerService;
	private ObservableList<CheckerResult> results = FXCollections.observableArrayList();
	
	@FXML
	public void initialize() throws IOException {
		// Import left bar
		FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
		AnchorPane leftbar = loader1.load();
		LeftBar.getChildren().add(leftbar);
		
		// Initialize checker service
		checkerService = new CheckerServiceImpl();
		
		// Setup UI components if they exist
		setupTableView();
		setupButtons();
		resetStatistics();
	}
	
	private void setupTableView() {
		if (resultsTableView != null && emailColumn != null && statusColumn != null) {
			emailColumn.setCellValueFactory(data -> 
				new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail())
			);
			statusColumn.setCellValueFactory(data -> 
				new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus())
			);
			resultsTableView.setItems(results);
		}
	}
	
	private void setupButtons() {
		if (checkButton != null) {
			checkButton.setOnAction(event -> handleCheck());
		}
		if (clearButton != null) {
			clearButton.setOnAction(event -> handleClear());
		}
	}
	
	private void handleCheck() {
		if (emailInputArea == null || emailInputArea.getText().trim().isEmpty()) {
			return;
		}
		
		// Parse emails from text area (one per line)
		String[] lines = emailInputArea.getText().split("\\r?\\n");
		List<String> emails = new ArrayList<>();
		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				emails.add(trimmed);
			}
		}
		
		if (emails.isEmpty()) {
			return;
		}
		
		disableButtons(true);
		
		Task<List<CheckerResult>> checkTask = new Task<List<CheckerResult>>() {
			@Override
			protected List<CheckerResult> call() throws Exception {
				updateProgress(0, emails.size());
				updateMessage("Initializing checker...");
				
				checkerService.initialize();
				
				List<CheckerResult> checkResults = new ArrayList<>();
				int processed = 0;
				
				for (String email : emails) {
					updateMessage("Checking: " + email);
					CheckerResult result = checkerService.checkEmail(email);
					checkResults.add(result);
					
					processed++;
					updateProgress(processed, emails.size());
				}
				
				checkerService.cleanup();
				updateMessage("Checking complete!");
				
				return checkResults;
			}
		};
		
		checkTask.setOnSucceeded(e -> {
			List<CheckerResult> checkResults = checkTask.getValue();
			results.setAll(checkResults);
			updateStatistics(checkResults);
			disableButtons(false);
			resetProgressBar();
		});
		
		checkTask.setOnFailed(e -> {
			checkerService.cleanup();
			disableButtons(false);
			resetProgressBar();
			System.err.println("Checking failed: " + checkTask.getException().getMessage());
		});
		
		if (progressBar != null) {
			progressBar.progressProperty().bind(checkTask.progressProperty());
		}
		if (progressText != null) {
			progressText.textProperty().bind(checkTask.messageProperty());
		}
		
		new Thread(checkTask).start();
	}
	
	private void handleClear() {
		if (emailInputArea != null) {
			emailInputArea.clear();
		}
		results.clear();
		resetStatistics();
		resetProgressBar();
	}
	
	private void disableButtons(boolean disable) {
		if (checkButton != null) {
			checkButton.setDisable(disable);
		}
		if (clearButton != null) {
			clearButton.setDisable(disable);
		}
	}
	
	private void resetProgressBar() {
		if (progressBar != null) {
			progressBar.progressProperty().unbind();
			progressBar.setProgress(0);
		}
		if (progressText != null) {
			progressText.textProperty().unbind();
			progressText.setText("Ready");
		}
	}
	
	private void updateStatistics(List<CheckerResult> checkResults) {
		int validCount = 0;
		int invalidCount = 0;
		
		for (CheckerResult result : checkResults) {
			if (result.isValid()) {
				validCount++;
			} else {
				invalidCount++;
			}
		}
		
		final int finalValidCount = validCount;
		final int finalInvalidCount = invalidCount;
		
		Platform.runLater(() -> {
			if (validCountText != null) {
				validCountText.setText(String.valueOf(finalValidCount));
			}
			if (invalidCountText != null) {
				invalidCountText.setText(String.valueOf(finalInvalidCount));
			}
		});
	}
	
	private void resetStatistics() {
		Platform.runLater(() -> {
			if (validCountText != null) {
				validCountText.setText("0");
			}
			if (invalidCountText != null) {
				invalidCountText.setText("0");
			}
		});
	}
}
