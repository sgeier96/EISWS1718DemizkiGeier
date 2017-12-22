package com.example.stefangeier.intime;

import java.util.ArrayList;

/**
 * Created by Stefan Geier on 21.11.2017.
 */

class Household {
    private String householdName;
    private ArrayList<User> residents = new ArrayList<User>();
    private ArrayList<Resource> availableResources = new ArrayList<>();

    Household(String householdName){
        this.householdName = householdName;
    }

    Household(String householdName, User resident){
        this.householdName = householdName;
        residents.add(resident);
    }

    Household(String householdName, ArrayList<User> residents){
        this.householdName = householdName;
        this.residents = residents;
    }

    public void addResource(Resource resource){
        availableResources.add(resource);
    }
    public void removeResource(Resource resource){
        availableResources.remove(resource);
    }
    public void addResident(User passedUser) {residents.add(passedUser);}
    public void removeResident(User passedUser) {residents.remove(passedUser);}
}
