package com.playground.fondoahorro.integration;

import com.playground.fondoahorro.domain.inputport.BirthdayGiftService;
import com.playground.fondoahorro.domain.inputport.DashboardService;
import com.playground.fondoahorro.application.importing.dto.ImportExecutionException;
import com.playground.fondoahorro.application.importing.dto.ImportPlan;
import com.playground.fondoahorro.application.importing.service.ImportService;
import com.playground.fondoahorro.domain.inputport.LoanService;
import com.playground.fondoahorro.domain.inputport.MovementService;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.inputport.SavingService;
import com.playground.fondoahorro.domain.inputport.SettlementService;
import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.entity.LoanInterestCharge;
import com.playground.fondoahorro.domain.entity.LoanPayment;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.enums.Fund;
import com.playground.fondoahorro.domain.vo.FundBalances;
import com.playground.fondoahorro.domain.vo.DashboardSnapshot;
import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.vo.MonthlyFundContributions;
import com.playground.fondoahorro.domain.vo.MovementFilter;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.enums.DashboardPeriod;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.entity.MovementType;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.vo.PersonSummary;
import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.entity.Settlement;
import com.playground.fondoahorro.FondoAhorroApplication;
import com.playground.fondoahorro.infrastructure.adapters.DatabaseBackup;
import com.playground.fondoahorro.presentation.person.controller.PersonDetailController;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the financial rules that need a real database to prove: balances
 * derived from the movement ledger (section 42: "Saldos") and all-or-nothing
 * transactions (section 42: "Transacciones"). Runs against an isolated,
 * throwaway SQLite file under a JUnit temp directory — never the real
 * data/fondo-ahorro.db the application uses.
 */
class FinancialIntegrationTest {

    @TempDir
    static Path tempDir;

    private static Path databaseFile;
    private static ConfigurableApplicationContext context;
    private PersonService personService;
    private SavingService savingService;
    private LoanService loanService;
    private BirthdayGiftService birthdayGiftService;
    private MovementService movementService;
    private SettlementService settlementService;
    private ImportService importService;
    private MovementRepository movementRepository;
    private MovementTypeRepository movementTypeRepository;
    private DashboardService dashboardService;

    @BeforeAll
    static void initDatabase() {
        databaseFile = tempDir.resolve("created/by/spring/fondo-ahorro.db");
        context = new SpringApplicationBuilder(FondoAhorroApplication.class)
                .web(WebApplicationType.NONE)
                .run("--database.path=" + databaseFile,
                        "--spring.datasource.hikari.connection-init-sql=PRAGMA foreign_keys=ON");
    }

    @org.junit.jupiter.api.AfterAll
    static void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @BeforeEach
    void wireServices() {
        personService = context.getBean(PersonService.class);
        savingService = context.getBean(SavingService.class);
        loanService = context.getBean(LoanService.class);
        birthdayGiftService = context.getBean(BirthdayGiftService.class);
        movementService = context.getBean(MovementService.class);
        settlementService = context.getBean(SettlementService.class);
        importService = context.getBean(ImportService.class);
        movementRepository = context.getBean(MovementRepository.class);
        movementTypeRepository = context.getBean(MovementTypeRepository.class);
        dashboardService = context.getBean(DashboardService.class);
    }

    private static Money pesos(String value) {
        return Money.of(new BigDecimal(value));
    }

    @Test
    void javafxControllersAreCreatedBySpringWithPrototypeScope() {
        PersonDetailController first = context.getBean(PersonDetailController.class);
        PersonDetailController second = context.getBean(PersonDetailController.class);

        assertNotSame(first, second);

        for (String beanName : context.getBeanDefinitionNames()) {
            Class<?> beanType = context.getType(beanName);
            if (beanType != null
                    && beanType.getPackageName().startsWith("com.playground.fondoahorro.presentation.")
                    && beanType.getSimpleName().endsWith("Controller")) {
                context.getBean(beanName);
            }
        }
    }

    @Test
    void springCreatesTheDatabaseDirectoryAndRunsMigrations() {
        assertTrue(Files.isRegularFile(databaseFile));
        assertTrue(Files.isDirectory(databaseFile.getParent()));
    }

    @Test
    void databaseBackupCanCreateAndRestoreASqliteSnapshot() throws Exception {
        DatabaseBackup backup = context.getBean(DatabaseBackup.class);
        Path backupFile = tempDir.resolve("backups/fondo-ahorro.db");
        int peopleBefore = personService.list(null, true).size();

        backup.copyTo(backupFile);
        personService.createPerson("Test Restaurar Copia", LocalDate.of(1990, 1, 1), null);
        assertEquals(peopleBefore + 1, personService.list(null, true).size());

        backup.restoreFrom(backupFile);

        assertEquals(peopleBefore, personService.list(null, true).size());
    }

