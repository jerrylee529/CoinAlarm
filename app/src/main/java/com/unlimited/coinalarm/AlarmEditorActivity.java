package com.unlimited.coinalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unlimited.coinalarm.data.ApplicationData;

public class AlarmEditorActivity extends AppCompatActivity {
    private String cc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_editor);

        ApplicationData applicationData = (ApplicationData)getApplication();

        cc_id = getIntent().getStringExtra("cc_id");

        TextView text_cc_id = (TextView)findViewById(R.id.text_cc_id);
        text_cc_id.setText(cc_id);

        EditText edt_price = (EditText) findViewById(R.id.edt_price);
        edt_price.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.00)));

        AppCompatButton btnOk = (AppCompatButton)findViewById(R.id.tv_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editPrice = (EditText)findViewById(R.id.edt_price);
                //EditText editDuration = (EditText)findViewById(R.id.edt_duration);
                Intent i = new Intent();
                i.putExtra("cc_id", cc_id);
                i.putExtra("price", Double.parseDouble(editPrice.getText().toString()));
                //i.putExtra("duration", editDuration.getText());
                setResult(1, i);
                finish();
            }
        });

        AppCompatButton btnCancel = (AppCompatButton)findViewById(R.id.tv_cancel);

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
