package com.hwiandyong.firebase.project.fixtagram.java;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hwiandyong.firebase.project.fixtagram.R;
import com.hwiandyong.firebase.project.fixtagram.databinding.ActivitySignInBinding;
import com.hwiandyong.firebase.project.fixtagram.java.models.User;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ActivitySignInBinding binding;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivitySignInBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();

            // Views
            setProgressBar(R.id.progressBar);

        // Click listeners
        binding.buttonSignIn.setOnClickListener(this);
        binding.buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            if(mAuth.getCurrentUser().getEmail().equals("admin@itc.ac.kr")){
                String username = usernameFromEmail(mAuth.getCurrentUser().getEmail());

                // Write new user
                writeNewUser(mAuth.getCurrentUser().getUid(), username, mAuth.getCurrentUser().getEmail());

                // Go to AdminActivity
                startActivity(new Intent(SignInActivity.this, A_MainActivity.class));
                finish();
            }
            else {
                onAuthSuccess(mAuth.getCurrentUser());
            }
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressBar();
        String email = binding.fieldEmail.getText().toString();
        String password = binding.fieldPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            if(binding.fieldEmail.getText().toString().equals("admin@itc.ac.kr")){
                                Toast.makeText(SignInActivity.this, "관리자 로그인",
                                        Toast.LENGTH_SHORT).show();
                                String username = usernameFromEmail(task.getResult().getUser().getEmail());

                                // Write new user
                                writeNewUser(task.getResult().getUser().getUid(), username, task.getResult().getUser().getEmail());

                                // Go to AdminActivity
                                startActivity(new Intent(SignInActivity.this, A_MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(SignInActivity.this, "로그인 성공",
                                        Toast.LENGTH_SHORT).show();
                                onAuthSuccess(task.getResult().getUser());
                            }

                        } else {
                            Toast.makeText(SignInActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressBar();
        String email = binding.fieldEmail.getText().toString();
        String password = binding.fieldPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "회원가입 성공",
                                    Toast.LENGTH_SHORT).show();
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "회원가입 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.fieldEmail.getText().toString())) {
            binding.fieldEmail.setError("이메일을 입력해주세요");
            result = false;
        } else {
            binding.fieldEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.fieldPassword.getText().toString())) {
            binding.fieldPassword.setError("비밀번호를 입력해주세요");
            result = false;
        } else {
            binding.fieldPassword.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            signIn();
        } else if (i == R.id.buttonSignUp) {
            signUp();
        }
    }
}
