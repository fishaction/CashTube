package jp.blog.petabyte.cashtube2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class SelectChannelActivity extends AppCompatActivity {

    Button backButton;
    Button forwardButton;
    Button addButton;
    WebView webView;

    String id;
    String url;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);

        backButton = findViewById(R.id.backButton);
        forwardButton = findViewById(R.id.forwardButton);
        addButton = findViewById(R.id.addButton);
        webView = findViewById(R.id.webView);

        activity = this;

        addButton.setEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String _url) {
                super.onLoadResource(view, _url);
                if(_url.startsWith("https://m.youtube.com/user")||
                        _url.startsWith("https://m.youtube.com/channel")){
                    url = _url;
                    addButton.setEnabled(true);
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://m.youtube.com");

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                webView.goBack();
                addButton.setEnabled(false);
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                webView.goForward();
                addButton.setEnabled(false);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(activity);
                progressDialog.setTitle("通信待機中...");
                progressDialog.setMessage("チャンネルの情報を取得しています");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                YouTubeChannel.addChannel(url, new YouTubeChannel.AddedChannel() {
                    @Override
                    public void postAdded() {
                        progressDialog.cancel();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
