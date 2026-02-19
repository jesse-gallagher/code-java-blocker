package frostillicus.xsp.codejavablocker;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.ibm.designer.domino.napi.NotesAPIException;
import com.ibm.designer.domino.napi.NotesCollection;
import com.ibm.designer.domino.napi.NotesCollectionEntry;
import com.ibm.designer.domino.napi.NotesConstants;
import com.ibm.designer.domino.napi.NotesDatabase;
import com.ibm.designer.domino.napi.util.NotesIterator;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;
import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;

public class BlockingServletFactory implements IServletFactory {
    private static final Logger log = Logger.getLogger(BlockingServletFactory.class.getPackage().getName());

    private static final Set<String> VETOED_FILES = new HashSet<>(Arrays.asList(
        ".classpath",
        ".project",
        "plugin.xml"
    ));
    private static final List<String> VETOED_PREFIXES = Arrays.asList(".settings/");
    private Set<String> codeJavaFiles;

    @Override
    public void init(ComponentModule module) {
        if(module instanceof NSFComponentModule) {
            Set<String> result = new HashSet<>();
            // Read in the names of Code/Java resources ahead of time
            try {
                NotesDatabase db = NotesContext.getCurrent().getNotesDatabase();
                int designNoteId = -0xFFe0;
                NotesCollection collection = db.openCollection(designNoteId, 0);
                try {
                    NotesIterator iter = collection.readEntries(32775, 0, 32);;
                    try {
                        while(iter.hasNext()) {
                            NotesCollectionEntry entry = (NotesCollectionEntry)iter.next();
                            try {
                                String flags = entry.getItemValueAsString(NotesConstants.DESIGN_FLAGS);
                                if(flags.indexOf(NotesConstants.DESIGN_FLAG_JAVAFILE) > -1) {
                                    // Title is e.g. com/example/MyClass.java
                                    String title = entry.getItemValueAsString(NotesConstants.FIELD_TITLE);
                                    result.add(title);
                                }
                            } finally {
                                entry.recycle();
                            }
                        }
                    } finally {
                        iter.recycle();
                    }
                } finally {
                    collection.recycle();
                }
            } catch (NotesAPIException e) {
               log.log(Level.SEVERE, "Encountered exception processing design for module " + module, e);
            }
            this.codeJavaFiles = result;
        }
    }

    @Override
    public ServletMatch getServletMatch(String contextPath, String path) throws ServletException {
        if(this.codeJavaFiles != null) {
            // Path is like "/xsp/foo", with no query string
            String filePath = path.substring(5);
            if(VETOED_FILES.contains(filePath)) {
                log.finest(() -> MessageFormat.format("Blocking vetoed file {0}", path));
                return new ServletMatch(NullServlet.INSTANCE, path, "");
            } else if(this.codeJavaFiles.contains(filePath)) {
                log.finest(() -> MessageFormat.format("Blocking Code/Java {0}", path));
                return new ServletMatch(NullServlet.INSTANCE, path, "");
            } else {
                // Test vetoed prefixes
                for(String prefix : VETOED_PREFIXES) {
                    if(filePath.startsWith(prefix)) {
                        log.finest(() -> MessageFormat.format("Blocking vetoed prefix {0} for file {0}", prefix, path));
                        return new ServletMatch(NullServlet.INSTANCE, path, "");
                    }
                }
            }
        }
        return null;
    }

}
