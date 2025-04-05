package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.LoanPredicate;
import seedu.address.model.person.LoanPredicate.LoanParameter;

/**
 * Filters and displays all loans across all persons in the address book
 * based on their given predicates based on loan parameters:
 * person (shows loans for this person)
 * amount (less than | greater than or equals)
 * loanType (simple | compound)
 * dueDate (earlier than | later than or equals)
 * paidStatus (paid | unpaid).
 */
public class LoanFilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";

    public static final String CLEAR = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters loans of specified person by given parameters.\n"
            + "Parameters: [personIndex] pred/ [predicate type] [predicate parameters]\n"
            + "Available Predicate Types: amount, loanType, dueDate, paidStatus \n"
            + "amount parameters:   pred/ amount [< or >] [amount] \n"
            + "dueDate parameters:  pred/ dueDate [< or >] [date in yyyy-mm-dd] \n"
            + "loanType parameters: pred/ loanType [s or c] \n"
            + "isPaid parameters:   pred/ isPaid [y or n] \n"
            + "Example: " + COMMAND_WORD + " 3 pred/ amount > 100.00 pred/ loanType s";

    private final Set<LoanPredicate> predicateSet;
    private final Integer personIndex;
    private Boolean clear = false;

    /**
     * Constructs a LoanFilterCommand.
     *
     * @param predicateSet Set of LoanPredicate which will be used for filtering
     */
    public LoanFilterCommand(Integer personIndex, Set<LoanPredicate> predicateSet) {
        this.personIndex = personIndex;
        this.predicateSet = predicateSet;
    }

    /**
     * Constructs a LoanFilterCommand used for clearing the filter
     *
     * @param predicateSet Set of LoanPredicate which will be used for filtering
     */
    public LoanFilterCommand(Integer personIndex, boolean clear) {
        this.clear = clear;
        this.personIndex = personIndex;
        this.predicateSet = null;
    }

    /**
     * Executes the filterLoan command, displaying all loans that match the paid status.
     * For each person, any matching loans are displayed under their name.
     *
     * @param model The model containing the list of persons and their loans.
     * @return A CommandResult with the filtered loan information or a message if none found.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        String result = "Filtered by these predicates: \n";

        // case for clearing
        if (clear) {
            model.filter(personIndex - 1, null);
        } else {
            // default predicate will always resolve to true
            LoanPredicate combinedPred = new LoanPredicate(
                LoanParameter.AMOUNT,
                Optional.empty(),
                Optional.of(-10f),
                Optional.empty(),
                Optional.of('>')
            );

            // combine predicates
            for (LoanPredicate pred : predicateSet) {
                combinedPred = combinedPred.and(pred);
                result += pred.toString();
            }

            // filter
            model.filter(personIndex - 1, combinedPred);
        }

        /*
        if (!model.getIsChangeable()) {
            //throw new CommandException(UniquePersonList.UNMODIFIABLE_MESSAGE);
        }

        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex == null) {
            throw new CommandException("hi");
        }

        if (this.personIndex > lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = model.getFilteredPersonList().get(this.personIndex - 1);
        List<Loan> filteredLoans = person.getLoanList().getLoans(); // todo demeter

        // for all predicates, list -> filteredlist
        for (LoanPredicate pred : predicateSet) {
            filteredLoans = filterLoanList(filteredLoans, pred);
        }

        // handle empty result list
        if (filteredLoans.isEmpty()) {
            return new CommandResult("No loans found.");
        }

        // build string for result
        StringBuilder result = new StringBuilder();
        for (Loan loan : filteredLoans) {
            result.append(loan + "\n");
        }

        return new CommandResult(result.toString().trim());
        */
        return new CommandResult(result);
    }

    /*
    private List<Loan> filterLoanList(List<Loan> loans, LoanPredicate pred) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (pred.test(loan)) {
                result.add(loan);
            }
        }
        return result;
    }

    public static void printList(List<Loan> loans) {
        for (Loan loan: loans) {
            System.out.println(loan.toString() + "\n");
        }
    }
    */
}
