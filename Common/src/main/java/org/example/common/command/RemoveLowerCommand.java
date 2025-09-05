package org.example.common.command;

import org.example.common.data.Person;
import java.io.Serializable;

public class RemoveLowerCommand extends Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Person person;

    public RemoveLowerCommand(String arg, Person person) {
        super("remove_lower", arg);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}