    @Test
    void fundBalancesAreDerivedFromIncomeMinusExpensePerFundAndMethod() {
        // Deltas, not absolute totals: other @Test methods in this class share the
        // same database (only @BeforeAll initializes it), so an assertion on the
        // absolute balance would be polluted by whichever tests already ran.
        FundBalances before = movementService.getBalances();

        Person person = personService.createPerson("Test Saldos", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("500000"), LocalDate.now(), PaymentMethod.CASH, null);
        savingService.registerSaving(person.id(), pesos("300000"), LocalDate.now(), PaymentMethod.TRANSFER, null);
        loanService.createLoan(person.id(), pesos("100000"), 300, LocalDate.now(), PaymentMethod.CASH, null);

        FundBalances after = movementService.getBalances();

        assertEquals(pesos("400000"), after.savingsCash().minus(before.savingsCash()),
                "500.000 ahorrado - 100.000 prestado, ambos en efectivo");
        assertEquals(pesos("300000"), after.savingsTransfer().minus(before.savingsTransfer()));
        assertEquals(pesos("700000"), after.savingsTotal().minus(before.savingsTotal()));
    }

    @Test
    void loanPaymentPersistsTheUpdatedBalanceAndItsMovementTogether() {
        Person person = personService.createPerson("Test Transaccion Commit", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("100000"), LocalDate.now(), PaymentMethod.CASH, null);
        Loan loan = loanService.createLoan(person.id(), pesos("100000"), 300, LocalDate.now(), PaymentMethod.CASH, null);

        loanService.registerPayment(loan.id(), pesos("50000"), LocalDate.now(), PaymentMethod.CASH, null);

        Loan reloaded = loanService.findById(loan.id()).orElseThrow();
        assertEquals(pesos("50000"), reloaded.paidAmount());

        int movementsForPerson = movementService.list(new MovementFilter(null, null, null, null, null, person.id(), null)).size();
        assertEquals(3, movementsForPerson, "el aporte, el desembolso y el pago deben quedar juntos, todos registrados");
    }

    @Test
    void readingAnOverdueLoanGeneratesItsPendingInterestCharges() {
        Person person = personService.createPerson("Test Interes Vencido", LocalDate.of(1990, 1, 1), null);
        LocalDate loanDate = LocalDate.now().minusMonths(3);
        savingService.registerSaving(person.id(), pesos("200000"), loanDate, PaymentMethod.CASH, null);
        Loan loan = loanService.createLoan(person.id(), pesos("100000"), 300, loanDate, PaymentMethod.CASH, null);

        // findById is a read, but with monthly-on-balance interest and no
        // background scheduler, reading a stale loan is what catches it up —
        // this proves that side effect actually fires instead of staying implicit.
        Loan reloaded = loanService.findById(loan.id()).orElseThrow();
        List<LoanInterestCharge> charges = loanService.chargesForLoan(loan.id());

        assertTrue(charges.size() >= 1, "at least one monthly charge should already be overdue");
        assertEquals(pesos("100000"), reloaded.principalBalance(), "accrual never touches principal");
        assertEquals(pesos("3000").multiply(BigDecimal.valueOf(charges.size())), reloaded.interestOwed(),
                "3% of the untouched 100000 balance per elapsed month, not capitalized");
    }

    @Test
    void aBackdatedPaymentIsAllocatedOnlyAgainstInterestDueByItsOwnDateNotByToday() {
        Person person = personService.createPerson("Test Pago Retroactivo", LocalDate.of(1990, 1, 1), null);
        LocalDate loanDate = LocalDate.now().minusMonths(2);
        savingService.registerSaving(person.id(), pesos("200000"), loanDate, PaymentMethod.CASH, null);
        Loan loan = loanService.createLoan(person.id(), pesos("100000"), 300, loanDate, PaymentMethod.CASH, null);

        // Dated exactly one month after the loan, when only the first month's
        // 3000 interest charge is due — not the second month's, which is only
        // due "today". If accrual wrongly ran up to today before allocating
        // this payment, it would compete against interest that hadn't accrued yet.
        LocalDate firstPaymentDate = loanDate.plusMonths(1);
        LoanPayment payment = loanService.registerPayment(loan.id(), pesos("3000"), firstPaymentDate, PaymentMethod.CASH, null);

        assertEquals(pesos("3000"), payment.interestPortion());
        assertEquals(Money.ZERO, payment.principalPortion());

        Loan reloadedToday = loanService.findById(loan.id()).orElseThrow();
        assertEquals(pesos("3000"), reloadedToday.interestOwed(),
                "the second month's charge should still be pending after only the first month's interest was paid");
        assertEquals(pesos("100000"), reloadedToday.principalBalance());
    }

