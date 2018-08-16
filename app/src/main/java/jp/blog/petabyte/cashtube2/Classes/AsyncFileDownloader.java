package jp.blog.petabyte.cashtube2.Classes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class AsyncFileDownloader extends AsyncTask<String,Integer,Boolean>{

    public interface AsyncCallBack{
        void preExecute();
        void postExecute(boolean result);
        void progressUpdate(int progress);
        void cancel();
    }

    public AsyncFileDownloader(AsyncCallBack _asyncCallBack){
        mAsyncCallBack = _asyncCallBack;
    }

    private AsyncCallBack mAsyncCallBack = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncCallBack.preExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mAsyncCallBack.postExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mAsyncCallBack.progressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncCallBack.cancel();
    }

    @Override
    protected Boolean doInBackground(String... values) {
        HttpURLConnection con = null;
        File file = new File(values[1]);
        file.getParentFile().getParentFile().mkdir();
        file.getParentFile().mkdir();
        try {
            // アクセス先URL
            final URL url = new URL(values[0]);
            // 出力ファイルフルパス
            final String filePath = values[1];

            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // ファイルのダウンロード処理を実行
                // 読み込み用ストリーム
                final InputStream input = con.getInputStream();
                final DataInputStream dataInput = new DataInputStream(input);
                // 書き込み用ストリーム
                final FileOutputStream fileOutput = new FileOutputStream(filePath);
                final DataOutputStream dataOut = new DataOutputStream(fileOutput);
                // 読み込みデータ単位
                final byte[] buffer = new byte[4096];
                // 読み込んだデータを一時的に格納しておく変数
                int readByte = 0;

                // ファイルを読み込む
                while((readByte = dataInput.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, readByte);
                }
                // 各ストリームを閉じる
                dataInput.close();
                fileOutput.close();
                dataInput.close();
                input.close();
                // 処理成功
                return true;
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        return false;

    }
}
