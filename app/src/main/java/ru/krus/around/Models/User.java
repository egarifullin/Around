package ru.krus.around.Models;

public class User {
    private String name;
    private String userName;
    private String webSite;
    private String aboutUser;
    private String email;
    private String phoneUser;
    private String id;
    private int avatarMockUpResource;
    private String avatarUrl;
    private int postsCount;
    private int followersCount;
    private int followingCount;


    public User(){

    }

    public User(String name, String userName, String webSite, String aboutUser, String email, String phoneUser, String id, int avatarMockUpResource, String avatarUrl) {
        this.name = name;
        this.userName = userName;
        this.webSite = webSite;
        this.aboutUser = aboutUser;
        this.email = email;
        this.phoneUser = phoneUser;
        this.id = id;
        this.avatarMockUpResource = avatarMockUpResource;
        this.avatarUrl = avatarUrl;
    }

    public User(String name, String userName, String webSite, String aboutUser, String email, String phoneUser, String id, int avatarMockUpResource, String avatarUrl, int postsCount, int followersCount, int followingCount) {
        this.name = name;
        this.userName = userName;
        this.webSite = webSite;
        this.aboutUser = aboutUser;
        this.email = email;
        this.phoneUser = phoneUser;
        this.id = id;
        this.avatarMockUpResource = avatarMockUpResource;
        this.avatarUrl = avatarUrl;
        this.postsCount = postsCount;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getAvatarMockUpResource() {
        return avatarMockUpResource;
    }

    public void setAvatarMockUpResource(int avatarMockUpResource) {
        this.avatarMockUpResource = avatarMockUpResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
