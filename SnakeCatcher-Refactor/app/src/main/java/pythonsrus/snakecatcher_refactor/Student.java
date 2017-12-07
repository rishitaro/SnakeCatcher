package pythonsrus.snakecatcher_refactor;

/**
 * Created by rishitaroy on 12/7/17.
 */

public class Student  {
    private  String name;
    private String emailId;
    private int age;

    public Student(String emailId, int age, String name) {
        this.emailId = emailId;
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmailId() {
        return emailId;
    }

    public Student() {
    }
}
