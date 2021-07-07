package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private PrintWriter stderr;
    private static BurpExtender burpExtender;


    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;
        burpExtender = BurpExtender.this;
        //print to UI
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        // obtain an extension helpers object
        helpers = callbacks.getHelpers();

        // set our extension name
        callbacks.setExtensionName("Param Finder");

        //Map<URL, Set<String>> uniqueInScopeParams = getAllParameters(history);
        stdout.println("Creating tab...");
        ParamFinderTab tab = new ParamFinderTab(callbacks, stdout, stderr, helpers);
        
    }
    
    public static void main(String args[]) {
    	
    }
}