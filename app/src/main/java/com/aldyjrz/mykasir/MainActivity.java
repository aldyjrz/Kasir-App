package com.aldyjrz.mykasir;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
 import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldyjrz.mykasir.adapter.MenuGridAdapter;
import com.bumptech.glide.Glide;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity   {

    //general
    private static final String urlListMenu = "https://www.dropbox.com/s/rnpv3j15nz8t1f8/menumakanan.xml?dl=1";
    private int totalHarga = 0;
    private List<MenuItem> listData;
    private String pesanan = "";

    //component
    private GridView gridView;
    private TextView txtTotal;
    private SwipeRefreshLayout swipe;
    private ImageView tambah, kurang;
    private static int _counter = 45;
    private String _stringVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTotal = findViewById(R.id.total);
        gridView = findViewById(R.id.gridViewMenu);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewItem(listData.get(position));
            }
        });

        if (listData == null) {
            new MyTask().execute(urlListMenu);
        }

        swipe = findViewById(R.id.swipeMenu);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                txtTotal.setText("Total : Rp. 0");
                new MyTask().execute(urlListMenu);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkout:
                viewPembayaran();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @SuppressLint("StaticFieldLeak")
    class MyTask extends AsyncTask<String, Void, Void> {

         @Override
        protected Void doInBackground(String... params) {
            try {
                listData = getData(params[0]);
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        e.printStackTrace();

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            swipe.setRefreshing(false);
            if (null == listData || listData.size() == 0) {
                Toast.makeText(MainActivity.this, "Menu Tidak Ditemukan", Toast.LENGTH_LONG).show();
            } else {
                 gridView.setAdapter(new MenuGridAdapter(MainActivity.this, R.layout.grid_view, listData));
            }

            setTotal(0);
        }
    }

    public List<MenuItem> getData(String url) {
        MenuItem objItem;
        List<MenuItem> listItem = null;

        try {
            listItem = new ArrayList<>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(url).openStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");

            int batas = nList.getLength();

            for (int temp = 0; temp < batas; temp++) {
                {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        objItem = new MenuItem();
                        objItem.setNama(getTagValue("nama", eElement));
                        objItem.setHarga(getTagValue("harga", eElement));
                        objItem.setLink(getTagValue("link", eElement));
                        listItem.add(objItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItem;
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }

    @SuppressLint("SetTextI18n")
    private void viewItem(final MenuItem data) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.item, null);
        ImageView imgItem = convertView.findViewById(R.id.imgItem);
        TextView txtDesc = convertView.findViewById(R.id.txtDesc);
        final TextView txtJml = convertView.findViewById(R.id.txtJml);
        tambah = convertView.findViewById(R.id.tambah);
        kurang = convertView.findViewById(R.id.kurang);


        Glide.with(this).load(data.getLink()).into(imgItem);
        txtDesc.setText(data.getNama() + " | Rp." + data.getHarga());

        pesanan += txtDesc.getText().toString() + "\n";
         tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _counter++;
                        _stringVal = Integer.toString(_counter);
                        txtJml.setText(_stringVal);
                    }
                });

            }
        });

        kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _counter;
                        _stringVal = Integer.toString(_counter);
                        txtJml.setText(_stringVal);
                    }
                });
            }
        });

        alertDialog.setView(convertView).setTitle("");
        final AlertDialog mAlertDialog = alertDialog.setPositiveButton("PESAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pesanan = Integer.valueOf(data.getHarga());
                if (!txtJml.getText().toString().equals("") && !txtJml.getText().toString().equals("0")) {
                    pesanan = Integer.valueOf(txtJml.getText().toString()) * Integer.valueOf(data.getHarga());
                }
                setTotal(pesanan);
            }
        }).create();

        mAlertDialog.show();
    }

    private void viewPembayaran() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.checkout, null);

        TextView txtTotal = convertView.findViewById(R.id.txtTotal);
        TextView txtPesanan = convertView.findViewById(R.id.txtPesanan);

        txtPesanan.setText(pesanan);
        txtTotal.setText(this.txtTotal.getText().toString());

        alertDialog.setView(convertView).setTitle("");
        final AlertDialog mAlertDialog = alertDialog.setPositiveButton("BAYAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Terima Kasih", Toast.LENGTH_LONG).show();
            }
        }).create();

        mAlertDialog.show();
    }

    private void setTotal(int nilai) {
        totalHarga = totalHarga + nilai;
        txtTotal.setText("Total : Rp. " + totalHarga);
    }
}
