package informatics.uk.ac.ed.track.esm.util;


import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class WebServiceHelper {

    public enum RequestMethod { POST }

    public final static String POST = "POST";
    public final static String UTF8 = "UTF-8";
    public final static String LOG_TAG = "WebServiceHelper";

    public final static String PARAM_PARTICIPANT_ID = "participantId";
    public final static String PARAM_NOTIFICATION_TIME = "notificationTime";
    public final static String PARAM_SURVEY_COMPLETED_TIME = "surveyCompletedTime";
    public final static String PARAM_SURVEY_COLUMN_NAMES = "surveyColumnNames";
    public final static String PARAM_SURVEY_RESPONSES = "surveyResponses";

    public final static String OUT_PARAM_SUCCESS = "success";
    public final static int SUCCESS_CODE = 1;
    public final static String SUCCESS_MESSAGE = "message";

    private static String getPostDataString(ContentValues params) {
        StringBuilder paramsSb = new StringBuilder();
        boolean first = true;

        for (String key : params.keySet()) {
            if (paramsSb.length() > 0) {
                paramsSb.append("&");
            }

            try {
                paramsSb.append(URLEncoder.encode(key, UTF8));
                paramsSb.append("=");
                paramsSb.append(URLEncoder.encode(params.getAsString(key), UTF8));
            } catch (UnsupportedEncodingException uee) {
                Log.e(LOG_TAG, "Error in parsing parameters.", uee);
            }
        }

        return paramsSb.toString();
    }

    // function get json from url
    // by making HTTP POST or GET method

    /**
     * Makes HTTP Request to web service.
     * @param url Web service method URL.
     * @param method Request Method (Currently only POST supported.)
     * @param params POST body parameters.
     * @return Server response as JSONObjects. Null if if response is not HTTP_OK.
     */
    public static JSONObject makeHttpRequest(String url, RequestMethod method,
                                      ContentValues params) {

        String json = null;
        JSONObject jsonObject = null;

        // Making HTTP request
        try {

            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setDoInput(true); // allow receiving data

            switch (method) {
                case POST:
                    connection.setRequestMethod(POST);
                    connection.setDoOutput(true); // allow sending data
                    connection.setChunkedStreamingMode(0); // default chunk length

                    // set parameters as POST body and upload
                    OutputStream outStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(outStream, UTF8));
                    writer.write(getPostDataString(params));
                    writer.flush();
                    writer.close();
                    outStream.close();


                    // get & parse response
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inStream, "iso-8859-1"));
                        StringBuilder responseSb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseSb.append(line);
                            responseSb.append("\n");
                        }
                        inStream.close();
                        json = responseSb.toString();
                    }
                    break;
            }

            // disconnect to release resources
            connection.disconnect();
        }
        catch (MalformedURLException mue) {
            Log.e(LOG_TAG, "MalformedURLException", mue);
        }
        catch (IOException ioe) {
            Log.e(LOG_TAG, "IOException", ioe);
        }

        if (json != null) {
            // try parse the string to a JSON object
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException joe) {
                Log.e(LOG_TAG, "Error parsing server response to JSON.", joe);
            }
        }

        return jsonObject;
    }

}


