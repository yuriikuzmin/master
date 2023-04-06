package com.kuzmin.master;

import static com.kuzmin.master.R.layout.listview_item_frame;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class YourOrder extends AppCompatActivity {

    TextView tv_your_order;
    EditText et_your_number;
    ListView lv_order_your;
    Button btn_your_order, btn_your_menu;
    ArrayList<JSONObject> jsonObjectArrayList;
    String idOrderYour, addressOrder, recordingdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_order);

        tv_your_order=(TextView) findViewById(R.id.text_your_order);
        et_your_number=(EditText) findViewById(R.id.et_your_master);
        lv_order_your=(ListView) findViewById(R.id.lv_order_your);
        btn_your_order=(Button) findViewById(R.id.btn_your_view);
        lv_order_your.setVisibility(View.GONE);

        lv_order_your.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idEx=jsonObjectArrayList.get(position).optString("id");
                String recordingdateEx=jsonObjectArrayList.get(position).optString("recordingdate");
                String addressEx=jsonObjectArrayList.get(position).optString("address");
                String problemEx=jsonObjectArrayList.get(position).optString("problem");
                String duedateEx=jsonObjectArrayList.get(position).optString("duedate");
                String nameEx=jsonObjectArrayList.get(position).optString("name");
                String telEx=jsonObjectArrayList.get(position).optString("tel");
                String completiondateEx=jsonObjectArrayList.get(position).optString("completiondate");

                Intent intent=new Intent(YourOrder.this, YourOrderView.class);
                intent.putExtra("idEx", idEx);
                intent.putExtra("recordingdateEx", recordingdateEx);
                intent.putExtra("addressEx", addressEx);
                intent.putExtra("problemEx", problemEx);
                intent.putExtra("duedateEx", duedateEx);
                intent.putExtra("nameEx", nameEx);
                intent.putExtra("telEx", telEx);
                intent.putExtra("completiondateEx", completiondateEx);
                Log.d("LOG.Extra", "Номер заказа : "+ idEx + "\nДата записи : "+recordingdateEx+ "\nАдрес : "+ addressEx+"\nПроблема : "+ problemEx+ "\nСрок выполнения : "+duedateEx
                        + "\nИмя закзчика : "+nameEx+ "\nТелефон : "+telEx+"\nДата выполнения : "+completiondateEx);
                startActivity(intent);
            }
        });

    }

    public void onClickOrderMenu(View view){
        Intent intent =new Intent(YourOrder.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickYourView(View view){
        String master= et_your_number.getText().toString();
        if(master.isEmpty()) {
            Toast.makeText(YourOrder.this, R.string.toast_numb_master, Toast.LENGTH_SHORT).show();

        } else {new ViewYourOrder(YourOrder.this, tv_your_order).execute(master);}

    }

    public class ViewYourOrder extends AsyncTask<String, Void, String>{
        public Context context;
        TextView order;


        public ViewYourOrder(Context context, TextView tv_your_order) {
            this.context=context;
            this.order=tv_your_order;
        }

        public ViewYourOrder() {
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{

            String master=arg[0];
            String link="http://10.0.2.2/your.php";
            String data = URLEncoder.encode("master", "UTF-8")+"="+URLEncoder.encode(master, "UTF-8");

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
            btn_your_order.setVisibility(View.GONE);
            tv_your_order.setVisibility(View.GONE);
            et_your_number.setVisibility(View.GONE);
            lv_order_your.setVisibility(View.VISIBLE);
            this.order.setText(data);
            Log.d("LOG", "Проверили на содержание ответ" + data.toString());

            JSONObject object=null;
            try {
                object=new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArray=object.getJSONArray("order_your");
                ArrayList<JSONObject> yourOrderForExtra=getJsonArray(jsonArray);
                jsonObjectArrayList=yourOrderForExtra;

                ArrayList<String>yourOrderForView=getArrayList(jsonArray);
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(YourOrder.this, listview_item_frame, yourOrderForView);
                lv_order_your.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private ArrayList<JSONObject> getJsonArray(JSONArray jsonArray){
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
        private ArrayList<String> getArrayList(JSONArray jsonArray) throws JSONException {
            //Функция получения 2-х переменных для вывода в ListView и выбора заказа. Переходим на тип String from JSONArray

            ArrayList<String> bList=new ArrayList<String>();
            String temp=null;


            if(jsonArray!=null){
                for(int i=0; i<jsonArray.length(); i++){


                    idOrderYour=jsonArray.getJSONObject(i).get("id").toString();
                    addressOrder=jsonArray.getJSONObject(i).get("address").toString();
                    recordingdate=jsonArray.getJSONObject(i).get("recordingdate").toString();
                    temp= "Номер заказа: "+ idOrderYour+"\nДата записи: "+ recordingdate+"\nАдрес: "+ addressOrder;
                    bList.add(temp);

                    Log.d("LOG",jsonArray.getJSONObject(i).toString() );
                    Log.d("LOG", "Номер заказа: "+ idOrderYour+"\nАдрес: "+ addressOrder);

                }

            }
            return bList; //возвращаем ArrayList<String> из 2-х переменных

        }


    }
    }

