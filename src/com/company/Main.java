package com.company;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    @BeforeClass
    public void APICall() throws Exception {
        boolean[] testFlags = new boolean[2];
        Arrays.fill(testFlags,false);
        JSONObject jsonObject = new JSONObject();
        String url = "http://www.accuweather.com/ajax-service/livefeed/topnews-us/?format=json";
        String htmlPage = usingHttpClientFacade(url);
        JSONArray jsonArray = new JSONArray (htmlPage);
        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);

            if(!testFlags[0]){
               testFlags[0] =  APITest1(jsonObject);
            }

            if(!testFlags[1]){
                testFlags[1] = APITest2(jsonObject);
            }

            APITest3(jsonObject);
            APITest4(jsonObject);
        }



    }

    @Test
    public boolean APITest1(JSONObject jsonObject) throws JSONException {
        String purple = jsonObject.get("Tags").toString();
        if (purple.equalsIgnoreCase("[]")) {
            Assert.assertTrue(purple.equalsIgnoreCase("[]"),"There is at least one item with empty array for Tags");
            return true;
        }
        return false;
    }

    @Test
    public boolean APITest2(JSONObject jsonObject) throws Exception {

        String purple = jsonObject.get("Tags").toString();
        if(!purple.isEmpty() && purple.equalsIgnoreCase("[]") != false) {
            Assert.assertTrue(!purple.isEmpty(), "There is at least one item with non-empty array for Tags");
            return true;
        }
        return false;
    }

    @Test
    public void APITest3(JSONObject jsonObject) throws Exception{
        Assert.assertTrue(!jsonObject.get("ThumbUrl").toString().isEmpty(),"Found an empty ThumbUrl field!!!");
    }

    @Test
    public void APITest4(JSONObject jsonObject) throws Exception {

        String tmpString1;
        String tmpString2;

        tmpString1 = jsonObject.get("Tags").toString();
        tmpString1 = tmpString1.replace("[","");
        tmpString1 = tmpString1.replace("]","");
        tmpString1 = tmpString1.replace("\"","");
        tmpString2 = jsonObject.get("TagsText").toString();
        tmpString2 = tmpString2.replace("'","");
        Assert.assertEquals(tmpString1,tmpString2);
    }




    private static String usingHttpClientFacade(String url) throws Exception {
        HttpGet getRequest = new HttpGet(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //ResponseHandler<String> responseHandler = new BasicResponseHandler();
        getRequest.addHeader("accept", "application/json");

        HttpResponse response = (HttpResponse) httpclient.execute(getRequest);
        //String pageContent =  httpclient.execute(getRequest,responseHandler);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        String output;
        System.out.println("Output from Server .... \n");
        output = br.readLine();
        //System.out.println(output);

        getRequest.releaseConnection();
        httpclient.getConnectionManager().shutdown();


        return output;

    }
}
