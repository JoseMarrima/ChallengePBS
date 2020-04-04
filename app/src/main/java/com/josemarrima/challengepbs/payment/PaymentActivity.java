package com.josemarrima.challengepbs.payment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.josemarrima.challengepbs.R;
import com.josemarrima.challengepbs.data.PaymentDetails;
import com.josemarrima.challengepbs.databinding.ActivityMainBinding;
import com.josemarrima.challengepbs.databinding.DialogAddPaymentBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private View dialogView;
    private int cashAmount = 0, bankTransferAmount = 0, creditCardAmount = 0, total_amount = 0,
            cashBankCreditFlag = 0, counter = 0;
    private PaymentDetails[] paymentDetails;
    private PaymentViewModel paymentViewModel;

    private String[] spinnerItems = new String[]{
            "Cash",
            "Bank Transfer",
            "Credit Card"
    };
    private List<String> spinnerItemsList = new ArrayList<>(Arrays.asList(spinnerItems));

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        binding.addPaymentsTextview.setOnClickListener(view -> showCustomDialogBox());

        // If the File exists, load the data from the file
        if (isFilePresent(getApplicationContext(), "LastPayment.txt")) {
            LoadDataFromFile();
        }

        binding.totalAmount.setText(String.valueOf(total_amount));

        // Save data in the file when clicked on Save button
        binding.saveButton.setOnClickListener(view -> saveDataInFile());
    }

    private void saveDataInFile() {
        paymentViewModel.saveDataInFile(cashAmount, bankTransferAmount, creditCardAmount);
        displayToast("Saved!");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spinnerItem = adapterView.getItemAtPosition(i).toString();

        switch (spinnerItem) {
            case "Cash": {
                cashBankCreditFlag = 1;
                showAdditionalEditBoxes(false);
                break;
            }

            case "Bank Transfer": {
                cashBankCreditFlag = 2;
                showAdditionalEditBoxes(true);
                break;
            }

            case "Credit Card": {
                cashBankCreditFlag = 3;
                showAdditionalEditBoxes(false);
                break;
            }

            default:
                cashBankCreditFlag = 0;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void LoadDataFromFile() {
        paymentDetails = paymentViewModel.loadDataFromFile();

        // If all types of payments have been made, no new payment can be made
        if (paymentDetails[0].getAmount() != 0 && paymentDetails[1].getAmount() != 0 &&
                paymentDetails[2].getAmount() != 0) {
            showAddPayment(false, Color.GRAY);
        }

        if (paymentDetails[0].getAmount() != 0) {
            displayCashChipDetailsFromFile();
        }

        if (paymentDetails[1].getAmount() != 0) {
            displayBankTransferChipDetailsFromFile();
        }

        if (paymentDetails[2].getAmount() != 0) {
            displayCreditCardChipDetailsFromFile();
        }

        binding.cashChip.setOnCloseIconClickListener(view -> removeCashChip());
        binding.bankTransferChip.setOnCloseIconClickListener(view -> removeBankTransferChip());
        binding.creditCardChip.setOnCloseIconClickListener(view -> removeCreditCardChip());

        total_amount = cashAmount + bankTransferAmount + creditCardAmount;
    }

    private void displayCashChipDetailsFromFile() {
        binding.cashChip.setVisibility(View.VISIBLE);
        cashAmount = paymentDetails[0].getAmount();
        binding.cashChip.setText("Cash = Rs. " + cashAmount);
        spinnerItemsList.remove("Cash");
    }

    private void displayBankTransferChipDetailsFromFile() {
        binding.bankTransferChip.setVisibility(View.VISIBLE);
        bankTransferAmount = paymentDetails[1].getAmount();
        binding.bankTransferChip.setText("Bank Transfer = Rs. " + bankTransferAmount);
        spinnerItemsList.remove("Bank Transfer");
    }

    private void displayCreditCardChipDetailsFromFile() {
        binding.creditCardChip.setVisibility(View.VISIBLE);
        creditCardAmount = paymentDetails[2].getAmount();
        binding.creditCardChip.setText("Credit Card = Rs. " + creditCardAmount);
        spinnerItemsList.remove("Credit Card");
    }

    private void removeCashChip() {
        binding.cashChip.setVisibility(View.GONE);
        total_amount -= cashAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        cashAmount = 0;
        spinnerItemsList.add("Cash");
        showAddPayment(true, Color.BLUE);
    }

    private void removeCreditCardChip() {
        binding.creditCardChip.setVisibility(View.GONE);
        total_amount -= creditCardAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        creditCardAmount = 0;
        spinnerItemsList.add("Credit Card");

        showAddPayment(true, Color.BLUE);
    }

    private void removeBankTransferChip() {
        binding.bankTransferChip.setVisibility(View.GONE);
        total_amount -= bankTransferAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        bankTransferAmount = 0;
        spinnerItemsList.add("Bank Transfer");

        showAddPayment(true, Color.BLUE);
    }

    private void showCustomDialogBox() {

        DialogAddPaymentBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(getBaseContext()), R.layout.dialog_add_payment,
                        null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        dialogView = binding.getRoot();
        binding.spinner.setOnItemSelectedListener(this);
        binding.setLifecycleOwner(this);

        builder.setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> { })
                .setNegativeButton("CANCEL", (dialogInterface, i) -> displayToast("Canceled!"))
                .setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            switch (cashBankCreditFlag) {
                case 1: {
                    if (Objects.requireNonNull(binding.amountEditTextDialogbox
                            .getText()).toString().equals("")) {
                        throwErrorForEmptyField(binding.amountEditTextDialogbox);
                        return;
                    }
                    displayCashChip(binding.amountEditTextDialogbox);
                    break;
                }

                case 2: {
                    if (Objects.requireNonNull(binding.providerEditTextDialogbox
                            .getText()).toString().trim().equals("")) {
                        throwErrorForEmptyField(binding.providerEditTextDialogbox);
                        return;
                    }
                    if (Objects.requireNonNull(binding.transactionReferenceEditTextDialogbox
                            .getText()).toString().trim().equals("")) {
                        throwErrorForEmptyField(binding.transactionReferenceEditTextDialogbox);
                        return;
                    }
                    if (Objects.requireNonNull(binding.amountEditTextDialogbox
                            .getText()).toString().equals("")){
                        throwErrorForEmptyField(binding.amountEditTextDialogbox);
                        return;
                    }
                    displayBankTransferChip(binding.amountEditTextDialogbox);
                    break;
                }

                case 3: {
                    if (Objects.requireNonNull(binding.amountEditTextDialogbox
                            .getText()).toString().equals("")){
                        throwErrorForEmptyField(binding.amountEditTextDialogbox);
                        return;
                    }
                    displayCreditCardChip(binding.amountEditTextDialogbox);
                    break;
                }
            }

            displayToast("Added");

            if (cashAmount != 0 && bankTransferAmount != 0 && creditCardAmount != 0) {
                showAddPayment(false, Color.GRAY);
            }

            alertDialog.dismiss();
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.dropdown_menu_popup_item, spinnerItemsList);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinner.setAdapter(spinnerArrayAdapter);
    }

    private void displayCreditCardChip(EditText amountEditText) {
        binding.creditCardChip.setVisibility(View.VISIBLE);
        creditCardAmount = Integer.parseInt(amountEditText.getText().toString());
        binding.creditCardChip.setText("Credit Card = Rs. " + creditCardAmount);
        total_amount += creditCardAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        counter = 3;

        spinnerItemsList.remove("Credit Card");
    }

    private void displayBankTransferChip(EditText amountEditText) {
        binding.bankTransferChip.setVisibility(View.VISIBLE);
        bankTransferAmount = Integer.parseInt((amountEditText).getText().toString());
        binding.bankTransferChip.setText("Bank Transfer = Rs. " + bankTransferAmount);
        total_amount += bankTransferAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        counter = 2;
        spinnerItemsList.remove("Bank Transfer");
    }

    private void displayCashChip(EditText amountEditText) {
        binding.cashChip.setVisibility(View.VISIBLE);
        cashAmount = Integer.parseInt(amountEditText.getText().toString());
        binding.cashChip.setText("Cash = Rs. " + cashAmount);
        total_amount += cashAmount;
        binding.totalAmount.setText(String.valueOf(total_amount));
        counter = 1;
        spinnerItemsList.remove("Cash");
    }

    private void throwErrorForEmptyField(EditText amountEditText) {
        amountEditText.setError(getString(R.string.field_required));
        amountEditText.requestFocus();
    }

    private void showAddPayment(boolean bool, int colour) {
        binding.addPaymentsTextview.setEnabled(bool);
        binding.addPaymentsTextview.setTextColor(colour);
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }

    // To display Provider & Transaction reference editTextBoxes
    private void showAdditionalEditBoxes(boolean bool) {
        if (bool) {
            dialogView.findViewById(R.id.provider_edit_text_input_layout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.transaction_reference_edit_text_input_layout).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.provider_edit_text_input_layout).setVisibility(View.GONE);
            dialogView.findViewById(R.id.transaction_reference_edit_text_input_layout).setVisibility(View.GONE);
        }
    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }
}
