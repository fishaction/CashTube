package jp.blog.petabyte.cashtube2.Classes;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.EditText;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class AsyncFileWriter extends AsyncTask<String,Integer,Boolean> {
    public interface AsyncCallBack{
        void preExecute();
        void postExecute(boolean result);
        void progressUpdate(int progress);
        void cancel();
    }
    private AsyncCallBack mAsyncCallBack = null;

    public AsyncFileWriter(AsyncCallBack _asyncCallBack){
        mAsyncCallBack = _asyncCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncCallBack.preExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mAsyncCallBack.progressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mAsyncCallBack.postExecute(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncCallBack.cancel();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        File file = new File(strings[1]);
        file.getParentFile().mkdir();
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(strings[0]);
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
