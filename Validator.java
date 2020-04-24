package timesheets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Validator {


    public Boolean notSomeDay(LocalDateTime ldt1, LocalDateTime ldt2) {
        return !ldt1.toLocalDate().equals(ldt2.toLocalDate());
    }

    public Boolean beginDateIsLater(LocalDateTime begin, LocalDateTime end) {
        return begin.isAfter(end);
    }

    public Boolean notInEmployeeList (List<Employee> employees, String name) {
        for (Employee employee: employees) {
            if(employee.getFullName().equals(name)) {
                return false;
            }
        }
        return true;
    }
}
