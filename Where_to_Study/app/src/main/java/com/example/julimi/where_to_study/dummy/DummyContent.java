package com.example.julimi.where_to_study.dummy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import android.os.Environment;
import 	java.io.FileOutputStream;
import 	java.io.OutputStreamWriter;

import android.content.Context;
/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    /**
     * write string to a file
     * @param data: the string to output
     * @param filename: file name
     */
    public static void writeToFile(String data,String filename)
    {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/buildings/"
                        );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, filename);

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static final int COUNT = 25;
    private static final String GET_URL_PRE = "https://api.uwaterloo.ca/v2/buildings/MC/";
    private static final String GET_URL_POST = "/courses.json?key=2d5402f20d57e1dd104101f9fa7dae27";
    private static final String USER_AGENT = "Marshmallow/6.0";
    private static int Len = 0;

    public static JSONObject jsonObject = new JSONObject();
    public static StringBuilder responseStrBuilder;

    public static void sendGet(String GET_URL) throws IOException {
        URL obj = new URL(GET_URL);

        // obtain a new connection
        HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

        // prepare the request
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User_Agent", USER_AGENT);
        int response = urlConnection.getResponseCode();
        //System.out.println("Connection Response Code: " + response);

        // read the request
        try {
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            responseStrBuilder = new StringBuilder();

            String inStr;
            while ((inStr = streamReader.readLine()) != null) responseStrBuilder.append(inStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
           // Log.d("","JSON value: " + jsonObject.getJSONArray("data").length());
            Len = jsonObject.getJSONArray("data").length();
            in.close();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to convert to JSONObject!");
        } finally {
            urlConnection.disconnect();
        }
    }

    private static class BackgroundTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String GET_URL;
                for (int i = 1000; i < 1100; i++) {
                    GET_URL = GET_URL_PRE + Integer.toString(i) + GET_URL_POST;


                    DummyContent.sendGet(GET_URL);
                    if (Len != 0) {
                        System.out.println(i);
                        writeToFile("{\"data\":[{","MC.txt");
                        writeToFile("\"subject\":","MC.txt");
                        //write
                    }
                }
                System.out.println("1111111111111");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fail to do sendGet() in background");

            }

            return responseStrBuilder.toString();
        }
    }

    public static void getResponse() {
        new BackgroundTask().execute();
    }

    public static int loopForFloor(JSONObject object, int floorLevel) {
        int count = 0;
        try {
            JSONArray got = object.getJSONArray("data");
            //int count = 0;
            for(int i = 0; i < got.length(); i++) {
                if (got.getJSONObject(i).getString("floor") == Integer.toString(floorLevel)) {
                    count++;
                }
            }


        }catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to count!");
        }

        return count;

    }

    static {
        getResponse();
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
