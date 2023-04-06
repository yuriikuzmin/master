package com.kuzmin.master;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousChannelGroup;

public class BookingOrder extends AppCompatActivity {
    TextView text_booking, text_json, id_order;
    EditText id_master;
    Button btn_save_booking, btn_menu_booking;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        text_booking=(TextView) findViewById(R.id.text_booking);
        text_json=(TextView) findViewById(R.id.text_order_json);
        id_master=(EditText) findViewById(R.id.et_booking_master);
        id_order=(TextView) findViewById(R.id.et_booking_order);
        btn_menu_booking=(Button) findViewById(R.id.btn_booking_menu);
        btn_save_booking=(Button) findViewById(R.id.btn_save_booking);

        Intent intent = getIntent();
        String idEx=intent.getStringExtra("idEx");
        String recordingdateEx=intent.getStringExtra("recordingdateEx");
        String addressEx=intent.getStringExtra("addressEx");
        String problemEx=intent.getStringExtra("problemEx");
        String duedateEx=intent.getStringExtra("duedateEx");
        String nameEx=intent.getStringExtra("nameEx");
        String telEx=intent.getStringExtra("telEx");

       String tempAll=  "\nДата записи : "+recordingdateEx+ "\nАдрес : "+ addressEx+"\nПроблема : "+ problemEx+ "\nСрок выполнения : "+duedateEx
                + "\nИмя закзчика : "+nameEx+ "\nТелефон : "+telEx;
       id_order.setText(idEx);
       text_json.setText(tempAll);


    }
    public void onClickBookingMenu (View view){
        Intent intent = new Intent(BookingOrder.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickSaveBooking(View view){
        String master=id_master.getText().toString();
        String order=id_order.getText().toString();

        new BookingSave(BookingOrder.this, text_booking).execute(order, master);

    }
    public class BookingSave extends AsyncTask<String, Void, String>{
        public Context context;
        public TextView booking;

        public BookingSave(Context context, TextView text_booking) {
            this.context=context;
            this.booking=text_booking;

        }
        public BookingSave() {
        }
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String order=(String) arg[0];
                String master=(String) arg[1];
                String link = "http://10.0.2.2//booking.php";
                String data = URLEncoder.encode("order", "UTF-8")+"="+URLEncoder.encode(order, "UTF-8")+ " && " +URLEncoder.encode("master", "UTF-8")+"="+ URLEncoder.encode(master, "UTF-8");

                URL url=new URL(link);

                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream ops=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                writer.write( data );
                writer.flush();
                writer.close();
                ops.close();
                Log.d("LOG", "Отправили данные" + data.toString());
                InputStream ips=conn.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(ips, "UTF-8"));
                StringBuilder sb= new StringBuilder();
                String line=null;
                while((line=reader.readLine())!=null){
                    sb.append(line);
                    break;
                }
                reader.close();
                ips.close();
                conn.disconnect();
                Log.d("LOG", "Получили данные" + sb.toString());
                return sb.toString();

            }catch (Exception e){
                return new String("Exception: "+ e.getMessage());
            }


        }
        @Override
        protected void onPostExecute(String data) {
            this.booking.setText(data);
            btn_save_booking.setVisibility(View.GONE);
            id_master.setVisibility(View.GONE);
            id_order.setVisibility(View.VISIBLE);

            Log.d("LOG", "Проверили на содержание ответ" + data.toString());
        }
    }
}
