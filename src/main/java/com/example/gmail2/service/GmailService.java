package com.example.gmail2.service;

import com.example.gmail2.config.ExternalMailConfiguration;
import com.example.gmail2.model.*;
import com.example.gmail2.utils.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service

public class GmailService {
    private final RestTemplate restTemplate;
    private final ExternalMailConfiguration externalMailConfiguration;

    String newString = "";
    public  GmailService(RestTemplate restTemplate, ExternalMailConfiguration externalMailConfiguration){
        this.externalMailConfiguration = externalMailConfiguration;
        this.restTemplate = restTemplate;
    }
    public Object getPrimaryKey(UserPass userPass){
        //UserPass userPassSivia = new UserPass("sivia", "hippo");
        //User.map.put(userPassSivia, UUID.randomUUID());
        /*if(User.map.containsKey(userPass)){
            return new ResponseEntity<>( User.map.get(userPass).toString(), HttpStatus.OK);
        }*/

        for(UUID key : User.map.keySet()){
            if(userPass.equals(User.map.get(key))){
                return new ResponseEntity<>( key.toString(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("nonexistent username  ", HttpStatus.UNAUTHORIZED);
        //return userPass.toString();
        //return User.map.toString();
    }
    public Object receiveExternalMail(GmailinTransit gmailinTransit){
        String fromUserName = gmailinTransit.getFrom();
        String toUserName = gmailinTransit.getRecipientUsername();

        for(UUID key : User.map.keySet()){
            if(toUserName.equals(User.map.get(key).getUsername())){
                ExternalGmail externalGmail = new ExternalGmail(fromUserName, toUserName, gmailinTransit.getMessage());
                User.EXTERNAL_GMAILS.add(externalGmail);
                User.GMAILS.add(null);
                return new ResponseEntity<>( "you got mail", HttpStatus.OK);

            }

        }
        return new ResponseEntity<>( "no such person", HttpStatus.NOT_FOUND);




    }
    public Object receiveString(StringObject string){

        for(int i = string.getString().length()-1; i >= 0; i--){
            newString += string.getString().charAt(i) + "";
        }
        return new ResponseEntity<>( newString, HttpStatus.OK);
    }
    public Object showString(){
        return new ResponseEntity<>(newString, HttpStatus.OK);
    }
    public Object sendString(StringObject string){
        String headerValue = new String(Base64.getEncoder().encode(externalMailConfiguration.getKey().getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-key", headerValue);
        HttpEntity<StringObject> httpEntity = new HttpEntity<>(string, headers);

        try {
            restTemplate.exchange("http://" + "localhost:8382/api/v1/email/receiveString", HttpMethod.POST, httpEntity, Void.class);
        }
        catch (HttpStatusCodeException a) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }


    public Object send(GmailinTransit gmailinTransit){
        try{
            UUID uuid = UUID.fromString(gmailinTransit.getFrom());
            String headerValue = new String(Base64.getEncoder().encode(externalMailConfiguration.getKey().getBytes()));
            HttpHeaders headers = new HttpHeaders();
            headers.add("api-key", headerValue);
            HttpEntity<GmailinTransit> httpEntity = new HttpEntity<>(gmailinTransit, headers);
            if(User.map.containsKey(uuid) == false){
                return new ResponseEntity<>("invalid UUID", HttpStatus.UNAUTHORIZED);

            }
            for(UUID key : User.map.keySet()){
                if(gmailinTransit.getRecipientUsername().equals(User.map.get(key).getUsername())){
                    Gmail gmail = new Gmail(key, uuid, gmailinTransit.getMessage());
                    User.GMAILS.add(gmail);
                    User.EXTERNAL_GMAILS.add(null);

                    return new ResponseEntity<>( "message sent", HttpStatus.OK);
                }


            }
            //if the recipient was not found in the map
            gmailinTransit.from =  User.map.get(uuid).getUsername();
            ExternalGmail externalGmail = new ExternalGmail( gmailinTransit.from ,gmailinTransit.recipientUsername, gmailinTransit.message);
            User.GMAILS.add(null);
            User.EXTERNAL_GMAILS.add(externalGmail);

            try {
                restTemplate.exchange("http://" + "localhost:8382/api/v1/email/receiveExternalMail", HttpMethod.POST, httpEntity, Void.class);
            }
            catch (HttpStatusCodeException a) {
                return new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("message sent", HttpStatus.OK);

        }
        catch (IllegalArgumentException e){

            return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);

        }


    }
    public Object inbox(Key key){
        UUID user = UUID.fromString(key.getKey());
        if(User.map.containsKey(user) == false){
            return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);
        }


        /*for(int i = 0; i < User.GMAILS.size(); i++){
            if(User.GMAILS.get(i).getTo().equals(user)){
                String username = User.map.get(User.GMAILS.get(i).getFrom()).getUsername();
                InboxView inboxView = new InboxView(username, User.GMAILS.get(i).getMessage());
                inboxContents.add(inboxView);
            }
        }*/
        List<InboxView> inboxContents = new LinkedList<>();
        for (int i = 0; i < User.GMAILS.size(); i++){
            if(User.GMAILS.get(i) == null){
                    InboxView inboxView = new InboxView(User.EXTERNAL_GMAILS.get(i).getFromUserName(), User.EXTERNAL_GMAILS.get(i).getMessage());
                    inboxContents.add(inboxView);

            }
            else{
                if(User.GMAILS.get(i).getTo().equals(user)){
                    String username = User.map.get(User.GMAILS.get(i).getFrom()).getUsername();
                    InboxView inboxView = new InboxView(username, User.GMAILS.get(i).getMessage());
                    inboxContents.add(inboxView);
                }
            }

        }



        return new ResponseEntity<>(inboxContents, HttpStatus.OK);
    }

    public Object outbox(Key key){
        UUID user = UUID.fromString(key.getKey());
        if(User.map.containsKey(user) == false){
            return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<OutboxView> outboxContents = new LinkedList<>();


        for (int i = 0; i < User.GMAILS.size(); i++){
            if(User.GMAILS.get(i) == null){
                    OutboxView outboxView = new OutboxView(User.EXTERNAL_GMAILS.get(i).getToUserName(), User.EXTERNAL_GMAILS.get(i).getMessage());
                    outboxContents.add(outboxView);

            }
            else{
                if(User.GMAILS.get(i).getFrom().equals(user)){
                    String username = User.map.get(User.GMAILS.get(i).getTo()).getUsername();
                    OutboxView outboxView = new OutboxView(username, User.GMAILS.get(i).getMessage());
                    outboxContents.add(outboxView);
                }
            }

        }
        return new ResponseEntity<>(outboxContents, HttpStatus.OK);
    }

}
