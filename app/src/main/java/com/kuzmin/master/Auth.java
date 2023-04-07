package com.kuzmin.master;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Auth extends AppCompatActivity {

    public String telMaster, passwordMaster;
    EditText phone, password;
    TextView auth_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth_text=(TextView) findViewById(R.id.auth_text);
        phone=(EditText) findViewById(R.id.et_number_tel);
        password=(EditText) findViewById(R.id.et_password);
    }

    public void onClickEnter(View view){
        String fl="1";
        String tel=phone.getText().toString();
        String pass=password.getText().toString();

        if(tel.isEmpty()| pass.isEmpty()){
            Toast.makeText(Auth.this, R.string.toast_auth, Toast.LENGTH_SHORT).show();

        }else { new AuthClass(Auth.this, auth_text).execute(fl, tel, pass); }

       // Intent intent=new Intent(Auth.this, MainActivity.class);
       // startActivity(intent);
    }

   public void onClickReg(View view){
        String fl="2";
       String tel=phone.getText().toString();
       String pass=password.getText().toString();
       Log.d("LOG", "Переменные :" + fl + tel + pass );
       if(tel.isEmpty()| pass.isEmpty()){
           Toast.makeText(Auth.this, R.string.toast_auth, Toast.LENGTH_SHORT).show();

       }else { new AuthClass(Auth.this, auth_text).execute(fl, tel, pass); }

      // Intent intent= new Intent(Auth.this, MainActivity.class);
        // startActivity(intent);
   }

    public  class AuthClass extends AsyncTask<String, Void, String> {
        Context context;
        TextView auth_text;

        public AuthClass(Context context, TextView auth_text) {
            this.context = context;
            this.auth_text = auth_text;
        }

        public AuthClass() {
        }
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String fl = (String) arg[0];
                String tel = (String) arg[1];
                String pass = (String) arg[2];

                String link = "http://10.0.2.2/auth.php";

                String data = URLEncoder.encode("fl", "UTF-8") + "=" + URLEncoder.encode(fl, "UTF-8") + " && " + URLEncoder.encode("tel", "UTF-8") + "=" + URLEncoder.encode(tel, "UTF-8") + "&&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
                URL url = new URL(link);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);//установить сделать ввод как истинна
                conn.setDoOutput(true);//сделать вывод
                OutputStream ops=conn.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();//смывать, убирать
                bufferedWriter.close();

                InputStream ips=conn.getInputStream();
                BufferedReader reader=new BufferedReader((new InputStreamReader(ips, "UTF-8")));
                StringBuilder stringBuilder=new StringBuilder();
                Log.d("LOG", "Запустили поток входящий в андроид");
                String line=null;
                while ((line=reader.readLine())!=null){
                    stringBuilder.append(line);//добавить
                    break;
                }
                Log.d("LOG", "Проверили на содержание ответ sb.toString=" + stringBuilder.toString());
                reader.close();
                ips.close();
                conn.disconnect();
                return stringBuilder.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String data){
            Log.d("LOG", "Проверили на содержание ответ sb.toString=" + data.toString());
           auth_text.setText(data.toString());
           //auth_text.setVisibility(View.GONE);
            String temp="error";
            String idEx=data.toString();
            if(!temp.equals(idEx)){
           Toast.makeText(Auth.this, R.string.toast_auth_last,Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(Auth.this, MasterAdd.class);

                Log.d("LOG Strem", "присвоили переменную idEx ="+idEx);
                intent.putExtra("idEx", idEx);
           startActivity(intent);
            } else {
                Toast.makeText(Auth.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                String fl=" ";
                phone.setText("");
                password.setText("");
                auth_text.setText(R.string.auth_tv);
            }

        }
    }

}
