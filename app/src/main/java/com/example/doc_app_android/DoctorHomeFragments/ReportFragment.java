package com.example.doc_app_android.DoctorHomeFragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.doc_app_android.R;
import com.example.doc_app_android.data_model.ReportData;
import com.example.doc_app_android.databinding.FragmentReportBinding;
import com.example.doc_app_android.databinding.ProfileDialogBinding;
import com.example.doc_app_android.databinding.ValidationDialogBinding;
import com.example.doc_app_android.view_model.ProfileViewModel;
import com.example.doc_app_android.view_model.ReportDataViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int CAMERA_CAPTURE_CODE = 101;
    public static final int GALLERY_IMAGE_CODE = 102;
    private ProfileDialogBinding profileDialogBinding;
    private Dialog dialog2;
    private Bitmap picture;
    private FragmentReportBinding binding;
    private ContentValues values;
    private Uri imageUri;
    private String xray_id, xray_date, time, category, body_area, report;
    private Dialog dialog;
    private Uri resultUri;
    private ReportData reportData;
    private com.example.doc_app_android.Adapter.ReportData written_data;
    private ReportDataViewModel reportDataViewModel;
    private int patientId;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int permission_count = 0;
    private int mDay, mMonth, mYear, mHour, mMinute;
    private String selectedTime, selectedDate;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // This particular line will hide the default toolbar of the Home Activity when fragment gets opened.
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false);
        binding.setLifecycleOwner(this);

        assert getArguments() != null;
        patientId = getArguments().getInt("patientId");
        reportDataViewModel = new ViewModelProvider(requireActivity()).get(ReportDataViewModel.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);

        String date = sdf.format(new Date());
        String finalDate = date;
        date = "DATE: " + date;


        preferences = requireContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        selectedTime = preferences.getString("X-RAY-TIME-REPORT", "");
        selectedDate = preferences.getString("X-RAY-DATE-REPORT", "");
        binding.xrayIdEdittext.setText(preferences.getString("X-RAY-ID-Report", ""));
        binding.xrayDateEdittext.setText(preferences.getString("X-RAY-DATE-REPORT", ""));
        binding.xrayTimeEdittext.setText(preferences.getString("X-RAY-TIME-REPORT", ""));
        binding.xrayCategoryEdittext.setText(preferences.getString("X-RAY-CATEGORY-REPORT", ""));
        binding.xrayBodyAreaEdittext.setText(preferences.getString("X-RAY-BODYAREA-REPORT", ""));
        binding.xrayCheckupTv.setText(preferences.getString("X-RAY-REPORT-REPORT", ""));
        //String savedUri = preferences.getString("X-RAY-IMAGE-REPORT", "");
//        if(!TextUtils.isEmpty(savedUri)){
//            binding.xrayImageView.setImageURI(Uri.parse(savedUri));
//        }

        binding.xrayDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = calendar.get(Calendar.MONTH);
                mYear = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = String.valueOf(year + "-" + addZeroToStart(String.valueOf(month+1)) + "-" + addZeroToStart(String.valueOf(dayOfMonth)));
                        binding.xrayDateEdittext.setText(selectedDate);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });


        binding.xrayTimeEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime = String.valueOf(addZeroToStart(String.valueOf(hourOfDay)) + ":" + addZeroToStart(String.valueOf(minute)) + ":00");
                        binding.xrayTimeEdittext.setText(selectedTime);
                    }
                }, mHour, mMinute, true);
                timePickerDialog.show();

            }
        });





        binding.xrayCheckupDate.setText(date);

        binding.xrayCheckDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.xrayCheckupTv.setText("");
            }
        });

        binding.xraySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat time1 = new SimpleDateFormat("hh:mm:ss", Locale.US);



                xray_id = binding.xrayIdEdittext.getText().toString();
                xray_date = date1.format(new Date());
                time = time1.format(new Date());
                Log.d("TD", "DATE: " + xray_date);
                Log.d("TD", "TIME: " + time);
                category = binding.xrayCategoryEdittext.getText().toString();
                body_area = binding.xrayBodyAreaEdittext.getText().toString();
                report = binding.xrayCheckupTv.getText().toString();

                preferences = requireContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("X-RAY-ID-Report", xray_id);
                editor.putString("X-RAY-DATE-REPORT", selectedDate);
                editor.putString("X-RAY-TIME-REPORT", selectedTime);
                editor.putString("X-RAY-CATEGORY-REPORT", category);
                editor.putString("X-RAY-BODYAREA-REPORT", body_area);
                editor.putString("X-RAY-REPORT-REPORT", report);
                //editor.putString("X-RAY-IMAGE-REPORT", String.valueOf(resultUri));
                editor.apply();


                dialog = new Dialog(getActivity());
                ValidationDialogBinding binding1 = ValidationDialogBinding.inflate(LayoutInflater.from(getContext()));
                binding1.getRoot().setBackgroundResource(android.R.color.transparent);
                binding1.buttonOk
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                dialog.setCancelable(false);
                dialog.setContentView(binding1.getRoot());
                Window window1 = dialog.getWindow();
                window1.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                if (TextUtils.isEmpty(xray_id) || TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime) || TextUtils.isEmpty(category) || TextUtils.isEmpty(body_area) || TextUtils.isEmpty(report)) {
                    binding1.messageTextview.setText("Please enter all the details.");
                    dialog.show();
                } else if (resultUri == null) {
                    binding1.messageTextview.setText("Please select an image.");
                    dialog.show();
                } else {
                    Toast.makeText(requireContext(), "Filled SuccessFully", Toast.LENGTH_SHORT).show();

                    written_data = new com.example.doc_app_android.Adapter.ReportData(finalDate, report);
                    reportData = new ReportData(patientId, xray_id, selectedDate, selectedTime, category, body_area, 2, resultUri);
                    reportDataViewModel.addPatientReport(reportData, written_data, requireContext(), requireActivity().getSupportFragmentManager());



                }


            }
        });
        // Inflate the layout for this fragment

