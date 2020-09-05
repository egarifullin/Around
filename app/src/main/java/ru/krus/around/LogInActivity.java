package ru.krus.around;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import ru.krus.around.Models.User;

public class LogInActivity extends AppCompatActivity {

    private static final int RC_IMAGE_PICKER = 123;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private TextView toogleLoginSignUpTextView;
    private Button loginSignUpButton;
    private boolean loginModeActive;
    private String savedAvatarImageUri;

    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference avatarImagesStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        storage = FirebaseStorage.getInstance();
        avatarImagesStorageReference = storage.getReference().child("avatar_images");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        toogleLoginSignUpTextView = findViewById(R.id.toogleLoginSignUpTextView);
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSignUpUser(emailEditText.getText().toString().trim(),passwordEditText.getText().toString().trim());
            }
        });

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(LogInActivity.this, VideosActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode==RESULT_OK) {
            Uri selectedAvatarUri = data.getData();
            CropImage.activity(selectedAvatarUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference avatarReference = avatarImagesStorageReference.child(resultUri.getLastPathSegment());
                UploadTask uploadTask = avatarReference.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return avatarReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            savedAvatarImageUri = downloadUri.toString();
                            Toast.makeText(LogInActivity.this,"Изображение обновлено!", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void loginSignUpUser(String email, String password) {
        if (!loginModeActive){
            if (passwordEditText.getText().toString().trim().length()<7){
                Toast.makeText(this, "Пароль должен содержать не менее 7 символов!",
                        Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "Введите ваш email!",
                        Toast.LENGTH_LONG).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(LogInActivity.this, VideosActivity.class);
                                    startActivity(intent);
                                    //updateUI(user);
                                } else {
                                    Toast.makeText(LogInActivity.this, "Ошибка входа!",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }

        }else{
            if (!passwordEditText.getText().toString().trim().equals(repeatPasswordEditText.getText().toString().trim())){
                Toast.makeText(this, "Пароли не совпадают!",
                        Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length()<7){
                Toast.makeText(this, "Пароль должен содержать не менее 7 символов!",
                        Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "Введите ваш email!",
                        Toast.LENGTH_LONG).show();
            }
            else{
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(LogInActivity.this, EditProfileActivity.class);
                                    intent.putExtra("fromSignIn", true);
                                    startActivity(intent);

                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LogInActivity.this, "Ошибка входа!",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {

        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName("");
        user.setAvatarUrl("");
        user.setAboutUser("");
        user.setFollowersCount(0);
        user.setFollowingCount(0);
        user.setPhoneUser("");
        user.setPostsCount(0);
        user.setUserName("");
        user.setWebSite("");

        usersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void toogleLogInMode(View view) {
        if (!loginModeActive){
            loginModeActive = true;
            loginSignUpButton.setText("Зарегистрироваться");
            toogleLoginSignUpTextView.setText("Есть аккаунт? Нажмите для входа");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        }else {
            loginModeActive = false;
            loginSignUpButton.setText("Вход");
            toogleLoginSignUpTextView.setText("Зарегистрировать аккаунт!");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }
}
