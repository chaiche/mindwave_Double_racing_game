package com.neurosky.mindwavemobiledemo;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawWaveView extends View {
	private static final String TAG = "DrawWaveView";
	private Path path,path2;
	public Paint paint = null ,paint2 = null,paintText= null;

	int VIEW_WIDTH= 0;
	int VIEW_HEIGHT = 0;
	Bitmap cacheBitmap = null;
	Canvas cacheCanvas = null;
	Paint bmpPaint = new Paint();
	private int maxPoint = 0;
	private int currentPoint = 0;
	private int maxValue = 0;
	private int minValue = 0;
	private float x = 0;
	private float y = 0;
	private float prex = 0;
	private float prey = 0;
	private boolean restart = true;
	
	private int mBottom = 0;
	private int mHeight = 0;
	private int mLeft = 0;
	private int mWidth = 0;
	
	private float mPixPerHeight = 0;
	private float mPixPerWidth = 0;
	
	public DrawWaveView(Context context){
		super(context);
	}

	public DrawWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		
	}
	public void setValue(int maxPoint, int maxValue, int minValue){
		this.maxPoint = maxPoint;
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
	
	public void initView(){


		mBottom = this.getBottom();
		mWidth = this.getWidth();
		mLeft = this.getLeft();
		mHeight = this.getHeight();
		
		mPixPerHeight = (float)mHeight/(maxValue  - minValue);
		mPixPerWidth =  (float)mWidth/maxPoint ;
		
//		Log.d(TAG,"initView  mWidth= " + mWidth + " , mHeight= " + mHeight  );
//		Log.d(TAG,"initView  mBottom= " + mBottom + " , mLeft= " + mLeft  );
//		Log.d(TAG,"initView  mPixPerHeight= "+ mPixPerHeight +" ,mPixPerWidth=" +mPixPerWidth);
		cacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		cacheCanvas = new Canvas();
		path = new Path();
		path2 = new Path();
		cacheCanvas.setBitmap(cacheBitmap);
		
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		
		paint.setAntiAlias(true);
		paint.setDither(true);
		currentPoint =0;

		paint2 = new Paint(Paint.DITHER_FLAG);
		paint2.setColor(Color.RED);
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setStrokeWidth(4);

		paint2.setAntiAlias(true);
		paint2.setDither(true);
		currentPoint2 =0;


		paintText= new Paint(Paint.DITHER_FLAG);
		paintText.setColor(Color.GREEN);
		paintText.setStyle(Paint.Style.STROKE);
		paintText.setStrokeWidth(1);
		paintText.setAntiAlias(true);
		paintText.setDither(true);
		paintText.setTextSize(25);
	}
	
	public void clear()
	{
		Paint clearPaint = new Paint();
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		cacheCanvas.drawPaint(clearPaint);
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		currentPoint = 0;
		path.reset();
		currentPoint2 = 0;
		path2.reset();

		invalidate();
	}
	public boolean isReady(){
		return initFlag;
	}
	
	public void updateData(int data){
		if(!initFlag){
			return;
		}

		y = translateData2Y(data);
		x = translatePoint2X(currentPoint);
//		Log.d(TAG,"updateData x: " + x + "   , y: " + y);
		if(currentPoint == 0){
			path.moveTo(x, y);
			currentPoint ++;
			prex = x;
			prey = y;
		}else if(currentPoint == maxPoint){
			cacheCanvas.drawPath(path,paint);
			currentPoint = 0;
		}else{
			path.quadTo(prex,prey,x, y);	
			currentPoint ++;
			prex = x;
			prey = y;
		}
		invalidate();
		if(currentPoint == 0){
			clear();
		}
	}

	private float prex2 = 0;
	private float prey2 = 0;
	private int currentPoint2 = 0;

	public void updateDataformediation(int data){
		if(!initFlag){
			return;
		}

		y = translateData2Y(data);
		x = translatePoint2X(currentPoint2);
//		Log.d(TAG,"updateData x: " + x + "   , y: " + y);
		if(currentPoint2 == 0){
			path2.moveTo(x, y);
			currentPoint2 ++;
			prex2 = x;
			prey2 = y;
		}else if(currentPoint2 == maxPoint){
			cacheCanvas.drawPath(path2,paint2);
			currentPoint2 = 0;
		}else{
			path2.quadTo(prex2,prey2,x, y);
			currentPoint2 ++;
			prex2 = x;
			prey2 = y;
		}
		invalidate();
		if(currentPoint2 == 0){
			clear();
		}
	}
	/**
	 * y = top + height - (data -minValue) * height/(2*maxValue)
	 * @param data
	 * @return
	 */
	private float translateData2Y(int data){
		return (float)mBottom - (data - minValue) *mPixPerHeight ;
	}
	/**
	 * x = mLeft + mWidth/
	 * @param point
	 * @return
	 */
	private float translatePoint2X(int point){
		return (float)mLeft + point * mPixPerWidth;
	}
	
    public boolean initFlag = false;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
		canvas.drawPath(path, paint);
		canvas.drawPath(path2, paint2);
		//super.onDraw(canvas);

		paintText.setColor(Color.GREEN);
		canvas.drawText("Attention", mWidth - 150, 20, paintText);
		paintText.setColor(Color.RED);
		canvas.drawText("Meditation",mWidth-150,50,paintText);
		
	}
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		initFlag = false;
		 Log.d(TAG, "onConfigurationChanged");
	}

	// for rotate screen things
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		 Log.d(TAG,"onAttachedToWindow");
		 initFlag = false;
	}
	// for rotate screen things
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		Log.d(TAG,"onLayout");

		 if(!initFlag){
			 initView();
			 initFlag = true;
		 }

	}
}
