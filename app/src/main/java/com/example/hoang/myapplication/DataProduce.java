package com.example.hoang.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Aunthencation.PhoneAuthActivity;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.UI.UserDetailActivity;
import com.example.hoang.myapplication.UI.UserTypeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import me.echodev.resizer.Resizer;

public class DataProduce {
    private Uri linkDownload;

    public Uri uploadImage(final Context mContext, Uri resultUri, final StorageReference ref, final DatabaseReference refSave) {
        linkDownload = null;
        try {
            Bitmap resizedImage = new Resizer(mContext)
                    .setTargetLength(1080)
                    .setQuality(50)
                    .setOutputFormat("JPEG")
                    .setOutputFilename("resized_image")
                    .setSourceImage(new File(resultUri.getLastPathSegment()))
                    .getResizedBitmap();

            if (resizedImage != null) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                ref.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                linkDownload = taskSnapshot.getDownloadUrl();
                                if (linkDownload != null) refSave.setValue(linkDownload + "");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Cập nhật thông tin thành công " + (int) progress + "%");
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linkDownload;
    }

    public void linkAccount(AuthCredential authCredential, final FirebaseUser user, final Context context) {
        if (user != null) {
            user.reload();
            user.linkWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user.reload();
                        Log.d("hieuhk", "onComplete: link account "+user.getEmail());

                    } else {
                        Log.d("hieuhk", "failer: link account " + task.getException());

                    }
                }

            });
        }
    }
}
