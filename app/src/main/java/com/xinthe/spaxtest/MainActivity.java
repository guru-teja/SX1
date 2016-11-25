package com.xinthe.spaxtest;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;
import com.squareup.picasso.Picasso;
import com.xinthe.spax.BeaconCollector;
import com.xinthe.spax.CloudMessagingInterface;
import com.xinthe.spax.DefaultSNSAWSLocationService;
import com.xinthe.spax.MessagingController;
import com.xinthe.spax.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CloudMessagingInterface{

    BeaconCollector bc;
    MessagingController mc;


    public List<DataModel> MyNotificationsList;

    TextView t1,t2,t3,t4;
    ImageView image;

    HashMap<String, String> map = new HashMap<String, String>();
    public static final String TAG = "theexception";

    String jsondata,name,teja;
    DataModel mydata;

    String sID,sTitle,sMessage,sAttachment,sCouponcode,sValidity;
    String Title[];
    String Message[];
    String Description[];
    String Couponcode[];
    String Validity[];
    int ID = 1;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        for (int i = 0; i < MyData.aID.size(); i++) {
//            data.add(new DataModel(
//                    MyData.aID[i],
//                    MyData.aTitle[i],
//                    MyData.aMessage[i],
//                    MyData.aAttachment[i],
//                    MyData.aValidity[i],
//                    MyData.aCouponcode[i]
//            ));
            data.add(new DataModel(MyData.aID.get(i),MyData.aTitle.get(i),MyData.aMessage.get(i),MyData.aAttachment.get(i),MyData.aCouponcode.get(i),MyData.aValidity.get(i)));

        }


        loaddata();

      //  removedItems = new ArrayList<Integer>();


        adapter = new DataAdapter(data,this);
        recyclerView.setAdapter(adapter);


//        t1 = (TextView)findViewById(R.id.Title);
//        t2 = (TextView)findViewById(R.id.Message);
//        t3 = (TextView)findViewById(R.id.Couponcode);
//        t4 = (TextView)findViewById(R.id.Validity);
//        image = (ImageView)findViewById(R.id.imageView);
//        Map<String, String> retMap = new HashMap<String, String>();


        bc = new BeaconCollector(this.getApplicationContext(), Constants.XINTHE_UUID, BeaconCollector.SNSconfig);
        bc.startUpload();
        mc = new MessagingController(this,this);

    }
    @Override
    public void onMessageReceived(Bundle bundle)
    {
        String msg = bundle.getString(MessagingController.MESSAGE_CONTENT);
        String json = bundle.getString(MessagingController.DATA_PAYLOAD);
        name     = json.toString();
        jsondata = json.toString();

        if (jsondata != null) {
            try
            {
                 JSONObject sys = new JSONObject(jsondata);
//               JSONObject sys = reader.getJSONObject("Image Packet");
//               JSONObject details = sys.getJSONObject("notification");

                sID = sys.getString("ID");
                sTitle = sys.getString("Title");
                sMessage = sys.getString("Message");
                sValidity = sys.getString("Validity");
                sCouponcode = sys.getString("Couponcode");
                sAttachment = sys.getString("Attachment");


                MyData.aID.add(sID);
                MyData.aTitle.add(sTitle);
                MyData.aMessage.add(sMessage);
                MyData.aValidity.add(sValidity);
                MyData.aCouponcode.add(sCouponcode);
                MyData.aAttachment.add(sAttachment);



                loaddata();


            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                 Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();


//        try
//        {
//
//            JSONObject jObject = new JSONObject(name);
//            Iterator<?> keys = jObject.keys();
//
//            while (keys.hasNext()) {
//                String key = (String) keys.next();
//                String value = jObject.getString(key);
//                String tag = "dell";
//
//                map.put(key, value);
//                Log.d(tag,"the data in hashmap "+map);
//            }
//        }
//
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }


//        Picasso.with(this)
//            .load(sAttachment)
//            .placeholder(R.drawable.ic_stat_ic_notification)
//            .error(R.drawable.error1)      // optional
//            .resize(250, 200)              // optional
//            .onlyScaleDown()
//            // .rotate(90)                 // optional
//            .into(image);
//
//        t1.setText(sTitle);
//        t2.setText(sMessage);
//        t3.setText("CouponCode: " + sCouponcode);
//        t4.setText("Validity: " + sValidity);


    }


    public void logoclicked(View v)
    {
        Toast.makeText(this, "Logo Clicked", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,SecondActivity.class);
        startActivity(intent);
        finish();


    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());

        popup.show();
    }
    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_refresh:
                    Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_expand:
                    Toast.makeText(MainActivity.this, "Expand", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    public void loaddata()
    {
    if(data != null) {
    data = new ArrayList<DataModel>();
    for (int i = 0; i < MyData.aID.size(); i++) {
//            data.add(new DataModel(
//                    MyData.aID[i],
//                    MyData.aTitle[i],
//                    MyData.aMessage[i],
//                    MyData.aAttachment[i],
//                    MyData.aValidity[i],
//                    MyData.aCouponcode[i]
//            ));
        data.add(new DataModel(MyData.aID.get(i), MyData.aTitle.get(i), MyData.aMessage.get(i), MyData.aAttachment.get(i), MyData.aCouponcode.get(i), MyData.aValidity.get(i)));

    }

    adapter = new DataAdapter(data, this);
    recyclerView.setAdapter(adapter);
    adapter.notifyItemInserted(0);

}

    }

}
