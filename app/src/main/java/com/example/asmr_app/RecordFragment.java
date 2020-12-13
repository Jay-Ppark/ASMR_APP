package com.example.asmr_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.os.AsyncTask;


public class RecordFragment extends Fragment implements View.OnClickListener {


    private NavController navController;

    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView fileNameText;

    private  boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;
    private String recordFilePath;

    private Chronometer timer;


    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);

        /**
         *  Execute AsyncTask sub-class "NetworkTask" : GET
         * */
        //String url = "http://1.232.112.199";
        //RecordFragment.NetworkTask networkTask = new RecordFragment.NetworkTask(url,null, "get");
        //networkTask.execute();
    }

    // AsyncTask caller가 destroy 되지 않도록 주의: https://mailmail.tistory.com/12
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String filename;
        private File record;
        private String opt;

        public NetworkTask(String url, String filename, File record, String opt) {
            this.url = url;
            this.filename = filename;
            this.record = record;
            this.opt = opt;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            Map<String, String> header = new HashMap<>();

            if (opt.equals("get")) {
                result = HttpUtils.get(url + "/conversion", header);
            } else if (opt.equals("put")) {
                result = HttpUtils.put(url, header, filename);
            } else if (opt.equals("post")) {
                try {
                    result = HttpUtils.post(url + "/conversion", header, filename, record);
                } catch(IOException e) {
                    Log.d("IOException", e.getMessage());
                }
            } else {
                result = HttpUtils.delete(url, header);
            }
            Log.d("url------------------", result);

            return result;
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.record_list_btn:
                /*
                    Navigation Controller
                    Part of Android JetPack, used for navigation between both fragments
                 */
                if(isRecording) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment2);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL",null);
                    alertDialog.setTitle("Audio still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");
                    alertDialog.create().show();
                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment2);
                }
                break;
            case R.id.record_btn:
                if(isRecording) {
                    //stop Recording
                    stopRecording();

                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped,null));
                    isRecording = false;
                } else {
                    //start Recording
                    if(checkPermissions()) {
                        startRecording();

                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        timer.stop();
        fileNameText.setText("File Saved\n" + recordFile);

        mediaRecorder.stop(); // stop() 호출시 자동으로 파일 저장까지 이루어짐
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;

        /** TODO: recordFile to be send to SERVER */
        /* --------------------------------------- */
        // record 경로 지정 필요? recordFile: String, recordFilePath: String
        String url = "http://1.232.112.199";
        String sendF = recordFilePath;
        File fileToSend = new File(sendF);
        RecordFragment.NetworkTask networkTask = new RecordFragment.NetworkTask(url, recordFile, fileToSend, "post");
        networkTask.execute();
        /* --------------------------------------- */
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.KOREA);
        Date now = new Date();

        recordFile = "Recording_" + formatter.format(now) + ".3gp"; // .mp4
        recordFilePath = recordPath + "/" + recordFile;
        Log.d("recordFilePath", recordFilePath);

        fileNameText.setText("Recording, File Name : " + recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // MPEG_4
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    //@Override
    public void onStop() {
        super.onStop();
        if(isRecording) {
            stopRecording();
        }
    }
}