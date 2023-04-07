package com.kuzmin.master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    String idMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Auth id=new Auth();
        idMaster=id.getIdMaster();
        Log.d("LOG MA", "Передали id="+idMaster);

    }
    public void onClickYourOrder(View view){
        Intent intent =new Intent (MainActivity.this, YourOrder.class);

        startActivity(intent);
    }
    public void onClickMasterAdd(View view){
        Intent intent=new Intent(MainActivity.this, MasterAdd.class);

        startActivity(intent);
    }

    public void onClickOrderView(View view){
        Intent intent=new Intent(MainActivity.this, OrderView.class);

        startActivity(intent);
    }
}