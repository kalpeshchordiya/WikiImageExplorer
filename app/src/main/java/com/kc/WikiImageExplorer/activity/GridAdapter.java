package com.kc.WikiImageExplorer.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kc.WikiImageExplorer.R;
import com.kc.WikiImageExplorer.listener.ItemClickListener;
import com.kc.WikiImageExplorer.model.PageObj;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class GridAdapter extends BaseAdapter {

    private static final String TAG = "GridAdapter";
    private Context context;
    private Map<String, PageObj> mWikiPages;
    private String[] mKeys;
    private ItemClickListener mItemClickListener;

    public GridAdapter(Context context, Map<String, PageObj> wikiPages, ItemClickListener
            itemClickListener) {
        super();
        this.context = context;
        this.mWikiPages = wikiPages;
        mKeys = wikiPages.keySet().toArray(new
                String[wikiPages.size()]);
        mItemClickListener = itemClickListener;
    }

    public void updateWikiPages(Map<String, PageObj> wikiPages) {
        this.mWikiPages = wikiPages;
        mKeys = wikiPages.keySet().toArray(new String[wikiPages.size()]);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mWikiPages.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertview == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.grid_item, parent, false);
            viewHolder.pageThumbnail = (ImageView) convertview.findViewById(R.id.page_thumbnail);
            viewHolder.pageTitle = (TextView) convertview.findViewById(R.id.page_title);
            convertview.setTag(viewHolder);
            convertview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(mWikiPages.get(mKeys[holder.position]));
                    }
                }
            });
        } else {
            viewHolder = (ViewHolder) convertview.getTag();
        }
        viewHolder.position = position;
        if (position%2 == 0) {
            convertview.setBackgroundColor(context.getResources().getColor(R.color
                    .grey_white));
        } else {
            convertview.setBackgroundColor(context.getResources().getColor(R.color
                    .full_white));
        }
        PageObj pageObj = mWikiPages.get(mKeys[position]);
        Log.d(TAG, "Position = " + position + " Query = " + pageObj);
        if (pageObj != null && pageObj.getThumbnail() != null) {
            Picasso.with(context).load(pageObj.getThumbnail().getSource()).placeholder(R.drawable
                    .image_loader)
                    .into(viewHolder.pageThumbnail);
        } else {
            Picasso.with(context).load(R.drawable.no_image_available).into(viewHolder.pageThumbnail);
        }
        if (pageObj != null && pageObj.getTitle() != null) {
            viewHolder.pageTitle.setText(pageObj.getTitle());
        }
        return convertview;
    }

    class ViewHolder {
        ImageView pageThumbnail;
        TextView pageTitle;
        int position;
    }
}
