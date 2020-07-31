package com.example.myeyes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Post extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
        // urls: 0-url, 1-kilometer, 2-latitude, 3-longitude

        try {

            Log.d("!!!!!","try");

            // JSONObject를 만들고 key value 형식으로 값을 저장해준다.

            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("kilometer", urls[1]);
            jsonObject.accumulate("latitude", urls[2]);
            jsonObject.accumulate("longitude", urls[3]);

           // jsonObject.accumulate("user_id", "androidTest");
           // jsonObject.accumulate("name", "111");

            HttpURLConnection con = null;

            BufferedReader reader = null;

            try {

                Log.d("!!!!!","try2");

                URL url = new URL(urls[0]);

                Log.d("!!!!!", urls[0]);

                // 연결을 함

                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");   // POST방식으로 보냄

                con.setRequestProperty("Cache-Control", "no-cache");   // 캐시 설정

                con.setRequestProperty("Content-Type", "application/json");   // application JSON 형식으로 전송

                con.setRequestProperty("Accept", "application/json");
               // con.setRequestProperty("Accept", "text/html");   // 서버에 response 데이터를 html로 받음


                con.setDoOutput(true);   // Outstream으로 post 데이터를 넘겨주겠다는 의미

                con.setDoInput(true);   // Inputstream으로 서버로부터 응답을 받겠다는 의미

                con.connect();

                // 서버로 보내기 위해서 스트림 만듦
                OutputStream outStream = con.getOutputStream();

                // 버퍼를 생성하고 넣음

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                writer.write(jsonObject.toString());

                writer.flush();

                writer.close();//버퍼를 받아줌

                // 서버로 부터 데이터를 받음

                InputStream stream = con.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";


                Log.d("!!!!!", "Reader"+reader);

                while ((line = reader.readLine()) != null) {

                    Log.d("!!!!!", "line"+line);
                    buffer.append(line);

                }

                return buffer.toString();   // 서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

            } catch (MalformedURLException e) {

                Log.d("!!!!!","MalformedURLException");

                e.printStackTrace();

            } catch (IOException e) {

                Log.d("!!!!!","IOException");
                e.printStackTrace();

            } finally {
                Log.d("!!!!!","finally");
                if (con != null) {
                    Log.d("!!!!!","finally if");
                    con.disconnect();
                }

                try {
                    Log.d("!!!!!","finally try");
                    if (reader != null) {


                        Log.d("!!!!!","finally try if");
                        reader.close();   // 버퍼를 닫아줌

                    }

                } catch (IOException e) {
                    Log.d("!!!!!","finally catch");
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Log.d("!!!!!","catch");
            e.printStackTrace();
        }
        return null;
    }

    @Override

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.d("!!!!!", result);
        //tvData.setText(result);   // 서버로 부터 받은 값을 출력
    }

}
