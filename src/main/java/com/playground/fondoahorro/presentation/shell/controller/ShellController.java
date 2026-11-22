package com.playground.fondoahorro.presentation.shell.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.playground.fondoahorro.presentation.loan.controller.LoanDetailController;
import com.playground.fondoahorro.presentation.loan.controller.LoanListController;
import com.playground.fondoahorro.presentation.dashboard.DashboardQuickAction;
import com.playground.fondoahorro.presentation.dashboard.controller.DashboardController;
import com.playground.fondoahorro.presentation.person.controller.PersonDetailController;
import com.playground.fondoahorro.presentation.person.controller.PersonListController;
import com.playground.fondoahorro.presentation.savings.controller.SavingDetailController;
import com.playground.fondoahorro.presentation.savings.controller.SavingListController;
import com.playground.fondoahorro.presentation.settlement.controller.SettlementDetailController;
import com.playground.fondoahorro.presentation.settlement.controller.SettlementListController;
import com.playground.fondoahorro.presentation.shared.Icons;
import com.playground.fondoahorro.presentation.shared.Icons.Icon;
import com.playground.fondoahorro.presentation.shared.SpringFXMLLoader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hosts the sidebar navigation and swaps the center content area.
 * Each nav handler currently shows a placeholder; later phases will replace
 * the placeholder with the real FXML view for that module.
 */
@Component
@Scope("prototype")
public class ShellController {

    private static final Logger log = LoggerFactory.getLogger(ShellController.class);
    private static final double NAV_ICON_SIZE = 17;
    private static final double SIDEBAR_EXPANDED_WIDTH = 224;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 68;

    @FXML
    private StackPane contentArea;
    @FXML
    private VBox sidebar;
    @FXML
    private Label sidebarTitle;
    @FXML
    private Button sidebarToggle;
    @FXML
    private ToggleButton dashboardNav;
    @FXML
    private ToggleButton personsNav;
    @FXML
    private ToggleButton savingsNav;
    @FXML
    private ToggleButton loansNav;
    @FXML
    private ToggleButton birthdaysNav;
    @FXML
    private ToggleButton settlementsNav;
    @FXML
    private ToggleButton movementsNav;
    @FXML
    private ToggleButton settingsNav;

    private final Map<ToggleButton, String> navigationLabels = new LinkedHashMap<>();
    private Timeline sidebarAnimation;
    private boolean sidebarCollapsed;

    @FXML
    private void initialize() {
        dashboardNav.setGraphic(Icons.node(Icon.DASHBOARD, NAV_ICON_SIZE));
        personsNav.setGraphic(Icons.node(Icon.PERSONS, NAV_ICON_SIZE));
        savingsNav.setGraphic(Icons.node(Icon.SAVINGS, NAV_ICON_SIZE));
        loansNav.setGraphic(Icons.node(Icon.LOANS, NAV_ICON_SIZE));
        birthdaysNav.setGraphic(Icons.node(Icon.BIRTHDAYS, NAV_ICON_SIZE));
        settlementsNav.setGraphic(Icons.node(Icon.SETTLEMENTS, NAV_ICON_SIZE));
        movementsNav.setGraphic(Icons.node(Icon.MOVEMENTS, NAV_ICON_SIZE));
        settingsNav.setGraphic(Icons.node(Icon.SETTINGS, NAV_ICON_SIZE));

        for (ToggleButton button : List.of(dashboardNav, personsNav, savingsNav, loansNav,
                birthdaysNav, settlementsNav, movementsNav, settingsNav)) {
            String label = button.getText();
            navigationLabels.put(button, label);
            button.setAccessibleText(label);
        }

        sidebar.setMinWidth(0);
        sidebar.setMaxWidth(Double.MAX_VALUE);
        sidebarToggle.setTooltip(new Tooltip("Contraer panel lateral"));

        loadDashboard();
    }

