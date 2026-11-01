import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;

// Task 1 Part A
public class Main {

    public static void main(String[] args) {

        List<Employee> employees = List.of(
                new Employee(1L, "Ahmad", "Khalil", Position.QA, LocalDate.of(1995, 3, 14)),
                new Employee(2L, "Sara", "Nasser", Position.BE, LocalDate.of(1990, 7, 2)),
                new Employee(3L, "Omar", "Haddad", Position.FE, LocalDate.of(1998, 11, 20)),
                new Employee(4L, "Lina", "Saleh", Position.BE, LocalDate.of(1993, 1, 9)),
                new Employee(5L, "Yousef", "Odeh", Position.HR, LocalDate.of(1988, 5, 30)),
                new Employee(6L, "Rana", "Issa", Position.QA, LocalDate.of(2000, 9, 15)));
        Function<Employee, String> getIdFunction = employee -> employee.getId().toString(); // toString convert Long to
                                                                                            // String
        String result = employees.stream()
                .map(getIdFunction)
                .collect(Collectors.joining(","));
        System.out.println(result);

        // Map<Long, Employee> employeeMap = employees.stream()
        // .collect(Collectors.toMap(// toMap takes two functions, the first one is the
        // key and the second one is the value
        // Employee::getId,
        // Function.identity())); // identity function returns the input argument as is,
        // so it will return the Employee object itself

        // if tow employees have the same id, Collectores.toMap throws and
        // IlegalStateException, so we need to handle that case by providing a merge
        // function that will be used to resolve the conflict
        Map<Long, Employee> employeeMapWithMerge = employees.stream()
                .collect(Collectors.toMap(
                        Employee::getId,
                        Function.identity(),
                        (existing, replacement) -> existing)); // if two employees have the same id, we will keep the
                                                               // existing one and ignore the replacement one

        Map<Position, List<Employee>> employeesByPosition = employees.stream()
                // groupingby automaticly create list for each group
                .collect(Collectors.groupingBy(Employee::getPosition)); // classifier function is used to group the
                                                                        // employees by their position, and the result
                                                                        // is a map where the key is the position and
                                                                        // the value is a list of employees with that
                                                                        // position
        System.out.println(employeesByPosition);

        Map<Position, Long> countByPosition = employees.stream() // return Long cause of what the downstream returns.
                .collect(Collectors.groupingBy(
                        Employee::getPosition, Collectors.counting())); // counting is a downstream collector that
                                                                        // counts the number of elements in each group
        System.out.println(countByPosition);

        // Task 1 Part B
        List<Card> cards = List.of(
                new Card("c1", "fp-A", LocalDate.of(2027, 5, 31)),
                new Card("c2", "fp-B", LocalDate.of(2026, 12, 31)),
                new Card("c3", "fp-A", LocalDate.of(2029, 1, 31)),
                new Card("c4", "fp-C", LocalDate.of(2028, 3, 31)),
                new Card("c5", "fp-B", LocalDate.of(2028, 7, 31)),
                new Card("c6", "fp-A", LocalDate.of(2025, 2, 28)));
        // nullsfirst treats nulls as older than anything
        // natural order orders dates numiraclly.
        Comparator<LocalDate> dateComparator = Comparator.nullsFirst(Comparator.naturalOrder());

        Map<String, Card> noSharedFingerPrints = cards.stream()
                .collect(Collectors.toMap(
                        card -> card.getFingerprint(),
                        Function.identity(),
                        (existing, replacement) -> {
                            int comparsion = dateComparator.compare(existing.getExpiryDate(),
                                    replacement.getExpiryDate());
                            if (comparsion > 0) {
                                return existing;
                            } else if (comparsion < 0) {
                                return replacement;
                            } else
                                return existing;
                        }
                // both null? return existining
                ));

        List<Card> cardList = noSharedFingerPrints.values().stream()
                .collect(Collectors.toList());

        System.out.println(cardList.toString());

        // Task 2 Salary Calcolater

        LocalDate asOF = LocalDate.of(2026, 1, 1);
        employees.forEach(employee -> {
            System.out.println(employee.getFirstName()+" : "+calculate(employee, asOF));
        });
// Task 3
        Function<Employee,String> convertEmployeeToString = employee -> String.join(
            " " , employee.getFirstName() , employee.getLastName()
        );

        Optional<Employee> oldest = findOldest(employees);

        String oldestEmployeeName = oldest.map(convertEmployeeToString).orElse("No employees"); // not an expinsive approuch so orElse is fine
        
        System.out.println(oldestEmployeeName);

        Predicate<Employee> isQA = employee -> employee.getPosition() == Position.QA;
        Predicate<Employee> isFE = employee -> employee.getPosition() == Position.FE;

        List<Employee> matchingEmployees = employees.stream()
            .filter((isQA.or(isFE)).and(bornBefore(1996)))
            .collect(Collectors.toList());
        System.out.println(matchingEmployees);




        //Supplier

        List<Employee> testEmployees = Stream.generate(employeeSupplier)
                                .limit(100)
                                .collect(Collectors.toList());
        System.out.println(testEmployees.size());

        //Consumer

        employees.forEach(combiend);


    }



