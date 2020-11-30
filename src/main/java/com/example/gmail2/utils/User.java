package com.example.gmail2.utils;

import com.example.gmail2.model.ExternalGmail;
import com.example.gmail2.model.Gmail;
import com.example.gmail2.service.UserPass;
import lombok.Data;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@UtilityClass
public class User {

    //public final static Map<UserPass, UUID> map = new HashMap<UserPass, UUID>();
    public final static Map<UUID, UserPass> map = Stream.of(
            new AbstractMap.SimpleEntry<>(UUID.randomUUID(), UserPass.builder()
                    .password("pass")
                    .username("cookoo")
                    .build()  ),
            new AbstractMap.SimpleEntry<>(UUID.randomUUID(), UserPass.builder()
                    .password("pass")
                    .username("noony")
                    .build()   )
    )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));



    public final static List<Gmail> GMAILS =  new ArrayList<>();
    public final static List<ExternalGmail> EXTERNAL_GMAILS =  new ArrayList<>();


}
