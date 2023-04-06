package com.kuzmin.master;

import static com.kuzmin.master.R.id.lv_order;
import static com.kuzmin.master.R.layout.activity_order_view;
import static com.kuzmin.master.R.layout.listview_item_frame;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class OrderView extends AppCompatActivity {
    TextView textView_order;
    EditText date_order;
    Button btn_order;
    ListView listOrder;
    ArrayList<JSONObject> infoList;//
    String id, problem, address, duedate, name, recordingdate, tel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_order_view);

        textView_order=(TextView) findViewById(R.id.text_order);
        date_order=(EditText) findViewById(R.id.order_date);

        btn_order=(Button) findViewById(R.id.btn_order);
        listOrder=(ListView) findViewById(lv_order);

        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //id, problem, address, duedate, name, recordingdate, tel

            //Получаем из ArrayList<JSONObject> infoList значение переменных String
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idEx=infoList.get(position).optString("id");
                String recordingdateEx=infoList.get(position).optString("recordingdate");
                String addressEx=infoList.get(position).optString("address");
                String problemEx=infoList.get(position).optString("problem");
                String duedateEx=infoList.get(position).optString("duedate");
                String nameEx=infoList.get(position).optString("name");
                String telEx=infoList.get(position).optString("tel");

                //Передаем в другую активность  ключ значение переменной
                Intent intent=new Intent(OrderView.this, BookingOrder.class);

                intent.putExtra("idEx", idEx);
                intent.putExtra("recordingdateEx", recordingdateEx);
                intent.putExtra("addressEx", addressEx);
                intent.putExtra("problemEx", problemEx);
                intent.putExtra("duedateEx", duedateEx);
                intent.putExtra("nameEx", nameEx);
                intent.putExtra("telEx", telEx);
                Log.d("LOG.Extra", "Номер заказа : "+ idEx + "\nДата записи : "+recordingdateEx+ "\nАдрес : "+ addressEx+"\nПроблема : "+ problemEx+ "\nСрок выполнения : "+duedateEx
                        + "\nИмя закзчика : "+nameEx+ "\nТелефон : "+telEx);
                startActivity(intent);

            }
        });
    }


    public void onClickOrderFromBase(View view){
        String data=date_order.getText().toString();
        new OrderBase(OrderView.this, textView_order).execute(data);

    }

    public class OrderBase extends AsyncTask <String, Void, String>{
         private Context context;
         private TextView list;

        public OrderBase(Context context, TextView textView_order) {
            this.context=context;
            this.list=textView_order;
        }

        public OrderBase() {
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String date=(String) arg[0];

                String link = "http://10.0.2.2/order.php";
                String data = URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date, "UTF-8");

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
            listOrder.setVisibility(View.VISIBLE);

            textView_order.setVisibility(View.GONE);
            date_order.setVisibility(View.GONE);
            btn_order.setVisibility(View.GONE);
          //  this.list.setText(data);
            JSONObject object= null;
            try {
                object = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray=object.getJSONArray("order_main");
                ArrayList<JSONObject> orderForExtra= getJSONArray(jsonArray);
                infoList=orderForExtra;

                ArrayList<String> orderFromJson=getArrayListFromJSONArray(jsonArray);
                //simple_selectable_list_item
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(OrderView.this, listview_item_frame, orderFromJson);
                listOrder.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("LOG", "Проверили на содержание ответ" + data.toString());


        }
        private ArrayList<JSONObject> getJSONArray(JSONArray jsonArray){
            //Функция получения массива фомата JSONObject для передачи всех элементов выбраного json объекта в другую активность
            ArrayList<JSONObject> aJList = new ArrayList<JSONObject>();
            try {
                if(jsonArray!= null){
                    for(int i = 0; i<jsonArray.length();i++){
                        aJList.add(jsonArray.getJSONObject(i));
                    }
                }
            }catch (JSONException js){
                js.printStackTrace();
            }
            return aJList;
        }

        private ArrayList<String> getArrayListFromJSONArray(JSONArray jsonArray) throws JSONException {
            //Функция получения 2-х переменных для вывода в ListView и выбора заказа. Переходим на тип String from JSONArray

             ArrayList<String> bList=new ArrayList<String>();
             String temp=null;


            if(jsonArray!=null){
                for(int i=0; i<jsonArray.length(); i++){

                    infoList.add(jsonArray.getJSONObject(i));
                    problem=jsonArray.getJSONObject(i).get("problem").toString();
                    address=jsonArray.getJSONObject(i).get("address").toString();
                    temp= "Адрес : "+ address+"\nПроблема : "+ problem;
                    bList.add(temp);

                    Log.d("LOG",jsonArray.getJSONObject(i).toString() );
                    Log.d("LOG", "Проблема: "+ problem + " Адрес : "+ address);

                }

            }
            return bList; //возвращаем ArrayList<String> из 2-х переменных

        }

    }
}
