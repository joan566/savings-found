package com.playground.fondoahorro;

import com.playground.fondoahorro.presentation.shared.Dialogs;
import com.playground.fondoahorro.presentation.shared.SpringFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Exception startupError;
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        try {
            applicationContext = new SpringApplicationBuilder(FondoAhorroApplication.class)
                    .web(WebApplicationType.NONE)
                    .run();
            SpringFXMLLoader.initialize(applicationContext);
        } catch (Exception e) {
            // Caught here (rather than left to propagate) so start() can still
            // open a window and show a human message instead of the app dying
            // silently before any UI exists.
            startupError = e;
        }
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        if (startupError != null) {
            log.error("Error crítico al iniciar la aplicación", startupError);
            Dialogs.error("No fue posible iniciar",
                    "No fue posible iniciar la aplicación porque hubo un problema con la base de datos.\n"
                            + "Por favor cierra el programa e inténtalo de nuevo. Si el problema continúa, "
                            + "contacta a soporte técnico.");
            Platform.exit();
            return;
        }

        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/shell.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            primaryStage.setTitle("Fondo de Ahorro");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.show();

            log.info("Aplicación iniciada.");
        } catch (Exception e) {
            log.error("Error crítico al iniciar la aplicación", e);
            Dialogs.error("No fue posible iniciar",
                    "No fue posible abrir la ventana principal de la aplicación.\n"
                            + "Por favor cierra el programa e inténtalo de nuevo.");
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
