package seedu.agendum.sync;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static seedu.agendum.commons.core.Config.DEFAULT_DATA_DIR;

//@@author A0003878Y
public class SyncProviderGoogle extends SyncProvider {
    private final Logger logger = LogsCenter.getLogger(SyncProviderGoogle.class);

    private static final String APPLICATION_NAME = "Agendum";
    private static final String CALENDAR_NAME = "Agendum Calendar";
    private static final File DATA_STORE_DIR = new File(DEFAULT_DATA_DIR);
    private static final String CLIENT_ID = "1011464737889-n9avi9id8fur78jh3kqqctp9lijphq2n.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "ea78y_rPz3G4kwIV3yAF99aG";
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;

    private Calendar agendumCalendar;

    public SyncProviderGoogle() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (IOException var3) {
            System.err.println(var3.getMessage());
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    @Override
    public void start() {
        logger.info("Initializing Google Calendar Sync");
        try {
            Credential t = authorize();
            client = (new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, t)).setApplicationName("Agendum").build();
            agendumCalendar = getAgendumCalendar();
            
            syncManager.setSyncStatus(Sync.SyncStatus.RUNNING);
        } catch (IOException var3) {
            System.err.println(var3.getMessage());
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    @Override
    public void stop() {
        logger.info("Stopping Google Calendar Sync");
        syncManager.setSyncStatus(Sync.SyncStatus.NOTRUNNING);
    }

    @Override
    public void addNewEvent(Task task) {
        Date startDate = Date.from(task.getStartDateTime().get().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(task.getEndDateTime().get().atZone(ZoneId.systemDefault()).toInstant());
        String id = Integer.toString(abs(task.hashCode()));

        EventDateTime startEventDateTime = new EventDateTime().setDateTime(new DateTime(startDate));
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(new DateTime(endDate));

        Event newEvent = new Event();
        newEvent.setSummary(String.valueOf(task.getName()));
        newEvent.setStart(startEventDateTime);
        newEvent.setEnd(endEventDateTime);
        newEvent.setId(id);

        try {
            Event result = client.events().insert(agendumCalendar.getId(), newEvent).execute();
            logger.info(result.toPrettyString());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deleteEvent(Task task) {
        try {
            String id = Integer.toString(abs(task.hashCode()));
            client.events().delete(agendumCalendar.getId(), id).execute();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private Credential authorize() throws Exception {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(CLIENT_ID);
        details.setClientSecret(CLIENT_SECRET);

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(details);

        GoogleAuthorizationCodeFlow flow = (new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton("https://www.googleapis.com/auth/calendar"))).setDataStoreFactory(dataStoreFactory).build();
        return (new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())).authorize("user");
    }


    private Calendar getAgendumCalendar() throws IOException {
        CalendarList feed = (CalendarList)client.calendarList().list().execute();
        logger.info("Searching for Agnendum Calendar");

        for (CalendarListEntry entry : feed.getItems()) {
            if (entry.getSummary().equals(CALENDAR_NAME)) {
                logger.info(CALENDAR_NAME + " found");
                Calendar calendar = client.calendars().get(entry.getId()).execute();
                logger.info(calendar.toPrettyString());
                return calendar;
            }

        }

        logger.info(CALENDAR_NAME + " not found, creating it");
        Calendar entry = new Calendar();
        entry.setSummary(CALENDAR_NAME);
        Calendar calendar = client.calendars().insert(entry).execute();
        logger.info(calendar.toPrettyString());
        return calendar;
    }
}
