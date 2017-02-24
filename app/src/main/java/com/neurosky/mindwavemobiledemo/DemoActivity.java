package com.neurosky.mindwavemobiledemo;


import com.neurosky.connection.TgStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This activity is the man entry of this app. It demonstrates the usage of 
 * (1) TgStreamReader.redirectConsoleLogToDocumentFolder()
 * (2) TgStreamReader.stopConsoleLog()
 * (3) demo of getVersion
 */
public class DemoActivity extends Activity {

	private static final String TAG = DemoActivity.class.getSimpleName();

	Settings set;

	private ViewPager mViewPager;
	private MyViewPagerAdapter mViewPagerAdp;


	ArrayList<MyCustomView> mListView;

	HttpUrlConnection hp =null;
	Map<String,String> m_map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main_view);


		set = new Settings(this);
		set.setConnect_way(0);
		set.setWhich_model(0);

		initView();
		// (1) Example of redirectConsoleLogToDocumentFolder()
		// Call redirectConsoleLogToDocumentFolder at the beginning of the app, it will record all the log.
		// Don't forget to call stopConsoleLog() in onDestroy() if it is the end point of this app.
		// If you can't find the end point of the app , you don't have to call stopConsoleLog()
		TgStreamReader.redirectConsoleLogToDocumentFolder();
		// (3) demo of getVersion
		Log.d(TAG, "lib version: " + TgStreamReader.getVersion());

		mListView = new ArrayList<>();
		mListView.add(new MyCustomView(this,R.drawable.car,0));
		mListView.add(new MyCustomView(this,R.drawable.runcar,1));
		mListView.add(new MyCustomView(this,R.drawable.lala,2));


		mViewPager = (ViewPager)findViewById(R.id.prepare_viewpager);

		mViewPagerAdp = new MyViewPagerAdapter(mListView);
		mViewPager.setAdapter(mViewPagerAdp);
		mViewPager.setCurrentItem(0);

		m_map = new HashMap<String, String>();
		hp = new HttpUrlConnection("www.chaiche.tk",m_map);
		new Thread(mRunnable_sendNetWork).start();

	}
	public Runnable mRunnable_sendNetWork = new Runnable() {
		@Override
		public void run() {


			m_map.clear();
			hp.setMap(m_map);

			final String a = hp.sendHttpURLConnectionPOST("http://www.chaiche.tk/");
			if(a!=""){
				Settings.isConnectNetWork = true;
			}
			else{
				Settings.isConnectNetWork = false;
			}


		}
	};

	private Button btn_adapter = null;

	private ImageView igv1,igv2;

	private Spinner spn_sound;

	private String[] sound_status = {"  關閉","  開啟"};
	ArrayAdapter<String> adp_sound_status;

	private EditText edt;

	private void initView() {

		btn_adapter = (Button) findViewById(R.id.btn_adapter);
		btn_adapter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				set.setWhich_model(mViewPager.getCurrentItem());

				Intent intent = new Intent(DemoActivity.this, GameTestActivity.class);
				Log.d(TAG, "Start the GameTestActivity");

				startActivityForResult(intent, 101);
				//startActivity(intent);
			}
		});


		igv1 = (ImageView)findViewById(R.id.main_igv1);
		igv1.setOnClickListener(igvClick);
		igv1.setVisibility(View.INVISIBLE);
		igv2 = (ImageView)findViewById(R.id.main_igv2);
		igv2.setOnClickListener(igvClick);

		spn_sound = (Spinner)findViewById(R.id.main_spn_sound);
		adp_sound_status = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sound_status);
		spn_sound.setAdapter(adp_sound_status);

		spn_sound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Settings.play_sound = false;
				} else {
					Settings.play_sound = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		edt = (EditText)findViewById(R.id.main_edt);

		edt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				Settings.play_name = edt.getText().toString();
			}
		});


	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode ==RESULT_OK){
			if(requestCode==101){
				Intent intent = new Intent(DemoActivity.this, ShowWaveForm.class);
				Log.d(TAG, "Start the ShowWaveForm");
				startActivity(intent);
			}
		}
	}

	private ImageView.OnClickListener igvClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == igv1){
				set.setConnect_way(0);
				igv2.setVisibility(View.VISIBLE);
				igv1.setVisibility(View.INVISIBLE);
			}
			else{
				set.setConnect_way(1);
				igv1.setVisibility(View.VISIBLE);
				igv2.setVisibility(View.INVISIBLE);
			}
		}
	};



	@Override
	protected void onDestroy() {
		
		// (2) Example of stopConsoleLog()
		TgStreamReader.stopConsoleLog();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	private class MyCustomView extends View {

		private Movie mMovie;
		private long mMovieStart = 0;

		int whichmodel;

		Bitmap bmp_left,bmp_right;



		public MyCustomView(Context context,int id,int whichmodel) {
			super(context);
			// 以文件流的方式读取文件

			mMovie = Movie.decodeStream(getResources().openRawResource(id));

			this.whichmodel  = whichmodel;

			bmp_left = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_left);
			bmp_right = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_right);
		}

		@Override
		protected void onDraw(Canvas canvas) {



			long curTime = android.os.SystemClock.uptimeMillis();
			// 第一次播放
			if (mMovieStart == 0) {
				mMovieStart = curTime;
			}

			if (mMovie != null) {
				int duration = mMovie.duration();

				int relTime = (int) ((curTime - mMovieStart) % duration);
				mMovie.setTime(relTime);
				mMovie.draw(canvas, getWidth()/2-(mMovie.width()/2), getHeight()/2-(mMovie.height()/2));

				// 强制重绘

				invalidate();
			}

			super.onDraw(canvas);
			if(whichmodel==0 || whichmodel ==1){
				canvas.drawBitmap(bmp_right,getWidth()-(bmp_right.getWidth()*2),getHeight()/2-(bmp_right.getHeight()/2),null);
			}
			if(whichmodel==1 || whichmodel ==2){
				canvas.drawBitmap(bmp_left,(bmp_left.getWidth()),getHeight()/2-(bmp_left.getHeight()/2),null);
			}

		}
		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
								int bottom) {
			// TODO Auto-generated method stub
			super.onLayout(changed, left, top, right, bottom);
			Log.d(TAG, "onLayout"+whichmodel);

		}

	}

	public class MyViewPagerAdapter extends PagerAdapter{

		ArrayList<MyCustomView> mListView;

		public MyViewPagerAdapter(ArrayList<MyCustomView> arrayList){
			this.mListView = arrayList;

		}
		@Override
		public int getCount() {
			return mListView.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListView.get(position), 0);

			return mListView.get(position);
		}
		@Override
		public void destroyItem(ViewGroup container, int position,
								Object object) {
			container.removeView(mListView.get(position));

		}

		@Override
		public int getItemPosition(Object object) {

			return super.getItemPosition(object);
		}
	}

	public void goTest(View v){
		Intent it = new Intent(this, RecordActivity.class);
		startActivity(it);
	}

}
