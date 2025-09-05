package org.example.server.manager;

import org.example.common.data.Location;
import org.example.common.data.Person;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CollectionManager {
    private final TreeSet<Person> personTreeSet;
    private final LocalDateTime initializationDate;

    public CollectionManager() {

        this.personTreeSet = new TreeSet<>();
        this.initializationDate = LocalDateTime.now();
    }

    // Set the collection
    public void setPersonTreeSet(TreeSet<Person> loadedCollection) {
        if (loadedCollection != null) {
            personTreeSet.clear();
            personTreeSet.addAll(loadedCollection);
        }
    }


    public List<Person> getAllPersons() {
        return new ArrayList<>(personTreeSet);
    }


    public int generateId() {
        Random random = new Random();
        int id;
        List<Integer> existingIds = personTreeSet.stream()
                .map(Person::getId)
                .collect(Collectors.toList());
        do {
            id = random.nextInt(Integer.MAX_VALUE) + 1;
        } while (existingIds.contains(id));
        return id;
    }



    public boolean addPerson(Person person) {
        if (person.getId() == null || person.getId() <= 0) {
            person.setId(generateId());
        }
        return personTreeSet.add(person);
    }


    public boolean removePerson(Person person) {
        return personTreeSet.remove(person);
    }


    public void clear() {
        ArrayList<Person> personList = new ArrayList<>(personTreeSet);
        personList.forEach(personTreeSet::remove);
    }

    public Person getMaxById() {
        if (personTreeSet.isEmpty()) {
            return null;
        }
        return personTreeSet.last();
    }


    public long countByLocation(Location targetLocation) {
        if (targetLocation == null) {
            return 0;
        }
        long count = 0;
        for (Person person : personTreeSet) {
            Location personLocation = person.getLocation();
            if (personLocation == null) {
                continue;
            }
            // Handle nullable String name
            boolean nameMatch;
            if (targetLocation.getName() == null) {
                nameMatch = (personLocation.getName() == null); // Match if both are null
            } else {
                nameMatch = (personLocation.getName() != null && targetLocation.getName().equalsIgnoreCase(personLocation.getName()));
            }
            if (nameMatch) {
                count++;
            }
        }
        return count;
    }


    public Person getById(int id) {
        return personTreeSet.stream().filter(p -> p.getId() != null && p.getId() == id).findFirst().orElse(null);
    }


    public String getCollectionType() {
        return personTreeSet.getClass().getSimpleName();
    }



    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }



    public int getElementCount() {
        return personTreeSet.size();
    }




    public List<Person> removeLower(Person threshold) {
        TreeSet<Person> lower = new TreeSet<>(personTreeSet.headSet(threshold));
        personTreeSet.removeAll(lower);
        return new ArrayList<>(lower);
    }



    public double getMaxHeight() {
        return personTreeSet.stream().mapToDouble(Person::getHeight).max().orElse(0.0);
    }

    public double getAverageHeight() {
        if (personTreeSet.isEmpty()) {
            throw new IllegalStateException("Collection is empty");
        }
        return personTreeSet.stream().mapToDouble(Person::getHeight).average().orElse(0.0);
    }


    public boolean updatePerson(int id, Person updatedPerson) {
        Person existing = getById(id);
        if (existing != null) {
            personTreeSet.remove(existing);
            updatedPerson.setId(id);
            personTreeSet.add(updatedPerson);
            return true;
        }
        return false;
    }
}