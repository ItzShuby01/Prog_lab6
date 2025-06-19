package org.example.common.command;

import org.example.common.data.Person;
import java.io.Serializable;

public class AddIfMaxCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Person person;

    public AddIfMaxCommand(String arg, Person person) {
        super("add_if_max", arg);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}