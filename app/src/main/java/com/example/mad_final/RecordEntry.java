package com.example.mad_final;

// model class with getters/setters for a single score
public class RecordEntry {
    private int id;
    private String name;
    private int score;

    public RecordEntry() {}

    // do not use the third other constructor for inserts -- sqlite should auto generate IDs
    public RecordEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public RecordEntry(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }
}
