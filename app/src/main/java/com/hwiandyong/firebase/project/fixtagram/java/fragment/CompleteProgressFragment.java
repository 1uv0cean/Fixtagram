package com.hwiandyong.firebase.project.fixtagram.java.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class CompleteProgressFragment extends A_PostListFragment{
    public CompleteProgressFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("posts").orderByChild("progress").equalTo("처리완료");
    }
}
