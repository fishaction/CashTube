package jp.blog.petabyte.cashtube2;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeExtractor;
import jp.blog.petabyte.cashtube2.Classes.CustomListAdapter2;
import jp.blog.petabyte.cashtube2.Classes.CustomListItem2;
import jp.blog.petabyte.cashtube2.Classes.VideoInfo;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class SelectDownloadedChannel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_downloaded_channel);

        final Activity activity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final ListView listView = findViewById(R.id.channelListView);
        YouTubeChannel.getVideosInfo(new YouTubeChannel.GotCallback() {
            @Override
            public void postGot(final List<VideoInfo> videoInfos) {
                List<String> videoTitles = new ArrayList<String>();
                final ArrayList<CustomListItem2> customListItems = new ArrayList<>();
                for(int i = 0;i<videoInfos.size();i++){
                    videoTitles.add(videoInfos.get(i).chName);
                }
                videoTitles = new ArrayList<String>(new LinkedHashSet<>(videoTitles));
                final List<String> s = videoTitles;
                for (String str : s){
                    customListItems.add(new CustomListItem2(str));
                }
                CustomListAdapter2 customListAdapter2 = new CustomListAdapter2(activity,R.layout.custom_row2,customListItems);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,videoTitles);
                listView.setAdapter(customListAdapter2);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(activity,SelectVideoActivity.class);
                        intent.putExtra("chTitle",s.get(i));
                        startActivity(intent);
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
