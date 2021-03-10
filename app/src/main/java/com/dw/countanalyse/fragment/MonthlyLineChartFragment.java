package com.dw.countanalyse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dw.countanalyse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyLineChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyLineChartFragment extends Fragment {



    public MonthlyLineChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MonthlyLineChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyLineChartFragment newInstance() {
        MonthlyLineChartFragment fragment = new MonthlyLineChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_monthly_line_chart, container, false);
        return root;
    }
}
