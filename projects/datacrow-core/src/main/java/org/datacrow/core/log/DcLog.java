package org.datacrow.core.log;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.core.LogEvent;
import org.datacrow.core.utilities.CoreUtilities;

public class DcLog {
    
    private static final DcLog me = new DcLog();
    
    private Collection<ILogListener> listeners;
    
    public static DcLog getInstance() {
        return me;
    }
    
    private DcLog() {
        listeners = new ArrayList<ILogListener>();
    }
    
    public void addListener(ILogListener listener) {
        listeners.add(listener);
    }
    
    public void notify(LogEvent event) {
        for (ILogListener listener : listeners)
            listener.add(CoreUtilities.getTimestamp() + " - " + event.getLevel().name() + " - " + event.getMessage().getFormattedMessage().trim());
    }
}