    @Test
    void aTransactionThatFailsMidwayLeavesNoPartialWrite() {
        Person person = personService.createPerson("Test Rollback", LocalDate.of(1990, 1, 1), null);
        MovementType type = movementTypeRepository.findByCode("SAVINGS_CONTRIBUTION").orElseThrow();

        int before = movementService.list(MovementFilter.empty()).size();

        TransactionTemplate transactions = new TransactionTemplate(
                context.getBean(PlatformTransactionManager.class));
        assertThrows(RuntimeException.class, () -> transactions.execute(status -> {
            Movement movement = Movement.create(type.id(), Fund.SAVINGS, PaymentMethod.CASH, MovementKind.INCOME,
                    pesos("999999"), LocalDate.now(), person.id(), null, null, "esto debe revertirse");
            movementRepository.insert(movement);
            throw new RuntimeException("fallo simulado a mitad de la transacción");
        }));

        int after = movementService.list(MovementFilter.empty()).size();
        assertEquals(before, after, "el movimiento insertado antes del fallo no debió persistirse");
    }

    @Test
    void overpaymentIsRejectedBeforeAnyDatabaseWrite() {
        Person person = personService.createPerson("Test Sobrepago End To End", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("100000"), LocalDate.now(), PaymentMethod.CASH, null);
        Loan loan = loanService.createLoan(person.id(), pesos("100000"), 300, LocalDate.now(), PaymentMethod.CASH, null);

        assertThrows(IllegalArgumentException.class, () ->
                loanService.registerPayment(loan.id(), pesos("999999"), LocalDate.now(), PaymentMethod.CASH, null));

        Loan stillUnpaid = loanService.findById(loan.id()).orElseThrow();
        assertEquals(Money.ZERO, stillUnpaid.paidAmount(), "el intento rechazado no debió modificar el préstamo");
    }

    @Test
    void loanDisbursementIsRejectedWhenSavingsFundHasInsufficientBalance() {
        Person person = personService.createPerson("Test Saldo Insuficiente Prestamo", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("50000"), LocalDate.now(), PaymentMethod.CASH, null);

        int before = movementService.list(new MovementFilter(null, null, null, null, null, person.id(), null)).size();

        assertThrows(IllegalArgumentException.class, () ->
                loanService.createLoan(person.id(), pesos("999999999"), 300, LocalDate.now(), PaymentMethod.CASH, null));

        int after = movementService.list(new MovementFilter(null, null, null, null, null, person.id(), null)).size();
        assertEquals(before, after, "el desembolso rechazado no debió escribir ningún movimiento");
    }

    @Test
    void birthdayGiftIsRejectedWhenBirthdayFundHasInsufficientBalance() {
        Person person = personService.createPerson("Test Saldo Insuficiente Cumpleanos", LocalDate.of(1990, 1, 1), null);

        assertThrows(IllegalArgumentException.class, () ->
                birthdayGiftService.registerGift(person.id(), LocalDate.now(), pesos("999999999"), PaymentMethod.CASH, null));

        int movementsForPerson = movementService.list(new MovementFilter(null, null, null, null, null, person.id(), null)).size();
        assertEquals(0, movementsForPerson, "el regalo rechazado no debió escribir ningún movimiento");
    }

    @Test
    void editingASavingUpdatesTheSavingAndItsPairedMovementTogether() {
        Person person = personService.createPerson("Test Editar Ahorro", LocalDate.of(1990, 1, 1), null);
        Saving saving = savingService.registerSaving(person.id(), pesos("100000"), LocalDate.now(), PaymentMethod.CASH, "nota original");

        Saving updated = savingService.updateSaving(saving.id(), pesos("150000"), LocalDate.now(), PaymentMethod.TRANSFER, "nota corregida");

        assertEquals(pesos("150000"), updated.amount());
        assertEquals(PaymentMethod.TRANSFER, updated.paymentMethod());
        assertEquals("nota corregida", updated.notes());

        Movement movement = movementRepository.findByReference("savings", saving.id()).orElseThrow();
        assertEquals(pesos("150000"), movement.amount());
        assertEquals(PaymentMethod.TRANSFER, movement.paymentMethod());
        assertEquals("nota corregida", movement.notes());
    }

