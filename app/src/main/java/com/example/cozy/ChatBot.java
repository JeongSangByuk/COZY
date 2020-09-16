package com.example.cozy;

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

public class ChatBot extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {

        try {

            Log.d("!!!!!","챗봇");
            Log.d("!!!!!","try");

            // JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject();

            Log.d("!!!!!","3"+"   "+urls[3]);
            Log.d("!!!!!","1"+"   "+urls[1]);

            jsonObject.accumulate("input_sentence", urls[3]);   // 사용자 대화 문장
            jsonObject.accumulate("chatbot_id", "f646de27-3776-4d09-9a3d-257bb5b86b2b");

            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {

                Log.d("!!!!!","try2");

                URL url = new URL(urls[1]);

                // 연결을 함

                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");   // POST방식으로 보냄

                // Request Header 값 세팅

                con.setRequestProperty("Cache-Control", "no-cache");   // 캐시 설정
                con.setRequestProperty("Content-Type", "application/json");   // application JSON 형식으로 전송
                con.setRequestProperty("Accept", "application/json");
                // con.setRequestProperty("Accept", "text/html");   // 서버에 response 데이터를 html로 받음


                con.setDoOutput(true);   // Outstream으로 post 데이터를 넘겨주겠다는 의미

                con.setDoInput(true);   // Inputstream으로 서버로부터 응답을 받겠다는 의미

                con.connect();

                // 서버로 보내기 위해서 스트림 만듦
                // Request Body에 Data를 담기위해 OutputStream 객체를 생성
                OutputStream outStream = con.getOutputStream();

                // 버퍼를 생성하고 넣음
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();   // 버퍼를 받아줌

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

                return buffer.toString();   // 서버로 부터 받은 값을 리턴해줌

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

        Log.d("!!!!! 결과는?", result);
    }

}
