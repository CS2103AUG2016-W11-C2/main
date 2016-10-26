//@@author A0003878Y

package seedu.agendum.logic.parser;

import com.joestelmach.natty.DateGroup;
import org.reflections.Reflections;
import seedu.agendum.logic.commands.Command;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

public class EditDistanceCalculator {

    private static final int EDIT_DISTANCE_THRESHOLD = 3;

    public static Optional<String> parseString(String input) {
        Reflections reflections = new Reflections("seedu.agendum");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        String bestCommand = "";
        int bestCommandDistance = 999;

        for (Class<? extends Command> c :classes) {
            for (Field f : c.getFields()) {
                if (f.getName().equals("COMMAND_WORD")) {
                    try {
                        String commandWord = (String) f.get(null);
                        int commandWordDistance = distance(input, commandWord);

                        if (commandWordDistance < bestCommandDistance) {
                            bestCommand = commandWord;
                            bestCommandDistance = commandWordDistance;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (bestCommandDistance < EDIT_DISTANCE_THRESHOLD) {
            return Optional.of(bestCommand);
        } else {
            return Optional.empty();
        }
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
