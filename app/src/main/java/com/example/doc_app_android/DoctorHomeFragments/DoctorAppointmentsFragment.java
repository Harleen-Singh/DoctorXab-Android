package com.example.doc_app_android.DoctorHomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.doc_app_android.R;
import com.example.doc_app_android.databinding.FragmentDoctorAppointmentsBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorAppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorAppointmentsFragment extends Fragment {

    private FragmentDoctorAppointmentsBinding binding;
    private Boolean fromCheckupDetailsAppointment = false;
    private AppointmentV2 appointmentV2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorAppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorAppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorAppointmentsFragment newInstance(String param1, String param2) {
        DoctorAppointmentsFragment fragment = new DoctorAppointmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This particular line will hide the default toolbar of the Home Activity when fragment gets opened.
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }


    @Override
    public void onStop() {
        super.onStop();
        // This particular line will show the default toolbar of the Home Activity on Home Page when fragment gets closed.
        if (!fromCheckupDetailsAppointment) {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_appointments, container, false);
        binding.setLifecycleOwner(this);

        binding.doctorAppointmentProgress.setVisibility(View.VISIBLE);

        Bundle bundle1 = getArguments();
        if (bundle1 != null) {
            fromCheckupDetailsAppointment = bundle1.getBoolean("fromCheckupDetailsAppointment", false);
        }
        Bundle bundle = new Bundle();

        appointmentV2 = new AppointmentV2();


        if (fromCheckupDetailsAppointment) {

            bundle.putBoolean("fromDrawerAppointment", false);
            IndividualAppointmentFragment individualAppointmentFragment = new IndividualAppointmentFragment();
            individualAppointmentFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.appointmentsLayoutContainer, individualAppointmentFragment).setReorderingAllowed(true).commit();


        } else {
            bundle.putBoolean("fromDrawerAppointment", true);
            appointmentV2.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.appointmentsLayoutContainer, appointmentV2).setReorderingAllowed(true).commit();

        }

        binding.doctorAppointmentProgress.setVisibility(View.GONE);


        binding.doctorAppointmentBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return binding.getRoot();
    }
}