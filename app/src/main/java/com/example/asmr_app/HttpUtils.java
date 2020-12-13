package com.example.asmr_app;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Http2Connection;
import okio.BufferedSink;

public class HttpUtils {
    public static final String USER_AGENT = "application/json";

    public static String get(String strUrl, Map<String, String> header) {
        try {

            // Create Object
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // url 연결

            /** HttpURLConnection Configure */
            con.setRequestMethod("GET");        // 전송방식
            con.setConnectTimeout(10000);       //서버에 연결되는 // 컨텍션타임아웃 10초
            con.setReadTimeout(5000);           //InputStream 읽어오는 // 컨텐츠조회 타임아웃 5총

            con.setDoInput(true);
            con.setDoOutput(false); // url 연결을 출력용으로 사용하려는 경우에는 true로 사용

            System.out.println(con.toString());

            // Request Header 정의
            con.setRequestProperty("Content-Type", "application/json");
            /*
            // setRequestProperty < 여기는 사용해야함
            Iterator<String> keys = header.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                con.setRequestProperty(key, header.get(key));
            }
             */

            // getResponseCode
            System.out.println("con.getResponseCode() " + con.getResponseCode());
            System.out.println("con.getResponseMessage() " + con.getResponseMessage());
            /* < 여기는 사용해야함
            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n"); }
                br.close();
                return sb.toString();
            }
            // ResponseMessage != HTTP_OK
            else {
                System.out.println(con.getResponseMessage());
            }
             */
            return "true";
        }catch (ConnectException e) {
            Log.e("myConnectException", e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("myException", e.toString());
            e.printStackTrace();
        }
        return "fail";
    }

    public static String post(String strUrl, Map<String, String> header, String filename, File record) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", filename)
                .addFormDataPart("record", filename, RequestBody.create(MediaType.parse("multipart/form-data"), record))
                .build();

        Request request = new Request.Builder()
                .url(strUrl)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();

        return response.body().string();
    }

    public static String Post(String strUrl, Map<String, String> header, String filename, File record) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            Iterator<String> keys = header.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                con.setRequestProperty(key, header.get(key));
            }

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(filename);
            //DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            con.getOutputStream().write((filename).getBytes());
           // wr.write(URLEncoder.encode("name", filename));//wr.write(URLEncoder.encode("record", record));
            wr.flush();

            StringBuilder sb = new StringBuilder();

            Log.d("status code: ", Integer.toString(con.getResponseCode()));

            if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return sb.toString();
            } else {
                System.out.println(con.getResponseMessage());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "";
    }

    public static String put(String strUrl, Map<String, String> header, String jsonMessage) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("PUT");

            Iterator<String> keys = header.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                con.setRequestProperty(key, header.get(key));
            }

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(jsonMessage);
            wr.flush();

            StringBuilder sb = new StringBuilder();


            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return sb.toString();
            } else {
                System.out.println(con.getResponseMessage());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "";
    }

    public static String delete(String strUrl, Map<String, String> header) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");

            Iterator<String> keys = header.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                con.setRequestProperty(key, header.get(key));
            }

            con.setDoOutput(false);

            StringBuilder sb = new StringBuilder();


            if (con.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return sb.toString();


            } else {
                System.out.println(con.getResponseMessage());
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }
}

