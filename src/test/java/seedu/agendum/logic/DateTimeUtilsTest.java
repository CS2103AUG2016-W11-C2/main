package seedu.agendum.logic;

import org.junit.Test;
import seedu.agendum.logic.parser.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

//@@author A0003878Y
public class DateTimeUtilsTest {

    private void assertSameDateAndTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        assertEquals(dateTime1, dateTime2);
    }

    private void assertSameDate(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        LocalDateTime diff = dateTime1.minusHours(dateTime2.getHour()).minusMinutes(dateTime2.getMinute()).minusSeconds(dateTime2.getSecond());
        assertEquals(dateTime1, diff);
    }

    @Test
    public void parseNaturalLanguageDateTimeString_dateString_localDateTime() throws Exception {
        Optional<LocalDateTime> t = DateTimeUtils.parseNaturalLanguageDateTimeString("2016/01/01");
        assertSameDate(t.get(), LocalDateTime.of(2016,1,1,0,0));
    }

    @Test
    public void parseNaturalLanguageDateTimeString_dateStringWith24HRTime_localDateTime() throws Exception {
        Optional<LocalDateTime> t = DateTimeUtils.parseNaturalLanguageDateTimeString("2016/01/01 01:00");
        assertSameDateAndTime(t.get(), LocalDateTime.of(2016,1,1,1,0));
    }

    @Test
    public void parseNaturalLanguageDateTimeString_dateStringWithPMTime_localDateTime() throws Exception {
        Optional<LocalDateTime> t = DateTimeUtils.parseNaturalLanguageDateTimeString("2016/01/01 3pm");
        assertSameDateAndTime(t.get(), LocalDateTime.of(2016,1,1,15,0));
    }

    @Test
    public void parseNaturalLanguageDateTimeString_verboseDateString_localDateTime() throws Exception {
        Optional<LocalDateTime> t = DateTimeUtils.parseNaturalLanguageDateTimeString("january 10 2017");
        assertSameDate(t.get(), LocalDateTime.of(2017,1,10,0,0));
    }

    @Test
    public void parseNaturalLanguageDateTimeString_verboseDateStringWithTime_localDateTime() throws Exception {
        Optional<LocalDateTime> t = DateTimeUtils.parseNaturalLanguageDateTimeString("january 10 2017 5:15pm");
        assertSameDateAndTime(t.get(), LocalDateTime.of(2017,1,10,17,15));
    }

    @Test
    public void balanceStartEndDateTime_startDateAfterEndDate_startDateBeforeEndDate() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start;

        start = start.plusDays(1);
        end = start.plusHours(1);

        end = DateTimeUtils.balanceStartAndEndDateTime(start, end);
        assertSameDateAndTime(end, start.plusHours(1));
    }
    
    //@@author A0148095X
    @Test
    public void parseNaturalLanguageDateTimeString_emptyInput_emptyOptional() { 
        Optional<LocalDateTime> parsed = DateTimeUtils.parseNaturalLanguageDateTimeString("");
        assertFalse(parsed.isPresent());
    }

    @Test
    public void parseNaturalLanguageDateTimeString_nullInput_emptyOptional() { 
        Optional<LocalDateTime> parsed = DateTimeUtils.parseNaturalLanguageDateTimeString(null);
        assertFalse(parsed.isPresent());
    }

    @Test
    public void parseNaturalLanguageDateTimeString_inputNoGroups_emptyOptional() { 
        Optional<LocalDateTime> parsed = DateTimeUtils.parseNaturalLanguageDateTimeString("asd");
        assertFalse(parsed.isPresent());
    }    

}
