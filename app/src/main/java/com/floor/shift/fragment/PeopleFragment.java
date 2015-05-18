package com.floor.shift.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floor.shift.MainAct;
import com.floor.shift.R;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;

@SuppressLint({ "NewApi", "ValidFragment" })
public class PeopleFragment extends Fragment {
    private MainAct mAct;

    public PeopleFragment(MainAct mainActivity) {
        mAct = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_about, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        CommonMethods.setupUI(getActivity(), view.findViewById(R.id.lay_root));

        mAct.getTitleText().setText(getActivity().getResources().getString(R.string.about));

    }
}
