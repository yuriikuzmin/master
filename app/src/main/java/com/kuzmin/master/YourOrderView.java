package com.kuzmin.master;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class YourOrderView extends AppCompatActivity {

    TextView tv_numb_order, text_your_order_json, tv_date;
    EditText et_duedate_your_order;
    Button btn_save_date;
    String tempAll;
    int temp;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuor_order_view);

        tv_numb_order=(TextView) findViewById(R.id.tv_numb_order);
        text_your_order_json=(TextView) findViewById(R.id.text_your_order_json);
        et_duedate_your_order=(EditText) findViewById(R.id.et_duedate_your_order);
        btn_save_date=(Button) findViewById(R.id.btn_save_date);
        tv_date=(TextView) findViewById(R.id.tv_date);

        Intent intent = getIntent();

        String idEx=intent.getStringExtra("idEx");
        String recordingdateEx=intent.getStringExtra("recordingdateEx");
        String addressEx=intent.getStringExtra("addressEx");
        String problemEx=intent.getStringExtra("problemEx");
        String duedateEx=intent.getStringExtra("duedateEx");
        String nameEx=intent.getStringExtra("nameEx");
        String telEx=intent.getStringExtra("telEx");
        String completiondateEx=intent.getStringExtra("completiondateEx");

        String d="0000-00-00";//Вводим дополнительную переменную, чтобы можно было сравнить два String
        Log.d("LOG.completiondate :", completiondateEx);

        if (d.equals(completiondateEx)) {
            temp=1;
        } else {
            temp=2;
        }

        switch (temp) {
            case 1:
                tempAll="\nДата записи : "+recordingdateEx+"\nПроблема : "+ problemEx+ "\nСрок выполнения : "+duedateEx+ "\nАдрес : "+ addressEx
                        + "\nИмя закзчика : "+nameEx+ "\nТелефон : "+telEx;
                btn_save_date.setVisibility(View.VISIBLE);
                tv_date.setVisibility(View.VISIBLE);
                et_duedate_your_order.setVisibility(View.VISIBLE);
                text_your_order_json.setText(tempAll);
            break;
            case 2:
                 tempAll=  "\nДата записи : "+recordingdateEx+"\nПроблема : "+ problemEx+ "\nСрок выполнения : "+duedateEx+ "\nАдрес : "+ addressEx
                        + "\nИмя закзчика : "+nameEx+ "\nТелефон : "+telEx+"\nДата выполнения : "+completiondateEx;
                btn_save_date.setVisibility(View.GONE);
                tv_date.setVisibility(View.GONE);
                et_duedate_your_order.setVisibility(View.GONE);
                text_your_order_json.setText(tempAll);
                break;
        }
        tv_numb_order.setText(idEx);//выводим номер заказа отдельно

    }
    public void onClickYourMenu (View view){//Метод кнопки возврата в главное меню
        Intent intent = new Intent(YourOrderView.this, MainActivity.class);
        startActivity(intent);
    }
    public void onClickSaveDueDate(View view){

        String id=tv_numb_order.getText().toString();
        String dateCompletions=  et_duedate_your_order.getText().toString();
        Log.d("LOG dateCompletions ",dateCompletions );
        String b=null;
        if(dateCompletions.isEmpty()){
            Toast.makeText(YourOrderView.this, R.string.toast_date_completion, Toast.LENGTH_SHORT).show();
        } else {
            new ViewSaveDueDate(YourOrderView.this, text_your_order_json).execute(id, dateCompletions);}
    }

    public class ViewSaveDueDate extends AsyncTask<String, Void, String> {
        public Context context;
        TextView complDate;

        public ViewSaveDueDate(Context context, TextView complDate) {
            this.context = context;
            this.complDate = complDate;
        }

        public ViewSaveDueDate() {
        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String id=arg[0];
                String dateCompletions=arg[1];
                String link="http://10.0.2.2/yourview.php";
                String data =URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8")
                        + " && " +URLEncoder.encode("dateCompletions", "UTF-8")+"="+URLEncoder.encode(dateCompletions, "UTF-8");

                URL url=new  URL(link);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream ops=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                writer.write( data );

                writer.flush();
                writer.close();
                ops.close();


                InputStream ips=conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
                Log.d("LOG", "Запустили поток входящий в андроид");
                StringBuilder sb = new StringBuilder();

                String line = null;
                //Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.d("LOG", "Проверили на содержание ответ sb.toString=" + sb.toString());
                reader.close();
                ips.close();
                conn.disconnect();
                return sb.toString();

            }catch (Exception e){
                return new String("Exception: "+ e.getMessage());}

        }

        @Override
        protected void onPostExecute(String data) {
            String d1="0000-00-00";
            if(d1.equals(data)){
                text_your_order_json.setText(R.string.toast_date);
                tv_date.setVisibility(View.GONE);
            } else { tv_date.setText(data);
                text_your_order_json.setText(R.string.date_save);
                tv_date.setVisibility(View.VISIBLE);}

            btn_save_date.setVisibility(View.GONE);

            et_duedate_your_order.setVisibility(View.GONE);

        }
    }

}
