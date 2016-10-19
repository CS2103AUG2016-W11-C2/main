package seedu.agendum.logic.parser;

import seedu.agendum.logic.commands.*;
import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.commons.exceptions.IllegalValueException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static seedu.agendum.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.agendum.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern TASK_INDEXES_ARGS_FORMAT = Pattern.compile("((\\d+|\\d+-\\d+)?[ ]*,?[ ]*)+");

    private static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    private static final Pattern RENAME_ARGS_FORMAT = Pattern.compile("(?<targetIndex>\\d+)\\s+(?<name>[^/]+)");

    private static final Pattern ADD_ARGS_FORMAT = Pattern.compile("(?:.+?(?=(?:(?:by|from|to)\\s|$)))+?");

    private static final Pattern SCHEDULE_ARGS_FORMAT = Pattern.compile("(?:.+?(?=(?:(?:by|from|to)\\s|$)))+?");

    private static final String ARGS_FROM = "from";
    private static final String ARGS_BY = "by";
    private static final String ARGS_TO = "to";

    public Parser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case SelectCommand.COMMAND_WORD:
            return prepareSelect(arguments);

        case DeleteCommand.COMMAND_WORD:
            return prepareDelete(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case RenameCommand.COMMAND_WORD:
            return prepareRename(arguments);

        case ScheduleCommand.COMMAND_WORD:
            return prepareSchedule(arguments);

        case MarkCommand.COMMAND_WORD:
            return prepareMark(arguments);

        case UnmarkCommand.COMMAND_WORD:
            return prepareUnmark(arguments);

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        default:
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        Matcher matcher = ADD_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        try {
            matcher = ADD_ARGS_FORMAT.matcher(args.trim());

            String taskTitle = null;
            HashMap<String, Optional<LocalDateTime>> dateTimeMap = new HashMap<>();
            final String[] tokens = new String[]{ARGS_FROM, ARGS_TO, ARGS_BY};

            while (matcher.find()) {
                boolean matchedWithPrefix = false;

                for (String token:tokens) {
                    String s = matcher.group(0).toLowerCase();
                    if (s.startsWith(token)) {
                        s = s.substring(token.length(), s.length());
                        dateTimeMap.put(token, DateTimeParser.parseString(s));
                        matchedWithPrefix = true;
                    }
                }
                if (!matchedWithPrefix) {
                    taskTitle = matcher.group(0);
                }
            }

            if (dateTimeMap.containsKey(ARGS_BY)) {
                return new AddCommand(
                        taskTitle,
                        dateTimeMap.get(ARGS_BY)
                );
            } else if (dateTimeMap.containsKey(ARGS_FROM) && dateTimeMap.containsKey(ARGS_TO)) {
                return new AddCommand(
                        taskTitle,
                        dateTimeMap.get(ARGS_FROM),
                        dateTimeMap.get(ARGS_TO)
                );
            } else if (!dateTimeMap.containsKey(ARGS_FROM) && !dateTimeMap.containsKey(ARGS_TO) && !dateTimeMap.containsKey(ARGS_BY)) {
                return new AddCommand(
                        taskTitle
                );
            }
            else {
                return new IncorrectCommand(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the reschedule task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSchedule(String args){
        Matcher matcher = SCHEDULE_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleCommand.MESSAGE_USAGE));
        }

        HashMap<String, Optional<LocalDateTime>> dateTimeMap = new HashMap<>();
        final String[] tokens = new String[]{ARGS_FROM, ARGS_TO, ARGS_BY};

        int index = 0;
        matcher = SCHEDULE_ARGS_FORMAT.matcher(args.trim());
        while (matcher.find()) {
            boolean matchedWithPrefix = false;

            for (String token:tokens) {
                String s = matcher.group(0).toLowerCase();
                if (s.startsWith(token)) {
                    s = s.substring(token.length(), s.length());
                    dateTimeMap.put(token, DateTimeParser.parseString(s));
                    matchedWithPrefix = true;
                }
            }

            if (!matchedWithPrefix && index == 0) {
                Optional<Integer> optionalInt = parseIndex(matcher.group(0));
                if (optionalInt.isPresent()) {
                    index = optionalInt.get();
                }
                else {
                    return new IncorrectCommand(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleCommand.MESSAGE_USAGE)); 
                }
            }
        }

        if (dateTimeMap.containsKey(ARGS_BY)) {
            return new ScheduleCommand(
                    index, Optional.empty(),
                    dateTimeMap.get(ARGS_BY)
            );
        } else if (dateTimeMap.containsKey(ARGS_FROM) && dateTimeMap.containsKey(ARGS_TO)) {
            return new ScheduleCommand(
                    index,
                    dateTimeMap.get(ARGS_FROM),
                    dateTimeMap.get(ARGS_TO)
            );
        } else if (!dateTimeMap.containsKey(ARGS_FROM) && !dateTimeMap.containsKey(ARGS_TO) && !dateTimeMap.containsKey(ARGS_BY)) {
            return new ScheduleCommand(
                    index, Optional.empty(), Optional.empty()
            );
        }
        else {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        Set<Integer> taskIds = parseIndexes(args);
        if (taskIds.isEmpty()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(taskIds);
    }

    /**
     * Parses arguments in the context of the mark task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareMark(String args) {
        Set<Integer> taskIds = parseIndexes(args);
        if (taskIds.isEmpty()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }

        return new MarkCommand(taskIds);
    }
 
    /**
     * Parses arguments in the context of the unmark task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareUnmark(String args) {
        Set<Integer> taskIds = parseIndexes(args);
        if (taskIds.isEmpty()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
        }

        return new UnmarkCommand(taskIds);
    }

    /**
     * Parses arguments in the context of the rename task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareRename(String args) {
        final Matcher matcher = RENAME_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RenameCommand.MESSAGE_USAGE));
        }
        final String givenIndex = matcher.group("targetIndex");
        final String givenName = matcher.group("name").trim();
        final int index = Integer.parseInt(givenIndex);

        if (index <= 0) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RenameCommand.MESSAGE_USAGE));
        }

        try {
            return new RenameCommand(index, givenName);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Returns the specified indices in the {@code command} if positive unsigned integer(s) are given.
     *   Returns an empty set otherwise.
     */
    private Set<Integer> parseIndexes(String args) {
        final Matcher matcher = TASK_INDEXES_ARGS_FORMAT.matcher(args.trim());
        Set<Integer> taskIds = new HashSet<Integer>();

        if (!matcher.matches()) {
            return taskIds;
        }

        args = args.replaceAll("[ ]+", ",").replaceAll(",+", ",");
        String[] taskIdStrings = args.split(",");
        for (String taskIdString : taskIdStrings) {
            if (taskIdString.matches("\\d+")) {
                taskIds.add(Integer.parseInt(taskIdString));
            } else if (taskIdString.matches("\\d+-\\d+")) {
                String[] startAndEndIndexes = taskIdString.split("-");
                int startIndex = Integer.parseInt(startAndEndIndexes[0]);
                int endIndex = Integer.parseInt(startAndEndIndexes[1]);
                taskIds.addAll(IntStream.rangeClosed(startIndex,endIndex).boxed().collect(Collectors.toList()));
            }
        }
        if (taskIds.remove(0)) {
            return new HashSet<Integer>();
        }

        return taskIds;
    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

}