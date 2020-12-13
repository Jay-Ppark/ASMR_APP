package com.example.asmr_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // String url = "http://1.232.112.199:3391";
        //NetworkTask networkTask = new NetworkTask(url,null, "get");
        //networkTask.execute();
    }
   // public class NetworkTask extends AsyncTask<Void, Void, String> {
    //    private String url;
   //     private ContentValues values;
    //    private String opt;

    //    public NetworkTask(String url, ContentValues values, String opt) {
     //       this.url = url;
      //      this.values = values;
       //     this.opt = opt;
       // }

     /*   @Override
        protected String doInBackground(Void... params) {
            String result = "";
            Map<String, String> header = new HashMap<>();
           // RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
           // result = requestHttpURLConnection.request(url,values);
            if(opt.equals("get")){
                //HttpUtils httpUtils = new HttpUtils();
                //header.put("")
                result = HttpUtils.get(url, header);
            }else if(opt.equals("put")){
                result = HttpUtils.put(url,header,values.toString());
            }else if(opt.equals("post")){
                result = HttpUtils.post(url,header,values.toString());
            }else{
                result = HttpUtils.delete(url,header);
            }
            return result;
            //return getResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
        */
   // }
}