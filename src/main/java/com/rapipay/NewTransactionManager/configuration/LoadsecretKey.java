package com.rapipay.NewTransactionManager.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class LoadsecretKey {


    public static final Logger log = LogManager.getLogger(LoadsecretKey.class);
    private String postgresUrl;
    private String postgresUsername;
    private String postgresPassword;
    private String mongodbUrl ;
    public String getPostgresUrl() {
        return postgresUrl;
    }

    public void setPostgresUrl(String postgresUrl) {
        this.postgresUrl = postgresUrl;
    }

    public String getPostgresUsername() {
        return postgresUsername;
    }

    public void setPostgresUsername(String postgresUsername) {
        this.postgresUsername = postgresUsername;
    }

    public String getPostgresPassword() {
        return postgresPassword;
    }

    public void setPostgresPassword(String postgresPassword) {
        this.postgresPassword = postgresPassword;
    }

    public String getMongodbUrl() {
        return mongodbUrl;
    }

    public void setMongodbUrl(String mongodbUrl) {
        this.mongodbUrl = mongodbUrl;
    }


    public void getSecretKeyPostgres(){

        try {

//            String secretName = "postgresql-uat";
//            Region region = Region.of("ap-south-1");
//            SecretsManagerClient client = SecretsManagerClient.builder()
//                    .region(region)
//                    .build();
//            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
//                    .secretId(secretName)
//                    .build();
//            GetSecretValueResponse getSecretValueResponse = null;
//            try {
//                getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
//            } catch (NullPointerException e) {
//                log.error("Something went wrong ------> : {}",e.getMessage());
//
//            } catch (Exception e) {
//                log.error("Something went wrong ------> {} : {}",e.getMessage(), e);
//
//            }
//            String secret = getSecretValueResponse.secretString();

            String secret = "{\"url\":\"jdbc:postgresql://pos-postgrase-db.cjfmfhupk4x7.ap-south-1.rds.amazonaws.com/posuattxn?currentSchema=mst\",\"username\":\"posrapipay\",\"password\":\"rapi@12345\"}";
            JSONObject obj = new JSONObject(secret);
            postgresUrl = obj.optString("url");
            postgresPassword = obj.optString("password");
            postgresUsername = obj.optString("username");
        } catch (NullPointerException e) {
            log.error("Something went wrong ------> : {}",e.getMessage());

        } catch (Exception e) {
            log.error("Something went wrong ------> {} : {}",e.getMessage(), e);

        }
    }


    public void getSecretKeyMongoDb(){

        try {
//
//            String secretName = "mongodb-uat";
//            Region region = Region.of("ap-south-1");
//            SecretsManagerClient client = SecretsManagerClient.builder()
//                    .region(region)
//                    .build();
//            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
//                    .secretId(secretName)
//                    .build();
//            GetSecretValueResponse getSecretValueResponse = null;
//            try {
//                getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
//            } catch (NullPointerException e) {
//                log.error("Something went wrong ------> : {}",e.getMessage());
//
//            } catch (Exception e) {
//                log.error("Something went wrong ------> {} : {}",e.getMessage(), e);
//
//            }
//            String secret = getSecretValueResponse.secretString();
            String secret = "{\"url\":\"mongodb+srv://POSUAT:Rapi1234@pos-uat.bielv9s.mongodb.net/{databaseName}?retryWrites=true&w=majority\",\"username\":\"posrapipay\",\"password\":\"rapi@12345\"}";

            secret = secret.replace("{databaseName}","TRANSACTION_AUDIT");
            JSONObject obj = new JSONObject(secret);
            mongodbUrl = obj.optString("url");
            }catch (NullPointerException e) {
            log.error("Something went wrong ------> : {}",e.getMessage());

        } catch (Exception e) {
            log.error("Something went wrong ------> {} : {}",e.getMessage(), e);

        }
    }
}