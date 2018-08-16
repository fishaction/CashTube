package jp.blog.petabyte.cashtube2.Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.callback.Callback;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import jp.blog.petabyte.cashtube2.MainActivity;
import jp.blog.petabyte.cashtube2.R;
import jp.blog.petabyte.cashtube2.VideoPlayerView;

import static android.content.Context.NOTIFICATION_SERVICE;

public class YouTubeChannel {

    public interface CallBack{
        void postDownloaded();
    }

    public interface GotCallback{
        void postGot(List<VideoInfo> videoInfos);
    }

    public interface GotChannelsCallback{
        void postGot(List<ChannelInfo> channelInfos);
    }

    public interface GotChannelTitle{
        void postGot(String channelTitle,ChannelInfo cI);
    }

    public interface DeletedChannel{
        void postDelete();
    }

    public interface AddedChannel{
        void postAdded();
    }

    public interface DeletedVideo{
        void postDelete();
    }

    static final String API_KEY = "AIzaSyBsDkjXZ3mwj8h_t5f6PPDQmfyPxddqlZs";
    final static File file = Environment.getExternalStorageDirectory();
    final static String path = file.toString() + "/CashTube/plists.json";
    final static String videoJsonPath = file.toString() + "/CashTube/videos.json";
    static public void addChannel(final String _url, final AddedChannel addedChannel){

        //非同期でJsonファイルの読み込み
        final AsyncFileReader asyncFileReader = new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
            @Override
            public void postExecute(final String fileResult) {
                String id;
                String url = "";

                //Urlでチャンネルかユーザーか
                if(_url.startsWith("https://m.youtube.com/user")){
                    Pattern pattern = Pattern.compile("(?<=user/)[^/#\\&\\?]*");
                    Matcher matcher = pattern.matcher(_url);
                    if(matcher.find()){
                        id = matcher.group();
                        url = "https://www.googleapis.com/youtube/v3/channels" +
                                "?part=contentDetails&forUsername="+id+"&key="+API_KEY;
                    }
                }
                else if(_url.startsWith("https://m.youtube.com/channel")){
                    Pattern pattern = Pattern.compile("(?<=channel/)[^/#\\&\\?]*");
                    Matcher matcher = pattern.matcher(_url);
                    if(matcher.find()){
                        id = matcher.group();
                        url = "https://www.googleapis.com/youtube/v3/channels" +
                                "?part=contentDetails&id="+id+"&key="+API_KEY;
                    }
                }
                AsyncJsonDownloader asyncJsonDownloader = new AsyncJsonDownloader(new AsyncJsonDownloader.AsyncCallBack() {
                    @Override
                    public void postExecute(final JSONObject jsonObject) {
                        String _pListId = null;
                        try {
                            _pListId = jsonObject.getJSONArray("items").
                                    getJSONObject(0).getJSONObject("contentDetails").getJSONObject("relatedPlaylists")
                                    .getString("uploads");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(_pListId == null)
                            return;
                        final String pListId = _pListId;

                        AsyncJsonDownloader asyncJsonDownloader1 = new AsyncJsonDownloader(new AsyncJsonDownloader.AsyncCallBack() {
                            @Override
                            public void postExecute(JSONObject result) {
                                String json = null;
                                String latestVideoId = "";
                                try {
                                    latestVideoId = result.getJSONArray("items").getJSONObject(0).
                                            getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(fileResult == null){
                                    List<ChannelInfo> channelInfoArrayList = new ArrayList<ChannelInfo>();
                                    channelInfoArrayList.add(new ChannelInfo(pListId,latestVideoId));
                                    ChannelList channelList = new ChannelList(channelInfoArrayList);
                                    Gson gson = new Gson();
                                    json = gson.toJson(channelList);
                                }
                                else{
                                    try {
                                        Log.d("Test",fileResult);
                                        JSONObject _jsonObject = new JSONObject(fileResult);
                                        JSONArray channelInfos = _jsonObject.getJSONArray("channelInfos");
                                        List<ChannelInfo> list = new ArrayList<ChannelInfo>();
                                        for(int i = 0;i < channelInfos.length();i++){
                                            String _latest = channelInfos.getJSONObject(i).getString("latestVideoId");
                                            if(channelInfos.getJSONObject(i).getString("playListId").equals(pListId)){
                                                addedChannel.postAdded();
                                                Log.d("Test","該当しています");
                                                return;
                                            }
                                            String _playListId = channelInfos.getJSONObject(i).getString("playListId");
                                            list.add(new ChannelInfo(_playListId,_latest));
                                        }
                                        list.add(new ChannelInfo(pListId,latestVideoId));
                                        json = new Gson().toJson(new ChannelList(list));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                AsyncFileWriter afw = new AsyncFileWriter(new AsyncFileWriter.AsyncCallBack() {
                                    @Override
                                    public void preExecute() {

                                    }

                                    @Override
                                    public void postExecute(boolean result) {
                                        addedChannel.postAdded();
                                    }

                                    @Override
                                    public void progressUpdate(int progress) {

                                    }

                                    @Override
                                    public void cancel() {
                                    }
                                });
                                afw.execute(json,path);
                            }
                        });
                        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+pListId+"&maxResults=1&key="+API_KEY;
                        asyncJsonDownloader1.execute(url);
                    }
                });
                asyncJsonDownloader.execute(url);
            }
        });
        asyncFileReader.execute(path);
    }
    public static void downloadVideo(final String videoId,final boolean isAuto ,final Context context, CallBack _callBack){
        String url = "https://www.googleapis.com/youtube/v3/videos?id="+videoId+"&key="+API_KEY+"&fields=items(id,snippet(channelTitle,title,thumbnails),statistics)&part=snippet,contentDetails,statistics";
        Log.d("Test-Url", url);
        final String directoryPath = file.toString() + "/CashTube/videos";
        final CallBack callBack = _callBack;
        AsyncJsonDownloader asyncJsonDownloader = new AsyncJsonDownloader(new AsyncJsonDownloader.AsyncCallBack() {
            @Override
            public void postExecute(JSONObject result) {
                final String title;
                final String thumbnails;
                final String chName;
                try {
                    title = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title").replace("/","-");
                    thumbnails = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                    chName = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("channelTitle").replace("/","-");
                    String youtubeLink = "http://youtube.com/watch?v="+videoId;
                    new YouTubeExtractor(context){
                        @Override
                        protected void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
                            if(sparseArray != null){
                                int itag =22;
                                if(sparseArray.get(itag) == null){
                                    Log.d("Test","faild");
                                    return;
                                }
                                String downloadUrl = sparseArray.get(itag).getUrl();
                                Log.d("Test",downloadUrl);
                                final String dlPath = directoryPath+"/"+chName+"/"+title+".mp4";
                                AsyncFileDownloader asyncFileDownloader = new AsyncFileDownloader(new AsyncFileDownloader.AsyncCallBack(){
                                    @Override
                                    public void postExecute(boolean result) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                        String dateStr = sdf.format(new Date());
                                        Log.d("Test","Download was done");
                                        addVideo(new VideoInfo(title,chName,dlPath,dateStr,isAuto));
                                        int notNumber = NotificationID.getID();
                                        Intent intent = new Intent(context, VideoPlayerView.class);
                                        intent.putExtra("path",dlPath);
                                        intent.putExtra("ntc",notNumber);

                                        PendingIntent contentIntent =
                                                PendingIntent.getActivity(
                                                        context,
                                                        0,
                                                        intent,
                                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


                                        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                                        NotificationManager mNotificationManager =
                                                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);


                                        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                        mBuilder.setContentTitle("新しい動画が追加されました！");
                                        mBuilder.setContentText(chName +"-"+title);
                                        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                                        mBuilder.setContentIntent(contentIntent);
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            NotificationChannel channel = new NotificationChannel("ch1","channel",NotificationManager.IMPORTANCE_DEFAULT);
                                            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                                            mBuilder.setChannelId("ch1");
                                        }
                                        mNotificationManager.notify(notNumber, mBuilder.build());
                                        callBack.postDownloaded();
                                    }

                                    @Override
                                    public void preExecute() {
                                    }

                                    @Override
                                    public void cancel() {
                                    }

                                    @Override
                                    public void progressUpdate(int progress) {
                                        Log.d("Test",String.valueOf(progress));

                                    }
                                });
                                asyncFileDownloader.execute(downloadUrl,dlPath);
                            }
                        }
                    }.extract(youtubeLink,true,true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        asyncJsonDownloader.execute(url);
    }
    static void addVideo(final VideoInfo _videoInfo){
        final String p = file.toString() + "/CashTube/videos.json";
        AsyncFileReader asyncFileReader = new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
            String jsonStr;
            @Override
            public void postExecute(String result) {
                if(result == null){
                    Log.d("Test","nullでした");
                    List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                    videoInfos.add(_videoInfo);
                    VideoList videoList = new VideoList(videoInfos);
                    jsonStr = new Gson().toJson(videoList);

                    Log.d("Test",jsonStr);
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("videoInfos");
                        List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                        for (int i = 0;i < jsonArray.length();i++){
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String chName = jsonArray.getJSONObject(i).getString("chName");
                            String path = jsonArray.getJSONObject(i).getString("path");
                            boolean isAuto = jsonArray.getJSONObject(i).getBoolean("isAuto");
                            if(path.equals(_videoInfo.path)){
                                return;
                            }
                            String downloadedDate = jsonArray.getJSONObject(i).getString("downloadedDate");
                            videoInfos.add(new VideoInfo(title,chName,path,downloadedDate,isAuto));
                        }
                        videoInfos.add(_videoInfo);
                        VideoList videoList = new VideoList(videoInfos);
                        jsonStr = new Gson().toJson(videoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                AsyncFileWriter asyncFileWriter = new AsyncFileWriter(new AsyncFileWriter.AsyncCallBack() {
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
                });
                asyncFileWriter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jsonStr,p);
            }
        });
        asyncFileReader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,p);
    }
    public static void checkNewVideo(final Context _context){
        final String path = file.toString() + "/CashTube/plists.json";
        new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
            @Override
            public void postExecute(String result) {
                try {
                    if (result == null)
                        return;
                    JSONObject jsonObject = new JSONObject(result);
                    final JSONArray jsonArray = jsonObject.getJSONArray("channelInfos");
                    for (int i = 0;i < jsonArray.length() ; i++){
                        final JSONObject jsonChannelList = jsonArray.getJSONObject(i);
                        final String _latestVideoId = jsonChannelList.getString("latestVideoId");
                        final String _playListId = jsonChannelList.getString("playListId");
                        String apiUrl = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+_playListId+"&maxResults=1&key="+API_KEY;
                        new AsyncJsonDownloader(new AsyncJsonDownloader.AsyncCallBack() {
                            @Override
                            public void postExecute(JSONObject result) {
                                try {
                                    final String latestVideoId = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
                                    final String title = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
                                    final String chName = result.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("channelTitle");
                                    if(!latestVideoId.equals(_latestVideoId)){

                                        downloadVideo(latestVideoId,true,_context, new CallBack() {
                                            @Override
                                            public void postDownloaded() {
                                                new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
                                                    @Override
                                                    public void postExecute(String result) {
                                                        List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
                                                        JSONArray jA = null;
                                                        try {
                                                            jA = new JSONObject(result).getJSONArray("channelInfos");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        for(int i = 0;i<jA.length();i++){
                                                            try {
                                                                String pList = jA.getJSONObject(i).getString("playListId");
                                                                String vList = jA.getJSONObject(i).getString("latestVideoId");

                                                                if(pList.equals(_playListId)){
                                                                    vList = latestVideoId;
                                                                }
                                                                channelInfos.add(new ChannelInfo(pList,vList));
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        String jsonStr = new Gson().toJson(new ChannelList(channelInfos));
                                                        Log.d("Test",jsonStr);
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
                                                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jsonStr,path);
                                                    }
                                                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,path);
                                            }
                                        });
                                    }
                                    else{
                                        Log.d("Test","is Latest");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,apiUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(path);
    }
    public static void getVideosInfo(final GotCallback callback){
        AsyncFileReader asyncFileReader = new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
            @Override
            public void postExecute(String result) {
                List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
                if(result == null)
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray videosJson = jsonObject.getJSONArray("videoInfos");
                    for (int i =0;i<videosJson.length();i++){
                        String chName = videosJson.getJSONObject(i).getString("chName");
                        String downloadedDate = videosJson.getJSONObject(i).getString("downloadedDate");
                        String path = videosJson.getJSONObject(i).getString("path");
                        String title = videosJson.getJSONObject(i).getString("title");
                        boolean isAuto = videosJson.getJSONObject(i).getBoolean("isAuto");
                        videoInfos.add(new VideoInfo(title,chName,path,downloadedDate,isAuto));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.postGot(videoInfos);
            }
        });
        asyncFileReader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoJsonPath);
    }
    public static void getAddedChannels(final GotChannelsCallback gotChannelsCallback){
        new AsyncFileReader(new AsyncFileReader.AsyncCallBack() {
            @Override
            public void postExecute(String result) {
                List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
                try {
                    if(result == null){
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray infosJson = jsonObject.getJSONArray("channelInfos");
                    for (int i = 0;i<infosJson.length();i++){
                        String latestVideoId = infosJson.getJSONObject(i).getString("latestVideoId");
                        String playListId = infosJson.getJSONObject(i).getString("playListId");
                        channelInfos.add(new ChannelInfo(playListId,latestVideoId));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gotChannelsCallback.postGot(channelInfos);
            }
        }).execute(path);
    }
    public static void getChannelTitle(final ChannelInfo cI,final GotChannelTitle gotChannelTitle){
        new AsyncJsonDownloader(new AsyncJsonDownloader.AsyncCallBack() {
            @Override
            public void postExecute(JSONObject result) {
                String channelTitle = "";
                try {
                    JSONArray itemsJson = result.getJSONArray("items");
                    channelTitle = itemsJson.getJSONObject(0).getJSONObject("snippet").getString("channelTitle");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gotChannelTitle.postGot(channelTitle,cI);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+cI.playListId+"&maxResults=1&key="+API_KEY);
    }
    public static void deleteChannel(final ChannelInfo info, final DeletedChannel deletedChannel){
        getAddedChannels(new GotChannelsCallback() {
            @Override
            public void postGot(List<ChannelInfo> channelInfos) {
                List<ChannelInfo> channelInfoList = new ArrayList<ChannelInfo>();
                for(ChannelInfo cI : channelInfos){
                    if (!info.latestVideoId.equals(cI.latestVideoId)){
                        channelInfoList.add(cI);
                    }
                }
                String json = "";
                json = new Gson().toJson(new ChannelList(channelInfoList));
                Log.d("Test",json);

                new AsyncFileWriter(new AsyncFileWriter.AsyncCallBack() {
                    @Override
                    public void preExecute() {

                    }

                    @Override
                    public void postExecute(boolean result) {
                        deletedChannel.postDelete();
                        Log.d("Test","done");
                    }

                    @Override
                    public void progressUpdate(int progress) {

                    }

                    @Override
                    public void cancel() {

                    }
                }).execute(json,path);
            }
        });
    }
    public static void deleteVideo(final VideoInfo info,final DeletedVideo deletedVideo){
        final List<VideoInfo> videoInfoList = new  ArrayList<VideoInfo>();
        getVideosInfo(new GotCallback() {
            @Override
            public void postGot(List<VideoInfo> videoInfos) {
                for (VideoInfo vI : videoInfos){
                    if(!vI.path.equals(info.path)){
                        videoInfoList.add(vI);
                    }
                }
                String json = new Gson().toJson(new VideoList(videoInfoList));
                new AsyncFileWriter(new AsyncFileWriter.AsyncCallBack() {
                    @Override
                    public void preExecute() {

                    }

                    @Override
                    public void postExecute(boolean result) {
                        File file = new File(info.path);
                        file.delete();
                        deletedVideo.postDelete();
                    }

                    @Override
                    public void progressUpdate(int progress) {

                    }

                    @Override
                    public void cancel() {

                    }
                }).execute(json,videoJsonPath);
            }
        });
    }
}

class ChannelList{
    List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
    ChannelList(List<ChannelInfo> _channelInfos){
        channelInfos = _channelInfos;
    }
}

class VideoList{
    List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
    VideoList(List<VideoInfo> _videoInfos){
        videoInfos = _videoInfos;
    }
}

class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}