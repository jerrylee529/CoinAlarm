package com.unlimited.coinalarm.okex;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.unlimited.coinalarm.data.ApplicationData;
import com.unlimited.coinalarm.okex.model.Ticker;
import com.unlimited.coinalarm.service.AlarmReceiver;

import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkExQuotationService extends IntentService {
    private static final String TAG = "OkExQuotationService";

    //private AlarmThread alarmThread;

    private OkHttpClient client;

    private static final Map<String, String> map_instrumen_id;
    static
    {
        map_instrumen_id = new HashMap<>();
        map_instrumen_id.put("BTC-USDT", "BTC");
        map_instrumen_id.put("ETH-USDT", "ETH");
        map_instrumen_id.put("LTC-USDT", "LTC");
        //map_cc_id.put("EOS", "EOS");
    }

    public static final String QUOTATION_UPDATE_NOTIFICATION = "okex.quotation.update.notification";

    public static final String ALARM_NOTIFICATION = "okex.alarm.notification";

    public static final String QUOTATION_TICKER_URL_FORMAT = "https://www.okex.com/api/spot/v3/instruments/%s/ticker";

    public static void execute(Context context, Boolean isOn) {
        if (isOn) {
            Intent intentOne = new Intent(context, OkExQuotationService.class);
            context.startService(intentOne);
        } else {
            // 启动定时服务
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent ia = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, ia, 0);
            manager.cancel(pi);
        }
    }

    public OkExQuotationService() {
        super("OkExQuotationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "OnCreate 服务启动时调用");

        //alarmThread = new AlarmThread(alarmSetting);
        //alarmThread.start();

        client = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //启用前台服务，主要是startForeground()
        //Notification notification = new Notification(R.drawable.queen2, "用电脑时间过长了！白痴！"
        //        , System.currentTimeMillis());
        /*
        notification.(this, "快去休息！！！",
                "一定保护眼睛,不然遗传给孩子，老婆跟别人跑啊。", null);
                */
        //设置通知默认效果
        //notification.flags = Notification.FLAG_SHOW_LIGHTS;
        //startForeground(1, notification);

        // 启动定时服务
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long triggerAtTime = SystemClock.elapsedRealtime() + 60 * 1000;

        Intent ia = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, ia, 0);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handle intent");

        for (String instrument_id : map_instrumen_id.keySet()) {
            // 获取行情数据
            String jsonData = getQuotation(instrument_id);
            if (jsonData == null) {
                continue;
            }

            // 解析数据
            Ticker ticker = parseTicker(jsonData);
            if (ticker == null) {
                continue;
            }

            // 广播行情
            broadcastTicker(map_instrumen_id.get(instrument_id), ticker);

            // 告警处理
            alarm(map_instrumen_id.get(instrument_id), ticker);
        }
    }

    //服务被关闭时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy 服务关闭时");
    }

    private String getQuotation(String instrument_id) {
        String jsonData = "{\"instrument_id\": \"BTC-USDT\", \"last\": \"333.99\", \"best_bid\": \"333.98\", \"best_ask\": \"333.99\", \"high_24h\": \"0.193\"," +
                "\"open_24h\": \"0.193\"," +
                "\"low_24h\": \"333.98\"," +
                "\"base_volume_24h\": \"5957.11914015\"," +
                "\"quote_volume_24h\": \"5957.11914015\"," +
                "\"timestamp\": \"2015-11-14T20:46:03.511Z\"}";

        String url = String.format(QUOTATION_TICKER_URL_FORMAT, instrument_id);
        Request request = new Request.Builder() //利用建造者模式创建Request对象
            .url(url) //设置请求的URL
            .build(); //生成Request对象

        Response response = null;
        try {
            //将请求添加到请求队列等待执行，并返回执行后的Response对象
            response = client.newCall(request).execute();

            //获取Http Status Code.其中200表示成功
            if (response.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                jsonData = response.body().string();
                Log.d(TAG, jsonData);
            }
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        return jsonData;
    }

    private Ticker parseTicker(String bytes) {
        String jsonData = bytes;
        Gson gson = new Gson();
        Ticker ticker = gson.fromJson(jsonData, Ticker.class);
        return ticker;
    }

    private void broadcastTicker(String cc_id, Ticker ticker) {
        Intent intent = new Intent();
        intent.putExtra("cc_id", cc_id);
        intent.putExtra("price", Double.parseDouble(ticker.getLast()));
        intent.setAction(QUOTATION_UPDATE_NOTIFICATION);//action与接收器相同

        sendBroadcast(intent);
    }

    private void broadcastAlarm(String cc_id, Double price) {
        Intent intent = new Intent();
        intent.putExtra("cc_id", cc_id);
        intent.putExtra("price", price);
        intent.setAction(ALARM_NOTIFICATION);//action与接收器相同

        sendBroadcast(intent);
    }

    private void alarm(String cc_id, Ticker ticker) {
        ApplicationData applicationData = (ApplicationData)getApplication();
        Map<String, ApplicationData.AlarmItem> map = applicationData.getList();

        ApplicationData.AlarmItem alarmItem = map.get(cc_id);
        if (alarmItem != null && alarmItem.getOn()) {
            Double price = Double.parseDouble(ticker.getLast());
            Double d = Math.abs((price - alarmItem.getPrice())*100/alarmItem.getPrice());

            if (d <= alarmItem.getChangeRate()) {
                broadcastAlarm(cc_id, price);
            }
        }
    }

    private static String uncompress(byte[] bytes) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            final Deflate64CompressorInputStream zin = new Deflate64CompressorInputStream(in);
            final byte[] buffer = new byte[1024];
            int offset;
            while (-1 != (offset = zin.read(buffer))) {
                out.write(buffer, 0, offset);
            }
            return out.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* 使用websocket连接获取数据
    private class AlarmThread extends Thread {
        private static final String OKEX_API_URL = "wss://real.okex.com:10442/ws/v3";

        private OkHttpClient mClient;

        private AlarmSetting alarmSetting;

        public AlarmThread(AlarmSetting alarmSetting) {
            this.alarmSetting = alarmSetting;
        }

        @Override
        public void run() {
            mClient = new OkHttpClient();

            Request request = new Request.Builder().url(OKEX_API_URL).build();

            mClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    Log.v(TAG, "subscribe quotation");
                    //webSocket.send("{\"channel\":\"ok_sub_futureusd_btc_depth_quarter\",\"event\":\"addChannel\"}");
                    webSocket.send("{\"op\":\"subscribe\",\"args\":[\"spot/ticker:BTC-USDT\", \"spot/ticker:ETH-USDT\", \"spot/ticker:LTC-USDT\"]}");
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.v(TAG, "get text");
                    System.out.println(text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    Log.v(TAG, "get byte string");
                    String jsonData = uncompress(bytes.toByteArray());
                    //System.out.println(jsonData);
                    Gson gson = new Gson();

                    if (jsonData.startsWith("{\"event")) {
                        Event event = gson.fromJson(jsonData, Event.class);
                        System.out.println(event.getChannel());
                    } else if (jsonData.startsWith("{\"table")) {
                        Table table = gson.fromJson(jsonData, Table.class);
                        System.out.println(table.getData());
                        compute(table);
                    } else {
                        System.out.println(jsonData);
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    Log.v(TAG, t.getMessage());
                }
            });
        }

        private void compute(Table table) {
            for (int i = 0; i < table.getData().size(); i++) {
                Table.Item item = table.getData().get(i);
                System.out.println(item.getInstrument_id() + " " + item.getLast());

                Intent intent = new Intent();
                intent.putExtra("cc_id", map_instrumen_id.get(item.getInstrument_id()));
                intent.putExtra("price", item.getLast());
                intent.setAction(QUOTATION_UPDATE_NOTIFICATION);//action与接收器相同

                sendBroadcast(intent);
            }
        }
    }
    */
}
