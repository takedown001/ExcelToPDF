package com.trainig.excelviewer;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String TAG = "test";
    Button upload;
    ProgressDialog pDialog = null;
    private static final int STORAGE_PERMISSION_CODE = 101;
    String[] courses = { "Sunday","Monday", "Tuesday",
            "Wednesday", "Thrusday",
            "Firday", "Saturday"};
    private  int day =0;
    Button report ;
    String myResponse = " ";
    Spinner spino;
    private String filename =null;
    private static final int FILE_SELECT_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictModeManager.enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        report = findViewById(R.id.Report);
         spino = findViewById(R.id.spinner);
        spino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long text) {
                day =pos;
                Log.d(TAG,String.valueOf(day));

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            day =0;
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CallAPI().execute("https://blanched-jurisdicti.000webhostapp.com/pdf.php", String.valueOf(day));

            }
        });

        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spino.setAdapter(ad);
        upload = findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (checkPermission())
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        try {
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select a File to Upload"),
                                    FILE_SELECT_CODE);
                        } catch (android.content.ActivityNotFoundException ex) {

                            Toast.makeText(MainActivity.this, "Please install a File Manager.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        requestPermission();
                    }
                }


        });
    }
    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(MainActivity.this, MANAGE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.MANAGE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    class Uploadtoserver extends AsyncTask<String, String, String> {


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
                String serverURL = strings[0];
                String f =strings[1];
                File file = new File(f);
            try {
                filename =  file.getName();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uploaded_file",filename,
                                RequestBody.create(MediaType.parse("text/csv"), file))
                        .build();
                Request request = new Request.Builder()
                        .url(serverURL)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        Log.d("test",e.getMessage());
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    report.setVisibility(View.VISIBLE);
                                    spino.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(MainActivity.this, "Something Went Wrong With Upload", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                        Log.d("test",response.message());
                    }
                });

            } catch (Exception ex) {
                Log.d("test",ex.getMessage());
                // Handle the error
            }
            return null;
        }
    }


     class CallAPI extends AsyncTask<String, String, String> {
         @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Generating Pdf... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String url= params[0];

            //data to post
            final OkHttpClient[] client = {new OkHttpClient()};
            RequestBody data = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("day", params[1])
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(data)
                    .build();

            client[0].newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                          myResponse = response.body().string();
                          Log.d(TAG,myResponse);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int out=000;
                                try {
                                    out = Integer.parseInt(myResponse);
                                }catch (NumberFormatException e) {
                                    Log.d(TAG,e.getMessage());
                                }
                                if(out==200){
                                    Intent i = new Intent(MainActivity.this,webView.class);
                                    startActivity(i);
                                }else if(out==404){
                                    Toast.makeText(MainActivity.this, "Pdf Not Generated", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                   Uri uri = data.getData();
                    new Uploadtoserver().execute("https://blanched-jurisdicti.000webhostapp.com/UploadToServer.php",com.trainig.excelviewer.File.getPath(MainActivity.this,uri));
                }

        }

    }

}

