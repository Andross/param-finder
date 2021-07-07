package burp;

import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;

import java.util.Map.Entry;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ParameterFactory {
	public static Map<String, Set<String>> getAllParameters(IHttpRequestResponse[] history,
			IBurpExtenderCallbacks callbacks, PrintWriter stdout, PrintWriter stderr, IExtensionHelpers helpers) {
		stdout.println("In getAllParameters, history length is " + history.length);
		Map<String, Set<String>> uniqueInScopeParams = new HashMap<String, Set<String>>();

		for (int i = 0; i < history.length; i++) {
			byte[] request = history[i].getRequest();
			IHttpService httpService = history[i].getHttpService();
			try {
				IRequestInfo analyzedRequest = helpers.analyzeRequest(httpService, request);
				URL url = analyzedRequest.getUrl();
				if (callbacks.isInScope(url)) {
					// stdout.println(url.getHost() + " is in scope!");
					if (!uniqueInScopeParams.containsKey(url.getHost())) {
						stdout.println("Scope not found in set, adding: " + url.getHost());
						Set<String> uniqueParams = new LinkedHashSet<>();
						uniqueInScopeParams.put(url.getHost(), uniqueParams);
					}
					List<IParameter> params = analyzedRequest.getParameters();
					for (int j = 0; j < params.size(); j++) {
						if (analyzedRequest.getContentType() == 4) {
							byte[] jsonData = Arrays.copyOfRange(request, analyzedRequest.getBodyOffset(),
									request.length);
							JsonParams jparams = new JsonParams(jsonData, stdout); // Argument can also be byte[] or
																					// JsonElement
							for (String param : jparams.getParams()) {
								uniqueInScopeParams.get(url.getHost()).add(param);
							}
						}
						if ((int) params.get(j).getName().charAt(0) < 126
								|| (int) params.get(j).getName().charAt(0) > 48) {
							uniqueInScopeParams.get(url.getHost()).add(params.get(j).getName());
						}
					}

				}
			} catch (Exception e) {
				stderr.println(e);
			}

		}
		return uniqueInScopeParams;
	}

	public static String printParams(Set<String> allParams, String scope, PrintWriter stdout, JTextField pathTxtField) {
		PrintWriter writer;
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		String params = "";

		try {
			String fileName = scope + date + ".txt";
			String currentDir = pathTxtField.getText();

			stdout.println("Writing file to system: " + currentDir + File.separator + fileName);
			writer = new PrintWriter(currentDir + File.separator + scope + date + ".txt", "UTF-8");
			for (Iterator<String> it = allParams.iterator(); it.hasNext();) {
				String param = it.next();
				params = params + param + "\n";
				writer.println(param);
			}
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return params;
	}

}
