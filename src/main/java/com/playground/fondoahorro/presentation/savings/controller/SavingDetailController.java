package com.playground.fondoahorro.presentation.savings.controller;

import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.inputport.SavingService;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.presentation.shared.DateFormatter;
import com.playground.fondoahorro.presentation.shared.Dialogs;
import com.playground.fondoahorro.presentation.shared.Labels;
import com.playground.fondoahorro.presentation.shared.MoneyFormatter;
import com.playground.fondoahorro.presentation.shared.PersonInitials;
import com.playground.fondoahorro.presentation.shared.Toast;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope("prototype")
public class SavingDetailController {

    private static final Logger log = LoggerFactory.getLogger(SavingDetailController.class);

    @FXML
    private Label personInitialsLabel;
    @FXML
    private Label personLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label methodLabel;
    @FXML
    private Label notesLabel;
    @FXML
    private Button editButton;

    private final SavingService savingService;
    private final PersonService personService;
    private long savingId;
    private Runnable onBack;

    public SavingDetailController(SavingService savingService, PersonService personService) {
        this.savingService = savingService;
        this.personService = personService;
    }

    public void setSavingId(long savingId) {
        this.savingId = savingId;
        load();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void load() {
        Saving saving = savingService.findById(savingId)
                .orElseThrow(() -> new IllegalStateException("El ahorro no existe."));
        Person person = personService.findById(saving.personId())
                .orElseThrow(() -> new IllegalStateException("No se encontró la persona asociada al ahorro."));

        personInitialsLabel.setText(PersonInitials.fromName(person.name()));
        personLabel.setText(person.name());
        dateLabel.setText(DateFormatter.format(saving.date()));
        amountLabel.setText(MoneyFormatter.format(saving.amount()));
        methodLabel.setText(Labels.of(saving.paymentMethod()));
        notesLabel.setText(Objects.requireNonNullElse(saving.notes(), "Sin observaciones"));
    }

    @FXML
    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    @FXML
    private void handleEdit() {
        Saving saving = savingService.findById(savingId)
                .orElseThrow(() -> new IllegalStateException("El ahorro no existe."));
        SavingFormDialog.showForEdit(saving).ifPresent(input -> {
            editButton.setDisable(true);
            try {
                savingService.updateSaving(savingId, Money.of(input.amount()), input.date(), input.method(), input.notes());
                load();
                Toast.show(editButton.getScene().getWindow(), "Ahorro actualizado.");
            } catch (IllegalArgumentException e) {
                Dialogs.error(e.getMessage());
            } catch (Exception e) {
                log.error("Error al actualizar ahorro {}", savingId, e);
                Dialogs.error("Por ahora no fue posible actualizar el ahorro. Intenta nuevamente.");
            } finally {
                editButton.setDisable(false);
            }
        });
    }
}
