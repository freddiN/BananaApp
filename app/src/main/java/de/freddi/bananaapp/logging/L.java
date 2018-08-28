package de.freddi.bananaapp.logging;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.freddi.bananaapp.settings.Preferences;

public class L {
    private static final String LOGFILE_DIR = Environment.getExternalStorageDirectory() + "/BananaApp";
    private static final String LOGFILE_FILE = "/logfile.txt";

    public static void log(final String strTag, final String strLogme, final Preferences pref) {
        log(strTag, strLogme, pref.isDebugLogging());
    }

    public static void log(final String strTag, final String strLogme, final boolean isDebugging) {
        if (isDebugging) {
            Log.i(strTag, strLogme);

            if (!isExternalStorageWritable()) {
                return;
            }

            createFolder();

            final String strWrite = getCurrentTimeStamp() + " : " + strTag + " - " + strLogme + "\n";

            try (FileOutputStream fs = new FileOutputStream(new File(LOGFILE_DIR + LOGFILE_FILE), true)) {
                fs.write(strWrite.getBytes());
            } catch(final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteLogFile() {
        try {
            new File(LOGFILE_DIR + LOGFILE_FILE).delete();
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimeStamp() {
        return new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.GERMANY).format(new Date());
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static void createFolder() {
        final File logFolder = new File(LOGFILE_DIR);

        // create app folder
        if (!logFolder.exists() ) {
            logFolder.mkdirs();
        }
    }
}
