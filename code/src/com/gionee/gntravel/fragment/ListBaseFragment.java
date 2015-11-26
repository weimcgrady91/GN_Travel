package com.gionee.gntravel.fragment;

import android.support.v4.app.Fragment;

public abstract class ListBaseFragment extends Fragment {
	public abstract void loadDate();
	public abstract boolean onKeyBackDown();
}
