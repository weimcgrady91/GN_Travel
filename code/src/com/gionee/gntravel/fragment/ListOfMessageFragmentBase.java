package com.gionee.gntravel.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gionee.gntravel.R;

@SuppressLint("ValidFragment")
public class ListOfMessageFragmentBase extends Fragment {

	public int bottomLayoutid = -1;
	private View bottomView = null;

	public ListOfMessageFragmentBase() {
	}
	
	public ListOfMessageFragmentBase(int bottomLayoutid) {
		this.bottomLayoutid = bottomLayoutid;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_flight, null);
		ViewGroup bottom_bar = (ViewGroup) view.findViewById(R.id.bottom);

		if (bottomLayoutid == -1) {
			bottomView = inflater.inflate(R.layout.list_of_message_bottom,
					null);

		} else {
			bottomView = inflater.inflate(bottomLayoutid, null);
		}
		bottom_bar.addView(bottomView);
		return view;
	}
	
}
