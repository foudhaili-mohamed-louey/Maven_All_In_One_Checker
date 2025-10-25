package controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class NavigationLeftBarController {
	
	
	
	
	public void switchToEmailPage(MouseEvent event) throws IOException
	{
		System.out.println("switchToEmailPage works !!!");
		Parent root = FXMLLoader.load(getClass().getResource("/views/EmailPage.fxml"));
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		
	}
	
	public void switchToCheckerPage(MouseEvent event) throws IOException
	{
		System.out.println("switchToCheckerPage works !!!");
		Parent root = FXMLLoader.load(getClass().getResource("/views/CheckerPage.fxml"));
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		
	}
	
	public void switchToMarketingPage(MouseEvent event) throws IOException
	{
		System.out.println("switchToMarketingPage works !!!");
		Parent root = FXMLLoader.load(getClass().getResource("/views/MarketingPage.fxml"));
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void switchToAboutUSPage(MouseEvent event) throws IOException
	{
		System.out.println("switchToAboutUsPage works !!!");
		Parent root = FXMLLoader.load(getClass().getResource("/views/AboutUS.fxml"));
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}


}
