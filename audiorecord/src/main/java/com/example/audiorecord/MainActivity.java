package com.example.audiorecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.audiorecord.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;

public	class	MainActivity	extends	AppCompatActivity	{

    private ActivityMainBinding binding;
    private	static	final	int	REQUEST_CODE_PERMISSION	=	200;
    private String recordFilePath;
    private Button recordBut;
    private Button playBut;
    private	boolean	isWork;
    boolean	isStartRecording	=	true;
    boolean	isStartPlaying	=	true;
    private	MediaRecorder	recorder	=	null;
    private MediaPlayer player	=	null;
    private	final	String	TAG	=	MainActivity.class.getSimpleName();
    @Override
    protected	void	onCreate(Bundle	savedInstanceState)	{
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recordBut = binding.record;
        playBut = binding.play;
        playBut.setEnabled(false);
        recordFilePath	=	(new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "/audiorecordtest.3gp")).getAbsolutePath();
        int	audioRecordPermissionStatus	=	ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int	storagePermissionStatus	=	ContextCompat.checkSelfPermission(this,	android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if	(audioRecordPermissionStatus	==	PackageManager.PERMISSION_GRANTED	&&	storagePermissionStatus ==	PackageManager.PERMISSION_GRANTED)	{
            isWork	=	true;
        }	else	{
            //	Выполняется	запрос	к	пользователь	на	получение	необходимых	разрешений
            ActivityCompat.requestPermissions(this,	new	String[]	{android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},	REQUEST_CODE_PERMISSION);
        }
        recordBut.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                if	(isStartRecording)	{
                    recordBut.setText("Stop	recording");
                    playBut.setEnabled(false);
                    startRecording();
                }	else	{
                    recordBut.setText("Start	recording");
                    playBut.setEnabled(true);
                    stopRecording();
                }
                isStartRecording	=	!isStartRecording;
            }
        });
        playBut.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                if	(isStartPlaying)	{
                    playBut.setText("Stop	playing");
                    recordBut.setEnabled(false);
                    startPlaying();
                }	else	{
                    playBut.setText("Start	playing");
                    recordBut.setEnabled(false);
                    stopPlaying();
                }
                isStartPlaying	=	!isStartPlaying;
            }
        });
    }
    private	void startRecording(){
        recorder	=	new	MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try	{
            recorder.prepare();
        }	catch	(IOException e)	{
            Log.e(TAG,	"prepare()	failed");
        }
        recorder.start();
    }
    private	void	stopRecording()	{
        recorder.stop();
        recorder.release();
        recorder	=	null;
    }

    private	void	startPlaying()	{
        player	=	new	MediaPlayer();
        try	{
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
        }	catch	(IOException	e)	{
            Log.e(TAG,	"prepare()	failed");
        }
    }
    private	void	stopPlaying()	{
        player.release();
        player	=	null;
    }
    @Override
    public	void	onRequestPermissionsResult(int	requestCode, @NonNull String[]	permissions, @NonNull	int[]
            grantResults)	{
        //	производится	проверка	полученного	результата	от	пользователя	на	запрос	разрешения	Camera
        super.onRequestPermissionsResult(requestCode,	permissions,	grantResults);
        switch	(requestCode){
            case	REQUEST_CODE_PERMISSION:
                isWork		=	grantResults[0]	==	PackageManager.PERMISSION_GRANTED;
                break;
        }
        if	(!isWork){
            finish();
        };
    }
}
