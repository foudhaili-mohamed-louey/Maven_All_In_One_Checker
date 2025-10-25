package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class MarketingPageController {
	
	
	@FXML
	private Pane LeftBar;
	
	
	@FXML
	public void initialize() throws IOException
	{
	
	//import left bar
			FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/views/NavigationLeftBar.fxml"));
			AnchorPane leftbar = loader1.load();
			LeftBar.getChildren().add(leftbar);
	
	}



}
