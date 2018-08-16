package jp.blog.petabyte.cashtube2;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.blog.petabyte.cashtube2.Classes.DownloadService;

public class ShareDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_download);


        Intent i = getIntent();
        String action = i.getAction();
        String type = i.getType();

        new AlertDialog.Builder(this)
                .setTitle("注意")
                .setMessage("この機能は不完全です。ダウンロード中はマルチタスクからこの画面を閉じないでください。（非表示は可能）")
                .setPositiveButton("OK", null)
                .show();

        if(Intent.ACTION_SEND.equals(action)
                && "text/plain".equals(type)){
            i.getStringExtra(Intent.EXTRA_TEXT);
            Pattern pattern = Pattern.compile("(?<=youtu.be/)[^/#\\&\\?]*");
            Matcher matcher = pattern.matcher(i.getStringExtra(Intent.EXTRA_TEXT));
            if(matcher.find()){
                String id = matcher.group();
                Intent intent = new Intent(this,DownloadService.class);
                intent.putExtra("id",id);
                startService(intent);
            }
        }
    }
}
