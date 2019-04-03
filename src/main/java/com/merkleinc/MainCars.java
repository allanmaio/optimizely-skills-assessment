package com.merkleinc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.optimizely.ab.Optimizely;
import com.optimizely.ab.config.Experiment;
import com.optimizely.ab.config.Variation;
import com.optimizely.ab.event.AsyncEventHandler;
import com.optimizely.ab.event.LogEvent;
import com.optimizely.ab.notification.ActivateNotificationListener;
import com.optimizely.ab.notification.NotificationCenter;
import com.optimizely.ab.notification.TrackNotificationListener;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainCars {
    public static void main(String[] args) {

        String datafile = getDatafile();

        try {
            Optimizely optimizelyClient = Optimizely.builder(datafile, new AsyncEventHandler(100,2)).build();
            Random random = new Random();
            String userId = args.length == 0 || args[0] == null ? String.valueOf(random.nextInt()) : args[0];
            Map<String,Object> attributes = new HashMap<>();
            attributes.put("big_spender", true);

            int trackNotificationId = optimizelyClient.notificationCenter.addNotificationListener(NotificationCenter.NotificationType.Track, new TrackNotificationListener() {
                @Override
                public void onTrack(String eventKey, String userId, Map<String, ?> attributes, Map<String, ?> eventTags, LogEvent event) {
                    System.out.println("Registered event with key: " + eventKey + " for user: " + userId);
                }
            });

            int notificationId = optimizelyClient.notificationCenter.addNotificationListener(NotificationCenter.NotificationType.Activate, new ActivateNotificationListener() {
                @Override
                public void onActivate(Experiment experiment, String userId, Map<String, ?> attributes, Variation variation, LogEvent event) {
                    System.out.println("Registered activate event with variantID: " + variation + " for user: " + userId);
                }
            });

            showMenu(userId);
            if ("userTest".equals(userId)) {
                optimizelyClient.setForcedVariation("available_cars", "userTest", "variation_2");
            }


            Variation variation = optimizelyClient.activate("available_cars", userId, attributes);
            if (variation != null) {
                if (variation.is("variation_1")) {
                    showMenuLuxuryCars();
                } else if (variation.is("variation_2")) {
                    ShowMenuCheapCars();
                }
            } else {
                showMenuCarsAvailable();
            }

            int carChoosen = readUserOption();

            switch (carChoosen) {
                case 1:
                    optimizelyClient.track("car_one_chosen", userId, attributes);
                    optimizelyClient.track("car_acquired", userId, attributes);
                    break;
                case 2:
                    optimizelyClient.track("car_two_chosen", userId, attributes);
                    optimizelyClient.track("car_acquired", userId, attributes);
                    break;
                case 3:
                    optimizelyClient.track("car_three_chosen", userId, attributes);
                    optimizelyClient.track("car_acquired", userId, attributes);
                    break;

            }


            if(carChoosen != 0 && optimizelyClient.isFeatureEnabled("offer_insurance", userId, attributes)) {
                Integer discount = optimizelyClient.getFeatureVariableInteger("offer_insurance", "discount", userId, attributes);
                System.out.println("With this car we offer you a discount of " + discount + "% in your new insurance with us!");
                if(readInsuranceOption()) {
                    optimizelyClient.track("insurance_acquired", userId, attributes);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    private static boolean readInsuranceOption() {
        System.out.println("Choose [y/n]:");
        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();
        return "y".equalsIgnoreCase(option);
    }

    private static int readUserOption() {
        System.out.println("Which car would you like to buy? 1 - 2 - 3");
        System.out.println("User 0 if no car picked your interest");
        System.out.println("Choose [1, 2 or 3]: ");
        Scanner sc = new Scanner(System.in);
        int carChoosen = sc.nextInt();
        System.out.println("Car chosen is " + carChoosen);
        return carChoosen;
    }

    private static void ShowMenuCheapCars() {
        System.out.println("Currently available cars:");
        System.out.println("1- Volkswagen UP");
        System.out.println("2- Renault Clio");
        System.out.println("3- Nissan Micra");

    }

    private static void showMenuLuxuryCars() {
        System.out.println("Currently available luxurious cars:");
        System.out.println("1- Aston Martin");
        System.out.println("2- Ferrari 599 GTB");
        System.out.println("3- Bugatti Veyron");

    }

    private static void showMenuCarsAvailable() {
        System.out.println("Currently available cars:");
        System.out.println("1- Ford Focus");
        System.out.println("2- Mercedez Class A");
        System.out.println("3- Renault Megane");

    }

    private static void showMenu(String user) {
        System.out.println("Optimal Car shop");
        System.out.println("Welcome " + user);
        System.out.println("We have great cars in our portfolio:");

    }

    public static String getDatafile() {
        try {

            String datafile = null;
            Gson gson = new Gson();

            File jsonFile = Paths.get("dataFile.json").toFile();
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
            datafile = jsonObject.toString();

            if (datafile == null) {
                String url = "https://cdn.optimizely.com/datafiles/E3XnYhssAeKU7iffTUhQsW.json";
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpget = new HttpGet(url);
                datafile = EntityUtils.toString(
                        httpclient.execute(httpget).getEntity());

            }
            return datafile;
        }
        catch (Exception e) {
            System.out.print(e);
            return null;
        }
    }
}
