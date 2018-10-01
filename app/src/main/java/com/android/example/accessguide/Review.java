package com.android.example.accessguide;

class Review {
    private String UserId;
    private String Text;

    Review() {}

    Review(String userId, String text) {
        UserId = userId;
        Text = text;
    }

    public String getUserId() {
        return UserId;
    }

    void setUserId(String userId) {
        UserId = userId;
    }

    public String getText() {
        return Text;
    }

    void setText(String text) {
        Text = text;
    }
}
