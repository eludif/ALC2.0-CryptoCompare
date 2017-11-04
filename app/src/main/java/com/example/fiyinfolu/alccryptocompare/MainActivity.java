package com.example.fiyinfolu.alccryptocompare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<Currency> mCurrencyList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mHeaderTextView;
    private Button mRetryButton;

    private CurrencyAdapter mCurrencyAdapter;
    private ProgressDialog mProgressDialog;

    private String jsonURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayContainer.selectedCurrencyList = getArrayValue(this);

        if (ArrayContainer.selectedCurrencyList.size() == 0) {
            Intent intent = new Intent(MainActivity.this, CreateCurrencyCardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {

            StringBuilder stringBuilder = new StringBuilder();
            for (ArrayFormat arrayFormat : ArrayContainer.selectedCurrencyList) {

                stringBuilder.append(arrayFormat.getCurrency() + ",");

            }

            jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=" + stringBuilder.toString();

            mCurrencyAdapter = new CurrencyAdapter(mCurrencyList);

            new GetCurrencies().execute();

            if (jsonURL != null) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        handler.postDelayed(this, 10000);
                    }
                }, 10000);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_card) {

            startActivity(new Intent(MainActivity.this, CreateCurrencyCardActivity.class));

            return true;
        }

        if (id == R.id.action_refresh) {

            refresh();

            return true;
        }

        if (id == R.id.action_clear_cards) {

            mCurrencyList.clear();
            ArrayContainer.selectedCurrencyList.clear();
            mCurrencyAdapter.notifyDataSetChanged();

            CreateCurrencyCardActivity.storeArrayVal(ArrayContainer.selectedCurrencyList,MainActivity.this);

            mHeaderTextView.setText("No cards available, Create a new card.");

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHeaderTextView.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            mHeaderTextView.setLayoutParams(lp);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {


        private List<Currency> currencyList;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView etheriumText, btcCurrency, currText, cardHeader;
            public ImageView removeBtn;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                etheriumText = view.findViewById(R.id.eth_text);
                btcCurrency = view.findViewById(R.id.btc_text);
                cardHeader = view.findViewById(R.id.card_header);
                currText = view.findViewById(R.id.card_header2);
                removeBtn = view.findViewById(R.id.remove_btn);
            }

            @Override
            public void onClick(View view) {

                final int selectedItemPosition = mRecyclerView.getChildPosition(view);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose conversion type:");
                builder.setCancelable(false);
                final ListView optionList = new ListView(MainActivity.this);
                final String[] optionsArray = new String[]{"Bitcoin", "Etherium"};
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_selectable_list_item, optionsArray);
                optionList.setAdapter(adapter);

                builder.setView(optionList);
                builder.setCancelable(true);

                optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String option = adapter.getItem(position);

                        if (option.equals("Bitcoin")) {

                            Intent btcIntent = new Intent(MainActivity.this, BTCCoversionActivity.class);
                            btcIntent.putExtra("currency", currencyList.get(selectedItemPosition).getTitle());
                            btcIntent.putExtra("bitcoin", currencyList.get(selectedItemPosition).getBTCValue());
                            startActivity(btcIntent);


                        } else if (option.equals("Etherium")) {

                            Intent ethIntent = new Intent(MainActivity.this, ETHConversionActivity.class);
                            ethIntent.putExtra("currency", currencyList.get(selectedItemPosition).getTitle());
                            ethIntent.putExtra("etherium", currencyList.get(selectedItemPosition).getETHValue());
                            startActivity(ethIntent);

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();

            }
        }

        public CurrencyAdapter(List<Currency> currencyList) {
            this.currencyList = currencyList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.currency_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Currency cs = currencyList.get(position);
            holder.etheriumText.setText("ETH:\n "+cs.getETHValue());
            holder.btcCurrency.setText("BTC:\n "+cs.getBTCValue());
            holder.cardHeader.setText(ArrayContainer.selectedCurrencyList.get(position).getTitle());
            holder.currText.setText("( "+cs.getTitle()+" )");

            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    currencyList.remove(position);
                    ArrayContainer.selectedCurrencyList.remove(position);
                    mCurrencyAdapter.notifyDataSetChanged();

                    CreateCurrencyCardActivity.storeArrayVal(ArrayContainer.selectedCurrencyList,MainActivity.this);

                }
            });
        }

        @Override
        public int getItemCount() {
            return currencyList.size();
        }


    }

    public static ArrayList getArrayValue( Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);
        ArrayList<ArrayFormat>   rArray;
        Gson gson;
        gson = new GsonBuilder().create();
        String response = sharedPreferences.getString("currArray", null);

        if (response!=null){

            Type type = new TypeToken<ArrayList<ArrayFormat>>(){}.getType();
            rArray = gson.fromJson(response, type);

        }else {

            rArray = new ArrayList<>();

        }

        return rArray;

    }

    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Loading data from Server...");
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject btcObj = new JSONObject(jsonStr).getJSONObject("BTC");
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject("ETH");


                    for (int i = 0; i < ArrayContainer.selectedCurrencyList.size(); i++) {

                        Double btcVal = btcObj.getDouble(ArrayContainer.selectedCurrencyList.get(i).getCurrency());
                        Double ethVal = ethObj.getDouble(ArrayContainer.selectedCurrencyList.get(i).getCurrency());
                        Currency sCurrency = new Currency(ArrayContainer.selectedCurrencyList.get(i).getCurrency(), btcVal, ethVal);
                        mCurrencyList.add(sCurrency);

                    }

                } catch (final JSONException e) {


                }
                mProgressDialog.dismiss();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr != null) {

                mProgressDialog.dismiss();

                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                mHeaderTextView = (TextView) findViewById(R.id.headerText);
                mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                mHeaderTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setAdapter(mCurrencyAdapter);
                mCurrencyAdapter.notifyDataSetChanged();

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, CreateCurrencyCardActivity.class));
                    }
                });


            } else {

                mProgressDialog.dismiss();

                setContentView(R.layout.error_layout);

                mRetryButton = (Button) findViewById(R.id.retry_button);
                mRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new GetCurrencies().execute();

                    }
                });

                Toast.makeText(MainActivity.this, "Failed to connect to the server. Check your internet connection.", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private class RefreshCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject btcObj = new JSONObject(jsonStr).getJSONObject("BTC");
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject("ETH");

                    mCurrencyList.clear();

                    for (int i =0; i < ArrayContainer.selectedCurrencyList.size(); i++){

                        Double btcVal = btcObj.getDouble(ArrayContainer.selectedCurrencyList.get(i).getCurrency());
                        Double ethVal = ethObj.getDouble(ArrayContainer.selectedCurrencyList.get(i).getCurrency());
                        Currency sCurrency = new Currency(ArrayContainer.selectedCurrencyList.get(i).getCurrency(),btcVal , ethVal);
                        mCurrencyList.add(sCurrency);

                    }

                } catch (final JSONException e) {


                }
                mProgressDialog.dismiss();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){

                mRecyclerView.setAdapter(mCurrencyAdapter);
                mCurrencyAdapter.notifyDataSetChanged();

                mProgressDialog.dismiss();

            }else {

            }

        }
    }

    private void refresh(){

        new RefreshCurrencies().execute();

    }

}
