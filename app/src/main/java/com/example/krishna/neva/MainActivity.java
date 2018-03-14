package com.example.krishna.neva;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        public RecyclerView.Adapter adapter;
        ArrayList<HashMap<String, String>> list,newList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        list=new ArrayList<>();
        newList=new ArrayList<>();

        //checking for network connection
        ConnectivityManager  cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            //calling async task
            new Fetch().execute();
            recyclerView=findViewById(R.id.recycler);
            layoutManager=new LinearLayoutManager(this);
            adapter=new RecyclerAdapter(newList,getApplicationContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            Log.i("after", "onCreate:"+list.size());
        }
        else {
            Toast.makeText(this,"Please connect to a network",Toast.LENGTH_LONG).show();
           }
     }

    public void removeDuplicate() {
        ArrayList<String> c=new ArrayList<>();
        c.add("100");
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> hashmap = list.get(i);
            if(!c.contains(hashmap.get("id")))
            {
                c.add(hashmap.get("id"));
                HashMap<String,String> details=new HashMap<>();
                details.put("id",hashmap.get("id"));
                details.put("name",hashmap.get("name"));
                details.put("skills",hashmap.get("skills"));
                details.put("image",hashmap.get("image"));
                newList.add(details);
            }
        }
    }
    public class Fetch extends AsyncTask<Void,Void,Void> {

        protected ProgressDialog pDialog;
        protected String url="https://test-api.nevaventures.com/";

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler handler=new HttpHandler();
            String response=handler.requestCall(url);
            if(response!=null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                        for(int j=0;j<data.length();j++)
                        {
                            JSONObject c = data.getJSONObject(j);
                            String id=c.optString("id");
                            String name=c.optString("name");
                            String skills=c.optString("skills");
                            String image=c.optString("image");
                            HashMap<String,String> details=new HashMap<>();
                            if(!id.equals("") && !id.equals(" "))
                            {
                            details.put("id",id);
                            details.put("name",name);
                            details.put("skills",skills);
                            details.put("image",image);
                            list.add(details);
                            }
                        }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing())
            {
                pDialog.dismiss();
            }
            removeDuplicate();
            adapter.notifyDataSetChanged();
        }
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private String[] title={"a","b","c","d"};
        private String[] details={"1","2","3","4"};
        ArrayList<HashMap<String, String>> list;
        ArrayList<String> check=new ArrayList<>();
        String imageSrc="";
        Context mContext;

        public RecyclerAdapter(ArrayList<HashMap<String, String>> list,Context mContext)
        {
            this.list=list;
            this.mContext=mContext;
            check.add("100");
        }
        public static class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView image;
            public TextView UserName,UserSkill;


            public ViewHolder(View item)
            {
                super(item);
                image=(ImageView)item.findViewById(R.id.image);
                UserName=(TextView)item.findViewById(R.id.name);
                UserSkill=(TextView)item.findViewById(R.id.skill);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);
            ViewHolder myViewHolder = new ViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
                HashMap<String, String> hashmap= list.get(position);
                holder.UserName.setText(hashmap.get("name").toString());
                holder.UserSkill.setText(hashmap.get("skills").toString());
                imageSrc = hashmap.get("image").toString();
                Glide.with(mContext).load(imageSrc).asBitmap().centerCrop().placeholder(R.drawable.contact).into(new BitmapImageViewTarget(holder.image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
