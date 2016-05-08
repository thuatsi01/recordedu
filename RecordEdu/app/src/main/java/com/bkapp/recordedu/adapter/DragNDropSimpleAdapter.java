package com.bkapp.recordedu.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bkapp.recordedu.R;
import com.bkapp.recordedu.view.CustomListView;

public class DragNDropSimpleAdapter extends SimpleAdapter implements DragNDropAdapter {

    private int mPosition[];
    private int mHandler;
	private int selectItem = -1;
	private int sizeList = 0;
	private String nameItem = "";

	public DragNDropSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int handler) {
		super(context, data, resource, from, to);
		mHandler = handler;
		sizeList = data.size();
		setup(sizeList);
	}

	private void setup(int size) {
		mPosition = new int[size];
		for (int i = 0; i < size; ++i) {
			mPosition[i] = i;
		}
	}

	@Override
	public View getDropDownView(int position, View view, ViewGroup group) {
		return super.getDropDownView(mPosition[position], view, group);
	}

	@Override
	public Object getItem(int position) {
		return super.getItem(mPosition[position]);
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(mPosition[position]);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(mPosition[position]);
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		View v = super.getView(mPosition[position], view, group);
		TextView item = (TextView) v.findViewById(R.id.edit_item);
		item.setBackgroundResource(R.drawable.echonow_listitem_bg_edit);
		if (mPosition[position] == selectItem) {
			if (!nameItem.equals("")) {
				item.setText(nameItem);
			}
			item.setBackgroundResource(R.drawable.echonow_listitem_bg_highlighted_edit);
		}
		return v;
	}

	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(mPosition[position]);
	}

	@Override
	public void onItemDrag(CustomListView parent, View view, int position, long id) {
	}

	@Override
	public void onItemDrop(CustomListView parent, View view, int startPosition, int endPosition, long id) {
		int position = mPosition[startPosition];
		mPosition[startPosition] = mPosition[endPosition];
		if (startPosition < endPosition)
			for (int i = startPosition; i < endPosition; ++i) {
				mPosition[i] = mPosition[i + 1];
			}
		else if (endPosition < startPosition)
			for (int i = startPosition; i > endPosition; --i) {
				mPosition[i] = mPosition[i - 1];
			}
		mPosition[endPosition] = position;
	}

	@Override
	public int getDragHandler() {
		return mHandler;
	}

	public void selectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	@Override
	public int getSize() {
		return sizeList;
	}

	public void setString(String nameItem) {
		this.nameItem = nameItem;
	}
}
