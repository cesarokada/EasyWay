p
ckage com.example.cesar.easywaytest;

import android.net.http.AndroidHttpClient;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity {


    private EditText editendorigem;
    private EditText editenddestino;
    private Button calcular;
    private TextView exibetaxa;

    private long distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calcular = (Button) findViewById(R.id.calcular);
        exibetaxa = (TextView) findViewById(R.id.exibetaxa);

        calcular.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getRouteGoogleMaps(view);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void getRouteGoogleMaps(View view) throws UnsupportedEncodingException {
        editendorigem = (EditText) findViewById(R.id.editendorigem);
        editenddestino = (EditText) findViewById(R.id.editenddestino);
        String origem = URLEncoder.encode(editendorigem.getText().toString() ,"UTF-8");
        String destino = URLEncoder.encode(editenddestino.getText().toString(), "UTF-8");

        getRoute(origem,destino);

    }

    public void getRoute(final String origem, final String destino){
        new Thread(){
            public void run(){
						String url= "http://maps.googleapis.com/maps/api/directions/json?origin="
								+ origem+"&destination="
								+ destino+"&sensor=false";

                HttpResponse response;
                HttpGet request;
                AndroidHttpClient client = AndroidHttpClient.newInstance("route");

                request = new HttpGet(url);
                try {
                    response = client.execute(request);
                    final String answer = EntityUtils.toString(response.getEntity());

                    runOnUiThread(new Runnable(){
                        public void run(){
                            try {

                                 buildJSONRoute(answer);
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // PARSER JSON
    public void buildJSONRoute(String json) throws JSONException{
        JSONObject result = new JSONObject(json);
        JSONArray routes = result.getJSONArray("routes");

        distance = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
        Double distancia = 1.5+(4.5*distance/25000);
        String taxa = moeda(distancia);
        exibetaxa.setText(taxa);
    }

    public String moeda (Double valor){
        NumberFormat duasCasasDecimais = new DecimalFormat(".##");
        String s = "R$ " + (duasCasasDecimais.format(valor));

        return s;
    }

}
