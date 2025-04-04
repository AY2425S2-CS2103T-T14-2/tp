package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Pays a certain amount to a person's loan using their displayed indices from the address book.
 */
public class PayCommand extends Command {

    public static final String COMMAND_WORD = "pay";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Pays the loan of the person identified by the index number used in the displayed person list.\n"
            + "Parameters:\n"
            + "1. Pay by amount: INDEX LOAN_INDEX AMOUNT (up to 2 d.p)\n"
            + "   Example: " + COMMAND_WORD + " 1 1 100.00\n"
            + "2. Pay by months: INDEX LOAN_INDEX MONTHS'M'\n"
            + "   Example: " + COMMAND_WORD + " 1 1 3M\n"
            + "3. Pay all: INDEX LOAN_INDEX all\n"
            + "   Example: " + COMMAND_WORD + " 1 1 all";

    public static final String MESSAGE_PAYMENT_SUCCESS = "Payment successful: %1$s";
    private static final String MESSAGE_INVALID_LOAN_INDEX = "The loan index provided is invalid.";
    private static final String MESSAGE_INVALID_AMOUNT = "The amount must be a positive number.";
    private static final String MESSAGE_INVALID_MONTHS = "The number of months must be a positive integer.";

    private final Index targetIndex;
    private final int loanIndex;
    protected final PaymentType paymentType;
    protected final Float amount; //nullable if paying by months
    protected final Integer months; //nullable if paying by amount

    /**
     * Enum for payment type
     */
    public enum PaymentType {
        AMOUNT, MONTHS, ALL
    }

    /**
     * Constructor for paying by amount.
     */
    public PayCommand(Index targetIndex, int loanIndex, float amount) {
        requireNonNull(targetIndex);
        if (amount <= 0) {
            throw new IllegalArgumentException(MESSAGE_INVALID_AMOUNT);
        }
        this.targetIndex = targetIndex;
        this.loanIndex = loanIndex;
        this.amount = amount;
        this.months = null;
        this.paymentType = PaymentType.AMOUNT;
    }

    /**
     * Constructor for paying by months
     */
    public PayCommand(Index targetIndex, int loanIndex, int months) {
        requireNonNull(targetIndex);
        if (months <= 0) {
            throw new IllegalArgumentException(MESSAGE_INVALID_MONTHS);
        }
        this.targetIndex = targetIndex;
        this.loanIndex = loanIndex;
        this.amount = null;
        this.months = months;
        this.paymentType = PaymentType.MONTHS;
    }

    /**
     * Constructor for paying all remaining loan balance.
     */
    public PayCommand(Index targetIndex, int loanIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.loanIndex = loanIndex;
        this.amount = null;
        this.months = null;
        this.paymentType = PaymentType.ALL;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personWhoPaid = lastShownList.get(targetIndex.getZeroBased());
        int adjustedLoanIndex = loanIndex - 1; //adjust for zero-based index

        if (adjustedLoanIndex >= personWhoPaid.getLoanCount() || adjustedLoanIndex < 0) {
            throw new CommandException(MESSAGE_INVALID_LOAN_INDEX);
        }

        try {
            float totalPayment = 0;

            switch (paymentType) {
            case AMOUNT:
                totalPayment = amount;
                break;
            case MONTHS:
                float monthlyInstalment = personWhoPaid.getLoans()
                        .get(adjustedLoanIndex)
                        .getMonthlyInstalmentAmount();
                totalPayment = monthlyInstalment * months;
                break;
            case ALL:
                totalPayment = personWhoPaid.getLoans().get(adjustedLoanIndex).getRemainingOwed();
                break;
            default:
                throw new IllegalValueException("Unexpected value: " + paymentType);
            }

            personWhoPaid.payLoan(adjustedLoanIndex, totalPayment);

        } catch (IllegalValueException e) {
            throw new CommandException(e.getMessage());
        }

        return new CommandResult(String.format(MESSAGE_PAYMENT_SUCCESS, Messages.format(personWhoPaid)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PayCommand)) {
            return false;
        }
        PayCommand otherPayCommand = (PayCommand) other;
        return targetIndex.equals(otherPayCommand.targetIndex)
                && loanIndex == otherPayCommand.loanIndex
                && paymentType == otherPayCommand.paymentType
                && Objects.equals(amount, otherPayCommand.amount)
                && Objects.equals(months, otherPayCommand.months);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("loanIndex", loanIndex)
                .add("paymentType", paymentType)
                .add("amount", amount)
                .add("months", months)
                .toString();
    }
}
