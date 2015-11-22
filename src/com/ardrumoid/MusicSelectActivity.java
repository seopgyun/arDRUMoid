package com.ardrumoid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.ardrumoid.data.SelectMusicData;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MusicSelectActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MusicSelectActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_select);

		/* 임시 데이터 */
		final ArrayList<SelectMusicData> tempData = new ArrayList<SelectMusicData>();

		SelectMusicData temp = new SelectMusicData();
		temp.title = "Up Above";
		temp.url = "http://";
		tempData.add(temp);
		temp = new SelectMusicData();
		temp.title = "준비중...";
		temp.url = "http://";
		tempData.add(temp);

		final ImageView leftArrow = (ImageView) findViewById(
				R.id.select_music_arrow_left);
		final ImageView rightArrow = (ImageView) findViewById(
				R.id.select_music_arrow_right);

		SelectPagerAdapter mAdapter = new SelectPagerAdapter(this, tempData);
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

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
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
			Uri uri = Uri.parse(mData.get(position).url);
			iv.setImageURI(uri);
			container.addView(iv);

			final int p = position;
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MainApp.getPort() == null || MainApp.getConnection() == null) {
						Toast.makeText(MainApp.getContext(), "장치가 연결되지 않았습니다.",
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
