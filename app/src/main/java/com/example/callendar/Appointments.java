package com.example.callendar;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Appointments extends Application {
    private List<String> unresolved_appointments;

    public List<String> get_appointments(){
        return unresolved_appointments;
    }

    public String get_appointment_at(int position){
        return unresolved_appointments.get(position);
    }

    public void set_appointment(List<String> appointments){
        unresolved_appointments = new ArrayList<>();
        unresolved_appointments.addAll(appointments);
    }

    public void add_appointment(String appointment){
        unresolved_appointments.add(appointment);
    }



}
