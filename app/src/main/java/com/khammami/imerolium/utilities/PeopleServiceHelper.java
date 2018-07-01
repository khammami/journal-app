package com.khammami.imerolium.utilities;

import android.accounts.Account;
import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;

import java.io.IOException;
import java.util.Collections;


public class PeopleServiceHelper {

    private static final String APPLICATION_NAME = "journal_app";


    public static PeopleService setUp(Context context, Account mAccount) {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();



        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context,
                        Collections.singleton(
                                "")
                );
        credential.setSelectedAccount(mAccount);

        // credential can then be used to access Google services
        return new PeopleService.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
