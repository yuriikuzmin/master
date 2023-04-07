package com.kuzmin.master;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MasterAdd extends AppCompatActivity {
    TextView text_master_add;
    EditText name_master, second_name_master, address_master;
    Button btn_save_master, btn_back_menu;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_add);


        text_master_add=(TextView) findViewById(R.id.text_master_add);
        name_master=(EditText) findViewById(R.id.name_master);
        second_name_master=(EditText) findViewById(R.id.name_second_master);
        address_master=(EditText) findViewById(R.id.address_master);
        btn_back_menu=(Button) findViewById(R.id.btn_back);
        btn_save_master=(Button) findViewById(R.id.btn_save_master);

    }
    public void onClickBack(View view){
        Intent intent=new Intent(MasterAdd.this, MainActivity.class);
        startActivity(intent);
    }
    public void onClickSaveMaster(View view){
        Intent intent= getIntent();
        String id=intent.getStringExtra("idEx");
        Log.d("LOG", "приняли idEx= "+ id);

        String name=name_master.getText().toString();
        String second_name=second_name_master.getText().toString();
        String address=address_master.getText().toString();


        new GetData(MasterAdd.this, text_master_add).execute(id, name, second_name, address);
    }

    public class GetData extends AsyncTask<String, Void, String>{

        private Context context;
        private TextView textView;

        public GetData(Context context, TextView text_master_add) {//конструктор
            this.context=context;
            this.textView=text_master_add;
        }

        public GetData() { //конструктор
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String id= (String) arg [0];
                String name=(String) arg[1];
                String second_name=(String) arg[2];
                String address=(String) arg[3];


                String link = "http://10.0.2.2/index.php";
                String data = URLEncoder.encode("id", "UTF-8")+"="+ URLEncoder.encode(id, "UTF-8") + " && " +URLEncoder.encode("name", "UTF-8")+"="+ URLEncoder.encode(name, "UTF-8") + " && " + URLEncoder.encode("second_name", "UTF-8")+"="+URLEncoder.encode(second_name, "UTF-8")+"&&"+URLEncoder.encode("address", "UTF-8")+"="+URLEncoder.encode(address, "UTF-8");
                //10.0.2.2
                ///192.168.1.2
                URL url=new URL(link);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Log.d("LOG", "Запускаем поток исходящий в андроид");
                OutputStream ops=conn.getOutputStream();
                Log.d("LOG", "Запустили поток исходящий в андроид");
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();

                InputStream ips=conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
                Log.d("LOG", "Запустили поток входящий в андроид");
                StringBuilder sb = new StringBuilder();

                String line=null;
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                    break;
                }
                reader.close();
                ips.close();
                conn.disconnect();
                return sb.toString();


            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String data) {
            btn_save_master.setVisibility(View.GONE);
            this.textView.setText(data);
            Log.d("LOG", "Проверили на содержание ответ" + data.toString());
        }
    }
}
