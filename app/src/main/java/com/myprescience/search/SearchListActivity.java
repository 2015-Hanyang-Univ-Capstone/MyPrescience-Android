package com.myprescience.search;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.material.ProgressBarCircular;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.myprescience.util.Server.LUCENE_API;
import static com.myprescience.util.Server.SEARCH_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class SearchListActivity extends ActionBarActivity {

    private UserData userDTO;

    private ListView mSearchListView;
    private SearchListAdapter mSearchListAdapter;
    private boolean mLockListView = false;

    private EditText mInputQuery;

    Indicator mIndicator;
    private RelativeLayout mSearchLoadingLayout;
    private ProgressBarCircular mProgressBarCircular;

    private final int TRIGGER_SERACH = 1;
    private final long SEARCH_TRIGGER_DELAY_IN_MS = 750;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRIGGER_SERACH) {
                mSearchLoadingLayout.setVisibility(View.GONE);
                mProgressBarCircular.setVisibility(View.GONE);

                mSearchListAdapter = new SearchListAdapter(SearchListActivity.this, userDTO.getId());
                mSearchListView.setAdapter(mSearchListAdapter);
                mSearchListAdapter.notifyDataSetChanged();
                String input = mInputQuery.getText().toString();
                Log.e("Search", input);
                try {
                    new getSearchResult(getApplicationContext()).execute(
                            SERVER_ADDRESS + LUCENE_API + SEARCH_SONGS + URLEncoder.encode(input, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        userDTO = new UserData(getApplicationContext());
        setActionbar();
        mIndicator = new Indicator(this);
        mSearchLoadingLayout = (RelativeLayout) findViewById(R.id.searchLoadingLayout);

        mInputQuery = (EditText) findViewById(R.id.toolbar_search);

        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 변경 후 발생할 이벤트를 작성.
                handler.removeMessages(TRIGGER_SERACH);
                handler.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //텍스트가 변경될때마다 발생할 이벤트를 작성.
                if (mInputQuery.isFocusable()) {
                    mSearchLoadingLayout.setVisibility(View.VISIBLE);
                    mProgressBarCircular.setVisibility(View.VISIBLE);
                }
            }
        };

        //호출
        mInputQuery.addTextChangedListener(watcher);

        mSearchListView = (ListView) findViewById(R.id.searchListView);

        mSearchListAdapter = new SearchListAdapter(this, userDTO.getId());
        mSearchListView.setAdapter(mSearchListAdapter);

        mProgressBarCircular = (ProgressBarCircular) findViewById(R.id.loadingProgressBar);
        mProgressBarCircular.setBackgroundColor(getResources().getColor(R.color.color_base_theme));
    }

    public void setActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("key pressed", String.valueOf(event.getKeyCode()));
        return super.dispatchKeyEvent(event);
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };


    class getSearchResult extends AsyncTask<String, String, String> {

        private Context mContext;

        public getSearchResult(Context _context){
            this.mContext = _context;
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray resultArray = (JSONArray) jsonParser.parse(songJSON);

                if (resultArray.size() == 0) {
                    mSearchListAdapter.addItem(null, getString(R.string.search_no_result1), "");
                    mSearchListAdapter.addItem(null, "", getString(R.string.search_no_result2));
                }

                for(int i = 0; i < resultArray.size(); i ++) {
                    String result = (String) resultArray.get(i);
                    String[] songInfo = result.split("/");
                    if(songInfo.length == 3)
                       mSearchListAdapter.addItem(songInfo[0], songInfo[1], songInfo[2]);
                }
                mSearchListAdapter.notifyDataSetChanged();

            } catch (ParseException e) {
                e.printStackTrace();
                mSearchListAdapter.addItem(null, getString(R.string.search_no_result1), "");
                mSearchListAdapter.addItem(null, "", getString(R.string.search_no_result2));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}