package com.example.doc_app_android.DoctorHomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.doc_app_android.Adapter.PatientDetailsAdapter;
import com.example.doc_app_android.R;
import com.example.doc_app_android.actvities.SignNextActivity;
import com.example.doc_app_android.actvities.sign_as;
import com.example.doc_app_android.data_model.ProfileData;
import com.example.doc_app_android.databinding.ActivityHomeBinding;
import com.example.doc_app_android.databinding.FragmentDataLoaderBinding;
import com.example.doc_app_android.view_model.DataLoaderViewModel;

import java.util.ArrayList;
import java.util.Objects;


public class DataLoaderFragment extends Fragment {

    private RecyclerView rcv;
    private ArrayList<ProfileData> data1 = new ArrayList<>();
    private static String id;
    private DataLoaderViewModel dataLoaderViewModel;
    private PatientDetailsAdapter patientDetailsAdapter;
    private DataLoaderFragment fragment;
    private FragmentDataLoaderBinding binding;
    private ActivityHomeBinding activityHomeBinding;
    public String query_text = "";


    public DataLoaderFragment() {
        // Required empty public constructor
    }


    public static DataLoaderFragment newInstance(String problemId) {
        DataLoaderFragment fragment = new DataLoaderFragment();
        id = problemId;
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_loader, container, false);
        binding.setLifecycleOwner(this);
        patientDetailsAdapter = new PatientDetailsAdapter(getContext(), binding);
        binding.detailsRcv.setAdapter(patientDetailsAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(binding.detailsRcv.getItemAnimator())).setSupportsChangeAnimations(false);

        binding.dataLoadingProgressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.detailsRcv.setLayoutManager(mLayoutManager);
        dataLoaderViewModel = new ViewModelProvider(requireActivity()).get(DataLoaderViewModel.class);
        dataLoaderViewModel.getPatientListForDoctorHome(id).observe(getViewLifecycleOwner(), new Observer<ArrayList<ProfileData>>() {
            @Override
            public void onChanged(ArrayList<ProfileData> profileData) {
                patientDetailsAdapter.setdata(profileData);
                patientDetailsAdapter.notifyDataSetChanged();
                data1 = profileData;
                binding.dataLoadingProgressBar.setVisibility(View.GONE);
                if (profileData.isEmpty()) {
                    if (binding.dataFragmentContainer != null) {
//                        binding.dataFragmentContainer.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.no_data));
                        binding.detailsRcv.setVisibility(View.GONE);
                        binding.noDataPres.setVisibility(View.VISIBLE);
                        binding.noDataPres1.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("Testing", "binding.dataFragmentContainer is null");
                    }
                }
            }
        });

        EditText view = requireActivity().findViewById(R.id.edit_search);
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TextWatcher", "Entered Text:"  + s.toString());
                patientDetailsAdapter.getFilter().filter(s.toString());

            }
        });

        binding.bottomNavigationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SignNextActivity.class);
                intent.putExtra("doctorRegistration", false);
                intent.putExtra("catcher", true);
                intent.putExtra("FROM_DATA_LOADER_FRAGMENT", true);
                startActivity(intent);
            }
        });

        binding.detailsRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                binding.swipeToRefresh.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });

        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataLoaderViewModel.refresh(id);
                binding.swipeToRefresh.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }



}