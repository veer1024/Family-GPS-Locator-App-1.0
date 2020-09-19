package com.vaishnavas.familygpslocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class loginactivity extends AppCompatActivity {
    private Spinner spinner;
    EditText phonenumber;
    FirebaseAuth mAuth;
    String code;
    String otpentered;
    String phoneNumber;
    private ProgressBar progressbar;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        spinner = (Spinner) findViewById(R.id.spinnercontriescode);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

    }



    private void verifyingotpcode(String otpcode) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpcode, otpentered);
        signInWithPhoneAuthCredential(credential);
    }


    public void sendotp(String phoneNumber) {
        LayoutInflater verification = getLayoutInflater();
        View verifying = verification.inflate(R.layout.verifyinguser, null);
        final EditText otp = (EditText) verifying.findViewById(R.id.otp);

        Button otpdone = (Button) verifying.findViewById(R.id.otpdone);

        otpdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpentered = otp.getText().toString().trim();
                if(otpentered.isEmpty() || otpentered.length()<6){
                    Toast.makeText(loginactivity.this,"Enter the valid otp...",Toast.LENGTH_LONG).show();
                }
               else {
                    progressbar.setVisibility(View.VISIBLE);
                    verifyingotpcode(code);

                }
            }
        });
        // (sending otp
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // sending otp )

        AlertDialog alertDialogverification = new AlertDialog.Builder(loginactivity.this).setView(verifying).create();
        alertDialogverification.show();

    }

    // ( sending otp
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // ( this method called when code is sent
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            code = s;
        }

        // this method called when code is sent )
        // ( this method is called for automatic detection of code, if this get succed user do not have to enter the code manullay
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            otpentered = phoneAuthCredential.getSmsCode();
            if (otpentered != null) {
                progressbar.setVisibility(View.VISIBLE);
                verifyingotpcode(code);
            }
        }
        // this method is called for automatic detection of code )
// ( if the verfication get fail due to wrong code this method will call
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(loginactivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // if the verfication get fail due to wrong code this method will call )
    };
// sending otp)

    // ( sign in method , issme verifcation complete hone ke baad login hoga
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser User = mAuth.getCurrentUser();
                            // yaha tak anne par user ka verfication done and aab uska account create kiya ja sa
                           database.getReference("Users").child(phoneNumber).child("status").child("Mapping").setValue("notrunnig");
                      database.getReference("Users").child(phoneNumber).child("status").child("normally").setValue("nottracking");
                     database.getReference("Users").child(phoneNumber).child("letstrack").child("0000000000").setValue("UserName");
                            database.getReference("userContactNumber").child(phoneNumber).setValue(User.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });  // username ka ek collection krne ke liye
                            // ( storing info in the app data
                            GlobalInfo.countrycode=CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                            GlobalInfo.phonenumber=phoneNumber;
                            GlobalInfo globalInfo = new GlobalInfo(loginactivity.this);
                            globalInfo.SaveData();
                            // updating data in firebase
                            GlobalInfo.updatesInfo(phoneNumber);
                            // storing info in the app data )
                            Toast.makeText(loginactivity.this,"login done",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(loginactivity.this,loading.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(loginactivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    };

    public void register(View view) {
        //( real procedure for phone number registration and verification
        String countrycode = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        String Number = phonenumber.getText().toString().trim();
        if (Number.isEmpty() || Number.length() < 10) {
            phonenumber.setError("Phone number is required");
            phonenumber.requestFocus();
            return;
        } else {
            phoneNumber = "+" + countrycode + Number;
            sendotp(phoneNumber);
        }
        // real procedure for phone number registration and verification)


    }


//  sign in method , issme verifcation complete hone ke baad login hoga )
}
