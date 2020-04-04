package com.josemarrima.challengepbs.payment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.josemarrima.challengepbs.data.PaymentDetails;
import com.josemarrima.paymentrepository.PaymentRepository;

public class PaymentViewModel extends AndroidViewModel {

    private PaymentRepository paymentRepository;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        this.paymentRepository = new PaymentRepository(application);
    }

    void saveDataInFile(int cashAmount, int bankTransferAmount, int creditCardAmount) {
        paymentRepository.saveDataInFile(cashAmount, bankTransferAmount, creditCardAmount);
    }

    PaymentDetails[] loadDataFromFile() {
        return paymentRepository.loadDataFromFile();
    }

}
