package com.example.doc_app_android.DoctorHomeFragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doc_app_android.R;
import com.example.doc_app_android.data_model.ProfileData;
import com.example.doc_app_android.databinding.FragmentProfileBinding;
import com.example.doc_app_android.services.ProfileEditService;
import com.example.doc_app_android.view_model.ProfileViewModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProfileViewModel profileViewModel;
    private ProfileData profileData;
    private ProfileEditService profileEditService = new ProfileEditService();


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onResume() {
        super.onResume();
        // This particular line will hide the default toolbar of the Home Activity when fragment gets opened.
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }


    @Override
    public void onStop() {
        super.onStop();
        // This particular line will show the default toolbar of the Home Activity on Home Page when fragment gets closed.
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.setLifecycleOwner(this);


        binding.doctorProfileEdit.setVisibility(View.GONE);
        binding.profileProgress.setVisibility(View.VISIBLE);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getProfileDetails().observe(getViewLifecycleOwner(), new Observer<ProfileData>() {
            @Override
            public void onChanged(ProfileData profileData) {

                if (!TextUtils.isEmpty(profileData.getName()) && !TextUtils.isEmpty(profileData.getEmail()) && !TextUtils.isEmpty(profileData.getPhoneNumber())) {

                    binding.doctorNameText.setText(profileData.getName());
                    binding.emailAddressText.setText(profileData.getEmail());
                    binding.contactText.setText(profileData.getPhoneNumber());
                } else {
                    binding.doctorNameText.setText("Sam Dawson");
                    binding.emailAddressText.setText("example@gmail.com");
                    binding.contactText.setText("9999999999");
                }

                Picasso.get()
                        .load(profileData.getImage())
                        .placeholder(R.drawable.doctor_profile_image)
                        .into(binding.doctorProfileImageSave);

                binding.profileProgress.setVisibility(View.GONE);


            }
        });


        binding.profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(ProfileFragment.this).commit();
            }
        });

        binding.savedSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction().remove(ProfileFragment.this).commit();


            }
        });

        binding.savedEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.doctorProfileSave.setVisibility(View.GONE);
                binding.doctorProfileEdit.setVisibility(View.VISIBLE);

            }
        });

        binding.profileEditBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.doctorProfileEdit.setVisibility(View.GONE);
                binding.doctorProfileSave.setVisibility(View.VISIBLE);

            }
        });


        binding.btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());


                //builder.setPositiveButton()
                String name = binding.doctorName.getText().toString();
                String email = binding.doctorEmail.getText().toString();
                String phoneNumber = binding.doctorPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(phoneNumber)) {
                    builder.setMessage("Please enter Name, Email and Phone Number.");

                } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email)) {
                    builder.setMessage("Please enter Name and Email.");

                } else if(TextUtils.isEmpty(email) && TextUtils.isEmpty(phoneNumber)){
                    builder.setMessage("Please enter Email and Phone Number.");
                } else if(TextUtils.isEmpty(name) && TextUtils.isEmpty(phoneNumber)){
                    builder.setMessage("Please enter Name and Phone Number.");
                } else if( TextUtils.isEmpty(name)){
                    builder.setMessage("Please enter Name.");
                } else if(TextUtils.isEmpty(email)){
                    builder.setMessage("Please enter Email.");
                } else if(TextUtils.isEmpty(phoneNumber)){
                    builder.setMessage("Please enter Phone Number.");
                } else{
                    builder1.setMessage("Do you want to Edit?");
                    builder1.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            profileData = new ProfileData(binding.doctorEmail.getText().toString(), binding.doctorName.getText().toString(), binding.doctorPhoneNumber.getText().toString());
                            profileEditService.init(profileData, getContext());
                            profileViewModel.getEditedProfileDetails().observe(getViewLifecycleOwner(), new Observer<ProfileData>() {
                                @Override
                                public void onChanged(ProfileData profileData) {
                                    binding.emailAddressText.setText(profileData.getEmail());
                                }
                            });

                            getFragmentManager().beginTransaction().remove(ProfileFragment.this).commit();
                        }
                    });

                    builder1.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder1.create();
                    builder1.show();

                }
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create();
                builder.show();




            }
        });

        return binding.getRoot();
    }


}