package com.ece1886.seniordesign.perfectpopcornpopper.logs;

import android.util.Log;
import java.util.ArrayList;

/**
 * Updated 10/14/19
 * Simple Android logging tool with extra features such as saving logs in a session and dumping
 * them out at once to see history of performance throughout an Activity or Fragment's life
 * If issues are found please email me:
 * @author <a href="mailto:tristan.possessky@fedex.com">Tristan Possessky</a>
 */
@SuppressWarnings({"all"})
public class CaptainsLog {

    /**
     * Common Logging Levels Defined by Android SDK
     */
    public enum LogLevel {
        DEBUG, ERROR, INFO, VERBOSE, WARN, WTF
    }
    private static CaptainsLog _instance;
    private ArrayList<String> logs;


    public CaptainsLog(){
        logs = new ArrayList<>();
    }


    public static CaptainsLog getInstance() {
        if (_instance == null)
            _instance = new CaptainsLog();
        return _instance;
    }


    /**
     * Logging method, will default to debug log statements
     * @param tag tag of log message
     * @param msg contents of log message
     */
    public void log(String tag, String msg){
        if(tag == null || msg == null)
            throw new IllegalArgumentException("Null Arguments Provided to CaptainsLog");

        Log.d(tag, msg);
        logs.add(toString(tag, msg, LogLevel.DEBUG));
    }


    /**
     * Logging method with specific levels of logging
     * @param tag tag of log message
     * @param msg contents of log message
     * @param level Specified level
     * For level usage: https://www.javacodegeeks.com/2011/01/10-tips-proper-application-logging.html
     */
    public void log(String tag, String msg, LogLevel level){

        if(tag == null || msg == null || level == null)
            throw new IllegalArgumentException("Null Arguments Provided to CaptainsLog");

        switch (level){
            case INFO:
                Log.i(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
            case DEBUG:
                Log.d(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
            case ERROR:
                Log.e(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
            case WARN:
                Log.w(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
            case VERBOSE:
                Log.v(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
            case WTF:
                Log.wtf(tag, msg);
                logs.add(toString(tag, msg, level));
                break;
        }
    }


    /**
     * Get all log messages from a Captains log
     */
    public void logDump(){
        if(!logs.isEmpty()){
            Log.v("Captains Log", "STARTING LOG DUMP");
            for(int i = 0; i < logs.size(); i++)
                Log.v(i + ":", logs.get(i));

            Log.v("Captains Log", "END LOG DUMP");
        }
    }


    /**
     * Private inner method transferring contents of a user log to a string
     * @param tag tag of log
     * @param msg msg of log
     * @param level log level
     * @return string representation of the log
     */
    private static String toString(String tag, String msg, LogLevel level){
        switch (level){
            case INFO:
                return "Captains Log: INFO- " + tag + ": " + msg;
            case DEBUG:
                return "Captains Log: DEBUG- " + tag + ": " + msg;
            case ERROR:
                return "Captains Log: ERROR- " + tag + ": " + msg;
            case WARN:
                return "Captains Log: WARN- " + tag + ": " + msg;
            case VERBOSE:
                return "Captains Log: VERBOSE- " + tag + ": " + msg;
            case WTF:
                return "Captains Log: WTF- " + tag + ": " + msg;
        }
        return null;
    }
}