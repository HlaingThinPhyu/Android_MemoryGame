package iss.workshop.sa4108memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button mButtonFetch;
    private EditText mEditTextUrl;
    private ProgressBar mProgressBar;
    private TextView mDownloadText;
    private ActionBar mActionBar;

    private int counter = 0;
    private String[] htmlStringArray;
    private ArrayList<String> selectedPictureArray = new ArrayList<String>() ;
    private ArrayList<String> stringPictureList = new ArrayList<String>();
    private boolean isProgressBarVisible;
    public int PROGRESS_UPDATE = 2;
    public int DOWNLOAD_COMPLETED = 3;
    private ArrayList<String> testlist1 = new ArrayList<>();
    private boolean isNewCount;
    private int downloadcounter = 0;

    public ArrayList<String> getStringPictureList() {
        return stringPictureList;
    }

    public void setStringPictureList(ArrayList<String> stringPictureList) {
        this.stringPictureList = stringPictureList;
    }

    public ArrayList<String> getTestlist1() {
        return testlist1;
    }

    public void setTestlist1(ArrayList<String> testlist1) {
        this.testlist1 = testlist1;
    }

    public boolean isProgressBarVisible() {
        return isProgressBarVisible;
    }

    public void setProgressBarVisible(boolean progressBarVisible) {
        isProgressBarVisible = progressBarVisible;
    }

    public void setHtmlStringArray(String[] htmlStringArray) {
        this.htmlStringArray = htmlStringArray;
    }

    public String[] getHtmlStringArray() {
        return htmlStringArray;
    }

    @SuppressLint("HandlerLeak")
    Handler mainThreadHandler = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                setStringPictureList((ArrayList<String>) msg.obj);
            }
            else if (msg.what == PROGRESS_UPDATE){
//            if (msg.what == PROGRESS_UPDATE){

                    setProgressBarVisible(true);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(msg.arg1);
                    mDownloadText.setVisibility(View.VISIBLE);
                    mDownloadText.setText("Downloading " + msg.arg1 / 5 + " of 20 pictures");

                }

            else if (msg.what == DOWNLOAD_COMPLETED) {
                setProgressBarVisible(false);
                mProgressBar.setVisibility(View.GONE);

//                mDownloadText.setVisibility(View.VISIBLE);
                mDownloadText.setText("Please select 6 pictures");

                setProgressBarVisible(false);
                GridView gridView1 = findViewById(R.id.gridView1);
                gridView1.setNumColumns(4);
                ImageAdapter imgAdapter =new ImageAdapter(MainActivity.this,R.layout.image_row, (ArrayList<String>) testlist1);
                if (gridView1 != null){
                    gridView1.setAdapter(imgAdapter);
                    //this is not working normally for now
                    gridView1.setOnItemClickListener(MainActivity.this);
                }

            }
        }
    };

    //testdata
//    private String[] cartoons = {
//            "hug", "laugh", "peep", "snore", "stop",
//            "tired", "full", "what", "afraid", "no_way"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set on count new view
        isNewCount = true;
        //set progressbar
        mProgressBar = findViewById(R.id.progressBar1);
        //set TextView
        mDownloadText = findViewById(R.id.textDownload);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        isProgressBarVisible = false;
        if (isProgressBarVisible == false){
            mProgressBar.setVisibility(View.GONE);
            mDownloadText.setVisibility(View.GONE);
        }

//     startActivity(new Intent(MainActivity.this, DetailActivity.class));

        mButtonFetch = findViewById(R.id.button_fetch);
        if (mButtonFetch !=null){
            mButtonFetch.setOnClickListener(this);
        }

        for(int i = 1;i<21; i++){
            String filePath = "GamePhoto";
            String fileName = "photo_" + i + ".jpg";
            File mTargetFile = new File(MainActivity.this.getFilesDir(),filePath + "/" + fileName);
            testlist1.add(mTargetFile.getAbsolutePath());
        }

        deleteFilesinGamePhoto(MainActivity.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_fetch:
                if (isNewCount == true){
                    mEditTextUrl = findViewById(R.id.edit_text_url);
                    String urlString = mEditTextUrl.getText().toString();
                    System.out.println(urlString);
                    mDownloadText = findViewById(R.id.textDownload);
                    mDownloadText.setText("Start downloading");
                    Thread thread = new URLParserThread(urlString,MainActivity.this, mainThreadHandler);
                    thread.start();
                    isNewCount = !isNewCount;

                }
                else
                {
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                }


                break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        // checking index
//        File dir = new File(getStringPictureList().get(0));
//        System.out.println(dir.getParentFile());

        System.out.println("Index: " + String.valueOf(index));
        System.out.println("L: " + String.valueOf(l));
//        ArrayList<String> list2 = getStringPictureList();
//        System.out.println(list2);
        String[] array = new String[getStringPictureList().size()];
        for (int i=0;i<getStringPictureList().size();i++){
            array[i] = getStringPictureList().get(i);
//            System.out.println("This is after clicking: " + array[i]);
        }
//        System.out.println(array);

        /*ImageView imgview = view.findViewById(R.id.img1);
        System.out.println("*****TestingIMg*****" + imgview);*/



        //Still working on this

        if (this.selectedPictureArray.contains(array[index])){
            this.selectedPictureArray.remove(array[index]);
            counter -=1;
            new SoundPoolPlayer(this).playSoundWithRedId(R.raw.click);
            mDownloadText.setText("You have selected "+ String.valueOf(counter) + (counter==1? " picture":" pictures"));
            ImageView img_view = view.findViewById(R.id.imgtick);
            img_view.setVisibility(View.GONE);

            String expr = "You have unselected this image";
            Toast toast = Toast.makeText(this, expr, Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            counter +=1;
            this.selectedPictureArray.add(array[index]);
            new SoundPoolPlayer(this).playSoundWithRedId(R.raw.click);
            mDownloadText.setText("You have selected "+ String.valueOf(counter) + (counter==1? " picture":" pictures"));
            ImageView img_view = view.findViewById(R.id.imgtick);
            img_view.setVisibility(View.VISIBLE);
            String expr = "You have selected this image";
            Toast toast = Toast.makeText(this, expr, Toast.LENGTH_LONG);
            toast.show();


        }

        if (counter == 6){
            System.out.println("These are the urls selected for next activity:" + selectedPictureArray);
            System.out.println(counter);
            mDownloadText.setText("You have selected "+ String.valueOf(counter) + (counter==1? " picture":" pictures"));
            Intent intent = new Intent(this,DetailActivity.class);
            intent.putExtra("pictureList",selectedPictureArray);
            System.out.println(intent.getStringArrayListExtra("pictureList"));
            counter = 0;
            startActivity(intent);
            intent.removeExtra("pictureList");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        deleteFilesinGamePhoto(this);
    }

    protected void deleteFilesinGamePhoto(Context context) {
//        String filePath = "/data/user/0/iss.workshop.sa4108memorygame/files/GamePhoto";
//        String filePath = context.getFilesDir().getPath();
        String filePath = "GamePhoto";
        File file = new File(context.getFilesDir(),filePath);
        if (file.isDirectory()) {
            String[] filearray = file.list();
            System.out.println(Arrays.toString(filearray));
            System.out.println(file);
            for (int i = 0; i < filearray.length; i++) {
                new File(file, filearray[i]).delete();
            }
        }
    }

    protected void readFileName(int i, Context context){
        String filePath = "GamePhoto";
        String fileName = "photo_" + i + ".jpg";
        File mTargetFile = new File(context.getFilesDir(),filePath + "/" + fileName);
    }
}