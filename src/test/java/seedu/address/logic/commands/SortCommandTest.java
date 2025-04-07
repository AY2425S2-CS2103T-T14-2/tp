package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.SortCommand.ASC;
import static seedu.address.logic.commands.SortCommand.DESC;
import static seedu.address.logic.commands.SortCommand.OVERDUE;
import static seedu.address.logic.commands.SortCommand.AMOUNT;
import static seedu.address.logic.commands.SortCommand.NAME;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.UniquePersonList;
public class SortCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new seedu.address.model.AddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_validInputs_createsSortCommand() {
        SortCommand sortCommand = new SortCommand(NAME, ASC);
        assertEquals(NAME, sortCommand.sort);
        assertEquals(ASC, sortCommand.order);
    }

    @Test
    public void isValidSort_validSort_returnTrue() {
        assertEquals(true, SortCommand.isValidSort(OVERDUE));
        assertEquals(true, SortCommand.isValidSort(AMOUNT));
        assertEquals(true, SortCommand.isValidSort(NAME));
    }

    @Test
    public void isValidSort_invalidSort_returnFalse() {
        assertEquals(false, SortCommand.isValidSort("INVALID_SORT"));
    }

    @Test
    public void isValidOrder_validOrder_returnTrue() {
        assertEquals(true, SortCommand.isValidOrder(ASC));
        assertEquals(true, SortCommand.isValidOrder(DESC));
    }

    @Test
    public void isValidOrder_invalidOrder_returnFalse() {
        assertEquals(false, SortCommand.isValidOrder("INVALID_ORDER"));
    }

    @Test
    public void execute_sortPeople_called_success() throws CommandException {
        // Test sorting by NAME and ASC
        SortCommand sortCommand = new SortCommand(NAME, ASC);
        CommandResult result = sortCommand.execute(model);
        assertEquals("Sorted by NAME and ordered by ASC", result.getFeedbackToUser());

        // Test sorting by AMOUNT and DESC
        sortCommand = new SortCommand(AMOUNT, DESC);
        result = sortCommand.execute(model);
        assertEquals("Sorted by AMOUNT and ordered by DESC", result.getFeedbackToUser());
    }

    @Test
    public void execute_modelUnmodifiable_throwsCommandException() {
        model.setIsChangeable(false);
        SortCommand sortCommand = new SortCommand(NAME, ASC);
        assertThrows(CommandException.class, () -> sortCommand.execute(model), UniquePersonList.UNMODIFIABLE_MESSAGE);
    }
}
