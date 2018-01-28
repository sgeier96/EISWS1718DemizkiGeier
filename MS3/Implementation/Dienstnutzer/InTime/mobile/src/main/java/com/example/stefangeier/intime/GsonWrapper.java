package com.example.stefangeier.intime;

import com.google.gson.Gson;

/**
 * Created by Stefan Geier on 18.01.2018.
 */

public class GsonWrapper {

    String parameter;
    User user;
    Household household;
    Resource resource;
    String android_id;
    Object o;

    public GsonWrapper(User user, String parameter){ //e.g. for Registration
        this.parameter = parameter;
        this.user = user;
    }
    public GsonWrapper(String android_id, String parameter){ //e.g. for Login
        this.android_id = android_id;
        this.parameter = parameter;
    }
    public GsonWrapper(Household household, String parameter){ //e.g. for Creating a household
        this.parameter = parameter;
        this.household = household;
    }
    public GsonWrapper(Resource resource, String parameter){    //e.g. for editing/adding a resource
        this.parameter = parameter;
        this.resource = resource;
    }
    public GsonWrapper(Household household, User user, String parameter){   //...
        this.parameter = parameter;
        this.user = user;
        this.household = household;
    }
    public GsonWrapper(Household household, Resource resource, String parameter){   //...
        this.household = household;
        this.resource = resource;
        this.parameter = parameter;
    }
    public GsonWrapper(Household household, Resource resource, User user, String parameter){   //...
        this.household = household;
        this.resource = resource;
        this.user = user;
        this.parameter = parameter;
    }
    public GsonWrapper(Object o, String parameter){
        this.o = o;
        this.parameter = parameter;
    }
}
