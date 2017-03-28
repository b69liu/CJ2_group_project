package com.example.julimi.where_to_study;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.julimi.where_to_study.dummy.Model;
import android.provider.Settings.Secure;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class BuildingListView extends AppCompatActivity  {


    private SimpleItemRecyclerViewAdapter adapter;
    private SearchView sv;
    public class BackgroundThread extends Thread {

        public BackgroundThread (){}
        private String android_id = Secure.getString(BuildingListView.this.getContentResolver(),
                Secure.ANDROID_ID);

        @Override
        public void run() {
            while(true){
                String MACadd= getCurrentSsid(BuildingListView.this);
                System.out.println("just got MACadd");
                if(MACadd != null) {                  //if no wifi connection, not checking
                   MACadd = MACadd.replaceAll(":","");
                   MACadd = MACadd.toUpperCase();

                    // test in 2038
                    //MACadd = "D8C7C8189558";

                   System.out.println(MACadd);
                   String croom = "0";

                   try {
                       System.out.println("before crom="+Model.jsontranlator.length());
                       croom  = Model.jsontranlator.getString(MACadd);

                       String[] parts = croom.split("-");
                       if(parts.length >= 2){
                           croom =  parts[1];
                           Model.currentbuilding = parts[0];
                       }
                       System.out.println(croom);
                       Model.currentroom = croom;
                   }catch (JSONException je){
                       System.out.print("Not in school");
                       System.out.println("my MAC is "+ MACadd);
                       Model.currentroom = "0";       //if not in school, cannot find location
                   }



                       System.out.println("send RoomName: " + croom);
                }else {
                   Model.currentroom = "0";             //if wifi off, don't know where we are
                }


                String url = "http://54.88.214.21/rooms.php/"+ Model.currentbuilding+"/"
                        + Model.currentroom;
                Log.d("","curURL: " + url);
                //System.out.println(android_id);
                try {
                    LocReport(url);
                    Thread.sleep(300000);
                }catch (InterruptedException e) {

                } catch (IOException e) {

                }
            }
        }
    }


    //helpter to send message to server
    public static void LocReport(String GET_URL) throws IOException {
        URL obj = new URL(GET_URL);

        // obtain a new connection
        HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

        // prepare the request
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("User_Agent", "Marshmallow/6.0");
        int response = urlConnection.getResponseCode();
        //System.out.println("Connection Response Code: " + response);

        // read the request

            urlConnection.connect();

            Log.d("","Connection Code: " + response);
            urlConnection.disconnect();

    }


    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                //ssid = connectionInfo.getSSID(); // "eduroam"
                ssid = connectionInfo.getBSSID(); // "d8:c7:c8:17:0a:98"
                //ssid = connectionInfo.getMacAddress(); // "f8:a9:d0:4f:cc:5d"
                //ssid = Integer.toString(connectionInfo.getNetworkId()); // 5
                //ssid = Integer.toString(connectionInfo.getIpAddress());  // 374084618
            }
        }
        return ssid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        System.out.println("Ni shi shei");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        sv.setSubmitButtonEnabled(true);
        //sv.setQueryRefinementEnabled(true);
        //sv.setIconifiedByDefault(false);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        //Thread thread = new Thread(this);
        //thread.start();
        BackgroundThread bt=new BackgroundThread();
        bt.start();
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private boolean mTwoPane;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            System.out.println("in Buildinglist onCreate");
            Model.TranslatefileGet();
            Log.d("", "wo ri");

        }catch (IOException ioe){
            System.out.print("fail to call model.translatefileget in buildinglist");
        }
        setContentView(R.layout.activity_item_list_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar0);
        setSupportActionBar(toolbar);

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        //((RecyclerView)recyclerView).setLayoutManager(new LinearLayoutManager(this));
        ((RecyclerView)recyclerView).setItemAnimator(new DefaultItemAnimator());
        adapter = new SimpleItemRecyclerViewAdapter(Model.fakebuildinglist);
        setupRecyclerView((RecyclerView) recyclerView);
        System.out.println("Ni ma!");
        //sv = (SearchView) findViewById(R.id.search);



        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private String[] mValues;

        // new
        ArrayList<String> nmValues,filterList;
        CustomFilter filter;

        public SimpleItemRecyclerViewAdapter(String[] items) {

            mValues = items;

            //new
            nmValues = new ArrayList<String>(Arrays.asList(mValues));
            //System.out.println("Adapter: " + nmValues.get(0));
            filterList = nmValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //holder.mItem = mValues[position];
            holder.mNameView.setText(nmValues.get(position) + "                    ");


            //holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        //System.out.println("WAHAHA");
                        //Bundle arguments = new Bundle();
                       // arguments.putString(itemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        //itemDetailFragment fragment = new itemDetailFragment();
                        //fragment.setArguments(arguments);
                        //getSupportFragmentManager().beginTransaction()
                        //        .replace(R.id.item_detail_container, fragment)
                         //       .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RoomListView.class);
                        System.out.println("WAHAHA");

                        RoomListView.setBN(nmValues.get(position));
                        //Model.helpToLoad();
                        //intent.putExtra(RoomListView.BUILDING_NAME, mValues[position]);

                        //get number of people for selected building

                        //Model.downloadpeople(nmValues.get(position));

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return nmValues.size();
        }

        @Override
        public Filter getFilter() {
            if(filter==null)
            {
                System.out.println("Jin lai le");
                System.out.println("Adapter: " + filterList.get(0));
                filter=new CustomFilter(filterList,SimpleItemRecyclerViewAdapter.this);
            }
            return filter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNameView;
            //public Model.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.id);
            }


        }

        // help to filter
        public class CustomFilter extends Filter {
            SimpleItemRecyclerViewAdapter adapte;
            ArrayList<String> filterList;
            public CustomFilter(ArrayList<String> filterList,SimpleItemRecyclerViewAdapter adapter)
            {
                CustomFilter.this.adapte=adapter;
                CustomFilter.this.filterList=filterList;
            }
            //FILTERING OCURS
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                //CHECK CONSTRAINT VALIDITY
                if(constraint != null && constraint.length() > 0)
                {
                    //CHANGE TO UPPER
                    constraint=constraint.toString().toUpperCase();
                    Log.d("", "constraint: " + constraint);
                    //STORE OUR FILTERED PLAYERS
                    ArrayList<String> filteredPlayers=new ArrayList<>();
                    for (int i=0;i<filterList.size();i++)
                    {
                        //CHECK
                        if(filterList.get(i).toUpperCase().contains(constraint))
                        {
                            //ADD PLAYER TO FILTERED PLAYERS
                            System.out.println("You Jin lai le");
                            filteredPlayers.add(filterList.get(i));
                        }
                    }
                    results.count=filteredPlayers.size();
                    results.values=filteredPlayers;
                    System.out.println("filtered value " + results.values);
                }else
                {
                    results.count=filterList.size();
                    results.values=filterList;
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapte.nmValues= (ArrayList<String>) results.values;
                //adapte.mValues = nmValues.toArray(mValues);
                //REFRESH
                adapte.notifyDataSetChanged();
            }
        }
    }
}

