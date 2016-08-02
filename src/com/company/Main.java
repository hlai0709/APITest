package com.company;

/*
Author: Henry Lai
 */

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

    private JSONObject jsonObject = new JSONObject();
    private JSONArray jsonArray = new JSONArray();
    private boolean[] testFlags = new boolean[2];

    @BeforeClass
    public void APICall() throws Exception {
        Arrays.fill(testFlags,false);
        String url = "http://www.accuweather.com/ajax-service/livefeed/topnews-us/?format=json";
        String htmlPage = usingHttpClientFacade(url);
        jsonArray = new JSONArray(htmlPage);
    }

    @Test
    public void APITest1() throws JSONException {

        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            if(!testFlags[0]){
                String purple = jsonObject.get("Tags").toString();
                if (purple.equalsIgnoreCase("[]")) {
                    Assert.assertTrue(purple.equalsIgnoreCase("[]"),"There is at least one item with empty array for Tags");
                    testFlags[0] = true;
                }
            }
        }



    }

    @Test
    public void APITest2() throws Exception {

        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            if(!testFlags[1]){
                String purple = jsonObject.get("Tags").toString();
                if(!purple.isEmpty() && purple.equalsIgnoreCase("[]") != false) {
                    Assert.assertTrue(!purple.isEmpty(), "There is at least one item with non-empty array for Tags");
                    testFlags[1] = true;
                }
            }
        }

    }

    @Test
    public void APITest3() throws Exception{
        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            Assert.assertTrue(!jsonObject.get("ThumbUrl").toString().isEmpty(),"Found an empty ThumbUrl field!!!");
        }

    }

    @Test
    public void APITest4() throws Exception {

        String tmpString1;
        String tmpString2;
        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            tmpString1 = jsonObject.get("Tags").toString();
            tmpString1 = tmpString1.replace("[","");
            tmpString1 = tmpString1.replace("]","");
            tmpString1 = tmpString1.replace("\"","");
            tmpString2 = jsonObject.get("TagsText").toString();
            tmpString2 = tmpString2.replace("'","");
            Assert.assertEquals(tmpString1,tmpString2);
        }

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
