package timesheets;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSheetItem {
    private Validator validator;
    private Employee employee;
    private Project project;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;

    public TimeSheetItem(Validator validator, Employee employee, Project project, LocalDateTime beginDate, LocalDateTime endDate) {
        this.validator = validator;
        if (validator.notSomeDay(beginDate,endDate)) {
            throw new IllegalArgumentException("BeginDAte and Enddate not the sane day");
        }
        if (validator.beginDateIsLater(beginDate,endDate)) {
            throw new IllegalArgumentException("Begin date is later than Endate");
        }
        this.employee = employee;
        this.project = project;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }


    public Duration countDifferencBetweenDate() {
        return Duration.between(beginDate,endDate);
    }

    public Employee getEmployee() {
        return employee;
    }

    public Project getProject() {
        return project;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
