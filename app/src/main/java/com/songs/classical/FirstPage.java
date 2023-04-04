package com.songs.classical;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.songs.classical.models.ImageAdapter;


public class FirstPage extends AppCompatActivity {


    ImageButton allsongs;
    Button topbtn;
    Button share;
    public static int adCounter;
    public static final int TIME_COUNTER=2;  //Should be always greater than zero

    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;


    GridView androidGridView;

    String[] gridViewString = {
            "Beethoven", "Mozart", "Chopin", "Other"
    } ;
    int[] gridViewImageId = {

            R.drawable.beethoven, R.drawable.mozartic,
            R.drawable.chopinic, R.drawable.grey
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        adCounter++;





        AdRequest adRequest = new AdRequest.Builder().build();



        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(FirstPage.this);
// Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        interstitial.loadAd(adRequest);
// Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
// Call displayInterstitial() function



                if (adCounter%TIME_COUNTER==0)
                        displayInterstitial();



            }
        });





        if(isNetworkStatusAvialable(this)==false){
            Toast.makeText(FirstPage.this, "Please connect to internet to access all Music!",
                    Toast.LENGTH_LONG).show();

        }

        allsongs = (ImageButton) findViewById(R.id.allsongs);
        allsongs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), AllSongs.class);
                startActivity(i);
                finish();
            }
        });



        ImageAdapter adapterViewAndroid = new ImageAdapter(FirstPage.this, gridViewString, gridViewImageId);
        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(position==0 && isNetworkStatusAvialable(FirstPage.this)){
                    Intent i = new Intent(getApplicationContext(), FirstList.class);
                    startActivity(i);
                    finish();
                }
                else{

                }

                if(position==1 && isNetworkStatusAvialable(FirstPage.this)){

                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                    finish();
                }else{

                }


                if(position==2 && isNetworkStatusAvialable(FirstPage.this)){

                    Intent i = new Intent(getApplicationContext(), SecondList.class);
                    startActivity(i);
                    finish();

                }else{

                }


                if(position==3 && isNetworkStatusAvialable(FirstPage.this)){
                    Intent i = new Intent(getApplicationContext(), FourthList.class);
                    startActivity(i);
                    finish();
                }else{

                }




            }
        });




            topbtn = (Button) findViewById(R.id.rateus);
            topbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    launchMarket();
                    finish();
                }
            });

            share = (Button) findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    shareApp(getApplicationContext());
                }
            });

    }



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




    public void shareApp(Context context)
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Best Classical Songs");
            String sAux = "\nTry this new application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+ getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }



    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(FirstPage.this);
        builder.setMessage("Are you sure want to do this ?");
        builder.setCancelable(true);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


}
