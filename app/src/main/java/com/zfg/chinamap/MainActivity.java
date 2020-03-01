package com.zfg.chinamap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zfg.chinamap.api.ApiHelp;
import com.zfg.chinamap.bean.ProvincePneumoniaBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private List<ProvincePneumoniaBean> provincePneumoniaBeanList = new ArrayList<>();
    private MapView map_view;
    private RecyclerView recycler_view;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map_view = findViewById(R.id.map_view);
        recycler_view = findViewById(R.id.recycler_view);
        myAdapter = new MyAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(myAdapter);

        getAllData();
    }

    private void getAllData() {

        ApiHelp.getAllData(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure exception = " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = null;
                int code = response.code();

                if (null != response.body()) {
                    result = response.body().string();
                }

                if (ApiHelp.HTTP_OK == code && !TextUtils.isEmpty(result)) {

                    provincePneumoniaBeanList.clear();

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.optJSONArray("newslist");
                        if (null != jsonArray && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonNewslist = jsonArray.optJSONObject(i);
                                String provinceShortName = jsonNewslist.optString("provinceShortName");
                                int confirmedCount = jsonNewslist.optInt("confirmedCount");
                                int curedCount = jsonNewslist.optInt("curedCount");
                                int deadCount = jsonNewslist.optInt("deadCount");
                                ProvincePneumoniaBean bean = new ProvincePneumoniaBean(provinceShortName, confirmedCount, curedCount, deadCount);
                                provincePneumoniaBeanList.add(bean);
                                Log.i(TAG, "provinceShortName = " + provinceShortName + ", confirmedCount = " + confirmedCount + ", curedCount = " + curedCount + ", deadCount = " + deadCount);
                            }

                            map_view.setProvincePneumonia(provincePneumoniaBeanList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context mContext;
        private final LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.item_pneumonia, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProvincePneumoniaBean bean = provincePneumoniaBeanList.get(position);

            holder.name.setText(bean.getProvinceShortName());
            holder.confirmed_count.setText(String.valueOf(bean.getConfirmedCount()));
            holder.cured_count.setText(String.valueOf(bean.getCuredCount()));
            holder.dead_count.setText(String.valueOf(bean.getDeadCount()));
        }

        @Override
        public int getItemCount() {
            return provincePneumoniaBeanList == null ? 0 : provincePneumoniaBeanList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private TextView confirmed_count;
            private TextView cured_count;
            private TextView dead_count;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.name);
                confirmed_count = itemView.findViewById(R.id.confirmed_count);
                cured_count = itemView.findViewById(R.id.cured_count);
                dead_count = itemView.findViewById(R.id.dead_count);
            }
        }

    }
}
