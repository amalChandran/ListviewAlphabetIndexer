
package com.amalbit.test;

import java.util.ArrayList;
import java.util.List;

import com.amalbit.test.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener{
	
	

    protected static final String TAG = "MainActivity";
    private LinearLayout mIndexerLayout;
    private ListView mListView;
    private FrameLayout mTitleLayout;
    private TextView mTitleText;
    private RelativeLayout mSectionToastLayout;
    private TextView mSectionToastText;
    private ArrayList<Glossary> glossaries = new ArrayList<Glossary>();
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private AlphabetIndexer mIndexer;
    private ContactListAdapter mAdapter;
    private int lastSelectedPosition = -1;
    
//    private EditText etInput;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
    }

    @SuppressWarnings("deprecation")
    private void initView() {
//    	etInput = (EditText) findViewById(R.id.et_input);
        mIndexerLayout = (LinearLayout) findViewById(R.id.indexer_layout);
        mListView = (ListView) findViewById(R.id.contacts_list);
        mTitleLayout = (FrameLayout) findViewById(R.id.title_layout);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mSectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        mSectionToastText = (TextView) findViewById(R.id.section_toast_text);
        for(int i = 0; i < alphabet.length(); i++) {
            TextView letterTextView = new TextView(this);
            letterTextView.setText(alphabet.charAt(i)+"");
            letterTextView.setTextSize(14f);
            letterTextView.setGravity(Gravity.CENTER);
            LayoutParams params = new LinearLayout.LayoutParams(28, 0, 1.0f);
            letterTextView.setLayoutParams(params);
            letterTextView.setPadding(4, 0, 2, 0);
            mIndexerLayout.addView(letterTextView);
            mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
        }
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,
                new String[] { "display_name", "sort_key" }, null, null, "sort_key");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                Log.e("sortKey from cursor", ""+sortKey);
                Glossary glossary = new Glossary();
                glossary.setName(name);
                glossary.setSortKey(sortKey);
                glossaries.add(glossary);
            } while (cursor.moveToNext());
        }
        
        mAdapter = new ContactListAdapter(this, glossaries);
        startManagingCursor(cursor);
        mIndexer = new AlphabetIndexer(cursor, 1, alphabet);
        mAdapter.setIndexer(mIndexer);
        
        if(glossaries != null && glossaries.size() > 0) {
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(mOnScrollListener);
            mIndexerLayout.setOnTouchListener(mOnTouchListener);
        }
        
//        etInput.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				
//				
//
//					
//				
//				
//				
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
        
    }
    
    private String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
  
        private int lastFirstVisibleItem = -1;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == SCROLL_STATE_IDLE) {
                //mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            	//mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
            } else {
                //mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            // firstVisibleItem corresponding to the index of AlphabetIndexer(eg, B-->Alphabet index is 2)
            int sectionIndex = mIndexer.getSectionForPosition(firstVisibleItem);
            //next section Index corresponding to the positon in the listview
            int nextSectionPosition = mIndexer.getPositionForSection(sectionIndex + 1);
            Log.d(TAG, "onScroll()-->firstVisibleItem="+firstVisibleItem+", sectionIndex="
                    +sectionIndex+", nextSectionPosition="+nextSectionPosition);
            if(firstVisibleItem != lastFirstVisibleItem) {
                MarginLayoutParams params = (MarginLayoutParams) mTitleLayout.getLayoutParams();
                params.topMargin = 0;
                mTitleLayout.setLayoutParams(params);
                mTitleText.setText(String.valueOf(alphabet.charAt(sectionIndex)));
                ((TextView) mIndexerLayout.getChildAt(sectionIndex)).setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                lastFirstVisibleItem = firstVisibleItem;
            }
            
            // update AlphabetIndexer background
            if(sectionIndex != lastSelectedPosition) {
                if(lastSelectedPosition != -1) {
                    ((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                lastSelectedPosition = sectionIndex;
            }
            
            if(nextSectionPosition == firstVisibleItem + 1) {
                View childView = view.getChildAt(0);
                if(childView != null) {
                    int sortKeyHeight = mTitleLayout.getHeight();
                    int bottom = childView.getBottom();
                    MarginLayoutParams params = (MarginLayoutParams) mTitleLayout.getLayoutParams();
                    /*if(bottom < sortKeyHeight) {
                        float pushedDistance = bottom - sortKeyHeight;
                        params.topMargin = (int) pushedDistance;
                        mTitleLayout.setLayoutParams(params);
                    } else {*/
                        if(params.topMargin != 0) {
                            params.topMargin = 0;
                            mTitleLayout.setLayoutParams(params);
                        }
//                    }
                }
            }
            
        }
        
    };
    
    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float alphabetHeight = mIndexerLayout.getHeight();
            float y = event.getY();
            int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
            if (sectionPosition < 0) {
                sectionPosition = 0;
            } else if (sectionPosition > 26) {
                sectionPosition = 26;
            }
            if(lastSelectedPosition != sectionPosition) {
                if(-1 != lastSelectedPosition){
                   ((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                lastSelectedPosition = sectionPosition;
            }
            String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
            int position = mIndexer.getPositionForSection(sectionPosition);
            TextView textView = (TextView) mIndexerLayout.getChildAt(sectionPosition);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    mListView.smoothScrollToPositionFromTop(position,0,1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    mListView.smoothScrollToPositionFromTop(position,0,1);
                    break;
                case MotionEvent.ACTION_UP:
                    //mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mSectionToastLayout.setVisibility(View.GONE);
                default:
                    mSectionToastLayout.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
        
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        return true;
    }
    
    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
        	// Associate searchable configuration with the SearchView
//            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//            SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
//                    .getActionView();
//            searchView.setSearchableInfo(searchManager
//                    .getSearchableInfo(getComponentName()));
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void setupSearchView(MenuItem searchItem) {
    	 
//        if (isAlwaysExpanded()) {
//            mSearchView.setIconifiedByDefault(false);
//        } else {
//            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
//                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        }
 
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if (searchManager != null) {
//            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
// 
//            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
//            for (SearchableInfo inf : searchables) {
//                if (inf.getSuggestAuthority() != null
//                        && inf.getSuggestAuthority().startsWith("applications")) {
//                    info = inf;
//                }
//            }
//            mSearchView.setSearchableInfo(info);
//        }
 
        mSearchView.setOnQueryTextListener(this);
    }
 
    public boolean onQueryTextChange(String newText) {
       // mStatusView.setText("Query = " + newText);
        Log.i("Action Search Query", newText);
        mAdapter.getFilter().filter(newText);
		Log.i("mAdapter", ""+newText);
        return false;
    }
 
    public boolean onQueryTextSubmit(String query) {
        //mStatusView.setText("Query = " + query + " : submitted");
    	Log.i("Action Search Query", query);
        return false;
    }
 
    public boolean onClose() {
       // mStatusView.setText("Closed!");
    	Log.i("Action Search Query", "^%^%^%^%^");
        return false;
    }
 
    protected boolean isAlwaysExpanded() {
        return false;
    }

    

}