    @Test
    void reducingASavingBelowWhatWasAlreadyLentAgainstItIsRejected() {
        Person saver = personService.createPerson("Test Ahorrador Reduccion", LocalDate.of(1990, 1, 1), null);
        Saving saving = savingService.registerSaving(saver.id(), pesos("500000"), LocalDate.now(), PaymentMethod.CASH, null);

        // Borrow out the entire cash balance of the savings fund (as it stands right
        // after the contribution above), so the fund's cash bucket is left at exactly 0.
        Person borrower = personService.createPerson("Test Prestatario Reduccion", LocalDate.of(1990, 1, 1), null);
        FundBalances afterContribution = movementService.getBalances();
        loanService.createLoan(borrower.id(), afterContribution.savingsCash(), 300, LocalDate.now(), PaymentMethod.CASH, null);

        assertThrows(IllegalArgumentException.class, () ->
                        savingService.updateSaving(saving.id(), pesos("10000"), LocalDate.now(), PaymentMethod.CASH, null),
                "reducir el ahorro dejaría el fondo en efectivo en negativo dado el préstamo ya desembolsado");
    }

    @Test
    void settlementForAYearOnlyCountsThatYearsSavings() {
        Person person = personService.createPerson("Test Liquidacion Anual", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("1000000"), LocalDate.of(2025, 6, 1), PaymentMethod.CASH, null);
        savingService.registerSaving(person.id(), pesos("2000000"), LocalDate.of(2026, 6, 1), PaymentMethod.CASH, null);

        Settlement settlement2026 = settlementService.calculateForPerson(person.id(), 2026);

        assertEquals(pesos("2000000"), settlement2026.savingsTotal(), "no debe incluir el ahorro de 2025");
        assertEquals(pesos("60000"), settlement2026.interestAmount());
    }

    /**
     * Regression guard: PersonRepository.SUMMARY_SELECT computes outstandingDebt
     * with its own hand-written SQL against the loans table (not through
     * LoanService), so a schema change to loans' columns (like the V11 monthly-
     * interest migration) can silently break it without any other test noticing.
     */
    @Test
    void personSummaryReflectsAnActiveLoansPrincipalBalancePlusInterestOwed() {
        Person person = personService.createPerson("Test Resumen Persona Con Prestamo", LocalDate.of(1990, 1, 1), null);
        savingService.registerSaving(person.id(), pesos("200000"), LocalDate.now(), PaymentMethod.CASH, null);
        Loan loan = loanService.createLoan(person.id(), pesos("100000"), 300, LocalDate.now(), PaymentMethod.CASH, null);

        PersonSummary summary = personService.getSummary(person.id());
        assertEquals(loan.totalOwed(), summary.outstandingDebt());
        assertEquals(pesos("200000"), summary.totalSavings());

        PersonSummary fromList = personService.listWithSummary("Test Resumen Persona Con Prestamo", true).stream()
                .findFirst().orElseThrow();
        assertEquals(loan.totalOwed(), fromList.outstandingDebt());
    }

    @Test
    void importingAValidPlanReplaysEverythingInChronologicalOrderAndReconstructsTheLoanCorrectly() {
        LocalDate loanDate = LocalDate.now().minusMonths(2);
        LocalDate firstPaymentDate = loanDate.plusMonths(1);

        ImportPlan plan = new ImportPlan(
                List.of(new ImportPlan.PersonRow(2, "Test Import Pipeline", LocalDate.of(1985, 3, 15), "3001234567")),
                List.of(new ImportPlan.SavingRow(2, "Test Import Pipeline", pesos("200000"), loanDate, PaymentMethod.CASH, "aporte")),
                List.of(new ImportPlan.LoanRow(2, "1", "Test Import Pipeline", pesos("100000"), 300, loanDate, PaymentMethod.CASH,
                        "prestamo importado")),
                // Covers exactly the first month's 3000 interest, plus a 30000 abono a capital.
                List.of(new ImportPlan.PaymentRow(2, "1", firstPaymentDate, pesos("33000"), PaymentMethod.CASH, "interes + abono")));

        ImportService.ImportSummary summary = importService.execute(plan);

        assertEquals(1, summary.personsCreated());
        assertEquals(1, summary.savingsCreated());
        assertEquals(1, summary.loansCreated());
        assertEquals(1, summary.paymentsCreated());

        Person person = personService.list("Test Import Pipeline", true).stream().findFirst().orElseThrow();
        assertEquals(LocalDate.of(1985, 3, 15), person.birthDate());

        List<Loan> loans = loanService.historyForPerson(person.id());
        assertEquals(1, loans.size());
        Loan loan = loans.get(0);
        assertEquals(pesos("70000"), loan.principalBalance(), "100000 - 30000 abono a capital");
        assertEquals(pesos("33000"), loan.paidAmount());

        List<LoanPayment> payments = loanService.paymentsForLoan(loan.id());
        assertEquals(1, payments.size());
        assertEquals(pesos("3000"), payments.get(0).interestPortion());
        assertEquals(pesos("30000"), payments.get(0).principalPortion());

        int movementsForPerson = movementService.list(new MovementFilter(null, null, null, null, null, person.id(), null)).size();
        assertEquals(3, movementsForPerson, "el ahorro, el desembolso del préstamo y el pago deben quedar registrados");
    }

