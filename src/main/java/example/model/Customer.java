package example.model;

public class Customer {

    private final int id;
    private final String lname;
    private final String fname;
    private final String email;

    public Customer (int id, String lname, String fname, String email) {
        this.id = id;
        this.lname = lname;
        this.fname = fname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getLname() {
        return lname;
    }

    public String getFname() {
        return fname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString () {
        return "Customer:" + "\n" + "id: " + id + "\n" + "lname: " + lname + "\n" + "fname: " + fname + "\n" + "email: " + email + "\n";
    }
}
