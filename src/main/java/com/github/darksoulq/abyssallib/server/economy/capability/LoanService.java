package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LoanService {
    CompletableFuture<Loan> issueLoan(Account borrower, Account lender, Currency currency, BigDecimal principal, BigDecimal interestRate, Duration term, EconomyContext context, Component reason);
    CompletableFuture<Loan> issueCollateralizedLoan(Account borrower, Account lender, Currency currency, BigDecimal principal, BigDecimal interestRate, Duration term, Collection<ItemStack> collateral, EconomyContext context, Component reason);
    CompletableFuture<TransactionResult> payInstallment(UUID loanId, BigDecimal amount, Component reason);
    CompletableFuture<TransactionResult> payoffCompletely(UUID loanId, Component reason);
    CompletableFuture<TransactionResult> declareDefault(UUID loanId, Component reason);
    CompletableFuture<TransactionResult> seizeCollateral(UUID loanId, Component reason);
    CompletableFuture<TransactionResult> applyLateFee(UUID loanId, BigDecimal fee, Component reason);
    CompletableFuture<Optional<Loan>> fetch(UUID loanId);
    CompletableFuture<Collection<Loan>> fetchActiveLoans(Account borrower);

    record Loan(UUID id, Account borrower, Account lender, Currency currency, BigDecimal principal, BigDecimal remainingBalance, BigDecimal interestRate, Instant issueDate, Instant nextPaymentDue, Instant termEnd, EconomyContext context, LoanState state) {}

    enum LoanState {
        ACTIVE,
        PAID_OFF,
        DEFAULTED,
        COLLATERAL_SEIZED,
        FORGIVEN
    }
}