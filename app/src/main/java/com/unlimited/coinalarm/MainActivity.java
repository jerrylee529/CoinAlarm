package com.unlimited.coinalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unlimited.coinalarm.data.ApplicationData;
import com.unlimited.coinalarm.okex.OkExQuotationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String[] cc_ids = {"BTC", "ETH", "LTC"}; // cryptocurrency id
    //private static final String[] instrument_ids = {"BTC-USDT", "ETH-USDT", "LTC-USDT"}; // 用于请求行情的id

    private static final Map<String, String> map_cc_id;

    static {
        map_cc_id = new HashMap<>();
        map_cc_id.put("BTC", "BTC");
        map_cc_id.put("ETH", "ETH");
        map_cc_id.put("LTC", "LTC");
        //map_cc_id.put("EOS", "EOS");
    }

    private static final Map<String, Integer> map_image_id;

    static {
        map_image_id = new HashMap<>();
        map_image_id.put("BTC", R.drawable.bitcoin_48_48);
        map_image_id.put("ETH", R.drawable.ethereum_48_48);
        map_image_id.put("LTC", R.drawable.litecoin_mid_48);
        //map_image_id.put("EOS", R.drawable.tether_48_48);
    }

    private Map<String, Double> map_quotation;

    private ListView listView;

    private Toast toast;

    private BaseAdapter simpleAdapter;

    private ApplicationData applicationData;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == OkExQuotationService.QUOTATION_UPDATE_NOTIFICATION) {
                String cc_id = intent.getStringExtra("cc_id");
                Double price = intent.getDoubleExtra("price", 0.0);
                map_quotation.put(cc_id, price);
                simpleAdapter.notifyDataSetChanged();
            } else if (intent.getAction() == OkExQuotationService.ALARM_NOTIFICATION) {
                String cc_id = intent.getStringExtra("cc_id");
                Double price = intent.getDoubleExtra("price", 0.0);
                Intent alarmIntent = new Intent(MainActivity.this, AlarmActivity.class);
                alarmIntent.putExtra("cc_id", cc_id);
                alarmIntent.putExtra("price", price);
                startActivityForResult(alarmIntent, 0);
            } else {
                Log.e(TAG, "unknown action");
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OkExQuotationService.QUOTATION_UPDATE_NOTIFICATION);
        filter.addAction(OkExQuotationService.ALARM_NOTIFICATION);
        registerReceiver(updateReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationData = (ApplicationData) getApplication();

        map_quotation = new HashMap<>();

        initApplicationData();

        initQuotation();

        initListView();

        initMonitor();

        //AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, OkExQuotationService.class), PendingIntent.FLAG_CANCEL_CURRENT);
        //long now = System.currentTimeMillis();
        //am.setRepeating(AlarmManager.RTC_WAKEUP, now, 1000, pi);

        //Intent intentOne = new Intent(this, OkExQuotationService.class);
        //startService(intentOne);
        registerReceiver();
    }

    private void initMonitor() {
        final Button button = findViewById(R.id.btn_control);

        button.setText(applicationData.getOn()?R.string.stop_monitor:R.string.start_monitor);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkExQuotationService.execute(MainActivity.this, !applicationData.getOn());
                applicationData.setOn(!applicationData.getOn());
                button.setText(applicationData.getOn()?R.string.stop_monitor:R.string.start_monitor);
            }
        });
    }

    private void initListView() {
        //初始化Toast的主要作用是，在点击按钮是可以先立刻消除上一个toast
        //toast = Toast.makeText(getApplicationContext(), "", 0);

        listView = findViewById(R.id.list);

        simpleAdapter = new AlarmViewAdapter(this, applicationData.getList());

        listView.setAdapter(simpleAdapter);//为ListView绑定适配器

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) view.getTag();
                assert (key != null);
                ApplicationData.AlarmItem alarmItem = applicationData.getList().get(key);
                assert (alarmItem != null);
                Intent intent = new Intent(MainActivity.this, AlarmEditorActivity.class);
                intent.putExtra("cc_id", alarmItem.getCc_id());
                intent.putExtra("price", alarmItem.getPrice());
                intent.putExtra("duration", alarmItem.getDuration());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 刷新listview
        if (0 == resultCode) {
            return;
        }

        Map<String, ApplicationData.AlarmItem> items = applicationData.getList();
        String key = data.getStringExtra("cc_id");
        ApplicationData.AlarmItem alarmItem = items.get(key);
        assert (alarmItem != null);
        Double price = data.getDoubleExtra("price", 0.00);
        alarmItem.setPrice(price);

        applicationData.saveItems();

        simpleAdapter.notifyDataSetChanged();
    }

    private void initApplicationData() {
        applicationData.loadItems();
    }

    private void initQuotation() {
        Map<String, ApplicationData.AlarmItem> items = applicationData.getList();

        for (String key : items.keySet()) {
            map_quotation.put(key, 0.00);
        }
    }

    public class AlarmViewAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        Map<String, ApplicationData.AlarmItem> items;
        List<String> list;

        public AlarmViewAdapter(Context context, Map<String, ApplicationData.AlarmItem> items) {
            mInflater = LayoutInflater.from(context);
            this.items = items;
            list = new ArrayList<>();
            for (String key : items.keySet()) {
                list.add(key);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = View.inflate(MainActivity.this, R.layout.sample_alarm_item_view, null);
            String key = list.get(position);
            view.setTag(key);

            ApplicationData.AlarmItem alarmItem = items.get(key);

            ImageView item_img_view = view.findViewById(R.id.item_img);
            item_img_view.setImageResource(alarmItem.getImageId());

            // 设置textview中提示文本
            TextView item_name_view = view.findViewById(R.id.item_name);
            item_name_view.setText(alarmItem.getCc_id());

            TextView item_price_view = view.findViewById(R.id.item_price);
            item_price_view.setText("$" + map_quotation.get(key));

            TextView item_alarm_view = view.findViewById(R.id.item_alarm);
            item_alarm_view.setText("$" + alarmItem.getPrice());

            Button button = view.findViewById(R.id.item_switch);

            button.setTag(key);
            button.setText(alarmItem.getOn() ? "ON" : "OFF");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, ApplicationData.AlarmItem> itemMap = applicationData.getList();
                    String key = (String) v.getTag();
                    ApplicationData.AlarmItem alarmItem = itemMap.get(key);
                    alarmItem.setOn(!alarmItem.getOn());
                    ((Button) v).setText(alarmItem.getOn() ? "ON" : "OFF");
                    applicationData.saveItems();
                }
            });

            return view;
        }
    }
}
