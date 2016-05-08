package com.bkapp.recordedu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bkapp.recordedu.R;

public class CustomDropListEcho extends ArrayAdapter<String> implements SpinnerAdapter {

	private List<String> resource;
    private Context context;
    private LayoutInflater inflater;
    private int i = 0;

	public CustomDropListEcho(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.resource = objects;
		this.context = context;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return resource.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = getCustomView(position, convertView, parent);
		TextView item = (TextView) v.findViewById(R.id.item_echolist);
		item.setBackgroundResource(R.drawable.droplist_button_bg);
		return v;
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View v = inflater.inflate(R.layout.custom_list_echo, parent, false);
		TextView item = (TextView) v.findViewById(R.id.item_echolist);
		item.setText(this.resource.get(position));
		return v;
	}
}
