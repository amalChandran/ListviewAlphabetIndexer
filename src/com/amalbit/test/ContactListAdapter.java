package com.amalbit.test;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ContactListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<Glossary> glossariesList;
    /** This is mainly for listview search **/
    private ArrayList<Glossary> glossariesListForSearch;
    private LayoutInflater mInflater;
    private AlphabetIndexer mIndexer;
    
    public ContactListAdapter(Context context, ArrayList<Glossary> glossaries) {
        super();
        this.mContext = context;
        this.glossariesList = glossaries;
        glossariesListForSearch = glossaries;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return glossariesList.size();
    }

    @Override
    public Object getItem(int position) {
        return glossariesList.get(position);
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
        Glossary glossary = glossariesList.get(position);
        holder.contactName.setText(glossary.getName());
        int section = mIndexer.getSectionForPosition(position);
        if (position == mIndexer.getPositionForSection(section)) {
            holder.sortKey.setText(glossary.getSortKey());
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

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return myFilter;
	}
    
    Filter myFilter = new Filter() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void publishResults(CharSequence constraint, FilterResults results) {
			glossariesList = (ArrayList<Glossary>) results.values;
	          if (results.count > 0) {
	           notifyDataSetChanged();
	          } else {
	              notifyDataSetInvalidated();
	          } 
			
		}
		
		@Override
		public FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();   
	         ArrayList<Glossary> tempGlossaryList=new ArrayList<Glossary>();

	         if(constraint != null && glossariesListForSearch!=null) {
	             int length=glossariesListForSearch.size();
	             Log.i("Filtering", "glossaries size"+length);
	             int i=0;
	                while(i<length){
	                	Glossary item=glossariesListForSearch.get(i);
	                	// Real filtering:
	                	if(item.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
	                		tempGlossaryList.add(item);
	                	}
	                    i++;
	                }

	                filterResults.values = tempGlossaryList;
	                filterResults.count = tempGlossaryList.size();
	                Log.i("Filtering", "Filter result count size"+filterResults.count);
	          }
	          return filterResults;
		}
	};
}
