package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.Analytics;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private com.ibm.watson.developer_cloud.conversation.v1.model.Context context = null;
    private boolean initialRequest;
    private Context mContext;
    private String workspace_id;
    private String conversation_username;
    private String conversation_password;
//    private String analytics_APIKEY;
    private Logger myLogger;
    private static String IP_ADDRESS = "stafor.cafe24.com";
    private String mJsonString;
    private String dates, times, buildings;

    MyTimer myTimer;
    String test;
    URLConnector task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mContext = getApplicationContext();

        // IBM Watson Config
        conversation_username = mContext.getString(R.string.conversation_username);
        conversation_password = mContext.getString(R.string.conversation_password);
        workspace_id = mContext.getString(R.string.workspace_id);
//        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);

        //IBM Cloud Mobile Analytics
        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_SYDNEY);
        //Analytics is configured to record lifecycle events.
//        Analytics.init(getApplication(), "WatBot", analytics_APIKEY, false,false, Analytics.DeviceEvent.ALL);
        //Analytics.send();
        myLogger = Logger.getLogger("myLogger");
        // Send recorded usage analytics to the Mobile Analytics Service
        Analytics.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // Handle Analytics send success here.
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                // Handle Analytics send failure here.
            }
        });

        // Send logs to the Mobile Analytics Service
        Logger.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // Handle Logger send success here.
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                // Handle Logger send failure here.
            }
        });

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        inputMessage.setTypeface(typeface);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

    };

    // Sending a message to Watson Conversation Service
    private void sendMessage() {

        final String inputMsg = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputMsg);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
            myLogger.info("Sending a message to Watson Conversation Service");
        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputMsg);
            inputMessage.setId("100");
            this.initialRequest = false;
        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
                    service.setUsernameAndPassword(conversation_username, conversation_password);
                    InputData input = new InputData.Builder(inputMsg).build();
                    MessageOptions options = new MessageOptions.Builder(workspace_id).input(input).context(context).build();
                    MessageResponse response = service.message(options).execute();

                    //Passing Context of last conversation
                    if(response.getContext() !=null)
                    {
                        //context.clear();
                        context = response.getContext();
                    }
                    final Message outMessage=new Message();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if(null !=responseList && responseList.size()>0){
                                    String myMsg = (String)responseList.get(0);

                                    for (int i = 1; i < responseList.size(); i++) {
                                        myMsg = myMsg + "\n" + (String)responseList.get(i);
                                    }
                                    outMessage.setMessage(myMsg);
                                    outMessage.setId("2");
                            }
                            if (context.get("dates") != null && context.get("times") != null && context.get("buildings") != null) {
                                myTimer = new MyTimer();
                                dates = (String)context.get("dates");
                                times = (String)context.get("times");
                                buildings = (String)context.get("buildings");

                                // php연결
                                GetData task = new GetData();
                                Log.d("ReadMe", "time = " + myTimer.matchTime(times));
                                if (myTimer.matchTime(times).contains("/")) {   // 강의시간이 2개가 겹치면
                                    String[] mTimes = myTimer.matchTime(times).split("/");
                                    Log.d("ReadMe", "length = " + mTimes.length);
                                    task.execute( "http://" + IP_ADDRESS + "/getjson2.php", buildings, mTimes[0], mTimes[1], myTimer.getChatDow(dates));
                                    Log.d("ReadMe", "result = " + buildings + " + "  + mTimes[0] + " + "  + mTimes[1] + " + " + myTimer.getChatDow(dates));
                                } else {
                                    task.execute( "http://" + IP_ADDRESS + "/getjson2.php", buildings, myTimer.matchTime(times), "x", myTimer.getChatDow(dates));
                                }

                                context.clear();    //context 초기화
                                Log.e("ReadMe", "Context = > "  + dates + " / " + times + " / " + buildings);
                            }
                            messageArrayList.add(outMessage);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    //recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);
                                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Check Internet Connection
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            Toast.makeText(this, " 인터넷이 연결되어있지 않습니다 ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("ReadMe", "response - " + result);
            if (result != null) {
                mJsonString = result;
                showResult();
            } else {
                Toast.makeText(getApplicationContext(),"서버와 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "building=" + params[1] + "&time1=" + params[2] + "&time2=" + params[3] + "&dow=" + params[4];

            Log.d("ReadMe", "post = " + postParameters);
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(4000);
                httpURLConnection.setConnectTimeout(4000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("TAG", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d("TAG", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        String TAG_JSON = "lecture";
        String TAG_ClassRoom = "강의실";
        final Message outMessage=new Message();
        String myMsg = "[조회 결과]\n";

        Log.d("ReadMe", "showResult();");
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String classRoom = item.getString(TAG_ClassRoom);

                Log.d("ReadMe", "item: " + classRoom);
                myMsg = myMsg + classRoom;
                if (i != jsonArray.length() - 1) {
                    myMsg = myMsg + " / ";
                }

            }
            outMessage.setMessage(myMsg);
            outMessage.setId("2");
            messageArrayList.add(outMessage);
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        } catch (JSONException e) {
            Log.d("TAG", "showResult : ", e);
        }

    }

}



