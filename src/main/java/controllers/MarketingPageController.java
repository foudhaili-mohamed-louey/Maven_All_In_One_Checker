package controllers;

import java.io.IOException;
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
import services.EmailIntelligenceService;
import services.EmailIntelligenceServiceImpl;
import services.intelligence.models.EmailIntelligenceProfile;

public class MarketingPageController {
	
	@FXML
	private Pane LeftBar;
	
	@FXML
	private Button analyzeButton;
	
	@FXML
	private WebView reportWebView;
	
	@FXML
	private ProgressBar analysisProgress;
	
	private EmailIntelligenceService intelligenceService;
	
	@FXML
	public void initialize() throws IOException {
		// Import left bar
		FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
		AnchorPane leftbar = loader1.load();
		LeftBar.getChildren().add(leftbar);
		
		// Initialize intelligence service
		intelligenceService = new EmailIntelligenceServiceImpl();
		
		// Set up button action
		analyzeButton.setOnAction(event -> handleAnalyzeEmails());
		
		// Load initial message in WebView
		showWelcomeMessage();
	}
	
	@FXML
	public void handleAnalyzeEmails() {
		// Get sample emails for demonstration
		List<String> emails = getSampleEmails();
		
		if (emails.isEmpty()) {
			showErrorMessage("No emails to analyze. Please add emails to analyze.");
			return;
		}
		
		// Show progress
		analysisProgress.setVisible(true);
		analyzeButton.setDisable(true);
		
		// Perform analysis asynchronously
		intelligenceService.analyzeEmails(emails)
			.thenAccept(profiles -> {
				Platform.runLater(() -> {
					String htmlReport = intelligenceService.generateHTMLReport(profiles);
					reportWebView.getEngine().loadContent(htmlReport);
					analysisProgress.setVisible(false);
					analyzeButton.setDisable(false);
				});
			})
			.exceptionally(ex -> {
				Platform.runLater(() -> {
					showErrorMessage("Analysis failed: " + ex.getMessage());
					analysisProgress.setVisible(false);
					analyzeButton.setDisable(false);
				});
				return null;
			});
	}
	
	/**
	 * Get sample emails for demonstration
	 * In a real implementation, this would retrieve cleaned emails from EmailCleaningService
	 */
	private List<String> getSampleEmails() {
		// Sample emails for demonstration
		return Arrays.asList(
			"john.doe@gmail.com",
			"jane.smith@techcorp.com",
			"developer@github.com",
			"contact@startup.io",
			"user123@yahoo.com"
		);
	}
	
	private void showWelcomeMessage() {
		String html = "<!DOCTYPE html>" +
					  "<html><head><style>" +
					  "body { font-family: Arial, sans-serif; padding: 40px; background: #f5f7fa; }" +
					  "h1 { color: #2c3e50; }" +
					  "p { color: #7f8c8d; font-size: 16px; line-height: 1.6; }" +
					  "</style></head><body>" +
					  "<h1>Email Intelligence Analysis</h1>" +
					  "<p>Click the 'Analyze Emails' button to perform comprehensive email intelligence analysis.</p>" +
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
