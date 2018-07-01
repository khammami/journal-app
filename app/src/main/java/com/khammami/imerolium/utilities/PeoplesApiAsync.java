package com.khammami.imerolium.utilities;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class PeoplesApiAsync extends AsyncTask<Account, Void, Person> {

    private final WeakReference<Context> mContext;

    public PeoplesApiAsync(Context context){
        mContext = new WeakReference<>(context);
    }

    @Override
    protected Person doInBackground(Account... accounts) {

        Person mPerson = null;
        try {
            PeopleService peopleService = PeopleServiceHelper.setUp(mContext.get(), accounts[0]);

            mPerson = peopleService.people().get("people/me")
                    .setPersonFields("coverPhotos")
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mPerson;
    }
}
