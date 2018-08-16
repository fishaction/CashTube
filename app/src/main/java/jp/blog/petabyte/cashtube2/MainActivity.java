package jp.blog.petabyte.cashtube2;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.taptappun.taku.kobayashi.runtimepermissionchecker.RuntimePermissionChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.blog.petabyte.cashtube2.Classes.AsyncFileWriter;
import jp.blog.petabyte.cashtube2.Classes.AsyncJsonDownloader;
import jp.blog.petabyte.cashtube2.Classes.CustomListAdapter;
import jp.blog.petabyte.cashtube2.Classes.CustomListItem;
import jp.blog.petabyte.cashtube2.Classes.VideoInfo;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;


public class MainActivity extends AppCompatActivity {

    ListView listView;
    Activity activity;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        startCheckService();

        MobileAds.initialize(this, "ca-app-pub-1270972870113472~8370426724");

        RuntimePermissionChecker.requestPermission(this, 0, Manifest.permission.READ_EXTERNAL_STORAGE);
        RuntimePermissionChecker.requestPermission(this, 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        listView = findViewById(R.id.listView);
        ArrayList<CustomListItem> customListItems = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.open_video);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.add_channel);
        CustomListItem openVideoItem = new CustomListItem(bitmap,"取得した動画を開く");
        CustomListItem addChannelItem = new CustomListItem(bitmap2,"チャンネル追加");
        customListItems.add(openVideoItem);
        customListItems.add(addChannelItem);
        CustomListAdapter customListAdapter = new CustomListAdapter(activity,R.layout.custom_row,customListItems);
        listView.setAdapter(customListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    Intent intent = new Intent(activity,SelectDownloadedChannel.class);
                    activity.startActivity(intent);
                }
                else if (i==1){
                    Intent intent = new Intent(activity,SelectChannelActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //main.xmlの内容を読み込む
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("設定")){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void startCheckService(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("RunningService",serviceInfo.service.getClassName());
            if (LatestCheckService.class.getName().equals(serviceInfo.service.getClassName())) {
                Log.d("Test","same service actived");
                return;
            }
        }
        Intent intent = new Intent(getApplication(), LatestCheckService.class);
        startService(intent);
    }
}
