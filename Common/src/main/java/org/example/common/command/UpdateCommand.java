package org.example.common.command;

import org.example.common.data.Person;
import java.io.Serializable;

public class UpdateCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Person person; // The Person object with updated data

    public UpdateCommand(String arg, Person person) { // 'arg' will typically be the ID string
        super("update", arg);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}