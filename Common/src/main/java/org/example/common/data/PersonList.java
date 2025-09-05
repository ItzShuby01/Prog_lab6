package org.example.common.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "persons") // Root element name in XML
public class PersonList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Person> persons;

    // JAXB needs a no-arg constructor
    public PersonList() {}

    public PersonList(List<Person> persons) {
        this.persons = persons;
    }

    @XmlElement(name = "person")
    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}