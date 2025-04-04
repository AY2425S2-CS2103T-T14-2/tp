package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.UniquePersonList;

/**
 * Sorts the person list
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    public static final String OVERDUE = "OVERDUE"; // amount of time over due
    public static final String AMOUNT = "AMOUNT"; // total cost of all loans
    public static final String NAME = "NAME"; // name of person

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts people by sort condition and order. "
        + "Parameters: "
        + PREFIX_SORT + " SORT CONDITION: "
        + OVERDUE + ", "
        + AMOUNT + ", "
        + NAME + ", "
        + PREFIX_ORDER + " ORDER: "
        + ASC + ", "
        + DESC;


    protected final String sort;
    protected final String order;

    /**
    * Construct a sort command
    */
    public SortCommand(String sort, String order) {
        requireNonNull(sort);
        requireNonNull(order);

        this.sort = sort;
        this.order = order;
    }

    /**
    * Return if sort string is valid
    */
    public static Boolean isValidSort(String sort) {
        return sort.equals(OVERDUE) || sort.equals(AMOUNT) || sort.equals(NAME);
    }

    /**
    * Return if order string is valid
    */
    public static Boolean isValidOrder(String order) {
        return order.equals(ASC) || order.equals(DESC);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (!model.getIsChangeable()) {
            throw new CommandException(UniquePersonList.UNMODIFIABLE_MESSAGE);
        }

        model.sortPeople(sort, order);
        return new CommandResult("Sorted by " + sort + " and ordered by " + order);
    }

}
