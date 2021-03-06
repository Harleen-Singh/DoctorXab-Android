package com.example.doc_app_android.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.doc_app_android.data_model.PrescData;
import com.example.doc_app_android.services.prescService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentPrescViewModel extends AndroidViewModel {
    prescService service = new prescService();
    private Application app;
    public FragmentPrescViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = application;
    }
    public LiveData<ArrayList<PrescData>> getPrescData(){
        return service.getPrescData(app);
    }

    public void refresh(){
        service.loadData();
    }
}
