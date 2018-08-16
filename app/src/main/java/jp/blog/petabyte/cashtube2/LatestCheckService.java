package jp.blog.petabyte.cashtube2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.blog.petabyte.cashtube2.Classes.VideoInfo;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class LatestCheckService extends Service {
    public LatestCheckService() {
    }

    Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String[] autoMin = {sharedPref.getString("pref_autoGetter_every_min", "30")};


        final WifiManager[] wm = {(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE)};
        WifiInfo wifiInfo = wm[0].getConnectionInfo();
        SupplicantState state = wifiInfo.getSupplicantState();
        if (state == SupplicantState.COMPLETED){
            YouTubeChannel.checkNewVideo(context);
            Toast.makeText(this, "動画を確認しています。", Toast.LENGTH_SHORT).show();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        YouTubeChannel.getVideosInfo(new YouTubeChannel.GotCallback() {
                            @Override
                            public void postGot(List<VideoInfo> videoInfos) {
                                for (VideoInfo videoInfo : videoInfos){
                                    String strDate = videoInfo.downloadedDate;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                    try {
                                        Date date = sdf.parse(strDate);
                                        long currentTime = new Date().getTime()/(1000 * 60 * 60 * 24);
                                        long videoTime = date.getTime()/(1000 * 60 * 60 * 24);
                                        long difference = currentTime - videoTime;
                                        int autoDeleteDay = Integer.parseInt(sharedPref.getString("pref_autoDelete_days","7"));
                                        boolean isAutoDelete = sharedPref.getBoolean("pref_autoDelete_enable",false);
                                        if(autoDeleteDay <= 0){
                                            autoDeleteDay =1;
                                        }
                                        Log.d("test",String.valueOf(autoDeleteDay));
                                        if(difference >= autoDeleteDay && videoInfo.isAuto && isAutoDelete){
                                            YouTubeChannel.deleteVideo(videoInfo, new YouTubeChannel.DeletedVideo() {
                                                @Override
                                                public void postDelete() {

                                                }
                                            });
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        autoMin[0] = sharedPref.getString("pref_autoGetter_every_min", "");
                        if(autoMin[0].equals("0")){
                            autoMin[0] = "1";
                        }
                        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                        WifiInfo wifiInfo = wm.getConnectionInfo();
                        SupplicantState state = wifiInfo.getSupplicantState();
                        if (state == SupplicantState.COMPLETED){
                            YouTubeChannel.checkNewVideo(context);
                        }
                    }
                }, 0, Integer.parseInt(autoMin[0])*60000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this,LatestCheckService.class));
    }
}
