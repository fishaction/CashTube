package jp.blog.petabyte.cashtube2.Classes;

import android.graphics.Bitmap;

public class CustomListItem2 {
    private Bitmap mThumbnail = null;
    private String mTitle = null;

    /**
     * 空のコンストラクタ
     */
    public CustomListItem2() {};

    /**
     * コンストラクタ
     * @param title タイトル
     */
    public CustomListItem2(String title) {
        mTitle = title;
    }

    /**
     * タイトルを設定
     * @param title タイトル
     */
    public void setmTitle(String title) {
        mTitle = title;
    }


    /**
     * タイトルを取得
     * @return タイトル
     */
    public String getTitle() {
        return mTitle;
    }
}