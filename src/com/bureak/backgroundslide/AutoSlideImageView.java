/**
 * @Title: AutoSlideImageView.java 
 * @Package com.bureak.backgroundslide 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author BuShiJie bushijie33@gmail.com
 * @date 2014-7-26 下午4:55:28 
 * @version V3.0
 */
package com.bureak.backgroundslide;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * @ClassName: AutoSlideImageView
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author BuShiJie bushijie33@gmail.com
 * @date 2014-7-26 下午4:55:28
 * 
 */
public class AutoSlideImageView extends ImageView {
	public AutoSlideImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		doSlide(context);
	}

	public AutoSlideImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		doSlide(context);
	}

	// 通过水平仪改变移动方向
	private static final String TAG = "BackgroundSlide";
			
	private static SensorManager mSensorManager;
	private static Sensor mSensor;
	static final int SENSORLEFT = 1;
	static final int SENSORRIGHT = 2;
	static final int SENSORNOMORE = 3;
	private Matrix mMatrix = new Matrix();
	float mScaleFactor;
	private RectF mDisplayRect = new RectF();
	private ValueAnimator mCurrentAnimator;
	private static int MODE = 0;
	private int AfterMode = 0;
	private Drawable mDrawable;

	public AutoSlideImageView(Context context) {
		super(context);
		doSlide(context);
	}

	/**
	 * @param imageView
	 * @param context
	 * @Title: doSlide
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void doSlide(Context context) {
		mDrawable = getDrawable();
		if (mDrawable == null) {
			Toast.makeText(context, "dot find Drawable", Toast.LENGTH_LONG)
					.show();
		} else {
			moveBackground();
			mSensorManager = (SensorManager) context
					.getSystemService(context.SENSOR_SERVICE);
			mSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
			if (null == mSensorManager) {
				Toast.makeText(context, "deveice not support SensorManager",
						Toast.LENGTH_LONG).show();
			}
			mSensorManager.registerListener(mSensorListener, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME

		}
	}

	protected SensorEventListener mSensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor == null) {
				return;
			}

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				int x = (int) event.values[0];
				int y = (int) event.values[1];
				int z = (int) event.values[2];
				Log.d(TAG, "___x:" + x + "___y:" + y + "___z:" + z);
				if (x > 0) {// 左倾
					MODE = SENSORLEFT;
				} else if (x == 0) {// 水平
					MODE = SENSORNOMORE;
				} else {
					MODE = SENSORRIGHT;// 右倾
				}
				animate(MODE);
			}
		};

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	};

	protected void animate(int direction) {
		Log.e("", direction + "" + AfterMode);
		if (direction == AfterMode) {
			return;
		}
		switchMoveDirection(direction);
		AfterMode = direction;

	}

	private void animate(float from, float to) {
		mCurrentAnimator = ValueAnimator.ofFloat(from, to);
		mCurrentAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				float value = (Float) animation.getAnimatedValue();
				mMatrix.reset();
				mMatrix.postScale(mScaleFactor, mScaleFactor);
				mMatrix.postTranslate(value, 0);
				setImageMatrix(mMatrix);
			}
		});
		mCurrentAnimator.setDuration(3000);// 动作时长
		mCurrentAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		mCurrentAnimator.start();
	}

	/**
	 * @Title: switchMoveDirection
	 * @Description: 根据方向改变移动
	 * @param @param direction
	 * @return void
	 * @throws
	 */
	public void switchMoveDirection(int direction) {
		updateDisplayRect();
		switch (direction) {
		case SENSORLEFT://
			Log.d(TAG, "左");
			animate(mDisplayRect.left, mDisplayRect.left
					- (mDisplayRect.right - getWidth()));
			break;
		case SENSORRIGHT://
			Log.d(TAG, "右");
			animate(mDisplayRect.left, 0.0f);
			break;
		case SENSORNOMORE://
			Log.d(TAG, "不动");
			animate(mDisplayRect.left, mDisplayRect.left);
			break;
		}
	}

	private void updateDisplayRect() {
		mDisplayRect.set(0, 0, mDrawable.getIntrinsicWidth(),
				mDrawable.getIntrinsicHeight());
		mMatrix.mapRect(mDisplayRect);
	}

	protected void moveBackground() {
		// TODO Auto-generated method stub
		updateDisplayRect();
		setScaleType(ScaleType.MATRIX);

		post(new Runnable() {

			@Override
			public void run() {
				// mBackground控件所占的屏幕的高度
				int imgHeight = getHeight();
				// 返回这个imageview中的图片的实际 的高度
				int imgIntrinsicHeight = mDrawable.getIntrinsicHeight();

				// imageView控件所占的屏幕的宽度
				float imgWidth = getWidth();

				@SuppressWarnings("unused")
				int imgIntrinsicimgWidth = mDrawable.getIntrinsicWidth();

				/*
				 * 通过img 所在屏幕上的像素，以及图片实际的大小（单位像素） 的高度的 比值来 确定图片的缩放
				 * 以保证，图片在高上面能够填满图片的位置
				 */
				mScaleFactor = ((float) imgHeight / (float) imgIntrinsicHeight);
				mMatrix.postScale(mScaleFactor, mScaleFactor);// w,h

				/**
				 * 图片设置 android:scaleType="matrix" 初始位置为屏幕的一半减去图片的一半
				 */
				// int[] ScreenWidthAndSizeInPx =
				// UIUtils.getScreenWidthAndSizeInPx(BackgroundSlideActivity.this);
				float firstX = 160 - imgWidth / 2;
				// DebugLog.d(imgWidth
				// +"---"+ScreenWidthAndSizeInPx[0]+"--"+ScreenWidthAndSizeInPx[1]+"---"+firstX+"---"+mScaleFactor);
				mMatrix.postTranslate(-(mDisplayRect.right / 8), 0);
				setImageMatrix(mMatrix);
				// animate(SENSORRIGHT);
			}
		});

	}

	public void unregisterListener() {
		mSensorManager.unregisterListener(mSensorListener);
	}
}
