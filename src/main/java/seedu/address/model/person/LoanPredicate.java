package seedu.address.model.person;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tests that a {@code Loan}'s parameter matches any of the conditions given.
 */
public class LoanPredicate implements Predicate<Loan> {
    private final LoanParameter parameter;
    private final Optional<Integer> index;
    private final Optional<Float> value;
    private final Optional<LocalDate> date;
    private final Optional<Character> operator;

    /**
     * Constructs a {@code LoanPredicate} with the specified parameters.
     *
     * @param parameter The type of loan parameter to filter by.
     * @param index An optional index for filtering by a specific loan.
     * @param value An optional float value used for amount-based filtering.
     * @param date An optional {@code LocalDate} value used for due date-based filtering.
     * @param operator An optional character used as a comparison operator ('<', '>', 's', 'c', 'y', or 'n')
     *                 depending on the parameter type.
     */
    public LoanPredicate(LoanParameter parameter,
                         Optional<Integer> index,
                         Optional<Float> value,
                         Optional<LocalDate> date,
                         Optional<Character> operator) {
        this.parameter = parameter;
        this.index = index;
        this.value = value;
        this.date = date;
        this.operator = operator;
    }

    /**
     * Represents the different parameters that can be used to filter loans.
     */
    public enum LoanParameter {
        AMOUNT, DUE_DATE, LOAN_TYPE, IS_PAID, INVALID;

        /**
         * Converts a string representation of a loan parameter into a {@code LoanParameter} enum.
         *
         * @param paramType The string representation of the loan parameter (e.g., "amount", "duedate").
         * @return The corresponding {@code LoanParameter} enum value, or {@code INVALID} if the input is invalid.
         */
        public static LoanParameter fromString(String paramType) {
            switch (paramType.toLowerCase()) {
            case "amount":
                return AMOUNT;
            case "duedate":
                return DUE_DATE;
            case "loantype":
                return LOAN_TYPE;
            case "ispaid":
                return IS_PAID;
            default:
                return INVALID;
            }
        }
    }

    /**
     * Creates a new loan predicate
     * @param other
     * @return
     */
    public LoanPredicate and(LoanPredicate other) {
        return new LoanPredicate(
            this.parameter,
            this.index,
            this.value,
            this.date,
            this.operator
        ) {
            @Override
            public boolean test(Loan loan) {
                return LoanPredicate.this.test(loan) && other.test(loan);
            }
        };
    }

    @Override
    public boolean test(Loan loan) {
        switch (this.parameter) {
        case AMOUNT: {
            assert operator.isPresent()
                    && (operator.get() == '<' || operator.get() == '>') : "Operator for amount must be < or >";
            if (operator.get() == '<') {
                return loan.getRemainingOwed() < this.value.get();
            } else {
                return loan.getRemainingOwed() >= this.value.get();
            }
        }
        case DUE_DATE: {
            assert operator.isPresent()
                    && (operator.get() == '<' || operator.get() == '>') : "Operator for dueDate must be < or >";
            if (operator.get() == '<') {
                return loan.getDueDate().isBefore(this.date.get());
            } else {
                return loan.getDueDate().isAfter(this.date.get()) || loan.getDueDate().isEqual(this.date.get());
            }
        }
        case LOAN_TYPE: {
            assert operator.isPresent()
                    && (operator.get() == 's' || operator.get() == 'c') : "Operator for loanType must be s or c";
            if (operator.get() == 's') {
                return loan instanceof SimpleInterestLoan;
            } else {
                return loan instanceof CompoundInterestLoan;
            }
        }
        case IS_PAID: {
            assert operator.isPresent()
                    && (operator.get() == 'y' || operator.get() == 'n') : "Operator for isPaid must be y or n";
            char op = this.operator.get();
            return op == 'y' ? loan.isPaid() : !loan.isPaid();
        }
        case INVALID: {
            // todo throw exception
            return true;
        }
        default: {
            return true;
        }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LoanPredicate)) {
            return false;
        }

        LoanPredicate otherLoanPredicate = (LoanPredicate) other;
        return parameter == otherLoanPredicate.parameter;
    }

    @Override
    public String toString() {
        return switch (this.parameter) {
        case AMOUNT -> String.format("amount %s %s \n", operator.orElse(null), value.orElse(null));
        case DUE_DATE -> String.format("due %s %s \n", operator.orElse('a'), date.orElse(null));
        case LOAN_TYPE -> String.format("loan type %s%n",
                operator.orElse(' ') == 's' ? "simple interest" : "compound interest");
        case IS_PAID -> String.format("status %s%n",
                operator.orElse(' ') == 'y' ? "paid" : "unpaid");
        default -> "blank%n";
        };
    }
}
