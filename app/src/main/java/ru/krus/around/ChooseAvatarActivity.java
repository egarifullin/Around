package ru.krus.around;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class ChooseAvatarActivity extends AppCompatActivity {

    private static final int RC_IMAGE_PICKER = 12;
    private FirebaseStorage storage;
    private StorageReference avatarImagesStorageReference;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private String savedUserName;
    private String savedAvatarUrl;
    private String savedUserKey;

    private DatabaseReference userUpdateDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private String savedAvatarImageUri;
    String userNameFromIntent;

    Button updateButton;
    Button skipButton;

    Boolean needSkipButton;

    CircleImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);

        updateButton = findViewById(R.id.update_settings_button);
        skipButton = findViewById(R.id.skip_button);
        userAvatar = findViewById(R.id.set_profile_image);

        updateButton.setVisibility(View.GONE);
        /*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Set your avatar");
        */
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if (intent != null){
            needSkipButton = intent.getBooleanExtra("fromSignIn",false);
            userNameFromIntent = intent.getStringExtra("userName");
        }

        if (needSkipButton){
            skipButton.setVisibility(View.VISIBLE);
        }else{
            skipButton.setVisibility(View.GONE);
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
                        savedUserName = user.getName();
                        savedAvatarUrl = user.getAvatarUrl();
                        if (user.getAvatarUrl() != null){
                            Picasso.get().load(Uri.parse(user.getAvatarUrl())).into(userAvatar);
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
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updateUser(mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getUid(),savedUserName,savedAvatarUrl,savedAvatarImageUri,savedAvatarUrl);
                updateButton.setVisibility(View.GONE);
                Toast.makeText(ChooseAvatarActivity.this, "Avatar set successfully!",Toast.LENGTH_LONG).show();
                if (!needSkipButton){
                    onBackPressed();
                }else{
                    Intent intent = new Intent(ChooseAvatarActivity.this, VideosActivity.class);
                    intent.putExtra("userName", userNameFromIntent);
                    intent.putExtra("avatarUrl",savedAvatarUrl);
                    startActivity(intent);
                }
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
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseAvatarActivity.this, VideosActivity.class);
                intent.putExtra("userName", userNameFromIntent);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                            if (savedAvatarImageUri != null){
                                Picasso.get().load(Uri.parse(savedAvatarImageUri)).into(userAvatar);
                                updateButton.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(ChooseAvatarActivity.this,"Avatars downloaded in DB!", Toast.LENGTH_LONG).show();
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
/*
    private void updateUser(String email, String uid, String savedUserName, String savedAvatarUri, String savedAvatarNewImageUri, String key) {
        User user;
        if (savedAvatarNewImageUri != null ){
            user = new User(savedUserName, email,uid, 0 ,savedAvatarNewImageUri);
        }else{
            user = new User(savedUserName, email,uid, 0 ,savedAvatarUri);
        }

        userUpdateDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        userUpdateDatabaseReference.child(uid).setValue(user);

        return;
    }*/
}
