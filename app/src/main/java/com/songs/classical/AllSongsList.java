package com.songs.classical;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.songs.classical.models.SongModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AllSongsList extends AppCompatActivity {
    private TextView tvData, textView,songName;
    private ListView lvSongs;
    Button play, pause, star, next,download;;

    MediaPlayer mp = new MediaPlayer();
    String Address,name,actName;
    String path = "/classical Music/";
    SeekBar seekBar;
    Handler handler;
    Runnable runnable;
    boolean isPaused;
    int length;

    private long enqueue;
    private DownloadManager dm;



    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  clearApplicationData();
        setContentView(R.layout.activity_all_songs_list);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        lvSongs = (ListView) findViewById(R.id.lvSongs);
        songName = (TextView) findViewById(R.id.songName);
        pause = (Button) findViewById(R.id.pause);

        download = (Button) findViewById(R.id.download);
        Intent intent = getIntent();
        Address = intent.getExtras().getString("Address");
        name = intent.getExtras().getString("Name");
        actName = intent.getExtras().getString("actName");

        songName.setText(name);
        songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songName.setSingleLine(true);
        songName.setSelected(true);

        // next = (Button) findViewById(R.id.next);

        for (int i = 0; i < 3; i++) {
            Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
        }

        mp.reset();
        try {
            mp.setDataSource(Address);//Write your location here
            mp.prepare();

            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {

                    pause.setBackgroundResource(R.drawable.play6);

                    mp.pause();
                    length = mp.getCurrentPosition();
                    isPaused = true;
                } else {

                    pause.setBackgroundResource(R.drawable.pause8);


                    mp.seekTo(length);
                    mp.start();

                }
            }
        });



      //  new AllSongsList.JSONTask().execute("http://134.209.192.153/classical/all/all.json");


        if (isNetworkStatusAvialable(getApplicationContext()) == false) {
            Toast.makeText(getApplicationContext(), "This Application requires Internet Connection.", Toast.LENGTH_SHORT).show();
        }

//For Seekbar
        handler = new Handler();
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(mp.getDuration());

                playCycle();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {

                if (input) {
                    mp.seekTo(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //Downloade

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            ImageView view = (ImageView) findViewById(R.id.imageView1);
                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            view.setImageURI(Uri.parse(uriString));
                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }



    public void playCycle() {
        seekBar.setProgress(mp.getCurrentPosition());

        if (mp.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);

        }

    }





    @Override
    protected void onPause() {
        super.onPause();

        if(mp != null)
            mp.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mp!= null)
            mp.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mp!= null)
            mp.stop();

    }



    @Override
    public void onBackPressed() {

        if(actName == "allSong"){
            mp.stop();
            mp.release();
            Intent i = new Intent(getApplicationContext(), AllSongs.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        else if(actName == "beethoven"){
            mp.stop();
            mp.release();
            Intent i = new Intent(getApplicationContext(), FirstList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            
        }
        else if(actName == "mozartic"){
            mp.stop();
            mp.release();
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }
        else if(actName == "chopinic"){
            mp.stop();
            mp.release();
            Intent i = new Intent(getApplicationContext(), SecondList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }
        else if(actName == "others"){
            mp.stop();
            mp.release();
            Intent i = new Intent(getApplicationContext(), FourthList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }
        super.onBackPressed();

    }

    public class JSONTask extends AsyncTask<String, String, List<SongModel>> {

        @Override
        protected List<SongModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("songs");

                List<SongModel> songModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    SongModel songModel = new SongModel();
                    songModel.setSong(finalObject.getString("song"));
                    songModel.setAddress(finalObject.getString("address"));
                    songModel.setAuthor(finalObject.getString("actName"));


                    //Adding the final objects in the list
                    songModelList.add(songModel);
                }

                return songModelList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<SongModel> result) {
            super.onPostExecute(result);

            AllSongsList.SongAdapter adapter = new AllSongsList.SongAdapter(getApplicationContext(), R.layout.row, result);
            lvSongs.setAdapter(adapter);

            //TODO need to set data to the list
        }


    }







    public class SongAdapter extends ArrayAdapter {

        private List<SongModel> songModelList;
        private int resource;
        private LayoutInflater inflater;


        public SongAdapter(Context context, int resource, List<SongModel> objects) {
            super(context, resource, objects);
            songModelList = objects;
            this.resource = resource;



            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            ImageView ivSongIcon;
            TextView tvSong;
            Button info = null;
            TextView actName;

            // info = (Button)findViewById(R.id.info);

            ivSongIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            tvSong = (TextView) convertView.findViewById(R.id.tvSong);
            actName = (TextView) convertView.findViewById(R.id.author);

            tvSong.setText(songModelList.get(position).getSong());
            actName.setText(songModelList.get(position).getAuthor());









            tvSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });


            ivSongIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });


            actName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });






            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (createDirIfNotExists(path)) {
                        Toast.makeText(getApplicationContext(), "DownLoading..", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Song will be available in your local music player", Toast.LENGTH_LONG).show();
                        String home = System.getProperty("user.home");
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(Address));
                        request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().toString(),path + name + ".mp3");

                        enqueue = dm.enqueue(request);

                        //showDownload(v);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Caannot create directory", Toast.LENGTH_LONG).show();


                    }

                }
            });

            return convertView;
        }

    }


    public void showDownload(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }



}





