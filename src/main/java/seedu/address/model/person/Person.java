package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.model.person.contact.Contact;
import seedu.address.model.person.contact.ContactType;
import seedu.address.model.person.github.User;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person implements Comparable<Person> {

    // Identity fields
    private final Name name;

    // Data fields
    private final Address address;
    private final User gitHubUser;
    private final Role role;
    private final Timezone timezone;
    private final Set<Tag> tags = new HashSet<>();
    private final Map<ContactType, Contact> contacts = new HashMap<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Address address, Set<Tag> tags,
                  Map<ContactType, Contact> contacts, Role role, Timezone timezone) {
        requireAllNonNull(name, tags);
        this.name = name;
        this.address = address;
        this.role = role;
        this.timezone = timezone;
        this.tags.addAll(tags);
        this.contacts.putAll(contacts);
        // TODO: CHANGE githubUser in constructor
        // mock user
        this.gitHubUser = null;
    }

    public Name getName() {
        return name;
    }

    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }

    public Optional<User> getGitHubUser() {
        return Optional.ofNullable(gitHubUser);
    }

    public Optional<Role> getRole() {
        return Optional.ofNullable(role);
    }

    public Optional<Timezone> getTimezone() {
        return Optional.ofNullable(timezone);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable contact map, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Map<ContactType, Contact> getContacts() {
        return Collections.unmodifiableMap(contacts);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    @Override
    public int compareTo(Person other) {
        return this.getName().compareTo(other.getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return otherPerson.getName().equals(getName())
                && otherPerson.getAddress().equals(getAddress())
                && otherPerson.getTags().equals(getTags())
                && otherPerson.getContacts().equals(getContacts())
                && otherPerson.getRole().equals(getRole())
                && otherPerson.getTimezone().equals(getTimezone())
                && otherPerson.getGitHubUser().equals(getGitHubUser());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, address, tags, contacts, gitHubUser);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());

        Set<Tag> tags = getTags();
        if (!tags.isEmpty()) {
            builder.append("; Tags: ");
            tags.forEach(builder::append);
        }

        Map<ContactType, Contact> contacts = getContacts();
        if (!contacts.isEmpty()) {
            for (ContactType contactType : contacts.keySet()) {
                builder.append("; " + contactType + ": ");
                builder.append(contacts.get(contactType));
            }
        }

        getAddress().ifPresent(a -> builder.append("; Address: " + a));
        getRole().ifPresent(r -> builder.append("; Role: " + r));
        getTimezone().ifPresent(t -> builder.append("; Timezone: " + t));

        builder.append("; Github: ")
                .append(getGitHubUser());
        return builder.toString();
    }

}