//        binding.takePictureRetakeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "New Picture");
//                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//                imageUri = requireContext().getContentResolver().insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                try {
//                    startActivityForResult(openCameraIntent, CAMERA_CAPTURE_CODE);
//
//                } catch (ActivityNotFoundException e) {
//                    // display error state to the user
//                    Toast.makeText(getContext(), "Unable to Click Image, please try again!",
//                            Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });

//        binding.takePictureContinueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FinalReportFragment finalReportFragment = new FinalReportFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("image",encodeBitmapImage(picture));
//                finalReportFragment.setArguments(bundle);
//                requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragmentHome_container, finalReportFragment).addToBackStack("report").commit();
//
//            }
//        });


        binding.xrayBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.openCameraAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                profileDialogBinding = ProfileDialogBinding.inflate(LayoutInflater.from(getContext()));


                                dialog2 = new Dialog(getActivity());
                                //profileDialogBinding.getRoot().setBackgroundResource(android.R.color.transparent);
                                dialog2.setContentView(profileDialogBinding.getRoot());
                                dialog2.show();
                                Window window = dialog2.getWindow();
                                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                profileDialogBinding.openCamera.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        values = new ContentValues();
                                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                        imageUri = requireContext().getContentResolver().insert(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        try {
                                            startActivityForResult(openCameraIntent, CAMERA_CAPTURE_CODE);
                                            dialog2.dismiss();
                                            ;
                                        } catch (ActivityNotFoundException e) {
                                            // display error state to the user
                                            Toast.makeText(getContext(), "Unable to Click Image, please try again!",
                                                    Toast.LENGTH_SHORT).show();
                                            dialog2.dismiss();

                                        }

                                    }
                                });

                                profileDialogBinding.openGallery.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(openGalleryIntent, GALLERY_IMAGE_CODE);
                                        dialog2.dismiss();

                                    }
                                });

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                permission_count = permission_count + 1;
                                if(permission_count<2) {
                                    Log.d("PC", "permission count: " + permission_count);
                                    Toast.makeText(getContext(), "Permission Denied",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Permission Denied, Allow Permissions Manually to use the Feature.",
                                            Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();

                            }
                        }).check();


            }
        });

//        binding.takePhotoAdjustment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        binding.takePhotoCrop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Bitmap cropped = binding.takePhotoImageView.getCroppedImage(500, 500);
////                if (cropped != null)
////                    binding.takePhotoImageView.setImageBitmap(cropped);
//
//            }
//        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri pictureUri;


        switch (requestCode) {

            case CAMERA_CAPTURE_CODE:
                if (resultCode == -1) {

                    try {
                        picture = MediaStore.Images.Media.getBitmap(
                                requireContext().getContentResolver(), imageUri);
                        pictureUri = getImageUri(requireContext(), picture);
                        startCrop(pictureUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("debugging", "Bitmap value: " + picture);
                    Log.d("debugging", "" + picture.getHeight());
                    Log.d("debugging", "" + picture.getWidth());
                } else {
                    Toast.makeText(getContext(), "No Picture Clicked", Toast.LENGTH_SHORT).show();
                }
                break;


            case GALLERY_IMAGE_CODE:
                if (resultCode == -1 && data != null) {
                    Uri imageUri = data.getData();
                    startCrop(imageUri);
//                    try {
////                        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
////                        picture = BitmapFactory.decodeStream(inputStream);
////                        binding.takePhotoImageView.setImageBitmap(picture);
//
//
//                        Log.d("debugging", "Selected Picture");
//                        Log.d("debugging", "Bitmap value: " + picture);
//                        Log.d("debugging", "" + picture.getHeight());
//                        Log.d("debugging", "" + picture.getWidth());
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                } else {
                    Toast.makeText(getContext(), "No Picture Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == -1) {
                    assert result != null;
                    resultUri = result.getUri();
                    binding.xrayImageView.setImageURI(resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    assert result != null;
                    Exception error = result.getError();
                }
                break;

            default:
                Toast.makeText(getContext(), "No Picture Selected or Clicked", Toast.LENGTH_SHORT).show();
        }


    }

//    private boolean checkForPermission() {
//
//        ArrayList<String> Permission = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            Permission.add(Manifest.permission.CAMERA);
//        }
//
//        if (!Permission.isEmpty()) {
//            String[] permissions = Permission.toArray(new String[0]);
//            Log.d("PERMISSIONS", String.valueOf(permissions));
//            Log.d("PERMISSIONS", "Size: " + permissions.length);
//            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
//            return false;
//        } else
//            return true;
//
//    }

//    private String encodeBitmapImage(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
//        return android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
//    }


    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(requireContext(), this);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String addZeroToStart(String value) {
        if (value.length() == 1) {
            value = "0" + value;
            return value;
        }
        return value;
    }
}