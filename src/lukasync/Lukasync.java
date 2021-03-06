package lukasync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import lukasync.service.Scheduler;
import lukasync.service.SyncTask;
import lukasync.util.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class Lukasync {

    public static final boolean PRINT_DEBUG = true;
    public static final boolean MODE_DEV = false;
    public static final String INITIAL_IMPORT_UPDATE_TIME = "1990-12-31";

    public static String DB = "jdbc:mysql://londonsales.com.au/lukasync";
    public static String USER = "lukasync";
    public static String PASS = "Daniel1985";

    public static long INITIAL_WAIT = 0;
    public static long WAIT = 3600000;

    public static void main(String[] args) {
        if (args.length > 0) {
            for (int x = 0; x < args.length; x++) {
                String arg = args[x];
                switch (arg.toLowerCase()) {
                    case "--database":
                    case "-d":
                        if (x + 1 < args.length && !args[x + 1].contains("-")) {
                            DB = args[x + 1];
                            System.out.println("Sets the database to: " + args[x + 1]);
                            x++;
                        } else {
                            System.out.println("Not a valid database for -d/--database");
                        }
                        break;
                    case "--help":
                    case "-h":
                        System.out.println("Available Parameters (default value):");
                        System.out.println("\t-h or --help, shows this information");
                        System.out.println("\t-i or --initial-wait X, sets the waiting time(in seconds) before the first update. (" + INITIAL_WAIT + ")");
                        System.out.println("\t-w or --wait X, sets the waiting time(in seconds) between each update              (" + WAIT + ")");

                        System.out.println("\n\tConfig for the database containing sync data and settings for lukasync");
                        System.out.println("\t-d or --database X, sets the database          (" + DB + ")");
                        System.out.println("\t-u or --user X, sets the database user         (" + USER + ")");
                        System.out.println("\t-p or --password X, sets the database password (" + PASS + ")");

                        System.exit(0);
                    case "--initial-wait":
                    case "-i":
                        if (x + 1 < args.length && !args[x + 1].contains("-")) {
                            try {
                                INITIAL_WAIT = Integer.parseInt(args[x + 1]) * 60 * 1000;
                                System.out.println("Sets the initial wait time to: " + args[x + 1] + " minutes");
                            } catch (NumberFormatException nfe) {
                                System.out.println("Not a valid number for -i/--initial-wait");
                                System.exit(0);
                            }
                            x++;
                        } else {
                            System.out.println("Not a valid delay for -i/--initial-wait");
                        }
                        break;
                    case "--password":
                    case "-p":
                        if (x + 1 < args.length && !args[x + 1].contains("-")) {
                            PASS = args[x + 1];
                            System.out.println("Sets the database password to: " + args[x + 1]);
                            x++;
                        } else {
                            System.out.println("Not a valid password for -p/--password");
                        }
                        break;
                    case "--user":
                    case "-u":
                        if (x + 1 < args.length && !args[x + 1].contains("-")) {
                            USER = args[x + 1];
                            System.out.println("Sets the database user to: " + args[x + 1]);
                            x++;
                        } else {
                            System.out.println("Not a valid user for -u/--user");
                        }
                        break;
                    case "--wait":
                    case "-w":
                        if (x + 1 < args.length && !args[x + 1].contains("-")) {
                            try {
                                WAIT = Integer.parseInt(args[x + 1]) * 60 * 1000;
                                System.out.println("Sets the wait in between runs to: " + args[x + 1] + " minutes");
                            } catch (NumberFormatException nfe) {
                                System.out.println("Not a valid number for -w/--wait");
                                System.exit(0);
                            }
                            x++;
                        } else {
                            System.out.println("Not a valid delay for -w/--wait");
                        }
                        break;
                    default:
                        System.out.println("Faulty parameters!");
                        System.exit(0);
                }
            }
        } else {
            System.out.println("Lukasync can take terminal parameters(see --help).");
            System.out.println("It fetches connection details from a database stored at " + DB);
        }

        System.out.println("\n    The software is distributed in the hope that it will be useful,");
        System.out.println("    but without any warranty; without even the implied warranty of");
        System.out.println("    merchantability or fitness for a particular purpose.");
        System.out.println("    This software was coded by Lukas Klingsbo(lukas.klingsbo@gmail.com)");
        System.out.println("    and Emilio Nyaray(emilio@nyaray.com)");

        if (MODE_DEV) {
            throw new IllegalStateException("No dev-testing code in place, lol!"); // comment this out when testing
        } else {
            Scheduler scheduler = new Scheduler();
            scheduler.scheduleWithFixedDelay(new SyncTask(),
                    INITIAL_WAIT,
                    WAIT,
                    TimeUnit.MILLISECONDS);
        }
    }

    public static JSONObject fetchConfig() {
        JSONObject conf = new JSONObject();

        try {
            Connection conn = DriverManager.getConnection(
                    DB,
                    USER,
                    PASS
            );

            QueryBuilder serviceQuery = new QueryBuilder(
                    "id, name, type, connection_type," +
                            "address, database_name, username, password",
                    "service",
                    "",
                    "",
                    ""
            );

            PreparedStatement servicesStatement = conn.prepareStatement(serviceQuery.getQuery());
            ResultSet servicesResult = servicesStatement.executeQuery();
            while (servicesResult.next()) {
                JSONObject service = new JSONObject();

                service.put("id", servicesResult.getInt("id"));
                service.put("name", servicesResult.getString("name"));
                service.put("type", servicesResult.getString("type"));
                service.put("connectionType", servicesResult.getString("connection_type"));
                service.put("address", servicesResult.getString("address"));
                service.put("databaseName", servicesResult.getString("database_name"));
                service.put("username", servicesResult.getString("username"));
                service.put("password", servicesResult.getString("password"));
                service.put("destinations", new JSONArray());

                conf.putOnce("" + servicesResult.getInt("id"), service);
            }

            servicesResult.close();
            servicesStatement.close();

            QueryBuilder serviceFlowsQuery = new QueryBuilder(
                    "id, source, destination, enabled",
                    "service_flow",
                    "",
                    "",
                    ""
            );

            String serviceFlowsQueryString = serviceFlowsQuery.getQuery();
            PreparedStatement serviceFlowsStatement = conn.prepareStatement(serviceFlowsQueryString);

            JSONObject jobs = new JSONObject();
            ResultSet serviceFlowsResult = serviceFlowsStatement.executeQuery();
            while (serviceFlowsResult.next()) {
                String sourceKey = "" + serviceFlowsResult.getInt("source");
                JSONObject service = conf.getJSONObject(sourceKey);

                if (serviceFlowsResult.getBoolean("enabled")) {
                    JSONArray destinations = service.getJSONArray("destinations");
                    String destinationKey = "" + serviceFlowsResult.getInt("destination");

                    jobs.put(getJobKey(sourceKey, destinationKey), serviceFlowsResult.getInt("id"));
                    destinations.put(destinationKey);
                }
            }

            conf.put("jobs", jobs);

            serviceFlowsResult.close();
            serviceFlowsStatement.close();

            conn.close();
        } catch (Throwable t) {
            System.err.println("Couldn't fetch configuration due to an error:");
            t.printStackTrace();
        }

        return conf;
    }

    public static String getJobKey (String sourceKey, String destinationKey) {
        return sourceKey + ":" + destinationKey;
    }

    public static String getJobKey (int sourceKey, int destinationKey) {
        return getJobKey(""+sourceKey, ""+destinationKey);
    }
}