    @FXML
    private void toggleSidebar() {
        sidebarCollapsed = !sidebarCollapsed;
        if (sidebarAnimation != null) {
            sidebarAnimation.stop();
        }

        if (sidebarCollapsed) {
            if (!sidebar.getStyleClass().contains("collapsed")) {
                sidebar.getStyleClass().add("collapsed");
            }
            sidebarTitle.setVisible(false);
            sidebarTitle.setManaged(false);
            setNavigationLabelsCollapsed(true);
        }

        sidebarToggle.setText(sidebarCollapsed ? "›" : "‹");
        String toggleDescription = sidebarCollapsed ? "Expandir panel lateral" : "Contraer panel lateral";
        sidebarToggle.setAccessibleText(toggleDescription);
        sidebarToggle.setTooltip(new Tooltip(toggleDescription));

        double targetWidth = sidebarCollapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_EXPANDED_WIDTH;
        sidebarAnimation = new Timeline(new KeyFrame(Duration.millis(180),
                new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH)));
        sidebarAnimation.setOnFinished(event -> {
            if (!sidebarCollapsed) {
                sidebar.getStyleClass().remove("collapsed");
                sidebarTitle.setVisible(true);
                sidebarTitle.setManaged(true);
                setNavigationLabelsCollapsed(false);
            }
        });
        sidebarAnimation.play();
    }

    private void setNavigationLabelsCollapsed(boolean collapsed) {
        navigationLabels.forEach((button, label) -> {
            button.setText(collapsed ? "" : label);
            button.setTooltip(collapsed ? new Tooltip(label) : null);
        });
    }

    @FXML
    private void showDashboard() {
        loadDashboard();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/dashboard/dashboard.fxml"));
            Parent view = loader.load();
            DashboardController controller = loader.getController();
            controller.setOnQuickAction(this::handleDashboardQuickAction);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el dashboard", e);
            showPlaceholder("Dashboard");
        }
    }

    private void handleDashboardQuickAction(DashboardQuickAction action) {
        switch (action) {
            case PEOPLE -> {
                personsNav.setSelected(true);
                loadPersonList();
            }
            case MOVEMENTS -> {
                movementsNav.setSelected(true);
                showMovements();
            }
            case LOANS -> {
                loansNav.setSelected(true);
                showLoans();
            }
            case BIRTHDAYS -> {
                birthdaysNav.setSelected(true);
                showBirthdays();
            }
            case SETTLEMENTS -> {
                settlementsNav.setSelected(true);
                showSettlements();
            }
            case SETTINGS -> {
                settingsNav.setSelected(true);
                showSettings();
            }
        }
    }

    @FXML
    private void showPersons() {
        loadPersonList();
    }

    @FXML
    private void showSavings() {
        loadSavingList();
    }

    @FXML
    private void showLoans() {
        loadLoanList();
    }

    @FXML
    private void showBirthdays() {
        loadView("/fxml/birthday/birthday_list.fxml", "Cumpleaños");
    }

    @FXML
    private void showSettlements() {
        loadSettlementList();
    }

    @FXML
    private void showMovements() {
        loadView("/fxml/movement/movement_list.fxml", "Movimientos");
    }

    @FXML
    private void showSettings() {
        loadView("/fxml/settings/settings.fxml", "Configuración");
    }

    private void showPlaceholder(String section) {
        Label label = new Label(section + "\n\nEsta sección se implementará en una fase siguiente.");
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: -fx-text-muted;");
        StackPane.setAlignment(label, Pos.CENTER);
        contentArea.getChildren().setAll(label);
    }

    private void loadPersonList() {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/person/person_list.fxml"));
            Parent view = loader.load();
            PersonListController controller = loader.getController();
            controller.setOnPersonSelected(this::loadPersonDetail);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el listado de personas", e);
            showPlaceholder("Personas");
        }
    }

    private void loadPersonDetail(long personId) {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/person/person_detail.fxml"));
            Parent view = loader.load();
            PersonDetailController controller = loader.getController();
            controller.setOnBack(this::loadPersonList);
            controller.setOnLoanSelected(loanId -> loadLoanDetail(loanId, () -> loadPersonDetail(personId)));
            controller.setOnSettlementYearSelected((pid, year) -> loadSettlementDetail(pid, year, () -> loadPersonDetail(personId)));
            controller.setPersonId(personId);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el detalle de la persona {}", personId, e);
            showPlaceholder("Personas");
        }
    }

    private void loadSavingList() {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/savings/saving_list.fxml"));
            Parent view = loader.load();
            SavingListController controller = loader.getController();
            controller.setOnSavingSelected(this::loadSavingDetail);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el listado de ahorros", e);
            showPlaceholder("Ahorros");
        }
    }

    private void loadSavingDetail(long savingId) {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/savings/saving_detail.fxml"));
            Parent view = loader.load();
            SavingDetailController controller = loader.getController();
            controller.setOnBack(this::loadSavingList);
            controller.setSavingId(savingId);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el detalle del ahorro {}", savingId, e);
            showPlaceholder("Ahorros");
        }
    }

    private void loadLoanList() {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/loan/loan_list.fxml"));
            Parent view = loader.load();
            LoanListController controller = loader.getController();
            controller.setOnLoanSelected(loanId -> loadLoanDetail(loanId, this::loadLoanList));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el listado de préstamos", e);
            showPlaceholder("Préstamos");
        }
    }

    private void loadLoanDetail(long loanId, Runnable onBack) {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/loan/loan_detail.fxml"));
            Parent view = loader.load();
            LoanDetailController controller = loader.getController();
            controller.setOnBack(onBack);
            controller.setLoanId(loanId);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el detalle del préstamo {}", loanId, e);
            showPlaceholder("Préstamos");
        }
    }

    private void loadSettlementList() {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/settlement/settlement_list.fxml"));
            Parent view = loader.load();
            SettlementListController controller = loader.getController();
            controller.setOnPersonSelected((personId, year) -> loadSettlementDetail(personId, year, this::loadSettlementList));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el listado de liquidaciones", e);
            showPlaceholder("Liquidaciones");
        }
    }

    private void loadSettlementDetail(long personId, int year, Runnable onBack) {
        try {
            FXMLLoader loader = SpringFXMLLoader.create(getClass().getResource("/fxml/settlement/settlement_detail.fxml"));
            Parent view = loader.load();
            SettlementDetailController controller = loader.getController();
            controller.setOnBack(onBack);
            controller.setContext(personId, year);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar el detalle de la liquidación de la persona {} para {}", personId, year, e);
            showPlaceholder("Liquidaciones");
        }
    }

    private void loadView(String fxmlPath, String sectionNameForErrors) {
        try {
            Parent view = SpringFXMLLoader.create(getClass().getResource(fxmlPath)).load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            log.error("Error al cargar la vista {}", fxmlPath, e);
            showPlaceholder(sectionNameForErrors);
        }
    }
}
