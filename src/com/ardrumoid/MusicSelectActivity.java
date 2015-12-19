package com.ardrumoid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ardrumoid.data.SelectMusicData;
import com.ardrumoid.http.HttpConnectionManager;
import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicSelectActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MusicSelectActivity";

	private static final String GAE_URL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_select);

		new HttpAsyncTask().execute(GAE_URL);

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
	}

	private class HttpAsyncTask
			extends AsyncTask<String, Boolean, ArrayList<SelectMusicData>> {

		@Override
		protected ArrayList<SelectMusicData> doInBackground(String... params) {
			HttpURLConnection urlConn = null;
			BufferedReader jsonBufR = null;
			ArrayList<SelectMusicData> mData = null;
			JSONObject wholeObject = null;
			JSONObject dataObject = null;
			JSONArray dataArray = null;

			try {
				urlConn = HttpConnectionManager.getHttpURLConnection(params[0]);
				int responseCode = urlConn.getResponseCode();
				if (responseCode >= 200 && responseCode < 300) {
					jsonBufR = new BufferedReader(
							new InputStreamReader(urlConn.getInputStream()));
				} else {
					throw new IOException("responseCode < 200 || responseCode >= 300");
				}

				String line = "";
				StringBuilder buf = new StringBuilder();
				while ((line = jsonBufR.readLine()) != null) {
					buf.append(line);
				}
				
				mData = new ArrayList<SelectMusicData>();
				
				wholeObject = new JSONObject(buf.toString());
				String resultString = wholeObject.getString("result");
				
				if (resultString.equalsIgnoreCase("OK")) {
					dataArray = wholeObject.getJSONArray("data");
					
					for (int i = 0; i < dataArray.length(); i++) {
						SelectMusicData data = new SelectMusicData();
						dataObject = dataArray.getJSONObject(i);
						
						data.title = dataObject.getString("title");
						data.imgUrl = dataObject.getString("imgUrl");
						data.dataUrl = dataObject.getString("dataUrl");
						data.bgUrl = dataObject.getString("bdUrl");
						
						mData.add(data);
					}
				} else {
					mData = null;
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
				mData = null;
			} catch (JSONException je) {
				je.printStackTrace();
				mData = null;
			} catch (NullPointerException e) {
				e.printStackTrace();
				mData = null;
			} finally {
				HttpConnectionManager.setDismissConnection(urlConn, jsonBufR, null);
			}

			return mData;
		}

		@Override
		protected void onPostExecute(ArrayList<SelectMusicData> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			/* 임시 데이터 */
			final ArrayList<SelectMusicData> tempData = new ArrayList<SelectMusicData>();

			if (result == null) {
				Log.e(LOG_TAG, "연결실패");
			} else {
				tempData.clear();
				for (SelectMusicData data: result) {
					tempData.add(data);
				}
			}

			final ImageView leftArrow = (ImageView) findViewById(
					R.id.select_music_arrow_left);
			final ImageView rightArrow = (ImageView) findViewById(
					R.id.select_music_arrow_right);

			SelectPagerAdapter mAdapter = new SelectPagerAdapter(MusicSelectActivity.this,
					tempData);
			final ViewPager mViewPager = (ViewPager) findViewById(
					R.id.select_music_viewpager);
			final TextView tv = (TextView) findViewById(R.id.select_music_viewpager_text);
			tv.setText(tempData.get(0).title);
			mViewPager.setAdapter(mAdapter);
			mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					tv.setText(tempData.get(position).title);
					if (position == 0) {
						leftArrow.setVisibility(View.INVISIBLE);
						rightArrow.setVisibility(View.VISIBLE);
					} else if (position == tempData.size() - 1) {
						leftArrow.setVisibility(View.VISIBLE);
						rightArrow.setVisibility(View.INVISIBLE);
					} else {
						leftArrow.setVisibility(View.VISIBLE);
						rightArrow.setVisibility(View.VISIBLE);
					}

				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
		}

	}

	private class SelectPagerAdapter extends PagerAdapter {

		private Context mContext;
		private ArrayList<SelectMusicData> mData;
		private LayoutInflater inflater;

		public SelectPagerAdapter(Context context, ArrayList<SelectMusicData> data) {
			super();
			this.mContext = context;
			this.mData = data;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.wtf(LOG_TAG, "posiiton : " + position);
			// ImageView iv = new ImageView(mContext);
			SimpleDraweeView iv = (SimpleDraweeView) inflater
					.inflate(R.layout.view_pager_item, null);
			// Glide.with(mContext).load(mData.get(position)).into(iv);
			Uri uri = Uri.parse(mData.get(position).imgUrl);
			iv.setImageURI(uri);
			container.addView(iv);

			final int p = position;
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MainApp.getPort() == null || MainApp.getConnection() == null) {
						Toast.makeText(MainApp.getContext(), "장치가 연결되지 않았습니다.",
								Toast.LENGTH_SHORT).show();
					} else if (mData.get(p).dataUrl == null) {
						Toast.makeText(MainApp.getContext(), "준비 중입니다.",
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(MusicSelectActivity.this,
								MusicPlayActivity.class);
						intent.putExtra("data", mData.get(p));
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);
					}
				}
			});
			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

}
