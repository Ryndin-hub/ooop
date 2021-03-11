package com.company;

public class CharCounter implements Comparable{
    char character;
    int counter;

    public CharCounter(char c){
        character = c;
        counter = 1;
    }

    public boolean equals(Object obj){
        if (character == '\0' || obj == null) return true;
        CharCounter o = (CharCounter) obj;
        if (character == o.character){
            o.counter++;
            return true;
        }
        return false;
    }

    public int compareTo(Object obj){
        CharCounter o = (CharCounter) obj;
        if (counter == o.counter){
            return 0;
        } else if (counter > o.counter){
            return -1;
        } else return 1;
    }

    public int hashCode(){
        return character;
    }
}
