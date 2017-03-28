package com.example.julimi.where_to_study;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.text.TextUtils;

import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.example.julimi.where_to_study.dummy.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RoomDetailView} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RoomListView extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private boolean isRefresh;
    private SimpleItemRecyclerViewAdapter adapter;
    private SearchView sv;

    public static String BUILDING_NAME = "item_id";

    public static void setBN(String name) {
        BUILDING_NAME = name;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
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


        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isRefresh) {
            Model.ITEMS.clear();
            Model.ITEM_MAP.clear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRefresh = false;
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(BUILDING_NAME);
        //toolbar.setTitle(BUILDING_NAME);


        // search tool bar
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) findViewById(R.id.searchview);
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
       // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        System.out.println("BENGKUILE");
        Model.setBuilding(BUILDING_NAME);
        Model.helpToLoad();
        final View recyclerView = findViewById(R.id.item_list);

        //Model.getResponse(recyclerView);
        assert recyclerView != null;
        adapter = new SimpleItemRecyclerViewAdapter(Model.ITEMS);
        setupRecyclerView((RecyclerView) recyclerView);

        //System.out.println("WIFI: " + getCurrentSsid(recyclerView.getContext()));

        // swipe and refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                //System.out.println("HHHHHHAAAAAA");
                finish();
                overridePendingTransition(0, 0);
                Model.setBuilding(BUILDING_NAME);
                System.out.println("RI NI MA GOU " + BUILDING_NAME);
                //Model.helpToLoad();
                //System.out.println("Boolean1: " + ITEM_MAP.get("1").content);
                //assert recyclerView != null;
                //setupRecyclerView((RecyclerView) recyclerView);
                isRefresh = true;
                startActivity(getIntent());
                swipeRefreshLayout.setRefreshing(false);
                //System.out.println("Boolean2: " + ITEM_MAP.get("1").content);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View tm = view;
                //Snackbar.make(view, getCurrentSsid(view.getContext()), Snackbar.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(RoomListView.this).create();
                alertDialog.setTitle("Synchronization");
                alertDialog.setMessage("Are you sure to update the data of " + BUILDING_NAME + "?\n(WIFI Environment is suggested)");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Snackbar.make(tm, "Start synchronizing in Background", Snackbar.LENGTH_LONG).show();

                                Model.setBuilding(BUILDING_NAME);
                                Model.getResponse(tm);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                //finish();



                //startActivity(getIntent());
            }
        });


        //System.out.println("Boolean3: " + ITEM_MAP.get("1").content);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            navigateUpTo(new Intent(this, BuildingListView.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private final List<Model.DummyItem> mValues;
        private ArrayList<Model.DummyItem> nmValues,filterList;
        CustomFilter filter;

        public SimpleItemRecyclerViewAdapter(List<Model.DummyItem> items) {

            mValues = items;
            nmValues = new ArrayList<Model.DummyItem>(mValues);
            filterList = nmValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //contextT = parent.getContext();
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = nmValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);

            holder.mContentView.setText(nmValues.get(position).content);
            System.out.println("Content: " + nmValues.get(position).content);
            if (nmValues.get(position).content.length() <= 32) {
                holder.mContentView.setTextColor(Color.RED);
            } else {
                holder.mContentView.setTextColor(Color.parseColor("#228B22"));
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println("Boolean4: " + mTwoPane );
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(RoomDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        RoomDetailFragment fragment = new RoomDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RoomDetailView.class);
                        intent.putExtra(RoomDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        //Model.getResponse(v);
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
            public final TextView mIdView;
            public final TextView mContentView;
            public Model.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        // help to filter
        public class CustomFilter extends Filter {
            SimpleItemRecyclerViewAdapter adapte;
            ArrayList<Model.DummyItem> filterList;
            public CustomFilter(ArrayList<Model.DummyItem> filterList,SimpleItemRecyclerViewAdapter adapter)
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
                    ArrayList<Model.DummyItem> filteredPlayers=new ArrayList<>();
                    for (int i=0;i<filterList.size();i++)
                    {
                        //CHECK
                        if(filterList.get(i).content.substring(0,9).contains(constraint))
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
                adapte.nmValues= (ArrayList<Model.DummyItem>) results.values;
                //adapte.mValues = nmValues.toArray(mValues);
                //REFRESH
                adapte.notifyDataSetChanged();
            }
        }
    }
}
