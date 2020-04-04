package com.josemarrima.paymentrepository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.josemarrima.challengepbs.data.PaymentDetails;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PaymentRepository {
    private Application application;

    public PaymentRepository(Application application) {
        this.application = application;
    }

    public void saveDataInFile(int cashAmount, int bankTransferAmount, int creditCardAmount) {
        Gson gson = new Gson();
        PaymentDetails cashPayment = new PaymentDetails(cashAmount, "Cash");
        String jsonCash = gson.toJson(cashPayment);

        String transactionReference = "";
        String provider = "";
        PaymentDetails bankPayment = new PaymentDetails(bankTransferAmount, "Bank Transfer",
                provider, transactionReference);
        String jsonBank = gson.toJson(bankPayment);

        PaymentDetails creditPayment = new PaymentDetails(creditCardAmount, "Credit Card");
        String jsonCredit = gson.toJson(creditPayment);

        String[] jsonArray = new String[]{jsonCash, jsonBank, jsonCredit};

        String jsonFinal = gson.toJson(jsonArray);

        writeToFile(jsonFinal, application);
    }

    // Write user entered data to the "LastPayment.txt"
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput("LastPayment.txt", Context.MODE_PRIVATE));

            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public PaymentDetails[] loadDataFromFile() {
        String jsonDataSavedInFile = readFromFile(application);

        String jsonRaw = jsonDataSavedInFile.replaceAll("\\\\", "")
                .replaceAll("(\"\\{\")", "\\{\"")
                .replaceAll("(\"\\}\")", "\"\\}");

        return new Gson().fromJson(jsonRaw, PaymentDetails[].class);
    }

    // Read data from "LastPayment.txt"
    private String readFromFile(Context context) {

        String jsonDataSavedInFile = "";

        try {
            InputStream inputStream = context.openFileInput("LastPayment.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                jsonDataSavedInFile = stringBuilder.toString();
            }

        } catch (FileNotFoundException e) {
            Log.e("Main activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Main activity", "Can not read file: " + e.toString());
        }

        return jsonDataSavedInFile;
    }
}
