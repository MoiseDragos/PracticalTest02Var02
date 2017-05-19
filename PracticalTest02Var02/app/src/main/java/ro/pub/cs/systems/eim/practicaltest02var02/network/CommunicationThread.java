package ro.pub.cs.systems.eim.practicaltest02var02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02var02.model.WeatherForecastInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client information type!");
            String information = bufferedReader.readLine();
            Log.d("MF", "Information: " + information);
//            String informationType = bufferedReader.readLine();
//            Log.d("MF", "InformationType: " + informationType);
//            if (information == null || information.isEmpty() || informationType == null || informationType.isEmpty()) {
            if (information == null || information.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client information type!");
                return;
            }

            ArrayList<String> infoData = serverThread.getData();
            String finalInformation = null;

            if(infoData.contains(information)){
                Log.d("MF", "Aici1 " + information);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                finalInformation = information;
            } else {
                Log.d("MF", "Aici2 " + information);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + information);
//                HttpResponse httpGetResponse = httpClient.execute(httpGet);
//                HttpEntity httpGetEntity = httpGetResponse.getEntity();
//                Log.d("MF", "httpGetEntity " + httpGetEntity);
//                if (httpGetEntity != null) {
//                    // do something with the response
//                    Log.d("MF", EntityUtils.toString(httpGetEntity));
//                    Log.i(Constants.TAG, EntityUtils.toString(httpGetEntity));
//                }
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                Log.d("MF", "pageSource: " + pageSourceCode);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                if(pageSourceCode.indexOf(information) != -1){
                    Log.d("MF", "exists!");
                    serverThread.setInfoData(information);
                    printWriter.println(pageSourceCode);
                    printWriter.flush();
                }


//                Document document = Jsoup.parse(pageSourceCode);
//                Element element = document.child(0);
//                Elements elements = element.getElementsByTag(Constants.SCRIPT_TAG);
//                for (Element script: elements) {
//                    String scriptData = script.data();
//                    Log.d("MF", "scriptData: " + scriptData);
//                }
//                printWriter.println(result);
//                printWriter.flush();
            }

//            HashMap<String, WeatherForecastInformation> data = serverThread.getData();
//            WeatherForecastInformation weatherForecastInformation = null;
//            if (data.containsKey(information)) {
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
//                weatherForecastInformation = data.get(information);
//            } else {
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
//                List<NameValuePair> params = new ArrayList<>();
//                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, information));
//                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//                httpPost.setEntity(urlEncodedFormEntity);
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
//                if (pageSourceCode == null) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
//                    return;
//                }
//                Document document = Jsoup.parse(pageSourceCode);
//                Element element = document.child(0);
//                Elements elements = element.getElementsByTag(Constants.SCRIPT_TAG);
//                for (Element script: elements) {
//                    String scriptData = script.data();
//                    if (scriptData.contains(Constants.SEARCH_KEY)) {
//                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
//                        scriptData = scriptData.substring(position);
//                        JSONObject content = new JSONObject(scriptData);
//                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
//                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
//                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
//                        String condition = currentObservation.getString(Constants.CONDITION);
//                        String pressure = currentObservation.getString(Constants.PRESSURE);
//                        String humidity = currentObservation.getString(Constants.HUMIDITY);
//                        weatherForecastInformation = new WeatherForecastInformation(
//                                temperature, windSpeed, condition, pressure, humidity
//                        );
//                        serverThread.setData(information, weatherForecastInformation);
//                        break;
//                    }
//                }
//            }
//            if (weatherForecastInformation == null) {
//                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather ForecaSt Information is null!");
//                return;
//            }
//            String result = null;
//            switch(informationType) {
//                case Constants.ALL:
//                    result = weatherForecastInformation.toString();
//                    break;
//                case Constants.TEMPERATURE:
//                    result = weatherForecastInformation.getTemperature();
//                    break;
//                case Constants.WIND_SPEED:
//                    result = weatherForecastInformation.getWindSpeed();
//                    break;
//                case Constants.CONDITION:
//                    result = weatherForecastInformation.getCondition();
//                    break;
//                case Constants.HUMIDITY:
//                    result = weatherForecastInformation.getHumidity();
//                    break;
//                case Constants.PRESSURE:
//                    result = weatherForecastInformation.getPressure();
//                    break;
//                default:
//                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
//            }
//            printWriter.println(result);
//            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
//        } catch (JSONException jsonException) {
//            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
//            if (Constants.DEBUG) {
//                jsonException.printStackTrace();
//            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
