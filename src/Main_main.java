import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Test
    @Timeout(5)
    @DisplayName("Should set system look and feel successfully")
    void setLookAndFeel_Success() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class)) {
            mockedUIManager.when(() -> UIManager.setLookAndFeel(anyString())).thenReturn(null);
            mockedUIManager.when(() -> UIManager.getSystemLookAndFeelClassName()).thenReturn("test.LookAndFeel");

            assertThatCode(() -> Main.main(new String[]{}))
                .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Should handle look and feel exception gracefully")
    void setLookAndFeel_Failure() {
        try (MockedStatic<UIManager> mockedUIManager = mockStatic(UIManager.class)) {
            mockedUIManager.when(() -> UIManager.getSystemLookAndFeelClassName()).thenReturn("invalid.class");
            mockedUIManager.when(() -> UIManager.setLookAndFeel(anyString()))
                .thenThrow(new ClassNotFoundException("Test exception"));

            assertThatCode(() -> Main.main(new String[]{}))
                .doesNotThrowAnyException();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "true, Database connection successful!",
        "false, Database connection failed!"
    })
    @DisplayName("Should handle database connection test results")
    void testDatabaseConnection(boolean connectionResult, String expectedOutput) {
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class)) {
            mockedDatabaseUtil.when(DatabaseUtil::testConnection).thenReturn(connectionResult);

            if (!connectionResult) {
                try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
                    Main.main(new String[]{});
                    mockedJOptionPane.verify(() -> 
                        JOptionPane.showMessageDialog(
                            null, 
                            "Database connection failed. Please check your configuration.", 
                            "Database Error", 
                            JOptionPane.ERROR_MESSAGE
                        )
                    );
                }
            } else {
                Main.main(new String[]{});
            }
        }
    }

    @Test
    @DisplayName("Should start GUI on EDT successfully")
    void startGUI_Success() {
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable r = invocation.getArgument(0);
                    r.run();
                    return null;
                });

            assertThatCode(() -> Main.main(new String[]{}))
                .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Should handle GUI startup exception")
    void startGUI_Failure() {
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable r = invocation.getArgument(0);
                    r.run();
                    return null;
                });

            try (MockedStatic<LoginFrame> mockedLogin = mockStatic(LoginFrame.class)) {
                mockedLogin.when(LoginFrame::new).thenThrow(new RuntimeException("Test exception"));

                try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
                    Main.main(new String[]{});
                    mockedJOptionPane.verify(() -> 
                        JOptionPane.showMessageDialog(
                            null, 
                            "Error starting application: Test exception", 
                            "Application Error", 
                            JOptionPane.ERROR_MESSAGE
                        )
                    );
                }
            }
        }
    }
}

// Jacoco configuration example (build.gradle snippet):
/*
jacoco {
    toolVersion = "0.8.7"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

test {
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}
*/