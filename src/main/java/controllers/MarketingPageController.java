package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import services.EmailIntelligenceService;
import services.EmailIntelligenceServiceImpl;
import services.intelligence.models.EmailIntelligenceProfile;

public class MarketingPageController {
	
	@FXML
	private Pane LeftBar;
	
	@FXML
	private Button importButton;
	
	@FXML
	private Button analyzeButton;
	
	@FXML
	private WebView reportWebView;
	
	@FXML
	private ProgressBar analysisProgress;
	
	private EmailIntelligenceService intelligenceService;
	
	// Store imported emails
	private List<String> importedEmails = new ArrayList<>();
	
	@FXML
	public void initialize() throws IOException {
		// Import left bar
		FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
		AnchorPane leftbar = loader1.load();
		LeftBar.getChildren().add(leftbar);
		
		// Initialize intelligence service
		intelligenceService = new EmailIntelligenceServiceImpl();
		
		// Set up button actions
		importButton.setOnAction(event -> handleImportEmails());
		analyzeButton.setOnAction(event -> handleAnalyzeEmails());
		
		// Initially disable analyze button until emails are imported
		analyzeButton.setDisable(true);
		
		// Load initial message in WebView
		showWelcomeMessage();
	}
	
	@FXML
	public void handleImportEmails() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import Email List for Analysis");
		fileChooser.getExtensionFilters().add(
			new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv")
		);
		
		File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
		
		if (file != null) {
			try {
				importedEmails.clear();
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (!line.isEmpty()) {
							importedEmails.add(line);
						}
					}
				}
				
				if (importedEmails.isEmpty()) {
					showErrorMessage("No emails found in the file. Please select a file with email addresses.");
					analyzeButton.setDisable(true);
				} else {
					showImportSuccessMessage(importedEmails.size());
					analyzeButton.setDisable(false);
				}
			} catch (IOException e) {
				showErrorMessage("Failed to import emails: " + e.getMessage());
				analyzeButton.setDisable(true);
			}
		}
	}
	
	@FXML
	public void handleAnalyzeEmails() {
		if (importedEmails.isEmpty()) {
			showErrorMessage("No emails to analyze. Please import an email list first.");
			return;
		}
		
		// Show progress
		analysisProgress.setVisible(true);
		analyzeButton.setDisable(true);
		importButton.setDisable(true);
		
		// Perform analysis asynchronously
		intelligenceService.analyzeEmails(importedEmails)
			.thenAccept(profiles -> {
				Platform.runLater(() -> {
					String htmlReport = intelligenceService.generateHTMLReport(profiles);
					reportWebView.getEngine().loadContent(htmlReport);
					analysisProgress.setVisible(false);
					analyzeButton.setDisable(false);
					importButton.setDisable(false);
				});
			})
			.exceptionally(ex -> {
				Platform.runLater(() -> {
					showErrorMessage("Analysis failed: " + ex.getMessage());
					analysisProgress.setVisible(false);
					analyzeButton.setDisable(false);
					importButton.setDisable(false);
				});
				return null;
			});
	}
	
	private void showImportSuccessMessage(int emailCount) {
		String html = "<!DOCTYPE html>" +
					  "<html><head><style>" +
					  "body { font-family: Arial, sans-serif; padding: 40px; background: #f5f7fa; }" +
					  "h1 { color: #27ae60; }" +
					  "p { color: #7f8c8d; font-size: 16px; line-height: 1.6; }" +
					  "</style></head><body>" +
					  "<h1>Import Successful!</h1>" +
					  "<p>Successfully imported <strong>" + emailCount + " email(s)</strong> for analysis.</p>" +
					  "<p>Click the 'Analyze Emails' button to perform comprehensive email intelligence analysis.</p>" +
					  "</body></html>";
		reportWebView.getEngine().loadContent(html);
	}
	
	private void showWelcomeMessage() {
		String html = "<!DOCTYPE html>" +
					  "<html><head><style>" +
					  "body { font-family: Arial, sans-serif; padding: 40px; background: #f5f7fa; }" +
					  "h1 { color: #2c3e50; }" +
					  "p { color: #7f8c8d; font-size: 16px; line-height: 1.6; }" +
					  "</style></head><body>" +
					  "<h1>Email Intelligence Analysis</h1>" +
					  "<p>Click the 'Import Email List' button to import emails for analysis.</p>" +
					  "<p>The analysis will provide:</p>" +
					  "<ul>" +
					  "<li>Marketing persona segmentation</li>" +
					  "<li>Engagement level scoring</li>" +
					  "<li>Security risk assessment</li>" +
					  "<li>Digital footprint analysis</li>" +
					  "<li>Targeted marketing recommendations</li>" +
					  "</ul>" +
					  "</body></html>";
		reportWebView.getEngine().loadContent(html);
	}
	
	private void showErrorMessage(String message) {
		String html = "<!DOCTYPE html>" +
					  "<html><head><style>" +
					  "body { font-family: Arial, sans-serif; padding: 40px; background: #f5f7fa; }" +
					  "h1 { color: #e74c3c; }" +
					  "p { color: #7f8c8d; font-size: 16px; }" +
					  "</style></head><body>" +
					  "<h1>Error</h1>" +
					  "<p>" + message + "</p>" +
					  "</body></html>";
		reportWebView.getEngine().loadContent(html);
	}
}