    @Test
    void anImportRowRejectedAtExecutionTimeRollsBackEverythingCreatedEarlierInTheSameImport() {
        LocalDate today = LocalDate.now();
        ImportPlan plan = new ImportPlan(
                List.of(new ImportPlan.PersonRow(2, "Test Import Rollback Exec", LocalDate.of(1985, 3, 15), null)),
                List.of(new ImportPlan.SavingRow(2, "Test Import Rollback Exec", pesos("10000"), today, PaymentMethod.CASH, null)),
                // Requests far more than the fund could ever have — rejected only once actually executed.
                List.of(new ImportPlan.LoanRow(2, "1", "Test Import Rollback Exec", pesos("999999999"), 300, today,
                        PaymentMethod.CASH, null)),
                List.of());

        assertThrows(ImportExecutionException.class, () -> importService.execute(plan));

        assertTrue(personService.list("Test Import Rollback Exec", true).isEmpty(),
                "the person created earlier in the same transaction must be rolled back too");
    }

    @Test
    void dashboardChartGroupsOnlyIncomeByFundAndSelectedMonthRange() {
        LocalDate today = LocalDate.now();
        DashboardSnapshot before = dashboardService.getSnapshot(DashboardPeriod.LAST_6_MONTHS);
        MovementType incomeType = movementTypeRepository.findByKind(MovementKind.INCOME, true).stream()
                .findFirst().orElseThrow();
        MovementType expenseType = movementTypeRepository.findByKind(MovementKind.EXPENSE, true).stream()
                .findFirst().orElseThrow();

        Money savingsIncome = pesos("12345.67");
        Money birthdayIncome = pesos("25000");
        movementRepository.insert(Movement.create(incomeType.id(), Fund.SAVINGS, PaymentMethod.CASH,
                MovementKind.INCOME, savingsIncome, today, null, null, null, null));
        movementRepository.insert(Movement.create(incomeType.id(), Fund.BIRTHDAY, PaymentMethod.CASH,
                MovementKind.INCOME, birthdayIncome, today, null, null, null, null));
        movementRepository.insert(Movement.create(expenseType.id(), Fund.SAVINGS, PaymentMethod.CASH,
                MovementKind.EXPENSE, pesos("7000"), today, null, null, null, null));
        movementRepository.insert(Movement.create(incomeType.id(), Fund.SAVINGS, PaymentMethod.CASH,
                MovementKind.INCOME, pesos("999999"), DashboardPeriod.LAST_6_MONTHS.startDate(today).minusDays(1),
                null, null, null, null));

        DashboardSnapshot after = dashboardService.getSnapshot(DashboardPeriod.LAST_6_MONTHS);
        MonthlyFundContributions beforeCurrentMonth = before.monthlyFundContributions().stream()
                .filter(row -> row.month().equals(YearMonth.from(today)))
                .findFirst().orElseThrow();
        MonthlyFundContributions currentMonth = after.monthlyFundContributions().stream()
                .filter(row -> row.month().equals(YearMonth.from(today)))
                .findFirst().orElseThrow();

        assertEquals(6, after.monthlyFundContributions().size());
        assertEquals(beforeCurrentMonth.savings().plus(savingsIncome), currentMonth.savings());
        assertEquals(beforeCurrentMonth.birthdays().plus(birthdayIncome), currentMonth.birthdays());
        assertEquals(12, dashboardService.getSnapshot(DashboardPeriod.LAST_12_MONTHS)
                .monthlyFundContributions().size());
        assertEquals(today.getMonthValue(), dashboardService.getSnapshot(DashboardPeriod.CURRENT_YEAR)
                .monthlyFundContributions().size());
    }
}
