public class Person {
    private int id;
    private String name;
    private long contact;

    public Person() {
        this(0, "", 0);
    }

    public Person(int id, String name, long contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getContact() {
        return contact;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public String toString() {
        return "Employee ID: " + id + "\n" + "Name: " + name + "\n" + "Contact: " + contact;
    }

    public boolean equals(Object obj) {
        Person per = (Person)obj;
        if(id == per.id) {
            return true;
        } else {
            return false;
        }
    }
}