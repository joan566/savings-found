package com.playground.fondoahorro.presentation.dashboard.controller;

import com.playground.fondoahorro.domain.enums.DashboardPeriod;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.inputport.DashboardService;
import com.playground.fondoahorro.domain.vo.DashboardSnapshot;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.MonthlyFundContributions;
import com.playground.fondoahorro.domain.vo.MovementListItem;
import com.playground.fondoahorro.domain.vo.UpcomingBirthday;
import com.playground.fondoahorro.presentation.dashboard.DashboardQuickAction;
import com.playground.fondoahorro.presentation.shared.Badges;
import com.playground.fondoahorro.presentation.shared.Badges.BadgeStyle;
import com.playground.fondoahorro.presentation.shared.DateFormatter;
import com.playground.fondoahorro.presentation.shared.Icons;
import com.playground.fondoahorro.presentation.shared.Icons.Icon;
import com.playground.fondoahorro.presentation.shared.MoneyFormatter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DashboardController {

    private static final Color DIRECTION_ICON_COLOR_INCOME = Color.web("#16A34A");
    private static final Color DIRECTION_ICON_COLOR_EXPENSE = Color.web("#DC2626");

    @FXML
    private Label greetingLabel;
    @FXML
    private Label savingsHeaderLabel;
    @FXML
    private Label savingsIcon;
    @FXML
    private VBox savingsCard;
    @FXML
    private Label savingsTotalLabel;
    @FXML
    private Label savingsCashLabel;
    @FXML
    private Label savingsTransferLabel;
    @FXML
    private Label birthdayHeaderLabel;
    @FXML
    private Label birthdayIcon;
    @FXML
    private VBox birthdayCard;
    @FXML
    private Label birthdayTotalLabel;
    @FXML
    private Label birthdayCashLabel;
    @FXML
    private Label birthdayTransferLabel;
    @FXML
    private Label loansHeaderLabel;
    @FXML
    private Label loansIcon;
    @FXML
    private VBox loansCard;
    @FXML
    private Label loanedMoneyLabel;
    @FXML
    private Label outstandingDebtLabel;
    @FXML
    private Label activeLoanCountLabel;
    @FXML
    private Label personsHeaderLabel;
    @FXML
    private Label peopleIcon;
    @FXML
    private VBox peopleCard;
    @FXML
    private Label personCountLabel;
    @FXML
    private VBox birthdaysContainer;
    @FXML
    private VBox activityContainer;
    @FXML
    private Label birthdaysHeaderLabel;
    @FXML
    private Label activityHeaderLabel;
    @FXML
    private ComboBox<DashboardPeriod> periodSelector;
    @FXML
    private LineChart<String, Number> incomeChart;
    @FXML
    private NumberAxis chartYAxis;
    @FXML
    private Button peopleQuickLink;
    @FXML
    private Button movementQuickLink;
    @FXML
    private Button loansQuickLink;
    @FXML
    private Button birthdaysQuickLink;
    @FXML
    private Button settlementsQuickLink;
    @FXML
    private Button settingsQuickLink;

    private final DashboardService dashboardService;
    private Consumer<DashboardQuickAction> onQuickAction;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @FXML
    private void initialize() {
        greetingLabel.setText("¡" + greeting() + "! 👋");

        savingsCard.getStyleClass().add("summary-savings");
        birthdayCard.getStyleClass().add("summary-birthday");
        loansCard.getStyleClass().add("summary-loans");
        peopleCard.getStyleClass().add("summary-people");
        savingsTotalLabel.getStyleClass().add("dashboard-metric");
        birthdayTotalLabel.getStyleClass().add("dashboard-metric");
        outstandingDebtLabel.getStyleClass().add("value-expense");
        personCountLabel.getStyleClass().add("dashboard-person-count");

        savingsIcon.getStyleClass().add("summary-icon-blue");
        birthdayIcon.getStyleClass().add("summary-icon-green");
        loansIcon.getStyleClass().add("summary-icon-purple");
        peopleIcon.getStyleClass().add("summary-icon-orange");
        savingsIcon.setGraphic(Icons.node(Icon.SAVINGS, 20, Color.web("#2563EB")));
        birthdayIcon.setGraphic(Icons.node(Icon.BIRTHDAYS, 20, Color.web("#059669")));
        loansIcon.setGraphic(Icons.node(Icon.LOANS, 20, Color.web("#7C3AED")));
        peopleIcon.setGraphic(Icons.node(Icon.PERSONS, 20, Color.web("#EA580C")));
        birthdaysHeaderLabel.setGraphic(Icons.node(Icon.BIRTHDAYS, 16, Color.web("#2563EB")));
        activityHeaderLabel.setGraphic(Icons.node(Icon.MOVEMENTS, 16, Color.web("#2563EB")));

        periodSelector.setItems(FXCollections.observableArrayList(DashboardPeriod.values()));
        periodSelector.setValue(DashboardPeriod.LAST_6_MONTHS);
        periodSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refresh();
            }
        });

        incomeChart.setAnimated(false);
        chartYAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number cents) {
                return MoneyFormatter.format(Money.ofCents(cents.longValue()));
            }

            @Override
            public Number fromString(String text) {
                return MoneyFormatter.parse(text).map(Money::of).map(Money::toCents).orElse(0L);
            }
        });

        addQuickLinkStyle(peopleQuickLink, "quick-link-blue", Icon.PERSONS, "#2563EB");
        addQuickLinkStyle(movementQuickLink, "quick-link-orange", Icon.MOVEMENTS, "#EA580C");
        addQuickLinkStyle(loansQuickLink, "quick-link-green", Icon.LOANS, "#059669");
        addQuickLinkStyle(birthdaysQuickLink, "quick-link-purple", Icon.BIRTHDAYS, "#9333EA");
        addQuickLinkStyle(settlementsQuickLink, "quick-link-indigo", Icon.SETTLEMENTS, "#4F46E5");
        addQuickLinkStyle(settingsQuickLink, "quick-link-slate", Icon.SETTINGS, "#475569");

        refresh();
    }

    public void refresh() {
        DashboardSnapshot snapshot = dashboardService.getSnapshot(periodSelector.getValue());

        savingsTotalLabel.setText(MoneyFormatter.format(snapshot.fundBalances().savingsTotal()));
        savingsCashLabel.setText(MoneyFormatter.format(snapshot.fundBalances().savingsCash()));
        savingsTransferLabel.setText(MoneyFormatter.format(snapshot.fundBalances().savingsTransfer()));

        birthdayTotalLabel.setText(MoneyFormatter.format(snapshot.fundBalances().birthdayTotal()));
        birthdayCashLabel.setText(MoneyFormatter.format(snapshot.fundBalances().birthdayCash()));
        birthdayTransferLabel.setText(MoneyFormatter.format(snapshot.fundBalances().birthdayTransfer()));

        loanedMoneyLabel.setText(MoneyFormatter.format(snapshot.loanedMoney()));
        outstandingDebtLabel.setText(MoneyFormatter.format(snapshot.outstandingDebt()));
        activeLoanCountLabel.setText(String.valueOf(snapshot.activeLoanCount()));

        personCountLabel.setText(String.valueOf(snapshot.activePersonCount()));

        birthdaysContainer.getChildren().clear();
        if (snapshot.upcomingBirthdays().isEmpty()) {
            birthdaysContainer.getChildren().add(new Label("No hay próximos cumpleaños registrados."));
        } else {
            for (UpcomingBirthday item : snapshot.upcomingBirthdays()) {
                birthdaysContainer.getChildren().add(buildBirthdayRow(item));
            }
        }

        activityContainer.getChildren().clear();
        if (snapshot.recentActivity().isEmpty()) {
            activityContainer.getChildren().add(new Label("Aquí se mostrará la actividad más reciente."));
        } else {
            for (MovementListItem item : snapshot.recentActivity()) {
                activityContainer.getChildren().add(buildActivityRow(item));
            }
        }

        updateIncomeChart(snapshot.monthlyFundContributions());
    }

    public void setOnQuickAction(Consumer<DashboardQuickAction> onQuickAction) {
        this.onQuickAction = Objects.requireNonNull(onQuickAction);
    }

    private void updateIncomeChart(List<MonthlyFundContributions> contributions) {
        XYChart.Series<String, Number> savings = new XYChart.Series<>();
        savings.setName("Ahorros");
        XYChart.Series<String, Number> birthdays = new XYChart.Series<>();
        birthdays.setName("Cumpleaños");

        for (MonthlyFundContributions contribution : contributions) {
            String month = contribution.month().getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO"));
            String label = month.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-CO")) + month.substring(1);
            savings.getData().add(new XYChart.Data<>(label, contribution.savings().toCents()));
            birthdays.getData().add(new XYChart.Data<>(label, contribution.birthdays().toCents()));
        }

        incomeChart.getData().setAll(savings, birthdays);
    }

    @FXML
    private void openPeople() {
        runQuickAction(DashboardQuickAction.PEOPLE);
    }

    @FXML
    private void openMovements() {
        runQuickAction(DashboardQuickAction.MOVEMENTS);
    }

    @FXML
    private void openLoans() {
        runQuickAction(DashboardQuickAction.LOANS);
    }

    @FXML
    private void openBirthdays() {
        runQuickAction(DashboardQuickAction.BIRTHDAYS);
    }

    @FXML
    private void openSettlements() {
        runQuickAction(DashboardQuickAction.SETTLEMENTS);
    }

    @FXML
    private void openSettings() {
        runQuickAction(DashboardQuickAction.SETTINGS);
    }

    private void runQuickAction(DashboardQuickAction action) {
        Objects.requireNonNull(onQuickAction, "No se configuró la navegación rápida del dashboard.")
                .accept(action);
    }

    private static void addQuickLinkStyle(Button button, String styleClass, Icon icon, String color) {
        button.getStyleClass().add(styleClass);
        button.setGraphic(Icons.node(icon, 22, Color.web(color)));
    }

    private static String greeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Buenos días";
        } else if (hour < 19) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }

    private HBox buildBirthdayRow(UpcomingBirthday item) {
        Label name = new Label(item.person().name());
        name.setStyle("-fx-font-weight: bold;");
        Label date = new Label(DateFormatter.formatDayMonth(item.occurrenceDate()));
        date.getStyleClass().add("metric-label");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = item.giftGiven()
                ? Badges.of("Entregado", BadgeStyle.SUCCESS)
                : Badges.of("Pendiente", BadgeStyle.WARNING);

        HBox row = new HBox(10, name, date, spacer, status);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildActivityRow(MovementListItem item) {
        boolean isIncome = item.movement().kind() == MovementKind.INCOME;
        var icon = Icons.node(isIncome ? Icon.ARROW_UP : Icon.ARROW_DOWN, 14,
                isIncome ? DIRECTION_ICON_COLOR_INCOME : DIRECTION_ICON_COLOR_EXPENSE);
        VBox iconBox = new VBox(icon);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setStyle("-fx-background-color: " + (isIncome ? "-fx-success-subtle" : "-fx-danger-subtle") + "; "
                + "-fx-background-radius: 8; -fx-padding: 8; -fx-min-width: 30; -fx-min-height: 30;");

        Label category = new Label(item.movementTypeName());
        category.setStyle("-fx-font-weight: 600;");
        Label person = new Label(Objects.requireNonNullElse(item.personName(), "—"));
        person.getStyleClass().add("metric-label");
        VBox textBox = new VBox(1, category, person);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label date = new Label(relativeDate(item.movement().date()));
        date.getStyleClass().add("metric-label");
        Label value = new Label((isIncome ? "+" : "-") + MoneyFormatter.format(item.movement().amount()));
        value.getStyleClass().add(isIncome ? "value-income" : "value-expense");
        value.setStyle("-fx-font-weight: bold;");
        VBox valueBox = new VBox(1, date, value);
        valueBox.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(12, iconBox, textBox, spacer, valueBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0;");
        return row;
    }

    private static String relativeDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return "Hoy";
        }
        if (date.equals(today.minusDays(1))) {
            return "Ayer";
        }
        return DateFormatter.format(date);
    }
}
