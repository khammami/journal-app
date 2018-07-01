package com.khammami.imerolium.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.khammami.imerolium.AppExecutors;
import com.khammami.imerolium.data.db.AppDatabase;
import com.khammami.imerolium.data.net.AppNetworkDataSource;
import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.model.SyncPostTask;
import com.khammami.imerolium.utilities.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class AppRepository {
    private static final String TAG = AppRepository.class.getSimpleName();
    private static final String USERS_PATH = "users";
    private static final String POSTS_PATH = "posts";

    private static final Object LOCK = new Object();
    private static AppRepository sInstance;
    private final AppExecutors mExecutors;
    private final AppDatabase db;
    private final AppNetworkDataSource networkResource;
    private final FirebaseFirestore mFirestoreDb;

    private final MutableLiveData<List<Post>> remotePosts =  new MutableLiveData<>();

    private AppRepository(AppDatabase database, AppNetworkDataSource networkResource, AppExecutors executors) {
        this.db = database;
        this.networkResource = networkResource;
        this.mExecutors = executors;

        //firestore init
        this.mFirestoreDb = FirebaseFirestore.getInstance();
}

    public synchronized static AppRepository getInstance(
            AppDatabase database, AppNetworkDataSource networkSource, AppExecutors executors) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppRepository(database, networkSource, executors);
                Log.d(TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<List<Post>> getUserPosts() {
        return db.postDao().getUserPostList(getCurrentUserId());
    }

    public void insertPost(final Post post){

        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //set post id from firestore
                String postId = getNewFireStoreId(post);
                post.setId(postId);

                //local
                db.postDao().insertPost(post);

                //remote
                mFirestoreDb.collection(USERS_PATH).document(post.getUserId())
                        .collection(POSTS_PATH).document(postId).set(post)
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        db.syncPostDao().insertSyncPostTask(
                                AppUtils.createSyncPostTask(post, SyncPostTask.DELETE_ACTION));
                    }
                });
            }
        });
    }

    public LiveData<Post> getPost(String postId) {
        return db.postDao().getPost(postId);
    }

    public void updatePost(final Post post) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //local
                db.postDao().updatePost(post);

                //remote
                getFirestorePostsPath(post.getUserId()).document(post.getId())
                        .set(post).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        db.syncPostDao().insertSyncPostTask(
                                AppUtils.createSyncPostTask(post, SyncPostTask.SET_ACTION));
                    }
                });
            }
        });

    }

    public void deletePost(final Post post) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.postDao().deletePost(post.getId());

                getFirestorePostsPath(post.getUserId()).document(post.getId()).delete()
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        db.syncPostDao().insertSyncPostTask(
                                AppUtils.createSyncPostTask(post, SyncPostTask.DELETE_ACTION));
                    }
                });
            }
        });

    }

    public LiveData<List<Post>> fetchPostListFromFirestore(){
        if (getCurrentUserId() != null) {
            getFirestorePostsPath(getCurrentUserId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Post> posts = task.getResult().toObjects(Post.class);
                        remotePosts.postValue(posts);
                    } else {
                        remotePosts.postValue(null);
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        return remotePosts;
    }

    public void updateLocalPost(final List<Post> remotePosts) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Post> localPosts = db.postDao().getPostList(getCurrentUserId());
                //local db empty & remote db contain data
                if (remotePosts != null && remotePosts.size() > 0 && localPosts.size() == 0) {
                    db.postDao().insertPosts(remotePosts);
                }

                //remote db empty & local db contain data
                if (remotePosts != null && remotePosts.size() == 0 && localPosts.size() > 0) {
                    ceateFirestoreBatchWrite(localPosts).commit();
                }

                //
                if(remotePosts != null && remotePosts.size() > 0 && localPosts.size() > 0){
                    //List<SyncPostTask> syncTasks = db.syncPostDao().getSyncPostTasks(getCurrentUserId());

                    List<Post> rTmp = new ArrayList<>(remotePosts);
                    rTmp.removeAll(localPosts);

                    //add and update local posts
                    for (Post p : rTmp){
                        Post tmpPost = db.postDao().getPost(p.getId()).getValue();
                        if (tmpPost == null){
                            db.postDao().insertPost(p);
                        } else if (p.getUpdatedAt().after(tmpPost.getUpdatedAt())){
                            db.postDao().updatePost(p);
                        }
                    }

                    //batch posts to firestore & firestore rules will take care of add/update data
                    localPosts.removeAll(remotePosts);
                    ceateFirestoreBatchWrite(localPosts).commit();
                }

            }
        });
    }

    private String getNewFireStoreId(Post post){
        return mFirestoreDb.collection(USERS_PATH).document(post.getUserId())
                .collection(POSTS_PATH)
                .document().getId();
    }

    private CollectionReference getFirestorePostsPath(String userId){
        return mFirestoreDb.collection(USERS_PATH).document(userId)
                .collection(POSTS_PATH);
    }

    private String getCurrentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    private WriteBatch ceateFirestoreBatchWrite(List<Post> posts){
        WriteBatch batch = mFirestoreDb.batch();
        for (Post p: posts){
            DocumentReference pRef = getFirestorePostsPath(getCurrentUserId())
                    .document(p.getId());
            batch.set(pRef, p);
        }
        return batch;
    }
}