    // Task 2 Salary Calcolater

    public static BigDecimal calculateQA(Employee employee, LocalDate asOF) {
        int age = Period.between(employee.getDateOfBirth(), asOF).getYears();
        return BigDecimal.valueOf(1200 + (20 * age));
    }

    public static BigDecimal calculateBE(Employee employee, LocalDate asOF) {
        int age = Period.between(employee.getDateOfBirth(), asOF).getYears();
        return BigDecimal.valueOf(1000 + (35 * age));
    }

    public static BigDecimal calculateFE(Employee employee, LocalDate asOF) {
        int age = Period.between(employee.getDateOfBirth(), asOF).getYears();
        return BigDecimal.valueOf(1400 + (25 * age));
    }

    public static BigDecimal calculateHR(Employee employee, LocalDate asOF) {
        int age = Period.between(employee.getDateOfBirth(), asOF).getYears();
        return BigDecimal.valueOf(1100 + (15 * age));
    }

    private static final Map<Position, BiFunction<Employee, LocalDate, BigDecimal>> calculators = new EnumMap<>(
            Position.class);

    static {
        // methoeds aren't excuted yet, it's just a refrence (linking each position with
        // it's calcolater)
        calculators.put(Position.QA, Main::calculateQA);
        calculators.put(Position.BE, Main::calculateBE);
        calculators.put(Position.FE, Main::calculateFE);
        calculators.put(Position.HR, Main::calculateHR);
    }

    public static BigDecimal calculate(Employee employee, LocalDate asOF) {
        return Optional.ofNullable(calculators.get(employee.getPosition()))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "No Salary calculater for position : " + employee.getPosition()))
                .apply(employee, asOF);
    }


    // Task 3 
    public static Optional<Employee> findOldest(List<Employee> employees){ return employees.stream()
            .min(Comparator.comparing(Employee::getDateOfBirth));
    }

 
    public static Predicate<Employee> bornBefore(int year){
        return employee -> employee.getDateOfBirth().getYear() < year;
    }

        // Supplier
    static AtomicLong idCounter = new AtomicLong(1000L);
    static Random random = new Random();


    public static Supplier<Employee> employeeSupplier = () -> {
        Position[] positions = Position.values();
        Position randomPosition = positions[random.nextInt(positions.length)];
        Long uniqueId = idCounter.getAndIncrement();



        int minYear = 1970;
        int maxYear = 2005;
        int randomYear = minYear + random.nextInt(maxYear - minYear + 1);
        int randomDayOfYear = 1 + random.nextInt(LocalDate.of(randomYear, 1, 1).lengthOfYear());
        LocalDate randomDob = LocalDate.ofYearDay(randomYear, randomDayOfYear);

        return new Employee(uniqueId,"ahmad","mohammad", randomPosition, randomDob);

    };

    public static Consumer<Employee> logEmployee = employee ->
                System.out.println("New employee: " + employee.getFirstName());

    public static Consumer<Employee> sendWelcomeEmail = email -> 
                System.out.println("Welcome email sent to "+email.getFirstName()
                );

    public static Consumer<Employee> writeAudit = audit -> 
                System.out.println("AUDIT: Employee "+ audit.getId().toString() + " onboarded");
    public static Consumer<Employee> combiend = 
                logEmployee.andThen(sendWelcomeEmail.andThen(writeAudit));
}
