package com.example.opencvarduinobluetoothrobotv1.ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Blue Tooth Options");
    }

    public LiveData<String> getText() {
        return mText;
    }
}