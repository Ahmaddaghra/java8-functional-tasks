
import java.time.LocalDate;

class Employee {
Long id;
String firstName;
String lastName;
Position position;
LocalDate dateOfBirth;
public Employee(Long id, String firstName, String lastName, Position position, LocalDate dateOfBirth) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.position = position;
    this.dateOfBirth = dateOfBirth;
}

public Long getId() {
    return id;
}

public Position getPosition() {
    return position;
}



public LocalDate getDateOfBirth() {
    return dateOfBirth;
}

public String getFirstName() {
    return firstName;
}



@Override
public String toString() {
    return id.toString();
}

public String getLastName() {
    return lastName;
}



}


