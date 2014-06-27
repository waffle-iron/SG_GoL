package de.sydsoft.sg_gol.gui.javafx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

public class HelloDialog {
	// This dialog will consist of two input fields (username and password),
	 // and have two buttons: Login and Cancel.
	 
	 final TextField txUserName = new TextField();
	 final PasswordField txPassword = new PasswordField();
	 final Action actionLogin = new AbstractAction("Login") {
		 {  
	         ButtonBar.setType(this, ButtonType.OK_DONE); 
	     }
	       
	     // This method is called when the login button is clicked...
		@Override
		public void handle(ActionEvent ae) {
	          Dialog dlg = (Dialog) ae.getSource();
	          // real login code here
	          dlg.hide();
		}
	 };
	   
	 // This method is called when the user types into the username / password fields  
	 private void validate() {
	     actionLogin.disabledProperty().set( 
	           txUserName.getText().trim().isEmpty() || txPassword.getText().trim().isEmpty());
	 }
	   
	 // Imagine that this method is called somewhere in your codebase
	 private void showLoginDialog() {
	     Dialog dlg = new Dialog(null, "Login Dialog");
	       
	     // listen to user input on dialog (to enable / disable the login button)
	     ChangeListener<String> changeListener = new ChangeListener<String>() {
	         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	             validate();
	         }
	     };
	     txUserName.textProperty().addListener(changeListener);
	     txPassword.textProperty().addListener(changeListener);
	       
	     // layout a custom GridPane containing the input fields and labels
	     final GridPane content = new GridPane();
	     content.setHgap(10);
	     content.setVgap(10);
	       
	     content.add(new Label("User name"), 0, 0);
	     content.add(txUserName, 1, 0);
	     GridPane.setHgrow(txUserName, Priority.ALWAYS);
	     content.add(new Label("Password"), 0, 1);
	     content.add(txPassword, 1, 1);
	     GridPane.setHgrow(txPassword, Priority.ALWAYS);
	       
	     // create the dialog with a custom graphic and the gridpane above as the
	     // main content region
	     dlg.setResizable(false);
	     dlg.setIconifiable(false);
	     dlg.setGraphic(new ImageView(HelloDialog.class.getResource("login.png").toString()));
	     dlg.setContent(content);
	     dlg.getActions().addAll(actionLogin, Dialog.Actions.CANCEL);
	     validate();
	       
	     // request focus on the username field by default (so the user can
	     // type immediately without having to click first)
	     Platform.runLater(new Runnable() {
	         public void run() {
	             txUserName.requestFocus();
	         }
	     });
	
	     dlg.show();
	 }
	 
	 public static void main(String[] args) {
		new HelloDialog().showLoginDialog();
	}
}

