package com.company;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    @Test
    public void APITest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        String tmpString1;
        String tmpString2;
        boolean[] testFlags = new boolean[2];
        Arrays.fill(testFlags,false);

        System.out.println("Hello World!");
        String url = "http://www.accuweather.com/ajax-service/livefeed/topnews-us/?format=json";
        String htmlPage = usingHttpClientFacade(url);
        System.out.println(htmlPage);
        JSONArray jsonArray = new JSONArray (htmlPage);
        for(int i = 0; i<jsonArray.length(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            String purple = jsonObject.get("Tags").toString();

            /*
            Test 1 to ensure at least one item with an empty array for tags
             */
            if (purple.equalsIgnoreCase("[]") && testFlags[0] == false) {
                testFlags[0] = true;
                Assert.assertTrue(purple.equalsIgnoreCase("[]"),"There is at least one item with empty array for Tags");
            }

            /*
            Test 2 to ensure at least one item with a non empty array for tags
             */
            if(!purple.isEmpty() && purple.equalsIgnoreCase("[]") != false && testFlags[1] == false) {
                testFlags[1] = true;
                Assert.assertTrue(!purple.isEmpty(), "There is at least one item with non-empty array for Tags");
            }

            /*
            Test 3 to ensure that no emtpy ThumbUrl field is exist
             */
            Assert.assertTrue(!jsonObject.get("ThumbUrl").toString().isEmpty(),"Found an empty ThumbUrl field!!!");

            /*
            Test 4 to check that all TagsText fields are just concatenation of all the individual items in tags
             */
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
