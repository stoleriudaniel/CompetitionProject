package app.model;

public class Stage {
    private int id;
    private String denumire;

    public Stage(){}
    public Stage(String denumire){
        this.denumire=denumire;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
