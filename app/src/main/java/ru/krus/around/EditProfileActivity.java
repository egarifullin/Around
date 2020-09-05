package ru.krus.around;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.krus.around.Models.User;

public class EditProfileActivity extends AppCompatActivity {

    ImageView closeImage;

    private static final int RC_IMAGE_PICKER = 12;
    private FirebaseStorage storage;
    private StorageReference avatarImagesStorageReference;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private String savedUserName;
    private String savedAvatarUrl;
    private String savedName;
    private String savedPhone;
    private String savedWebSite;
    private String savedAboutUser;
    private String savedUserKey;

    private DatabaseReference userUpdateDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private String savedAvatarImageUri;
    private String newAvatarImageUri;
    String userNameFromIntent;
    private EditText nameInput;
    private EditText userNameInput;
    private EditText webSiteInput;
    private EditText aboutInput;
    private EditText phoneInput;
    private EditText emailInput;


    ImageView updateButton;
    TextView skipButton;
    TextView textTitle;

    Boolean needSkipButton;

    CircleImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        closeImage = findViewById(R.id.close_image);
        userAvatar = findViewById(R.id.profile_image);
        updateButton = findViewById(R.id.updateButton);
        textTitle = findViewById(R.id.titleText);

        nameInput = findViewById(R.id.name_input);
        userNameInput = findViewById(R.id.username_input);
        webSiteInput = findViewById(R.id.website_input);
        aboutInput = findViewById(R.id.bio_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if (intent != null){
            needSkipButton = intent.getBooleanExtra("fromSignIn",false);
            userNameFromIntent = intent.getStringExtra("userName");
        }

        if (needSkipButton){
            closeImage.setVisibility(View.GONE);
            textTitle.setText("Профиль");
        } else {
            closeImage.setVisibility(View.VISIBLE);
            textTitle.setText("Редактирование профиля");
        }

        storage = FirebaseStorage.getInstance();
        avatarImagesStorageReference = storage.getReference().child("avatar_images");

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (usersChildEventListener == null){
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())){
                        savedName = user.getName();
                        savedAvatarUrl = user.getAvatarUrl();
                        savedUserName =  user.getUserName();
                        savedAboutUser = user.getAboutUser();
                        savedWebSite = user.getWebSite();
                        savedPhone = user.getPhoneUser();
                        nameInput.setText(savedName);
                        emailInput.setText(user.getEmail());
                        userNameInput.setText(savedUserName);
                        aboutInput.setText(savedAboutUser);
                        webSiteInput.setText(savedWebSite);
                        phoneInput.setText(savedPhone);
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("")){
                            Picasso.get().load(Uri.parse(user.getAvatarUrl())).into(userAvatar);
                        } else {
                            userAvatar.setImageResource(R.drawable.user_image);
                        }
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            usersDatabaseReference.addChildEventListener(usersChildEventListener);
        }

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent1, "Choose a new avatar"),RC_IMAGE_PICKER);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser(nameInput.getText().toString(), userNameInput.getText().toString(), webSiteInput.getText().toString(), aboutInput.getText().toString(),
                        emailInput.getText().toString(), phoneInput.getText().toString(), mAuth.getCurrentUser().getUid(),savedAvatarUrl, newAvatarImageUri);
                //updateButton.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Avatar set successfully!",Toast.LENGTH_LONG).show();
                if (!needSkipButton){
                    onBackPressed();
                }else{
                    Intent intent = new Intent(EditProfileActivity.this, VideosActivity.class);
                    intent.putExtra("userName", userNameFromIntent);
                    intent.putExtra("avatarUrl",savedAvatarUrl);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
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
                            newAvatarImageUri = downloadUri.toString();
                            if (newAvatarImageUri != null) {
                                Picasso.get().load(Uri.parse(newAvatarImageUri)).into(userAvatar);
                            }
                            Toast.makeText(EditProfileActivity.this, "Avatars downloaded in DB!", Toast.LENGTH_LONG).show();
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


    private void updateUser(String name, String userName, String webSite, String aboutUser, String email, String phoneUser, String id, String avatarUrl, String newAvatarUrl) {
        User user;
        if (newAvatarUrl != null ){
            user = new User(name, userName,webSite, aboutUser, email, phoneUser, id,0 ,newAvatarUrl);
        }else{
            user = new User(name, userName,webSite, aboutUser, email, phoneUser, id,0 ,avatarUrl);
        }

        userUpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        userUpdateDatabaseReference.child(id).setValue(user);

        return;
    }
}
