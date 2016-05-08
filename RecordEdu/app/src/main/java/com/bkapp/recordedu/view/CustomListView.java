package com.bkapp.recordedu.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Adapter;
import android.widget.WrapperListAdapter;

import com.bkapp.recordedu.adapter.DragNDropAdapter;

public class CustomListView extends ListView {

	public static interface OnItemDragNDropListener {
		public void onItemDrag(CustomListView parent, View view, int position,
                               long id);

		public void onItemDrop(CustomListView parent, View view,
                               int startPosition, int endPosition, long id);
	}

	boolean mDragMode;
	Context context;
	WindowManager mWm;
	int mStartPosition = INVALID_POSITION;
	int mDragPointOffset; // Used to adjust drag view location
	int mDragHandler = 0;
	private int previousPosition;
	public ArrayList<Float> locationItem;
	// private TranslateAnimation aminationItem;
	private float locationYV, locationVNext;

	ImageView mDragView;

	OnItemDragNDropListener mDragNDropListener;

	private void init() {
		context = getContext();
		mWm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
	}

	public CustomListView(Context context) {
		super(context);
		init();
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setOnItemDragNDropListener(OnItemDragNDropListener listener) {
		mDragNDropListener = listener;
	}

	public void setDragNDropAdapter(DragNDropAdapter adapter) {
		mDragHandler = adapter.getDragHandler();
		// sizeList = adapter.getSize();
		setAdapter(adapter);
	}

	/**
	 * If the motion event was inside a handler view.
	 * 
	 * @param ev
	 * @return true if it is a dragging move, false otherwise.
	 */

	public boolean isDrag(MotionEvent ev) {
		if (mDragMode)
			return true;
		if (mDragHandler == 0)
			return false;

		int x = (int) ev.getX();
		int y = (int) ev.getY();

		int startposition = pointToPosition(x, y);

		if (startposition == INVALID_POSITION)
			return false;

		int childposition = startposition - getFirstVisiblePosition();
		View parent = getChildAt(childposition);
		View handler = parent.findViewById(mDragHandler);
		if (handler == null)
			return false;
		int[] locationHanler = new int[2];
		handler.getLocationOnScreen(locationHanler);
		int top = parent.getTop() + handler.getTop();
		int bottom = parent.getBottom() + handler.getBottom();
		int left = locationHanler[0];
		int right = left + handler.getWidth();
		return left <= x && x <= right && top <= y && y <= bottom;
	}

	public boolean isDragging() {
		return mDragMode;
	}

	public View getDragView() {
		return mDragView;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();
		if (action == MotionEvent.ACTION_DOWN && isDrag(ev))
			mDragMode = true;

		if (!mDragMode || !isDraggingEnabled)
			return super.onTouchEvent(ev);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			locationItem = new ArrayList<Float>();
			int first = getFirstVisiblePosition();
			int last = getLastVisiblePosition();
			for (int i = 0; i <= last - first; i++) {
				View v = getChildAt(i);
				locationItem.add(i, v.getY());
			}
			mStartPosition = pointToPosition(x, y);
			previousPosition = mStartPosition;
			if (mStartPosition != INVALID_POSITION) {
				int childPosition = mStartPosition - getFirstVisiblePosition();
				mDragPointOffset = y - getChildAt(childPosition).getTop();
				mDragPointOffset -= ((int) ev.getRawY()) - y;
				View vStartPosition = getChildAt(childPosition);
				locationVNext = vStartPosition.getY();
				startDrag(childPosition, y);
				drag(0, y, previousPosition);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			final int c = (int) ev.getX();
			final int d = (int) ev.getY();
			drag(0, y, this.pointToPosition(c, d));
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		default:
			mDragMode = false;

			if (mStartPosition != INVALID_POSITION) {
				// check if the position is a header/footer
				int actualPosition = pointToPosition(x, y);

				if (actualPosition > (getCount() - getFooterViewsCount()) - 1)
					actualPosition = INVALID_POSITION;

				stopDrag(mStartPosition - getFirstVisiblePosition(),
						actualPosition);
			}
			break;
		}
		return true;
	}

	/**
	 * Prepare the drag view.
	 * 
	 * @param childIndex
	 * @param y
	 */

	private void startDrag(int childIndex, int y) {
		View item = getChildAt(childIndex);
		if (item == null)
			return;
		long id = getItemIdAtPosition(mStartPosition);

		if (mDragNDropListener != null)
			mDragNDropListener.onItemDrag(this, item, mStartPosition, id);

		Adapter adapter = getAdapter();
		DragNDropAdapter dndAdapter;

		// if exists a footer/header we have our adapter wrapped
		if (adapter instanceof WrapperListAdapter) {
			dndAdapter = (DragNDropAdapter) ((WrapperListAdapter) adapter)
					.getWrappedAdapter();
		} else {
			dndAdapter = (DragNDropAdapter) adapter;
		}

		dndAdapter.onItemDrag(this, item, mStartPosition, id);
		item.setDrawingCacheEnabled(true);
		item.setDrawingCacheBackgroundColor(getResources().getColor(
				android.R.color.transparent));

		// Create a copy of the drawing cache so that it does not get recycled
		// by the framework when the list tries to clean up memory

		Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP;
		mWindowParams.x = 0;
		mWindowParams.y = y - mDragPointOffset;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		Context context = getContext();
		ImageView v = new ImageView(context);
		v.setImageBitmap(bitmap);

		mWm.addView(v, mWindowParams);
		mDragView = v;
		item.setVisibility(View.INVISIBLE);
		item.invalidate(); // We have not changed anything else.
	}

	/**
	 * Release all dragging resources.
	 * 
	 * @param childIndex
	 */

	private void stopDrag(int childIndex, int endPosition) {

		if (mDragView == null)
			return;
		int fist = getFirstVisiblePosition();
		View item = getChildAt(childIndex);
		item.setY(locationYV);

		if ((endPosition != INVALID_POSITION || previousPosition != mStartPosition)
				&& !locationItem.get(mStartPosition - fist).equals(locationYV)) {
			long id = getItemIdAtPosition(mStartPosition);

			if (mDragNDropListener != null)
				if (endPosition != INVALID_POSITION)
					mDragNDropListener.onItemDrop(this, item, mStartPosition,
							endPosition, id);
				else
					mDragNDropListener.onItemDrop(this, item, mStartPosition,
							previousPosition, id);
			Adapter adapter = getAdapter();
			DragNDropAdapter dndAdapter;
			// if exists a footer/header we have our adapter wrapped
			if (adapter instanceof WrapperListAdapter) {
				dndAdapter = (DragNDropAdapter) ((WrapperListAdapter) adapter)
						.getWrappedAdapter();
			} else {
				dndAdapter = (DragNDropAdapter) adapter;
			}
			if (endPosition != INVALID_POSITION)
				dndAdapter.onItemDrop(this, item, mStartPosition, endPosition,
						id);
			else
				dndAdapter.onItemDrop(this, item, mStartPosition,
						previousPosition, id);
		}

		mDragView.setVisibility(GONE);
		mWm.removeView(mDragView);

		mDragView.setImageDrawable(null);
		mDragView = null;

		item.setDrawingCacheEnabled(false);
		item.destroyDrawingCache();

		item.setVisibility(View.VISIBLE);

		invalidateViews(); // We have changed the adapter data, so change
							// everything
		Log.i("EchoEdu", Integer.toString(previousPosition));
		if (previousPosition != mStartPosition
				&& !locationItem.get(mStartPosition - fist).equals(locationYV)) {
			if (mStartPosition < previousPosition)
				for (int i = mStartPosition - fist; i <= previousPosition
						- fist; ++i) {
					getChildAt(i).setY(locationItem.get(i));
				}
			else if (previousPosition < mStartPosition)
				for (int i = mStartPosition - fist; i >= previousPosition
						- fist; --i) {
					getChildAt(i).setY(locationItem.get(i));
				}
		}
		mStartPosition = INVALID_POSITION;
	}

	/**
	 * Move the drag view.
	 * 
	 * @param x
	 * @param y
	 */

	private void drag(int x, int y, int position) {
		if (mDragView == null)
			return;
		// Log.i("EchoEdu", Integer.toString(position));
		if (position >= 0) {
			View v = getChildAt(position - getFirstVisiblePosition());
			locationYV = v.getY();
			v.setY(locationVNext);
			locationVNext = locationYV;
			previousPosition = position;
			Log.i("EchoEdu", Integer.toString(position));
		}
		int[] locationListView = new int[2];
		this.getLocationOnScreen(locationListView);

		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView
				.getLayoutParams();

		int top = locationListView[1];
		int bottom = this.getBottom();

		if (top <= y - mDragPointOffset && y - mDragPointOffset <= bottom)
			layoutParams.y = y - mDragPointOffset;
		else if (top > y - mDragPointOffset)
			layoutParams.y = top;
		else
			layoutParams.y = bottom;
		layoutParams.x = x;
		mWm.updateViewLayout(mDragView, layoutParams);
	}

	private boolean isDraggingEnabled = true;

	public void setDraggingEnabled(boolean draggingEnabled) {
		this.isDraggingEnabled = draggingEnabled;
	}
}
