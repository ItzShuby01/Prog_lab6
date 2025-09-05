package org.example.common.command;

import org.example.common.data.Person;
import java.io.Serializable;

public class AddCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Person person;

    public AddCommand(String arg, Person person) {
        super("add", arg);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}