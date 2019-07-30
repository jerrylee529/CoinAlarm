package com.unlimited.coinalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AlarmEditorActivity extends AppCompatActivity {
    private String cc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_editor);

        cc_id = getIntent().getStringExtra("cc_id");

        TextView text_cc_id = findViewById(R.id.text_cc_id);
        text_cc_id.setText(cc_id);

        EditText edt_price = findViewById(R.id.edt_price);
        edt_price.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.00)));

        EditText edt_change_rate = findViewById(R.id.edt_change_rate);
        edt_change_rate.setText(String.valueOf(getIntent().getDoubleExtra("change_rate", 0.00)));

        AppCompatButton btnOk = findViewById(R.id.tv_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editPrice = findViewById(R.id.edt_price);
                EditText edtChangeRate = findViewById(R.id.edt_change_rate);
                Intent i = new Intent();
                i.putExtra("cc_id", cc_id);
                i.putExtra("price", Double.parseDouble(editPrice.getText().toString()));
                i.putExtra("change_rate", Double.parseDouble(edtChangeRate.getText().toString()));
                //i.putExtra("duration", editDuration.getText());
                setResult(1, i);
                finish();
            }
        });

        AppCompatButton btnCancel = findViewById(R.id.tv_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent i = new Intent();
                i.putExtra("data", "0");
                setResult(0, i);
                */
                finish();
            }
        });
    }
}
