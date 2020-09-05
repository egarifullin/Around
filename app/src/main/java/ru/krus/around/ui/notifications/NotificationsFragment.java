package ru.krus.around.ui.notifications;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.krus.around.R;
import ru.krus.around.Models.User;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private String avatarUrl;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private String userNameDB;
    private String nameDB;
    private String bioDB;
    private String webSiteDB;
    private int userPostsCountDB;
    private int userFollowersCountDB;
    private int userFollowingCountDB;
    private TextView nicknameToolbarTextView;
    private TextView postsCountTextView;
    private TextView followersCountTextView;
    private TextView followingCountTextView;
    private TextView nameTextView;
    private TextView webSiteTextView;
    private TextView bioTextView;
    private CircleImageView profileAvatar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        nicknameToolbarTextView = root.findViewById(R.id.nicknameToolbarTextView);
        postsCountTextView = root.findViewById(R.id.postsCountTextView);
        followersCountTextView = root.findViewById(R.id.followersCountTextView);
        followingCountTextView = root.findViewById(R.id.followingCountTextView);
        nameTextView = root.findViewById(R.id.nameTextView);
        webSiteTextView = root.findViewById(R.id.websiteTextView);
        bioTextView = root.findViewById(R.id.bioTextView);
        profileAvatar = root.findViewById(R.id.profile_image);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (usersChildEventListener == null){
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        // savedUserName = user.getName();
                        avatarUrl = user.getAvatarUrl();
                        userNameDB = user.getUserName();
                        nameDB = user.getName();
                        bioDB = user.getAboutUser();
                        webSiteDB = user.getWebSite();
                        userPostsCountDB = user.getPostsCount();
                        userFollowersCountDB = user.getFollowersCount();
                        userFollowingCountDB = user.getFollowingCount();
                        nameTextView.setVisibility(View.VISIBLE);
                        if (nameDB != null && !nameDB.equals("")){
                            nameTextView.setVisibility(View.VISIBLE);
                            nameTextView.setText(nameDB);
                        } else {
                            nameTextView.setVisibility(View.GONE);
                        }
                        if (webSiteDB != null && !webSiteDB.equals("")){
                            webSiteTextView.setVisibility(View.VISIBLE);
                            webSiteTextView.setText(webSiteDB);
                        } else {
                            webSiteTextView.setVisibility(View.GONE);
                        }
                        if (bioDB != null && !bioDB.equals("")){
                            bioTextView.setVisibility(View.VISIBLE);
                            bioTextView.setText(bioDB);
                        } else {
                            bioTextView.setVisibility(View.GONE);
                        }
                        if (userNameDB != null) {
                            nicknameToolbarTextView.setText(userNameDB);
                        } else {
                            nicknameToolbarTextView.setText(user.getEmail());
                        }
                        if (postsCountTextView != null){
                            postsCountTextView.setText(String.valueOf(userPostsCountDB));
                        } else {
                            postsCountTextView.setText("0");
                        }
                        if (followersCountTextView != null){
                            followersCountTextView.setText(String.valueOf(userFollowersCountDB));
                        } else {
                            followersCountTextView.setText("0");
                        }
                        if (followingCountTextView != null){
                            followingCountTextView.setText(String.valueOf(userFollowingCountDB));
                        } else {
                            followingCountTextView.setText("0");
                        }
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("")){
                            Picasso.get().load(Uri.parse(user.getAvatarUrl())).into(profileAvatar);
                        } else {
                            profileAvatar.setImageResource(R.drawable.user_image);
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
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}
