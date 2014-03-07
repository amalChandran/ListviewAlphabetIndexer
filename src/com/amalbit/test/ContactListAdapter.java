package com.amalbit.test;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter/* implements SectionIndexer*/ {

    private Context mContext;
    private ArrayList<Contact> mContacts;
    private LayoutInflater mInflater;
    private AlphabetIndexer mIndexer;
    
    public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
        super();
        this.mContext = context;
        this.mContacts = contacts;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_item, null);
            holder = new ViewHolder();
            holder.sortKeyLayout = (LinearLayout) convertView.findViewById(R.id.sort_key_layout);
            holder.sortKey = (TextView) convertView.findViewById(R.id.sort_key);
            holder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contact = mContacts.get(position);
        holder.contactName.setText(contact.getName());
        int section = mIndexer.getSectionForPosition(position);
        if (position == mIndexer.getPositionForSection(section)) {
            holder.sortKey.setText(contact.getSortKey());
            holder.sortKeyLayout.setVisibility(View.VISIBLE);
        } else {
            holder.sortKeyLayout.setVisibility(View.GONE);
        }
        return convertView;
    }

    /*@Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }*/

    /**
     * 给当�?适�?器传入一个分组工具。
     * 
     * @param indexer
     */
    public void setIndexer(AlphabetIndexer indexer) {
        mIndexer = indexer;
    }
    
    private static class ViewHolder {
        LinearLayout sortKeyLayout;
        TextView sortKey;
        TextView contactName;
    }
}
