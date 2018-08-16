package jp.blog.petabyte.cashtube2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.blog.petabyte.cashtube2.Classes.CustomListAdapter2;
import jp.blog.petabyte.cashtube2.Classes.CustomListItem;
import jp.blog.petabyte.cashtube2.Classes.CustomListItem2;
import jp.blog.petabyte.cashtube2.Classes.VideoInfo;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class SelectVideoActivity extends AppCompatActivity {

    String selectedCh;
    Activity activity;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);

        listView = findViewById(R.id.videoListView);
        activity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        selectedCh = intent.getStringExtra("chTitle");

        UpdateList();

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
    void UpdateList(){
        final ArrayList<CustomListItem2> customListItems = new ArrayList<>();
        final CustomListAdapter2 customListAdapter2 = new CustomListAdapter2(activity,R.layout.custom_row2,customListItems);
        YouTubeChannel.getVideosInfo(new YouTubeChannel.GotCallback() {
            @Override
            public void postGot(List<VideoInfo> videoInfos) {
                final List<VideoInfo> list = new ArrayList<VideoInfo>();
                List<String> titles = new ArrayList<String>();
                for (VideoInfo v : videoInfos){
                    if(v.chName.equals(selectedCh)){
                        list.add(v);
                        customListItems.add(new CustomListItem2(v.title));
                    }
                }
                listView.setAdapter(customListAdapter2);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(activity,VideoPlayerView.class);
                        intent.putExtra("path",list.get(i).path);
                        startActivity(intent);
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {
                        final CharSequence[] items = {"再生","削除"};
                        AlertDialog.Builder listDlg = new AlertDialog.Builder(activity);
                        listDlg.setTitle("オプションを選択");
                        listDlg.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 1){
                                    YouTubeChannel.deleteVideo(list.get(i), new YouTubeChannel.DeletedVideo() {
                                        @Override
                                        public void postDelete() {
                                            UpdateList();
                                        }
                                    });
                                }
                                else{
                                    Intent intent = new Intent(activity,VideoPlayerView.class);
                                    intent.putExtra("path",list.get(i).path);
                                    startActivity(intent);
                                }
                            }
                        });
                        listDlg.show();
                        return true;
                    }
                });
            }
        });
    }
}
