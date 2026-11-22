package com.playground.fondoahorro.presentation.savings.controller;

import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.inputport.SavingService;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.vo.SavingListItem;
import com.playground.fondoahorro.presentation.shared.DateFormatter;
import com.playground.fondoahorro.presentation.shared.Dialogs;
import com.playground.fondoahorro.presentation.shared.EmptyStates;
import com.playground.fondoahorro.presentation.shared.FilterOption;
import com.playground.fondoahorro.presentation.shared.Icons.Icon;
import com.playground.fondoahorro.presentation.shared.Labels;
import com.playground.fondoahorro.presentation.shared.MoneyFormatter;
import com.playground.fondoahorro.presentation.shared.Toast;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class SavingListController {

    private static final Logger log = LoggerFactory.getLogger(SavingListController.class);

    @FXML
    private ComboBox<FilterOption<Long>> personFilterCombo;
    @FXML
    private TableView<SavingListItem> table;
    @FXML
    private TableColumn<SavingListItem, String> personColumn;
    @FXML
    private TableColumn<SavingListItem, String> dateColumn;
    @FXML
    private TableColumn<SavingListItem, String> amountColumn;
    @FXML
    private TableColumn<SavingListItem, String> methodColumn;
    @FXML
    private TableColumn<SavingListItem, String> notesColumn;

    private final SavingService savingService;
    private final PersonService personService;
    private Consumer<Long> onSavingSelected;

    public SavingListController(SavingService savingService, PersonService personService) {
        this.savingService = savingService;
        this.personService = personService;
    }

    @FXML
    private void initialize() {
        personColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().personName()));
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(DateFormatter.format(data.getValue().saving().date())));
        amountColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(MoneyFormatter.format(data.getValue().saving().amount())));
        methodColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(Labels.of(data.getValue().saving().paymentMethod())));
        notesColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(Objects.requireNonNullElse(data.getValue().saving().notes(), "—")));

        table.setPlaceholder(EmptyStates.of(Icon.SAVINGS, "Aún no hay ahorros registrados",
                "Cuando registres un ahorro aparecerá aquí.", "+ Nuevo ahorro", this::handleNewSaving));

        table.setRowFactory(tv -> {
            TableRow<SavingListItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && onSavingSelected != null) {
                    onSavingSelected.accept(row.getItem().saving().id());
                }
            });
            return row;
        });

        List<FilterOption<Long>> personOptions = new ArrayList<>();
        personOptions.add(new FilterOption<>("Todas las personas", null));
        for (Person person : personService.list(null, true)) {
            personOptions.add(new FilterOption<>(person.name(), person.id()));
        }
        personFilterCombo.setItems(FXCollections.observableArrayList(personOptions));
        personFilterCombo.getSelectionModel().selectFirst();
        personFilterCombo.valueProperty().addListener((obs, o, n) -> refresh());

        refresh();
    }

    public void setOnSavingSelected(Consumer<Long> handler) {
        this.onSavingSelected = handler;
    }

    @FXML
    private void handleNewSaving() {
        SavingFormDialog.show().ifPresent(input -> {
            try {
                savingService.registerSaving(input.personId(), Money.of(input.amount()), input.date(), input.method(), input.notes());
                refresh();
                Toast.show(table.getScene().getWindow(), "Ahorro registrado.");
            } catch (IllegalArgumentException e) {
                Dialogs.error(e.getMessage());
            } catch (Exception e) {
                log.error("Error al registrar ahorro", e);
                Dialogs.error("Por ahora no fue posible registrar el ahorro. Intenta nuevamente.");
            }
        });
    }

    private void refresh() {
        FilterOption<Long> selected = personFilterCombo.getValue();
        Long personId = selected == null ? null : selected.value();
        table.setItems(FXCollections.observableArrayList(savingService.list(personId)));
    }
}
