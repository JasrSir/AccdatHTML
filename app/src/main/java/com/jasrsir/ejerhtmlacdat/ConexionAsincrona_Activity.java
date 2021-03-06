package com.jasrsir.ejerhtmlacdat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.*;


public class ConexionAsincrona_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText direccion;
    private RadioButton radioJava;
    private RadioButton radioApache;
    private RadioButton radioAAHC;
    private Button conectar;
    private WebView web;
    private TextView tiempo;
    private static final String JAVA = "Java";
    private static final String APACHE = "Apache";
    private static final String AAHC = "AAHC";
    TareaAsincrona miTareaAsinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_asincrona);
        iniciar();
    }

    private void iniciar() {
        direccion = (EditText) findViewById(R.id.direccion);
        radioJava = (RadioButton) findViewById(R.id.radioJava);
        radioApache = (RadioButton) findViewById(R.id.radioApache);
        radioAAHC = (RadioButton) findViewById(R.id.radioAAHC);
        conectar = (Button) findViewById(R.id.conectar);
        conectar.setOnClickListener(this);
        web = (WebView) findViewById(R.id.web);
        tiempo = (TextView) findViewById(R.id.resultado);
    }

    @Override
    public void onClick(View v) {
        String texto = direccion.getText().toString();
        Resultado resultado;
        String tipo = APACHE;
        if (v == conectar) {
            if (radioAAHC.isChecked())
            AAHC();
        }else if (radioJava.isChecked())
            tipo = JAVA;
        else if (radioApache.isChecked())
            tipo = APACHE;
        miTareaAsinc = new TareaAsincrona(this);
        miTareaAsinc.execute(tipo, texto);
        tiempo.setText("Esperando zorra");

    }

    //TODO CLASE ASÍNCRONA a HTTP
    public class TareaAsincrona extends AsyncTask<String, Integer, Resultado> {

        private ProgressDialog progreso;
        private Context context;
        private long fin;
        private long inicio;


        public TareaAsincrona(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
            progreso = new ProgressDialog(context);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Conectando . . .");
            progreso.setCancelable(true);
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    TareaAsincrona.this.cancel(true);
                }
            });
            progreso.show();
        }

        protected Resultado doInBackground(String... cadena) {
            inicio = System.currentTimeMillis();
            Resultado resultado = null;
            int i = 1;
            try {
                    // operaciones en el hilo secundario
                publishProgress(i++);
                if (cadena[0].equals(JAVA))
                    resultado = Conexion.conectarJava(cadena[1]);
                else
                    resultado = Conexion.conectarApache(cadena[1]);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                resultado = new Resultado();
                resultado.setCodigo(false);
                resultado.setMensaje(e.getMessage());
            }
            return resultado;
        }

        protected void onProgressUpdate(Integer... progress) {
            progreso.setMessage("Conectando . . . " + Integer.toString(progress[0]));
        }

        protected void onPostExecute(Resultado resultado) {
            progreso.dismiss();
            fin = System.currentTimeMillis();
            // mostrar el resultado
            if (resultado.isCodigo())
                web.loadDataWithBaseURL(null, resultado.getContenido(), "text/html", "UTF-8", null);
            else
                web.loadDataWithBaseURL(null, resultado.getMensaje(), "text/html", "UTF-8", null);
            tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
        }

        protected void onCancelled() {
            progreso.dismiss();
            fin = System.currentTimeMillis();
            // mostrar cancelación
            web.loadDataWithBaseURL(null, "CANCELADO por PUTA", "text/html", "UTF-8", null);
            tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
        }
    }

    //TODO CLASE AsynHTTPCLient
    private void AAHC() {
        final String texto = direccion.getText().toString();
        final long inicio;
        final long[] fin = new long[1];
        final ProgressDialog progreso = new ProgressDialog(ConexionAsincrona_Activity.this);
        inicio = System.currentTimeMillis();
        RestClient.get(texto, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                //progreso.setCancelable(false);
                progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progreso.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // called when response HTTP status is "200 OK"
                fin[0] = System.currentTimeMillis();
                progreso.dismiss();
                web.loadDataWithBaseURL(texto,response,"text/html","UTF-8", null);
                tiempo.setText("Duración: " + String.valueOf(fin[0] - inicio) + " milisegundos");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                fin[0] = System.currentTimeMillis();
                progreso.dismiss();
                web.loadDataWithBaseURL(texto,"FALLO: " + response +" " + throwable.getMessage(),"text/html","UTF-8", null);
                tiempo.setText("Duración: " + String.valueOf(fin[0] - inicio) + " milisegundos");
            }
        });
    }
}

class RestClient {
    private static final String BASE_URL = "";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void cancelRequests(Context c, boolean flag) {
        client.cancelRequests(c, flag);
    }
}