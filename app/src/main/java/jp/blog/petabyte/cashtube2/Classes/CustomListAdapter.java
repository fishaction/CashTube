package jp.blog.petabyte.cashtube2.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.blog.petabyte.cashtube2.R;

public class CustomListAdapter extends ArrayAdapter<CustomListItem> {

    private int mResource;
    private List<CustomListItem> mItems;
    private LayoutInflater mInflater;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    public CustomListAdapter(Context context, int resource, List<CustomListItem> items) {
        super(context, resource, items);

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        CustomListItem item = mItems.get(position);

        // サムネイル画像を設定
        ImageView thumbnail = (ImageView)view.findViewById(R.id.imageView);
        thumbnail.setImageBitmap(item.getThumbnail());

        // タイトルを設定
        TextView title = (TextView)view.findViewById(R.id.textView);
        title.setText(item.getTitle());

        return view;
    }
}
