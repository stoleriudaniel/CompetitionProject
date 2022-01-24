package app.model;

import java.util.List;

public class Echipa {
    private int id;
    private String numeEchipa;
    private List<Person> membrii;
    private int punctajTotal;
    private int loc;

    public Echipa(){}
    public Echipa(String numeEchipa){
        this.numeEchipa=numeEchipa;
    }
    public Echipa(List<Person> membrii){
        this.membrii=membrii;
    }
    public Echipa(String numeEchipa, List<Person> membrii){
        this.membrii=membrii;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setNumeEchipa(String numeEchipa) {
        this.numeEchipa = numeEchipa;
    }

    public void setMembrii(List<Person> membrii) {
        this.membrii = membrii;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public void setPunctajTotal(int punctajTotal) {
        this.punctajTotal = punctajTotal;
    }

    public int getId() {
        return id;
    }

    public int getLoc() {
        return loc;
    }

    public int getPunctajTotal() {
        return punctajTotal;
    }

    public List<Person> getMembrii() {
        return membrii;
    }

    public String getNumeEchipa() {
        return numeEchipa;
    }
}
