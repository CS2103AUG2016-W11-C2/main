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
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SyncProviderGoogle extends SyncProvider {
    private final Logger logger = LogsCenter.getLogger(SyncProviderGoogle.class);

    private static final String APPLICATION_NAME = "Agendum";
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/calendar_sample");
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Calendar client;
    static final List<com.google.api.services.calendar.model.Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

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
    public void initialize() {
        logger.info("Initializing Google Calendar Sync");
        try {
            Credential t = authorize();
            client = (new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, t)).setApplicationName("Agendum").build();
            showCalendars();
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

    }

    private static Credential authorize() throws Exception {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId("1011464737889-n9avi9id8fur78jh3kqqctp9lijphq2n.apps.googleusercontent.com");
        details.setClientSecret("ea78y_rPz3G4kwIV3yAF99aG");

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(details);

        GoogleAuthorizationCodeFlow flow = (new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton("https://www.googleapis.com/auth/calendar"))).setDataStoreFactory(dataStoreFactory).build();
        return (new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())).authorize("user");
    }

    private static void showCalendars() throws IOException {
//        View.header("Show Calendars");
        CalendarList feed = (CalendarList)client.calendarList().list().execute();
//        View.display(feed);
        System.out.println(feed);
    }
}
