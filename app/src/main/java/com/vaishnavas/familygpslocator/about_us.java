package com.vaishnavas.familygpslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Objects;

public class about_us extends AppCompatActivity {
VideoView videoview;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_about_us);
       TextView linktotictac = (TextView) findViewById(R.id.otherapp);
       linktotictac.setMovementMethod(LinkMovementMethod.getInstance());
      TextView linktodm = (TextView) findViewById(R.id.aboutapp);
linktodm.setMovementMethod(LinkMovementMethod.getInstance());
       TextView linktofollow = (TextView) findViewById(R.id.followus);
  linktofollow.setMovementMethod(LinkMovementMethod.getInstance());
   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
   Objects.requireNonNull(getSupportActionBar()).hide();
   videoview = (VideoView)findViewById(R.id.videoView);
   videoview.requestFocus();

       Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
  videoview.setVideoURI(video);
  videoview.start();
       videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override
           public void onCompletion(MediaPlayer mediaPlayer) {
               // before going to loginactivity for registration lets check is the user is alredy login on this device or not ,if yes/ then directly movie to
               // and this will done by global info load data();

              videoview.start();
           }
       });
     }
}