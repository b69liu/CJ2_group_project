package com.example.julimi.where_to_study.dummy;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import android.os.Environment;
import 	java.io.FileOutputStream;
import 	java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import android.view.View;
import android.widget.Toast;

import static java.lang.Math.abs;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Model {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static final Map<String, Map<String, DummyItem> > Buildings = new HashMap<String, Map<String, DummyItem> >();

    public static final String fakebuildinglist[] = {
            "AHS",
            "AL",
            "ARC",
            "B1",
            "B2",
            "BMH",
            "C2",
            "CGR",
            "CPH",
            "DC",
            "DMS",
            "DWE",
            "E2",
            "E3",
            "E5",
            "E6",
            "EC4",
            "ECH",
            "EIT",
            "ESC",
            "EV1",
            "EV2",
            "EV3",
            "HH",
            "IHB",
            "M3",
            "MC",
            "ML",
            "OPT",
            "PAC",
            "PAS",
            "PHR",
            "PHY",
            "QNC",
            "RCH",
            "REN",
            "SJ1",
            "SJ2",
            "STC",
            "STP"};


    public static boolean isDownload = false;

    public static void writeToFile(String data,String filename)
    {


        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                /*Environment.DIRECTORY_DCIM +*/ "/buildings/"
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
    private static int COUNT = 0;
    private static final String GET_URL_PRE = "https://api.uwaterloo.ca/v2/buildings/";
    public static String GET_BUILDING = "RCH";
    private static final String GET_SLASH = "/";
    private static final String GET_URL_POST = "/courses.json?key=2d5402f20d57e1dd104101f9fa7dae27";
    private static final String USER_AGENT = "Marshmallow/6.0";
    private static String GET_FILE = "";
    private static String[] DoW = {
            "",
            "",
            "M",
            "T",
            "W",
            "Th",
            "F"
    };

    public static void setBuilding(String name) {
        GET_BUILDING = name;
        GET_FILE = name + ".txt";
    }
    private static int Len = 0;
    //private static String[] output;

    public static JSONObject jsonObject = new JSONObject();
    public static StringBuilder responseStrBuilder;

    private static boolean helpToGetFile(String today, String cday) {
        // M,W,F
        for (int i = 0; i < cday.length(); i++) {
            if (today.length() == 1) {
                char t1 = today.charAt(0);
                char t2 = cday.charAt(i);
                boolean b1 = t1 == t2;
                boolean b2 = true;
                if (i+1 < cday.length()) {
                    char t3 = cday.charAt(i + 1);

                    b2 = t3 != 'h';
                }
                if (b1 && b2) return true;

            } else {
                if (cday.charAt(i) == 'h') return true;

            }
        }
        return false;
    }


    public static String filterByDay(JSONArray oj, int noc, String[] inDetail) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CANADA);
        Date curdate = new Date();
        String curTime = format.format(curdate).toString();
        //curTime = "10:38";          //test
        System.out.println(curTime);
        boolean stt = true;
        String Min = "23:59";
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        //dayOfWeek = 3;          // test
        for (int i = 0; i < 31; i++) inDetail[i] = "";
        if (dayOfWeek == 1 || dayOfWeek == 7) return "            available all day";


        String dw = DoW[dayOfWeek];
        long difference = 0;


        try {
            //System.out.println("Enter !!!");
            Date min = format.parse(Min);
            curdate = format.parse(curTime);
            Date thedate = new Date();

            for (int i = 0; i < noc; i++) {
                boolean b4 = helpToGetFile(dw, oj.getJSONObject(i).getString("weekdays"));
                //System.out.println(i);
                if (!b4) continue;


                // store schedule
                String st = oj.getJSONObject(i).getString("start_time");
                String et = oj.getJSONObject(i).getString("end_time");
                //System.out.println("shit !");
                String fix = "8:00";
                Date dfix = format.parse(fix);
                Date dst = format.parse(st);
                Date det = format.parse(et);
                //System.out.println("shit !!");
                long deltaD = dst.getTime() - dfix.getTime();
                int timeSlotIndex1 = ((int) TimeUnit.MILLISECONDS.toMinutes(deltaD)) / 30;
                //System.out.println("deltaD: " + deltaD);

                deltaD = det.getTime() - dfix.getTime();
                int timeSlotIndex2 = ((int) TimeUnit.MILLISECONDS.toMinutes(deltaD)) / 30;
                int doTSI = timeSlotIndex2 - timeSlotIndex1;

                for (int k = 1; k < doTSI; k++) {
                    inDetail[k+timeSlotIndex1] = "paint";
                }
                inDetail[timeSlotIndex1] = oj.getJSONObject(i).getString("subject") + oj.getJSONObject(i).getString("catalog_number");
                inDetail[timeSlotIndex2] = st + " - " + et;


                thedate = format.parse(oj.getJSONObject(i).getString("start_time"));
                if(thedate.after(curdate)&&thedate.before(min)) {
                    stt = true;
                    min = thedate;
                }
                thedate = format.parse(oj.getJSONObject(i).getString("end_time"));
                if(thedate.after(curdate)&&thedate.before(min)) {
                    stt = false;
                    min = thedate;
                }

            }
            String test = format.format(min).toString();
            //System.out.println("Out !!!!");
            boolean b5 = test.equals(Min);
            if (b5) {

                return "            free until tomorrow";
            }
            difference = min.getTime() - curdate.getTime();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (stt) return "            available for " + TimeUnit.MILLISECONDS.toMinutes(difference) + "mins";
        else {
            //String oo = "            unavailable";

            return "            unavailable";
        }
        // show today's class
        //for (int i = 0; i < jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").length(); i++) {
        //    if (helpToGetFile(DoW[dayOfWeek], jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").getJSONObject(i).getString("weekdays")))
        //        System.out.println("Today: " + DoW[dayOfWeek] + " " + jsonObject.getJSONObject("building").getJSONObject("MC").getJSONArray("2034").getJSONObject(i).getString("start_time"));
        //}
    }

    public static void fileGet() throws IOException {
        File local = Environment.getExternalStoragePublicDirectory("/buildings/");
        File file = new File(local,GET_FILE);

        System.out.println(GET_FILE);


        try {
            //InputStream in = new BufferedInputStream(file.get);
            BufferedReader streamReader = new BufferedReader(new FileReader(file));
            responseStrBuilder = new StringBuilder();

            String inStr;
            while ((inStr = streamReader.readLine()) != null) responseStrBuilder.append(inStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
            // Log.d("","JSON value: " + jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).length());
            COUNT = jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).length();

            //System.out.println(jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).getJSONArray("2034").length());
            //Iterator keysToCopyIterator = jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).getJSONArray("2034").getJSONObject(0).keys();
            //List<String> keysList = new ArrayList<String>();
            //while(keysToCopyIterator.hasNext()) {
            //    String key = (String) keysToCopyIterator.next();
            //    keysList.add(key);
            //    System.out.println(key);
            //}
            //in.close();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to convert to JSONObject by File!");
        }
    }

    public static String[] roomGet(String building) throws IOException {

        String [] rit = null;
        String url = "http://54.88.214.21/rooms.php/" + building;
        URL obj = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
        System.out.println("!!!!!!!");

        urlConnection.setRequestMethod("GET");
        //urlConnection.setRequestProperty("User_Agent", USER_AGENT);
        //int response = urlConnection.getResponseCode();

        System.out.println("hahaha");
        try {
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            responseStrBuilder = new StringBuilder();

            String inStr;
            while ((inStr = streamReader.readLine()) != null) responseStrBuilder.append(inStr);

            JSONArray bArray = new JSONArray(responseStrBuilder.toString());

            rit = new String[bArray.length()];
            for (int i = 0; i < bArray.length(); i++) {
                rit[i] =  bArray.getJSONObject(i).getString("room");
            }
            // Log.d("","JSON value: " + jsonObject.getJSONArray("data").length());
            //Len = bObject.getJSONArray("data").length();
            in.close();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fail to convert to JSONObject!");
        } finally {
            urlConnection.disconnect();
        }
        return rit;
    }
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

        View view;
        ProgressDialog pd;


        public BackgroundTask() {this.view = null;}
        public BackgroundTask(View view) {

            this.view = view;
            this.pd = new ProgressDialog(view.getContext());
            this.pd.setCanceledOnTouchOutside(false);
        }

        //@Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            //if (values.length == 2) {
                pd.setProgress(values[0]);
            //   pd.setMax(values[1]);
            //}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Downloading...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                System.out.println("enter");

                String GET_URL;
                String []room = roomGet(GET_BUILDING);
                String content="{\"building\":{\"" + GET_BUILDING + "\":{";
                int idx = 0;
                //int ct = 0;


                boolean firstclass = true;
                //int noc = 0;
                for (int i = 0; i < room.length; i++) {


                    //String RoomNum = Integer.toString(i);
                    System.out.println(room[i]);
                    GET_URL = GET_URL_PRE + GET_BUILDING + GET_SLASH + room[i] + GET_URL_POST;
                    System.out.println(GET_URL);
                    boolean first_line = true;
                    Model.sendGet(GET_URL);
                    if (Len == 0) continue;

                    if(!firstclass) content += ",";
                    firstclass = false;
                    content += "\"" + room[i] + "\":[";
                    while (idx < Len) {
                        //System.out.println(i);
                        if(!first_line) content += ",";
                        first_line = false;
                        content = content+ "{\"subject\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("subject");
                        //jsonObject.getJSONArray().
                        content += "\",\"catalog_number\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("catalog_number");
                        content = content+ "\",\"weekdays\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("weekdays");
                        content = content+ "\",\"start_time\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("start_time");
                        content += "\"";
                        content = content+ ",\"end_time\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("end_time");
                        content += "\"";
                        content = content+ ",\"room\":\"";
                        content += jsonObject.getJSONArray("data").getJSONObject(idx).getString("room");
                        content = content+ "\"}";
                        ++idx;
                        //write
                    }
                    idx = 0;
                    content += "]";
                    onProgressUpdate((int) ((i / (float) room.length) * 100));
                }

                content += "}}}";
                writeToFile(content,GET_FILE);

                System.out.println("1111111111111");

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fail to do sendGet() in background");
                return "false";

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println("Fail to write!");
                return "false";
            }
            //return responseStrBuilder.toString();
            return "true";
        }


        @Override
        protected void onPostExecute(String s) {
            if(view != null) {

                if (pd.isShowing()) pd.dismiss();
                if (s == "true") {
                    //Toast.makeText(view.getContext(), "Synch Completed!", Toast.LENGTH_LONG).show();
                    Toast.makeText(view.getContext(), "Synch Completed! Please Swipe and Refresh", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(view.getContext(), "Error", Toast.LENGTH_LONG).show();
                //Snackbar.make(view, "Synchronization Completed! Please Swipe and Refresh", Snackbar.LENGTH_LONG).show();
            }
        }

    }

    public static void getResponse(View view) {
        new BackgroundTask(view).execute();
    }
    public static void getResponseO() {new BackgroundTask().execute();}


    public static void helpToLoad() {
        try {
            //getResponseO();
            //System.out.println("kkk");
            System.out.println("cao ni ma " + GET_FILE);

            fileGet();
            //System.out.println("33333");
            //String hehe = filterByDay(jsonObject, "2035");
            ITEMS.clear();
            ITEM_MAP.clear();

            //System.out.println(jsonObject.getJSONObject("building").getJSONObject("MC").length());
            Iterator it = jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).keys();

            // add item
            for (int i = 1; i <= COUNT && it.hasNext(); i++) {
                String key = (String) it.next();
                //System.out.println("Paracmeter: " + COUNT);

                //System.out.println(key);
                String[] inDetail = new String[31];
                System.out.println(jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).getJSONArray(key).length());
                String nkey = filterByDay(jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).getJSONArray(key), jsonObject.getJSONObject("building").getJSONObject(GET_BUILDING).getJSONArray(key).length(), inDetail);
                key += nkey;
                System.out.println(key);
                //Log.d("","inDetail[0]: " + inDetail[0]);
                addItem(createDummyItem(i, key, inDetail));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //static {

        //if (isDownload) getResponse();
        //isDownload = false;
        // Add some sample items.
    //    helpToLoad();

    //}

    private static void addItem(DummyItem item) {
        System.out.println("Check null: " + item.content);
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position, String item, String[] detail) {

        return new DummyItem(String.valueOf(position), GET_BUILDING + " " + item, detail);
    }

    /*private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }*/

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String[] details;


        public DummyItem(String id, String content, String[] details) {
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
