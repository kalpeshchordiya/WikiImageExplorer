package com.kc.WikiImageExplorer.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kc.WikiImageExplorer.R;
import com.kc.WikiImageExplorer.listener.ItemClickListener;
import com.kc.WikiImageExplorer.model.PageObj;

import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolders> {

    private static final String TAG = "GridAdapter";
    private Context context;
    private Map<String, PageObj> mWikiPages;
    private String[] mKeys;
    private ItemClickListener mItemClickListener;

    public RecyclerViewAdapter(Context context, Map<String, PageObj> wikiPages, ItemClickListener
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
    public RecyclerViewHolders onCreateViewHolder(ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.grid_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        rcv.position = i;
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders recyclerViewHolders, int position) {
        PageObj pageObj = mWikiPages.get(mKeys[position]);
        Log.d(TAG, "Position = " + position + " Query = " + pageObj);
        if (pageObj != null && pageObj.getThumbnail() != null) {
            Glide.with(context).load(pageObj.getThumbnail().getSource()).placeholder(R.drawable
                    .image_loader).animate(R.anim.popup_show).into(recyclerViewHolders
                    .pageThumbnail);
        } else {
            Glide.with(context).load(R.drawable.no_image_available).dontTransform().into
                    (recyclerViewHolders
                    .pageThumbnail);
        }
        if (pageObj != null && pageObj.getTitle() != null) {
            recyclerViewHolders.pageTitle.setText(pageObj.getTitle());
        }
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mWikiPages.size();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder{

        ImageView pageThumbnail;
        TextView pageTitle;
        int position;

        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            pageThumbnail = (ImageView) itemView.findViewById(R.id.page_thumbnail);
            pageTitle = (TextView) itemView.findViewById(R.id.page_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(itemView, mWikiPages.get
                                (mKeys[getPosition()]));
                    }
                }
            });
        }
    }
}
