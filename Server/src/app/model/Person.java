package app.model;

public class Person {
    private int id;
    private int idEchipa;
    private String userName;
    private String password;
    private int punctajAles;
    private int punctajTotal;
    private int loc;

    public Person(){}
    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdEchipa(int idEchipa) {
        this.idEchipa = idEchipa;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public int getIdEchipa() {
        return idEchipa;
    }

    public int getLoc() {
        return loc;
    }

    public String getUserName() {
        return userName;
    }
}