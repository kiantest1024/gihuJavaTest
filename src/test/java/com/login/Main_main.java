package com.login;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.*;

@ExtendWith(MockitoExtension.class)
class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Test main method with successful database connection")
    void testMain_successfulConnection_applicationStarts() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class);
             MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
             MockedStatic<SwingUtilities> mockedSwingUtilities = mockStatic(SwingUtilities.class)) {
            
            mockedDatabaseUtil.when(DatabaseUtil::testConnection).thenReturn(true);
            
            Main.main(new String[]{});
            
            mockedUIManager.verify(() -> UIManager.setLookAndFeel(anyString()));
            mockedDatabaseUtil.verify(DatabaseUtil::testConnection);
            assertTrue(outContent.toString().contains("Database connection successful!"));
            assertTrue(outContent.toString().contains("Login application started successfully!"));
        }
    }

    @Test
    @DisplayName("Test main method with failed database connection")
    void testMain_failedConnection_showsErrorMessageAndExits() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class);
             MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
             MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            
            mockedDatabaseUtil.when(DatabaseUtil::testConnection).thenReturn(false);
            
            Main.main(new String[]{});
            
            mockedDatabaseUtil.verify(DatabaseUtil::testConnection);
            assertTrue(outContent.toString().contains("Testing database connection..."));
            assertTrue(errContent.toString().contains("Database connection failed!"));
            mockedJOptionPane.verify(() -> 
                JOptionPane.showMessageDialog(
                    null,
                    "Database connection failed. Please check your configuration.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                )
            );
        }
    }

    @Test
    @DisplayName("Test main method with look and feel exception")
    void testMain_lookAndFeelException_printsErrorMessage() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class);
             MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class)) {
            
            mockedUIManager.when(() -> UIManager.setLookAndFeel(anyString()))
                .thenThrow(new Exception("Look and feel error"));
            mockedDatabaseUtil.when(DatabaseUtil::testConnection).thenReturn(true);
            
            Main.main(new String[]{});
            
            assertTrue(errContent.toString().contains("Could not set system look and feel: Look and feel error"));
            assertTrue(outContent.toString().contains("Database connection successful!"));
        }
    }

    @Test
    @DisplayName("Test main method with application startup exception")
    void testMain_applicationStartupException_showsErrorMessage() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class);
             MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
             MockedStatic<SwingUtilities> mockedSwingUtilities = mockStatic(SwingUtilities.class);
             MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            
            mockedDatabaseUtil.when(DatabaseUtil::testConnection).thenReturn(true);
            mockedSwingUtilities.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable r = invocation.getArgument(0);
                    r.run();
                    return null;
                });
            
            doThrow(new RuntimeException("Startup error")).when(mock(LoginFrame.class)).setVisible(true);
            
            Main.main(new String[]{});
            
            assertTrue(errContent.toString().contains("Error starting application: Startup error"));
            mockedJOptionPane.verify(() -> 
                JOptionPane.showMessageDialog(
                    null,
                    "Error starting application: Startup error",
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE
                )
            );
        }
    }
}