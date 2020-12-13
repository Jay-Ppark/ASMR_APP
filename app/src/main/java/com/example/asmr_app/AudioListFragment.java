package com.example.asmr_app;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick{

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView audioList;
    private File[] allFiles;

    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private File fileToPlay;
    private File AfileToSee;
    private File SfileToSee;

    //UI Elements
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;
    private TextView timetext;
    private Button alltext;
    private Button summarytext;
    private TextView showtext;

    private SeekBar playerSeekBar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);

        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        timetext = view.findViewById(R.id.timetext);
        alltext = view.findViewById(R.id.alltext);
        showtext = view.findViewById(R.id.showtext);
        summarytext = view.findViewById(R.id.summarytext);

        playerSeekBar = view.findViewById(R.id.player_seekbar);


        final String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".3gp");
            }
        });

        audioListAdapter = new AudioListAdapter(allFiles, this);

        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);



        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                showtext.setText("Press Button!");
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //cant do anything here for this app
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseAudio();
                } else {
                    if (fileToPlay != null) {
                        resumeAudio();
                    }
                }
            }
        });
        alltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String allmsg = fileToPlay.getName();
                allmsg = allmsg.replaceFirst("3gp","txt");
                allmsg = path + '/' + allmsg;
                AfileToSee = new File(allmsg);
                FileReader fr = null;
                BufferedReader bufrd = null;
                String str = null;
                String showtxt = null;
                if(AfileToSee.exists()) {
                    try {
                        fr = new FileReader(AfileToSee);
                        bufrd = new BufferedReader(fr);

                        while((str = bufrd.readLine()) != null)
                        {
                            showtxt = showtxt + str +'\n';
                        }
                        bufrd.close();
                        fr.close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    showtext.setText(showtxt);
                }else{
                    showtext.setText("No txt file!");
                }
            }
        });

        summarytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String summarymsg = fileToPlay.getName();
                summarymsg = summarymsg.replaceFirst("3gp", "txt");
                summarymsg = path + '/' + summarymsg;
                SfileToSee = new File(summarymsg);
                FileReader fr = null;
                BufferedReader bufrd = null;
                String str = null;
                String showtxt = null;
                if(SfileToSee.exists()) {
                    try {
                        fr = new FileReader(SfileToSee);
                        bufrd = new BufferedReader(fr);

                        while((str = bufrd.readLine()) != null)
                        {
                            showtxt = showtxt + str +'\n';
                        }
                        bufrd.close();
                        fr.close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    showtext.setText(showtxt);
                }else{
                    showtext.setText("No summary file!");
                }
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });

    }

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if(isPlaying) {
            stopAudio();
            //isPlaying = false;
            playAudio(fileToPlay);
        } else {
            //Log.d("here is error","error!");
            playAudio(fileToPlay);
            //isPlaying = true;
        }
        // check for working
        // Log.d("Play Log", "File Playing" + file.getName());
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn,null));
        playerHeader.setText("Paused");
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        isPlaying = true;
        playerHeader.setText("Playing");
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopAudio() {
        //Stop the Audio
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn,null));
        //playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private String timechange() {
        String stime;
        int h=0;
        int m=0;
        int s=0;
        int duration = mediaPlayer.getDuration()/1000;
        if(duration/3600 > 0)
        {
            h=duration/3600;
            duration=duration % 3600;
        }
        if(duration / 60 >= 0)
        {
            m=duration/60;
            duration=duration%60;
        }
        s=duration;
        stime=Integer.toString(h) +":"+Integer.toString(m)+":"+ Integer.toString(s);
        return stime;
    }

    private void playAudio(File fileToPlay) {

        //play the audio
        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn,null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");

        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerSeekBar.setProgress(0);
                //Log.d("finish","fin");
                playerHeader.setText("Finished");
            }
        });

        playerSeekBar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        timetext.setText(timechange());
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this,10);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying) {
            stopAudio();
        }
    }
}