package com.hwiandyong.firebase.project.fixtagram.java;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hwiandyong.firebase.project.fixtagram.R;
import com.hwiandyong.firebase.project.fixtagram.databinding.ActivityAPostDetailBinding;
import com.hwiandyong.firebase.project.fixtagram.java.models.Comment;
import com.hwiandyong.firebase.project.fixtagram.java.models.Post;
import com.hwiandyong.firebase.project.fixtagram.java.models.User;

import java.util.ArrayList;
import java.util.List;

public class A_PostDetailActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    private int ival;
    public static int glob=1;
    static SharedPreferences sPref;
    private SharedPreferences.Editor sE;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    private DatabaseReference mProgressReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private A_PostDetailActivity.CommentAdapter mAdapter;
    private ActivityAPostDetailBinding binding;
    private String strchkP;
    private RadioButton rb1,rb2,rb3;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityAPostDetailBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            RadioGroup radioGroup = ((RadioGroup) findViewById(R.id.radio_group));
            radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
            // Get post key from intent
            String mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
            if (mPostKey == null) {
                throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
            }
            rb1 = (RadioButton)findViewById(R.id.rg_btn1);
            rb2 = (RadioButton)findViewById(R.id.rg_btn2);
            rb3 = (RadioButton)findViewById(R.id.rg_btn3);

            sPref = getSharedPreferences("Pref",0);
            sE = sPref.edit();
            ival = sPref.getInt("Pref", 0);

            if(ival == R.id.rg_btn1){
                rb1.setChecked(true);
                glob=1;
            }else if(ival == R.id.rg_btn2){
                rb2.setChecked(true);
                glob=2;
            }else if(ival == R.id.rg_btn3) {
                rb3.setChecked(true);
                glob = 3;
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                      int state = 0;

                                                      public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                          if (checkedId == R.id.rg_btn1) {
                                                              sE.clear();
                                                              sE.putInt("Pref", checkedId);
                                                              sE.apply();
                                                              mProgressReference.setValue("처리대기");
                                                          } else if (checkedId == R.id.rg_btn2) {
                                                              sE.clear();
                                                              sE.putInt("Pref", checkedId);
                                                              sE.apply();
                                                              mProgressReference.setValue("처리중");
                                                          } else if (checkedId == R.id.rg_btn3) {
                                                              sE.clear();
                                                              sE.putInt("Pref", checkedId);
                                                              sE.apply();
                                                              mProgressReference.setValue("처리완료");
                                                          }
                                                      }
                                                  });
            // Initialize Database
            mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child("posts").child(mPostKey);
            mCommentsReference = FirebaseDatabase.getInstance().getReference()
                    .child("post-comments").child(mPostKey);
            mProgressReference = FirebaseDatabase.getInstance().getReference()
                    .child("posts").child(mPostKey).child("progress");

            binding.buttonPostComment.setOnClickListener(this);
            binding.recyclerPostComments.setLayoutManager(new LinearLayoutManager(this));

        }
        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                strchkP= ((RadioButton)findViewById(checkedId)).getText().toString();
            }
        };
        @Override
        public void onStart() {
            super.onStart();

            // Add value event listener to the post
            // [START post_value_event_listener]
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Post post = dataSnapshot.getValue(Post.class);
                    // [START_EXCLUDE]
                    binding.postAuthorLayout.postAuthor.setText(post.author);
                    binding.postTextLayout.postTitle.setText(post.title);
                    binding.postTextLayout.postBody.setText(post.body);
                    // [END_EXCLUDE]
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(A_PostDetailActivity.this, "게시물을 불러오는데 실패하였습니다",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };
            mPostReference.addValueEventListener(postListener);
            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
            mPostListener = postListener;

            // Listen for comments
            mAdapter = new A_PostDetailActivity.CommentAdapter(this, mCommentsReference);
            binding.recyclerPostComments.setAdapter(mAdapter);
        }

        @Override
        public void onStop() {
            super.onStop();
            sPref = getSharedPreferences("Pref",0);
            sE = sPref.edit();
            ival = sPref.getInt("Pref", 0);

            if(ival == R.id.rg_btn1){
                rb1.setChecked(true);
                glob=1;
            }else if(ival == R.id.rg_btn2){
                rb2.setChecked(true);
                glob=2;
            }else if(ival == R.id.rg_btn3) {
                rb3.setChecked(true);
                glob = 3;
            }
            // Remove post value event listener
            if (mPostListener != null) {
                mPostReference.removeEventListener(mPostListener);
            }

            // Clean up comments listener
            mAdapter.cleanupListener();
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.buttonPostComment) {
                postComment();
            }

        }
        private void postComment() {
            final String uid = getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            User user = dataSnapshot.getValue(User.class);
                            String authorName = user.username;

                            // Create new comment object
                            String commentText = binding.fieldCommentText.getText().toString();
                            Comment comment = new Comment(uid, authorName, commentText);

                            // Push the comment, it will appear in the list
                            mCommentsReference.push().setValue(comment);

                            // Clear the field
                            binding.fieldCommentText.setText(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        private static class CommentViewHolder extends RecyclerView.ViewHolder {

            public TextView authorView;
            public TextView bodyView;

            public CommentViewHolder(View itemView) {
                super(itemView);

                authorView = itemView.findViewById(R.id.commentAuthor);
                bodyView = itemView.findViewById(R.id.commentBody);
            }
        }

        private static class CommentAdapter extends RecyclerView.Adapter<A_PostDetailActivity.CommentViewHolder> {

            private Context mContext;
            private DatabaseReference mDatabaseReference;
            private ChildEventListener mChildEventListener;

            private List<String> mCommentIds = new ArrayList<>();
            private List<Comment> mComments = new ArrayList<>();

            public CommentAdapter(final Context context, DatabaseReference ref) {
                mContext = context;
                mDatabaseReference = ref;

                // Create child event listener
                // [START child_event_listener_recycler]
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                        // A new comment has been added, add it to the displayed list
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        // [START_EXCLUDE]
                        // Update RecyclerView
                        mCommentIds.add(dataSnapshot.getKey());
                        mComments.add(comment);
                        notifyItemInserted(mComments.size() - 1);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so displayed the changed comment.
                        Comment newComment = dataSnapshot.getValue(Comment.class);
                        String commentKey = dataSnapshot.getKey();

                        // [START_EXCLUDE]
                        int commentIndex = mCommentIds.indexOf(commentKey);
                        if (commentIndex > -1) {
                            // Replace with the new data
                            mComments.set(commentIndex, newComment);

                            // Update the RecyclerView
                            notifyItemChanged(commentIndex);
                        } else {
                            Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        String commentKey = dataSnapshot.getKey();

                        // [START_EXCLUDE]
                        int commentIndex = mCommentIds.indexOf(commentKey);
                        if (commentIndex > -1) {
                            // Remove data from the list
                            mCommentIds.remove(commentIndex);
                            mComments.remove(commentIndex);

                            // Update the RecyclerView
                            notifyItemRemoved(commentIndex);
                        } else {
                            Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                        // A comment has changed position, use the key to determine if we are
                        // displaying this comment and if so move it.
                        Comment movedComment = dataSnapshot.getValue(Comment.class);
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                        Toast.makeText(mContext, "Failed to load comments.",
                                Toast.LENGTH_SHORT).show();
                    }
                };
                ref.addChildEventListener(childEventListener);
                // [END child_event_listener_recycler]

                // Store reference to listener so it can be removed on app stop
                mChildEventListener = childEventListener;
            }

            @Override
            public A_PostDetailActivity.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.item_comment, parent, false);
                return new A_PostDetailActivity.CommentViewHolder(view);
            }


            @Override
            public void onBindViewHolder(A_PostDetailActivity.CommentViewHolder holder, int position) {
                Comment comment = mComments.get(position);
                holder.authorView.setText(comment.author);
                holder.bodyView.setText(comment.text);
            }

            @Override
            public int getItemCount() {
                return mComments.size();
            }

            public void cleanupListener() {
                if (mChildEventListener != null) {
                    mDatabaseReference.removeEventListener(mChildEventListener);
                }
            }
        }
    }
