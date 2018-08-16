package jp.blog.petabyte.cashtube2.Classes;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AsyncJsonDownloader extends AsyncTask<String,Integer,JSONObject>{
    public interface AsyncCallBack{
        void postExecute(JSONObject result);
    }

    private AsyncCallBack mAsyncCallBack = null;

    public AsyncJsonDownloader(AsyncCallBack _asyncCallBack){
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
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        mAsyncCallBack.postExecute(jsonObject);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(strings[0])
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
