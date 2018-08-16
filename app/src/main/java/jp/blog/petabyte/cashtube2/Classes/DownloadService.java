package jp.blog.petabyte.cashtube2.Classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    String id = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!= null){
            id = intent.getStringExtra("id");
        }
        YouTubeChannel.downloadVideo(id, false, this, new YouTubeChannel.CallBack() {
            @Override
            public void postDownloaded() {

            }
        });
        return START_STICKY_COMPATIBILITY;
    }
}
