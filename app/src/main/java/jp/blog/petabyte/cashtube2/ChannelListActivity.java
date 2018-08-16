package jp.blog.petabyte.cashtube2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.blog.petabyte.cashtube2.Classes.ChannelInfo;
import jp.blog.petabyte.cashtube2.Classes.YouTubeChannel;

public class ChannelListActivity extends AppCompatActivity {

    ListView listView;
    Activity activity;
    List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
    final List<String> titleList = new ArrayList<String>();
    Map<String,ChannelInfo> map = new HashMap<String, ChannelInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        listView = findViewById(R.id.channelList);
        activity = this;

        UpdateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final CharSequence[] items = {"削除"};
                AlertDialog.Builder listDlg = new AlertDialog.Builder(activity);
                listDlg.setTitle("オプションを選択");
                listDlg.setItems(
                        items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Test",channelInfos.get(i).latestVideoId);
                                String title = (String) listView.getItemAtPosition(i);

                                YouTubeChannel.deleteChannel(map.get(title), new YouTubeChannel.DeletedChannel() {
                                    @Override
                                    public void postDelete() {
                                        UpdateList();
                                    }
                                });
                            }
                        });
                listDlg.show();
            }
        });
    }
    void UpdateList(){
        channelInfos.clear();
        titleList.clear();
        map.clear();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,titleList);
        listView.setAdapter(arrayAdapter);
        YouTubeChannel.getAddedChannels(new YouTubeChannel.GotChannelsCallback() {
            @Override
            public void postGot(final List<ChannelInfo> _channelInfos) {
                channelInfos = _channelInfos;
                final int[] items = {0};
                final int[] itemSize = {_channelInfos.size()};
                final ProgressDialog progressDialog = new ProgressDialog(activity);
                progressDialog.setTitle("通信待機中...");
                progressDialog.setMessage("チャンネル名を取得しています");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                Log.d("Test",String.valueOf(channelInfos.size()));
                if(channelInfos.size() >0){
                    progressDialog.show();
                }
                for (int i = 0;i<_channelInfos.size();i++){
                    YouTubeChannel.getChannelTitle(_channelInfos.get(i), new YouTubeChannel.GotChannelTitle() {
                        @Override
                        public void postGot(String channelTitle,ChannelInfo cI) {
                            items[0] ++;
                            Log.d("Test",String.valueOf(items[0]));
                            if(items[0] >= itemSize[0]){
                                progressDialog.cancel();
                            }
                            titleList.add(channelTitle);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,titleList);
                            listView.setAdapter(arrayAdapter);
                            map.put(channelTitle,cI);
                        }
                    });
                }
            }
        });
    }
}
