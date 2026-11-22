package com.playground.fondoahorro.presentation.shared;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;

import java.net.URL;

public final class SpringFXMLLoader {

    private static volatile ApplicationContext applicationContext;

    private SpringFXMLLoader() {
    }

    public static void initialize(ApplicationContext context) {
        applicationContext = context;
    }

    public static FXMLLoader create(URL location) {
        ApplicationContext context = applicationContext;
        if (context == null) {
            throw new IllegalStateException("El contexto de Spring todavía no se ha inicializado.");
        }

        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(context::getBean);
        return loader;
    }
}
