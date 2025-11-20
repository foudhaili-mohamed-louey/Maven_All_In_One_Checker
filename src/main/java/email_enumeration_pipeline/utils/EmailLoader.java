package email_enumeration_pipeline.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmailLoader {
  private final URI fileUri;
  private final File file;
  private final List<String> emailList = new ArrayList<>();

  public EmailLoader(URI fileUri) {
    this.fileUri = fileUri;
    this.file = new File(fileUri);
    loadEmails();
  }

  private void loadEmails() {
    try (Scanner myReader = new Scanner(file)) {
      while (myReader.hasNextLine()) {
        emailList.add(myReader.nextLine());
      }
    } catch (FileNotFoundException e) {
      System.err.println("An error occurred while reading the file: " + e.getMessage());
    }
  }

  public List<String> getEmailList() {
    return emailList;
  }
}
