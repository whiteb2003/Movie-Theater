package com.group5.cpl.utils;

import com.group5.cpl.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class Movie_Seat_Formatter {

    public String formatSeats(int rows, int cols, int num) {// 5 5 0
        StringBuilder formattedSeats = new StringBuilder();
        int firstPosition = num / cols;// 0
        int colLabel = num - firstPosition * cols;// 0-0*5=0
        char rowLabel = (char) ('A' + firstPosition);// A
        formattedSeats.append(rowLabel).append(colLabel + 1);
        return formattedSeats.toString();
    }

    public String formatSeatsUsingCordinate(int rows, int cols, int i, int j) {
        StringBuilder formattedSeats = new StringBuilder();
        int colLabel = j;
        char rowLabel = (char) ('A' + i);
        formattedSeats.append(rowLabel).append(colLabel + 1);
        return formattedSeats.toString();
    }

    public int Reformat(int rows, int cols, String position) {
        int result = 0;
        String colLabel_raw = position.substring(0, 1);// A
        int colLabel = Integer.parseInt(position.substring(1));// 2
        char rowLabel_raw_char = colLabel_raw.charAt(0);// A
        int rowLabel = (int) rowLabel_raw_char - 'A' + 1;// 1
        int number = (rowLabel - 1) * cols + colLabel;//
        return number;
    }

    public int Reformat1(int rows, int cols, String position) {
        String colLabel_raw = position.substring(0, 1);// A B
        int colLabel = Integer.parseInt(position.substring(1));// 1 1
        char rowLabel_raw_char = colLabel_raw.charAt(0);// A B
        int rowLabel = (int) rowLabel_raw_char - 'A' + 1;// 1 2
        int number = (rowLabel - 1) * cols + colLabel;// A 1 2 3 4 5
        return number;
    }

    public List<Seat> sortSeatsInARoom(int rows, int cols, List<Seat> seats) {
        for (int i = 0; i < seats.size(); i++) {
            for (int j = i; j < seats.size(); j++) {
                if (Reformat(rows, cols, seats.get(i).getPosition()) > Reformat(rows, cols,
                        seats.get(j).getPosition())) {
                    Seat temp = seats.get(i);
                    seats.set(i, seats.get(j));
                    seats.set(j, temp);
                }
            }
        }
        return seats;
    }

    public List<Seat> sortSeatsInARoom1(int rows, int cols, List<Seat> seats) {
        for (int i = 0; i < seats.size(); i++) {
            for (int j = i; j < seats.size(); j++) {
                if (Reformat1(rows, cols, seats.get(i).getPosition()) > Reformat(rows, cols,
                        seats.get(j).getPosition())) {
                    Seat temp = seats.get(i);
                    seats.set(i, seats.get(j));
                    seats.set(j, temp);
                }
            }
        }
        return seats;
    }

    public static void main(String[] args) {
        Movie_Seat_Formatter m = new Movie_Seat_Formatter();
        System.out.println(m.Reformat1(5, 5, "B1"));
        // List<String> s = new ArrayList<>();
        // s.add("B1");
        // s.add("A1");
        // s.add("B2");
        // s.add("A3");
        // s.add("B4");
        // s.add("B3");
        // s.add("A4");
        // String s1 = "A1";
        // System.out.println(s1.subSequence(0, 1));
        // System.out.println(s);
    }
}