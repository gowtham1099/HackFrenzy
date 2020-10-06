package com.example.hackfrenzy;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Video_Adapter extends RecyclerView.Adapter<Video_Adapter.viewHolder> {

    private ArrayList<Status_Model> arrayList;
    private Context context;

    public Video_Adapter() {
    }

    public Video_Adapter(Context context, ArrayList<Status_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_status_video_template,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final Status_Model status_model=arrayList.get(position);
        holder.textView.setText(status_model.getTittle());
        holder.desc.setText(status_model.getDescription());
        holder.videoView.setVideoPath(status_model.getVideo_url());

       holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                holder.progressBar.setVisibility(View.GONE);
                mediaPlayer.start();
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.videoView.isPlaying()){
                    holder.videoView.pause();
                    holder.play.setVisibility(View.VISIBLE);
                }
                else{
                    holder.videoView.start();
                    holder.play.setVisibility(View.INVISIBLE);
                    holder.pause.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.pause.setVisibility(View.INVISIBLE);
                        }
                    },1000);
                }
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.desc.getContext(),"Downloading.....",Toast.LENGTH_LONG).show();
                downloadFile(holder.desc.getContext(),status_model.getTittle(),".mp4",DIRECTORY_DOWNLOADS,status_model.getVideo_url());
            }
        });
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(holder.desc.getContext(),MainActivity.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private TextView textView,desc;
        private ProgressBar progressBar;
        private ImageView play,pause,download,arrow;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.status_view);
            textView=itemView.findViewById(R.id.enter_tittle);
            desc=itemView.findViewById(R.id.descr);
            progressBar=itemView.findViewById(R.id.progressBar);
            play=itemView.findViewById(R.id.play);
            pause=itemView.findViewById(R.id.pause);
            download=itemView.findViewById(R.id.download);
            arrow=itemView.findViewById(R.id.arrow);
        }

    }
    public void downloadFile(Context context, String filename, String fileExtension, String destinationDirectory,String url){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, filename + fileExtension);

        downloadManager.enqueue(request);
    }
}
