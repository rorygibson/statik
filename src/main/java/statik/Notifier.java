package statik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

// Notify by making a POST to the URL.
public class Notifier {

    private static final Logger LOG = LoggerFactory.getLogger(Notifier.class);
    private final String url;

    public Notifier(String url) {
        this.url = url;
    }

    public void notify(String name, String value, String location) {
        LOG.debug("Notifying " + this.url + " of " + name + value + location);

        String urlParameters = URLEncoder.encode("occurrence-name") + "=" + URLEncoder.encode(name) + "&" +
                               URLEncoder.encode("occurrence-value") + "=" + URLEncoder.encode(value) + "&" +
                               URLEncoder.encode("occurrence-location") + "=" + URLEncoder.encode(location);
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.length()));
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            LOG.debug("\nSending 'POST' request to URL : " + url);
            LOG.debug("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            conn.disconnect();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
