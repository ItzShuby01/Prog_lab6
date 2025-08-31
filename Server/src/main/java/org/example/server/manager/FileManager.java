package org.example.server.manager;

import org.example.common.data.Person;
import org.example.common.data.PersonList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class FileManager {
    private final CollectionManager collectionManager;

    public FileManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }


    public void loadCollectionFromXml(String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            PersonList personList = (PersonList) unmarshaller.unmarshal(new File(filePath));
            List<Person> persons = personList.getPersons();
            if (persons == null) {
                persons = new ArrayList<>();
            }
            TreeSet<Person> loadedCollection = new TreeSet<>(persons);
            collectionManager.setPersonTreeSet(loadedCollection);
        } catch (JAXBException e) {
            System.out.println("Error loading collection: " + e.getMessage());
        }
    }

    public void saveCollectionToXml(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Save file path cannot be null or empty.");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(PersonList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            PersonList personList = new PersonList(collectionManager.getAllPersons());
            marshaller.marshal(personList, new File(filePath));
            System.out.println("Collection saved successfully!");
        } catch (Exception e) {
            System.out.println("Error saving collection: " + e.getMessage());
        }
    }
}