package com.example.doc_app_android.PatentHomeFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doc_app_android.Adapter.checkupHistoryAdapter;
import com.example.doc_app_android.R;
import com.example.doc_app_android.data_model.CkpHstryData;
import com.example.doc_app_android.databinding.FragmentPatientCheckUpHistoryBinding;
import com.example.doc_app_android.view_model.FragmentChkHstryViewModel;

import java.util.ArrayList;

public class patientCheckUpHistory extends Fragment {

    public patientCheckUpHistory() {
        // Required empty public constructor
    }

    public static patientCheckUpHistory newInstance() {
        patientCheckUpHistory fragment = new patientCheckUpHistory();
        return fragment;
    }

    public FragmentPatientCheckUpHistoryBinding binding;
    public FragmentChkHstryViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_patient_check_up_history,null,false);
        checkupHistoryAdapter adapter = new checkupHistoryAdapter();
        binding.rView.setAdapter(adapter);
        model = new ViewModelProvider(requireActivity()).get(FragmentChkHstryViewModel.class);
        binding.progressbar.setVisibility(View.VISIBLE);
        model.getChkHstryData().observe(getViewLifecycleOwner(), new Observer<ArrayList<CkpHstryData>>() {
            @Override
            public void onChanged(ArrayList<CkpHstryData> ckpHstryData) {
                Log.e("TAG", "onChanged: " + ckpHstryData);
                adapter.setData(ckpHstryData);
                Log.d("TAG", "onChanged: " + ckpHstryData);
                adapter.notifyDataSetChanged();
                if (ckpHstryData.isEmpty()){
                    binding.noDataPres.setVisibility(View.VISIBLE);
                    binding.noDataPres1.setVisibility(View.VISIBLE);
//                    binding.background.setBackground(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_cloud_off_24));
                }
                binding.swipe2refresh.setRefreshing(false);
                binding.progressbar.setVisibility(View.GONE);
            }
        });

        binding.swipe2refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model.refresh();
            }
        });





        return binding.getRoot();
    }


}