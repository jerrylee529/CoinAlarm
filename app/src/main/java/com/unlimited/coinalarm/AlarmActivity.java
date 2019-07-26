package com.unlimited.coinalarm;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        String cc_id = getIntent().getStringExtra("cc_id");
        Double price = getIntent().getDoubleExtra("price", 0.00);

        TextView text_cc_id = (TextView)findViewById(R.id.tv_1);
        text_cc_id.setText(cc_id);

        TextView text_price = (TextView) findViewById(R.id.tv_2);
        text_price.setText(String.valueOf(price));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        Button button = (Button)findViewById(R.id.btn_stop);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.stop();
                finish();
            }
        });
    }
}
