package com.example.fiyinfolu.alccryptocompare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by fiyinfolu on 11/4/17.
 */

public class CreateCurrencyCardActivity extends AppCompatActivity{

    private Button mCreateBtn;
    private Spinner mCurSpinner;
    private EditText mNameInput;
    private TextInputLayout mNameLayout;
    private ArrayList<String> currencyList;

    private String mSelectedItem = null;

    private String [] mCurrencies = {"USD","EUR","KES", "GBP","ZAR","BSD","CHF","COP","CLP","DZD","EGP","ILS","INR","JPY","KPW","KRW","LYD","LRD","NGN","TZS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        mCreateBtn = (Button) findViewById(R.id.create_button);
        mCurSpinner = (Spinner) findViewById(R.id.currency_spinner);
        mNameInput = (EditText) findViewById(R.id.titleInput);
        mNameLayout = (TextInputLayout) findViewById(R.id.title_layout);

        mCurSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mNameInput.getText())){

                    if (ArrayContainer.selectedCurrencyList.size()!=0){

                        Iterator<ArrayFormat> iter = ArrayContainer.selectedCurrencyList.iterator();

                        while (iter.hasNext()){

                            String curr = iter.next().getCurrency();

                            if (curr.equals(mSelectedItem)) {

                                Toast.makeText(CreateCurrencyCardActivity.this, "Card already exists", Toast.LENGTH_LONG).show();
                                return;

                            }

                        }
                    }


                    ArrayContainer.selectedCurrencyList.add(new ArrayFormat(mNameInput.getText().toString(), mSelectedItem));


                    storeArrayVal( ArrayContainer.selectedCurrencyList,CreateCurrencyCardActivity.this);

                    Toast.makeText(CreateCurrencyCardActivity.this, "Card Created Successfully", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(CreateCurrencyCardActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();



                }else {
                    mNameLayout.setError("Currency title cannot be empty");
                }

            }
        });

        currencyList = new ArrayList<>();

        for (int i = 0; i < mCurrencies.length; i++){

            String title = mCurrencies[i];
            currencyList.add(title);

        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CreateCurrencyCardActivity.this, android.R.layout.simple_spinner_item, currencyList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        mCurSpinner.setAdapter(myAdapter);

    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            mSelectedItem = currencyList.get(pos);

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public static void storeArrayVal(ArrayList<ArrayFormat> inArrayList, Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(inArrayList);

        prefEditor.putString("currArray", json);
        prefEditor.commit();

    }


}
