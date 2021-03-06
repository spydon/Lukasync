package lukasync.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import lukasync.Lukasync;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class Rest {
    public static JSONObject jsonGet(String urlStr, HashMap<String, String> headers) {
        if (Lukasync.PRINT_DEBUG) {
            System.out.println("\n DEBUG: making GET request");
            System.out.println("  url: " + urlStr);
            System.out.println("  headers: " + headers.toString());
        }

        try {
            URI uri = new URI(urlStr);
            HttpGet get = new HttpGet(uri);

            get.addHeader("Accept", "application/json");

            for (String key : headers.keySet()) {
                if (!key.equalsIgnoreCase("accept") && !key.equalsIgnoreCase("content-type")) {
                    get.addHeader(key, headers.get(key));
                }
            }

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(get);

            if (Lukasync.PRINT_DEBUG) {
                //debugResponse(response);
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
                String result = writer.toString();

                if (Lukasync.PRINT_DEBUG) {
                    System.out.println("DEBUG: response entity: " + result);
                }

                return new JSONObject(result);
            } else {
                throw new IllegalArgumentException(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a JSON error.", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a connection error.", e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException("jsonGet called with bad url.", e);
        }
    }

    public static JSONObject jsonPost(String urlStr, HashMap<String, String> headers) {
        return jsonPost(urlStr, headers, null);
    }

    public static JSONObject jsonPost(String urlStr, HashMap<String, String> headers, JSONObject payload) {
        if (Lukasync.PRINT_DEBUG) {
            System.out.println("\nDEBUG: making POST request");
            System.out.println("  url: " + urlStr);
            System.out.println("  headers: " + headers.toString());
            System.out.println("  payload: " + payload);
        }

        try {
            URI uri = new URI(urlStr);
            HttpPost post = new HttpPost(uri);

            post.addHeader("Content-Type", "application/x-www-form-urlencoded"); // LOL zurmo
            post.addHeader("Accept", "application/json");

            for (String key : headers.keySet())
                if (!key.equalsIgnoreCase("accept") && !key.equalsIgnoreCase("content-type"))
                    post.addHeader(key, headers.get(key));

            if (payload != null) {
                if (Lukasync.PRINT_DEBUG) {
                    System.out.println("DEBUG: POSTing payload: " + JSONUtil.jsonToURLEncoding(payload));
                }

                StringEntity entity = new StringEntity(JSONUtil.jsonToURLEncoding(payload));
                post.setEntity(entity);
            }

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(post);

            if (Lukasync.PRINT_DEBUG) {
                //debugResponse(response);
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
                String result = writer.toString();

                if (Lukasync.PRINT_DEBUG) {
                    System.out.println("DEBUG: response entity: " + result);
                }

                return new JSONObject(result);
            } else {
                throw new IllegalArgumentException(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a JSON error.", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a connection error.", e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException("jsonPost called with bad url.", e);
        }
    }

    public static JSONObject jsonPut(String urlStr, HashMap<String, String> headers, JSONObject payload) {
        if (Lukasync.PRINT_DEBUG) {
            System.out.println("\nDEBUG: making PUT request");
            System.out.println("  url: " + urlStr);
            System.out.println("  headers: " + headers.toString());
            System.out.println("  payload: " + payload);
        }

        try {
            URI uri = new URI(urlStr);
            HttpPut put = new HttpPut(uri);

            put.addHeader("Content-Type", "application/x-www-form-urlencoded"); // LOL zurmo
            put.addHeader("Accept", "application/json");

            for (String key : headers.keySet())
                if (!key.equalsIgnoreCase("accept") && !key.equalsIgnoreCase("content-type"))
                    put.addHeader(key, headers.get(key));

            if (payload != null) {
                if (Lukasync.PRINT_DEBUG) {
                    System.out.println("DEBUG: PUTing payload: " + JSONUtil.jsonToURLEncoding(payload));
                }

                StringEntity entity = new StringEntity(JSONUtil.jsonToURLEncoding(payload));
                put.setEntity(entity);
            }

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(put);

            if (Lukasync.PRINT_DEBUG) {
                //debugResponse(response);
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
                String result = writer.toString();

                if (Lukasync.PRINT_DEBUG) {
                   System.out.println("DEBUG: response entity: " + result);
                }

                return new JSONObject(result);
            } else {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
                String result = writer.toString();

                if (Lukasync.PRINT_DEBUG) {
                    System.out.println("DEBUG: response entity: " + result);
                }

                throw new IllegalArgumentException(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a JSON error.", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Encountered a connection error.", e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException("jsonPut called with bad url.", e);
        }
    }

    /*
     * Debug stuff
     */

    @SuppressWarnings("unused")
    private static void debugResponse(HttpResponse response) {
        System.out.println("\nDEBUG: ");
        System.out.println(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());

        for (Header header : response.getAllHeaders())
            System.out.println(header);

    }

}