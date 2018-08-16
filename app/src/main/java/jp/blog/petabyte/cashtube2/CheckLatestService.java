package jp.blog.petabyte.cashtube2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class CheckLatestService extends Service {
    public CheckLatestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,10000);
            }
        };
        handler.post(runnable);
    }
}
