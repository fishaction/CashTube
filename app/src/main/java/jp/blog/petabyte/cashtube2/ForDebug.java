package jp.blog.petabyte.cashtube2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import jp.blog.petabyte.cashtube2.Classes.AsyncFileWriter;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class ForDebug extends AppCompatActivity {

    Button button;
    Button button2;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_debug);

        activity =this;

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,SelectChannelActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YouTubeChannel.checkNewVideo(activity);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YouTubeChannel.downloadVideo("WXk69QJ-Wr4", false,activity, new YouTubeChannel.CallBack() {
                    @Override
                    public void postDownloaded() {
                        Log.d("Test","ダウンロードに成功");
                    }
                });
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AsyncFileWriter(new AsyncFileWriter.AsyncCallBack() {
                    @Override
                    public void preExecute() {

                    }

                    @Override
                    public void postExecute(boolean result) {

                    }

                    @Override
                    public void progressUpdate(int progress) {

                    }

                    @Override
                    public void cancel() {

                    }
                }).execute("{\"channelInfos\":[{\"latestVideoId\":\"lrL-OJHZ7ig\",\"playListId\":\"UUnG1zFJg7CydKRsx9NWt6zQ\"},{\"latestVideoId\":\"lrL-OJHZ7ig\",\"playListId\":\"UUGjV4bsC43On-YuiLZcfL0w\"},{\"latestVideoId\":\"lrL-OJHZ7ig\",\"playListId\":\"UU8n--cJ4AhExt2fgbWgjmxA\"}]}", Environment.getExternalStorageDirectory().toString()+"/CashTube/plists.json");
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,VideoPlayerView.class);
                intent.putExtra("path","/storage/emulated/0/CashTube/videos/Apodock/【EarthFall】超絶リアルなエイリアンゲー！【ゆっくり実況】.mp4");
                startActivity(intent);
            }
        });
    }
}
