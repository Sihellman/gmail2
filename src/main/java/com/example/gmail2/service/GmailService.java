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
    List<InboxView> inboxContents = new LinkedList<>();
    String newString = "";
    public  GmailService(RestTemplate restTemplate, ExternalMailConfiguration externalMailConfiguration){
        this.externalMailConfiguration = externalMailConfiguration;
        this.restTemplate = restTemplate;
    }
    public Object getPrimaryKey(UserPass userPass){//should not need loop
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

        return new ResponseEntity<>("better luck next time  ", HttpStatus.UNAUTHORIZED);
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


    public Object send(GmailinTransit gmailinTransit){//avoided a loop, had to use a loop
        try{
            UUID uuid = UUID.fromString(gmailinTransit.getFrom());
            String headerValue = new String(Base64.getEncoder().encode(externalMailConfiguration.getKey().getBytes()));
            HttpHeaders headers = new HttpHeaders();
            headers.add("api-key", headerValue);
            HttpEntity<GmailinTransit> httpEntity = new HttpEntity<>(gmailinTransit, headers);
            if(User.map.containsKey(uuid) == false){
                return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);

            }
            for(UUID key : User.map.keySet()){
                if(gmailinTransit.getRecipientUsername().equals(User.map.get(key).getUsername())){
                    Gmail gmail = new Gmail(key, uuid, gmailinTransit.getMessage());
                    User.GMAILS.add(gmail);
                    return new ResponseEntity<>( "message sent", HttpStatus.OK);
                }
                else{

                    try {
                        restTemplate.exchange("http://" + externalMailConfiguration.getUrl() + "/api/v1/email/receiveExternalMail", HttpMethod.POST, httpEntity, Void.class);
                    }
                    catch (HttpStatusCodeException a) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                }

            }
        }
        catch (IllegalArgumentException e){


        }

        return new ResponseEntity<>("nonexistent username", HttpStatus.NOT_FOUND);
    }
    public Object inbox(Key key){//avoided a loop
        UUID user = UUID.fromString(key.getKey());
        if(User.map.containsKey(user) == false){
            return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);
        }


        for(int i = 0; i < User.GMAILS.size(); i++){
            if(User.GMAILS.get(i).getTo().equals(user)){
                String username = User.map.get(User.GMAILS.get(i).getFrom()).getUsername();
                InboxView inboxView = new InboxView(username, User.GMAILS.get(i).getMessage());
                inboxContents.add(inboxView);

            }
        }
        for(int i = 0; i < User.EXTERNAL_GMAILS.size(); i++){

                String username = User.EXTERNAL_GMAILS.get(i).getFromUserName();
                InboxView inboxView = new InboxView(username, User.EXTERNAL_GMAILS.get(i).getMessage());
                inboxContents.add(inboxView);

        }




        return new ResponseEntity<>(inboxContents, HttpStatus.OK);
    }

    public Object outbox(Key key){//avoided a loop
        UUID user = UUID.fromString(key.getKey());
        if(User.map.containsKey(user) == false){
            return new ResponseEntity<>("invalid UUID", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<OutboxView> outboxContents = new LinkedList<>();

        for(int i = 0; i < User.GMAILS.size(); i++){
            if(User.GMAILS.get(i).getFrom().equals(user)){
                String username = User.map.get(User.GMAILS.get(i).getTo()).getUsername();
                OutboxView outboxView = new OutboxView(username, User.GMAILS.get(i).getMessage());
                outboxContents.add(outboxView);
            }
        }
        return new ResponseEntity<>(outboxContents, HttpStatus.OK);
    }

}
