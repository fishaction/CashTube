package jp.blog.petabyte.cashtube2.Classes;

public class VideoInfo{
    public String title;
    public String chName;
    public String path;
    public String downloadedDate;
    public boolean isAuto;
    VideoInfo(String _title,String _chName,String _path,String _downloadedDate,boolean _isAuto){
        title = _title;
        chName = _chName;
        path = _path;
        downloadedDate = _downloadedDate;
        isAuto = _isAuto;
    }
}
