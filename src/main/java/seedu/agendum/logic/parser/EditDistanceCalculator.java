package seedu.agendum.logic.parser;

import com.joestelmach.natty.DateGroup;
import org.reflections.Reflections;
import seedu.agendum.logic.commands.Command;

import java.util.Optional;
import java.util.Set;

/**
 * Created by vprem on 24/10/16.
 */
public class EditDistanceCalculator {
    public static Optional<String> parseString(String input) {
        Reflections reflections = new Reflections("seedu.agendum");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class c:classes) {
            c.COMMAND_WORD;
        }

        classes.stream().map(map)

        return "test";
    }

    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

}
