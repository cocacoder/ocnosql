package com.ailk.oci.ocnosql.example.httpclient;


import com.ailk.oci.ocnosql.common.util.CommonConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: lile3
 * Date: 14-4-24
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
public class MyHttpClient {
	static Log LOG = LogFactory.getLog(MyHttpClient.class);

    public static final String TABLENAME = "DR_QUERY_TEST";

    static final String baseUrl = "http://localhost:8080/drquery/common?type=";

    public static void main(String[] args) {
        try {
        String result = doQueryByHBaseAPI();
//        String result = doQueryBySQLAPI();
//        String result = doPut();
            LOG.info("doput=" + result);
        } catch (IOException e) {
        	LOG.error(e);
        }
    }

    public static String doQueryByHBaseAPI()throws IOException{
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("mobile","15810705424"));
        urlParameters.add(new BasicNameValuePair("startTime","20140401"));
        urlParameters.add(new BasicNameValuePair("endTime","20140403"));
        urlParameters.add(new BasicNameValuePair("APPNAME","weibo"));

        return doCommon("queryByHBaseAPI",urlParameters);
    }

    public static String doQueryBySQLAPI() throws IOException{
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("mobile","15810705424"));
        urlParameters.add(new BasicNameValuePair("startTime","20140401"));
        urlParameters.add(new BasicNameValuePair("endTime","20140403"));
//        urlParameters.add(new BasicNameValuePair("APPNAME","weibo"));

        return doCommon("queryBySQL",urlParameters);
    }

    public static String doPut() throws IOException{
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(CommonConstants.TABLE_NAME,TABLENAME+"201404"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.SKIPBADLINE,"false"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.BATCH_PUT,"true"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.HBASE_MAXPUTNUM,"1"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.SKIPBADLINE,"false"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.COLUMNS,"F:APPNAME,F:FLOW,F:PHONE_NO,F:START_TIME"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.SEPARATOR,";"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.ROWKEYGENERATOR,"md5"));
        urlParameters.add(new BasicNameValuePair(CommonConstants.ROWKEYCOLUMN,"PHONE_NO,START_TIME"));
        urlParameters.add(new BasicNameValuePair("records","qq;1;13836263798;20140403|YOUKU;2;13836263798;20140404"));

        return doCommon("put",urlParameters);
    }

    public static String doCommon(String type,List<NameValuePair> urlParameters) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(baseUrl+type);
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        LOG.info("Response Code : "
                + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

}
