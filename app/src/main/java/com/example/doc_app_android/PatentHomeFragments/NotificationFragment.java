package com.example.doc_app_android.PatentHomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doc_app_android.Adapter.notificationAdapter;
import com.example.doc_app_android.R;
import com.example.doc_app_android.data_model.NotiData;
import com.example.doc_app_android.databinding.FragmentNotificationBinding;
import com.example.doc_app_android.view_model.FragmentNotificationViewModel;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    public FragmentNotificationBinding binding;
    public FragmentNotificationViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification,null,false);
        notificationAdapter adapter = new notificationAdapter(requireContext(), getChildFragmentManager());
        binding.rView.setAdapter(adapter);
        model = new ViewModelProvider(requireActivity()).get(FragmentNotificationViewModel.class);
        binding.progressbar.setVisibility(View.VISIBLE);
        model.getNotiData().observeForever( new Observer<ArrayList<NotiData>>() {
            @Override
            public void onChanged(ArrayList<NotiData> notiData) {
                Collections.reverse(notiData);
                adapter.setData(notiData);
                adapter.notifyDataSetChanged();
                if (notiData.isEmpty()){
                    binding.rView.setVisibility(View.GONE);
                    binding.emptyNotificationList.setVisibility(View.VISIBLE);
                    binding.emptyNotificationListMessage.setVisibility(View.VISIBLE);
                }
                binding.progressbar.setVisibility(View.GONE);
                binding.swipe2refresh.setRefreshing(false);
            }
        });

        binding.swipe2refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model.refresh();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return binding.getRoot();
    }
}