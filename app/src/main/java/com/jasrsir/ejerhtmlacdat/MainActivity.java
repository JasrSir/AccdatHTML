package com.jasrsir.ejerhtmlacdat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializar();
    }
    //region Variables de clase
    private Button mBtnConHttp;
    private Button mBtnConAsin;
    private Button mBtnConVolley;
    private Button mBtnDescFich;
    private Button mBtnSubida;
    //endregion

    //Initialization Varibles
    private void inicializar() {
        mBtnConHttp = (Button) findViewById(R.id.btnConHttp);
        mBtnConAsin = (Button) findViewById(R.id.btnConAsin);
        mBtnConVolley = (Button) findViewById(R.id.btnLect);
        mBtnDescFich = (Button) findViewById(R.id.btnCodi);
        mBtnSubida = (Button) findViewById(R.id.btnExp);
    }

    public void getOnClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnConHttp:
                intent = new Intent(MainActivity.this, ConexionHttp_Activity.class);
                startActivity(intent);
                break;
            case R.id.btnConAsin:
                intent = new Intent(MainActivity.this, ConexionAsincrona_Activity.class);
                startActivity(intent);
                break;
            case R.id.btnLect:
                intent = new Intent(MainActivity.this, ConexionVolley_Activity.class);
                startActivity(intent);
                break;
            case R.id.btnCodi:
                intent = new Intent(MainActivity.this, Descarga_Activity.class);
                startActivity(intent);
                break;
            case R.id.btnExp:
                intent = new Intent(MainActivity.this, Subida_Activity.class);
                startActivity(intent);
                break;
        }
    }
}
