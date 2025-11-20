package email_enumeration_pipeline_tests;

import org.junit.jupiter.api.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import email_enumeration_pipeline.utils.EmailLoader;

import static org.junit.jupiter.api.Assertions.*;

class EmailLoaderTest {

  private Path tempFile;

  @BeforeEach
  void setUp() throws IOException {
    System.out.println("▶ Setting up temporary email file...");
    tempFile = Files.createTempFile("emails", ".txt");
    try (FileWriter writer = new FileWriter(tempFile.toFile())) {
      writer.write("user1@example.com\n");
      writer.write("user2@example.com\n");
      writer.write("user3@example.com\n");
    }
    System.out.println("✔ Temporary file created at: " + tempFile.toAbsolutePath());
  }

  @AfterEach
  void tearDown() throws IOException {
    System.out.println("▶ Cleaning up temporary files...");
    Files.deleteIfExists(tempFile);
    System.out.println("✔ Temporary file deleted");
  }

  @Test
  void testEmailLoaderReadsAllLines() {
    System.out.println("▶ Running test: testEmailLoaderReadsAllLines");
    EmailLoader loader = new EmailLoader(tempFile.toUri());
    List<String> emails = loader.getEmailList();

    System.out.println("Asserting email list is not null...");
    assertNotNull(emails, "Email list should not be null");

    System.out.println("Asserting correct number of emails...");
    assertEquals(3, emails.size(), "Should load 3 emails");

    System.out.println("Asserting specific emails exist in the list...");
    assertTrue(emails.contains("user1@example.com"), "Missing user1@example.com");
    assertTrue(emails.contains("user2@example.com"), "Missing user2@example.com");
    assertTrue(emails.contains("user3@example.com"), "Missing user3@example.com");

    System.out.println("✅ testEmailLoaderReadsAllLines passed\n");
  }

  @Test
  void testEmailLoaderHandlesEmptyFile() throws IOException {
    System.out.println("▶ Running test: testEmailLoaderHandlesEmptyFile");
    Path emptyFile = Files.createTempFile("empty", ".txt");
    System.out.println("Created empty file at: " + emptyFile.toAbsolutePath());

    EmailLoader loader = new EmailLoader(emptyFile.toUri());
    List<String> emails = loader.getEmailList();

    System.out.println("Asserting email list is not null...");
    assertNotNull(emails, "Email list should not be null");

    System.out.println("Asserting list is empty for empty file...");
    assertTrue(emails.isEmpty(), "Empty file should result in empty list");

    Files.deleteIfExists(emptyFile);
    System.out.println("✔ testEmailLoaderHandlesEmptyFile passed\n");
  }
}
