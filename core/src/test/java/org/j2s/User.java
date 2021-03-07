package org.j2s;

@J2SLibrary(name = "user-test-library")
@J2SModel
public class User {

    private String firstname;
    private String lastname;
    private int age;

    private String[] nicknames;

    private String[][] matrix;

    private Role mainRole;

    private Role[] roles;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String[] getNicknames() {
        return nicknames;
    }

    public void setNicknames(String[] nicknames) {
        this.nicknames = nicknames;
    }
}
