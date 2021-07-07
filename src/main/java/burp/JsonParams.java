package burp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.PrintWriter;
import java.util.ArrayList;

public class JsonParams {

    private final ArrayList<String> params = new ArrayList<>();
    private final ArrayList<String> primitives = new ArrayList<>();
    private final JsonElement rootElement;

    private void processJsonElement(JsonElement element, String name, PrintWriter stdout) {
        stdout.println("Processing Json Element");
    	if (element.isJsonArray()) {
        	//stdout.println("Array");
            for (JsonElement e : element.getAsJsonArray()) {
                processJsonElement(e, null, stdout);
            }
        }
        if (element.isJsonObject()) {
        	//stdout.println("Object");
            JsonObject obj = element.getAsJsonObject();
            for (String key : obj.keySet()) {
                params.add(key);
                processJsonElement(obj.get(key), key, stdout);
            }
        }
        if (element.isJsonPrimitive()) {
        	//stdout.println("Primitive");
            if (name == null) {
                primitives.add(element.getAsString());
            }
        }
    }

    private JsonElement createJsonElement(String json, PrintWriter stdout) {
    	//stdout.println("JSON String: " + json);
        return JsonParser.parseString(json);
    }

    public JsonParams(String json, PrintWriter stdout) {
        rootElement = createJsonElement(json, stdout);
        processJsonElement(rootElement, null, stdout);
    }

    public JsonParams(byte[] json, PrintWriter stdout) {
        rootElement = createJsonElement(new String(json), stdout);
        processJsonElement(rootElement, null, stdout);
    }
    
    public JsonParams(JsonElement element, PrintWriter stdout) {
        this.rootElement = element;
        processJsonElement(element, null, stdout);
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public ArrayList<String> getPrimitives() {
        return primitives;
    }

    public JsonElement getRootElement() {
        return rootElement;
    }
}
