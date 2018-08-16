package jp.blog.petabyte.cashtube2.Classes;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class AsyncFileReader extends AsyncTask<String,Integer,String> {
    public interface AsyncCallBack{
        void postExecute(String result);
    }
    private AsyncCallBack mAsyncCallBack = null;

    public AsyncFileReader(AsyncCallBack _asyncCallBack){
        mAsyncCallBack = _asyncCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mAsyncCallBack.postExecute(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected String doInBackground(String... strings) {
        File file = new File(strings[0]);
        FileInputStream fis = null;
        InputStreamReader isr = null;
        String line;
        try {
            if(!file.exists())
                return null;
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String outPut ="";
            while ((line = br.readLine()) != null) {
                outPut += line;
            }
            br.close();
            return outPut;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
