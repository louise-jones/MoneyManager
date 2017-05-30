package com.minidroid.moneymanager.ui.load;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyViewPagerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int layoutId = bundle.getInt("layoutId");
        View v = View.inflate(getActivity(), layoutId, null);
        return v;
    }
}
