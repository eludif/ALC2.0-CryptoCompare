package com.example.fiyinfolu.alccryptocompare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ETHConversionActivity extends AppCompatActivity {

    private TextView mETHHeaderText;
    private TextView mEthCurrHeaderText;
    private EditText mEthInputField;
    private EditText mEthCurrInputField;
    private TextView mEthCurrResultText;
    private TextView mEthResultText;
    private Button mConvertEthToCurrBtn;
    private Button mConvertToEthBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etherium_conversion);

        mETHHeaderText = (TextView) findViewById(R.id.eth_header);
        mEthCurrHeaderText = (TextView) findViewById(R.id.curr_to_eth_header);
        mEthInputField = (EditText) findViewById(R.id.eth_input);
        mEthCurrInputField = (EditText) findViewById(R.id.curr_eth_input);
        mEthCurrResultText = (TextView) findViewById(R.id.eth_curr_results_text);
        mEthResultText = (TextView) findViewById(R.id.eth_results_text);
        mConvertEthToCurrBtn = (Button) findViewById(R.id.convert_to_curr_eth);
        mConvertToEthBtn = (Button) findViewById(R.id.convert_to_eth_btn);

        Bundle prevIntent = getIntent().getExtras();
        final String ethCurr = prevIntent.getString("currency");
        final double etheriumRate = prevIntent.getDouble("etherium");

        mETHHeaderText.setText("Etherium to "+ethCurr);
        mEthCurrHeaderText.setText(ethCurr+" to Etherium");

        mConvertEthToCurrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double cryptInput = Double.parseDouble(mEthInputField.getText().toString());
                mEthCurrResultText.setText("Results: "+convertEthToCurrency(cryptInput,etheriumRate)+" "+ethCurr);

            }
        });

        mConvertToEthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double currInput = Double.parseDouble(mEthCurrInputField.getText().toString());
                mEthResultText.setText("Results: "+convertToEtherium(currInput,etheriumRate)+" Coins ");

            }
        });

    }

    private double convertEthToCurrency(double ethVal, double rate){

        return  ethVal * rate;

    }

    private double convertToEtherium(double currencyVal, double rate){

        return currencyVal / rate;

    }

}
