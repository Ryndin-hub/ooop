package com.company;

public class CharCounter {
    String character;
    int counter;

    public CharCounter(String c){
        character = c;
        counter = 1;
    }

    public boolean equals(Object obj){
        if (character == null || obj == null) return true;
        CharCounter o = (CharCounter) obj;
        if (character.equals(o.character)){
            o.counter++;
            return true;
        }
        return false;
    }

    public int hashCode(){
        return counter;
    }
}
