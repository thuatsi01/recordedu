package com.bkapp.recordedu.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bkapp.recordedu.R;

public class CustomListEcho extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> resource = new ArrayList<String>();
    private LayoutInflater inflater;
    private int selectItem = -1;
    private OnClickNextButton mCallNextListEcho;
    private boolean check = true;

	public interface OnClickNextButton {
		public void onNextListEcho(int position);
	}

	public CustomListEcho(Context context, int resource, ArrayList<String> objects, boolean check) {
		super(context, resource, objects);
		this.context = context;
		this.resource = objects;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.check = check;
		mCallNextListEcho = (OnClickNextButton) this.context;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View viewItem = inflater.inflate(R.layout.custom_list_echo_item, parent, false);
		final TextView mTVItem = (TextView) viewItem.findViewById(R.id.item_echolist);
		mTVItem.setText(resource.get(position));
		final ImageButton mIBNext = (ImageButton) viewItem.findViewById(R.id.button_next);
		if (!check) {
			mIBNext.setVisibility(View.INVISIBLE);
			mIBNext.setEnabled(false);
		} else {
			if (position == this.selectItem)
				mIBNext.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCallNextListEcho.onNextListEcho(position);
					}
				});
		}
		mIBNext.setFocusable(false);
		mIBNext.setFocusableInTouchMode(false);
		highlightListItem(position, mTVItem);

		return viewItem;
	}

	public void highlightListItem(int positionItem, TextView mTVSelected) {
		if (positionItem == this.selectItem) {
			mTVSelected.setBackgroundResource(R.drawable.echonow_listitem_bg_highlighted);
		}
	}

	public void selectItem(int selectItem) {
		this.selectItem = selectItem;
	}

}
