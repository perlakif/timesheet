package timesheets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class Company {
    private Validator validator;
    private List<Employee> employees;
    private List<Project> projects;
    private List<TimeSheetItem> timeSheetItems;

    public Company(Validator validator) {
        this.validator = validator;
        employees = new ArrayList<>();
        projects = new ArrayList<>();
        timeSheetItems = new ArrayList<>();
        this.loadEmployee();
        this.loadProjects();
    }

    private void loadEmployee() {
        try {
            Path file = Path.of("C:\\Users\\T360-ls-JM-s23\\IdeaProjects\\TimeSheet\\src\\employees.txt");
            List<String> enployeeList = Files.readAllLines(file);
            for (String employeeText: enployeeList) {
                String[] names = employeeText.split(" ");
                this.employees.add(new Employee(names[0], names[1]));
            }
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Can not read file " + ioe.getMessage());
        }
    }

    private void loadProjects() {
        try {
            Path file = Path.of("C:\\Users\\T360-ls-JM-s23\\IdeaProjects\\TimeSheet\\src\\projects.txt");
            List<String> projectsList = Files.readAllLines(file);
            for (String projectText: projectsList) {
                this.projects.add(new Project(projectText));
            }
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Can not read file " + ioe.getMessage());
        }
    }


    public void addTimeSheetItem(Employee employee, Project project, LocalDateTime beginDate, LocalDateTime endDate) {
        TimeSheetItem tsi = new TimeSheetItem(this.validator,employee,project,beginDate,endDate);
        timeSheetItems.add(tsi);
    }

    public List<ReportLine> calculateProjectByNameYearMonth(String name, int year, Month month) {
        List<ReportLine> result = this.emptyReportListWithProjects();
        for (TimeSheetItem tsi: this.timeSheetItems) {
            if(tsi.getEmployee().getFullName().equals(name)) {
                if (tsi.getBeginDate().getMonth().equals(month)) {
                    for (int i= 0; i<result.size(); i++) {
                        if(result.get(i).getProject().getName().equals(tsi.getProject().getName())) {
                            result.get(i).addTime((Long)tsi.countDifferencBetweenDate().toHours());
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<ReportLine> emptyReportListWithProjects() {
        List<ReportLine> result = new ArrayList<>();
        for (Project project: projects ) {
            result.add(new ReportLine(project,0L));
        }
        return result;
    }

    public long sumHours(List<ReportLine> reportLines) {
        long time = 0L;

        for (ReportLine reportLine: reportLines) {
            time=time+ reportLine.getTime();
        }
        return time;
    }

    public String prepareReport(String name, int year, Month month) {
        String result = "";

        if (this.validator.notInEmployeeList(this.employees,name)) {
            throw new IllegalArgumentException("Argument not in employees list "+ name );
        }

        timeSheetItems.sort((timeSheetItem, t1) -> (int)Duration.between(timeSheetItem.getBeginDate(), t1.getBeginDate()).toHours());
        
        int sumHoursOfDay=0;
        for (int i=0; i<timeSheetItems.size(); i++) {
            if (timeSheetItems.get(i).getEmployee().getFullName().equals(name) && timeSheetItems.get(i).getBeginDate().getMonth().equals(month)) {
                sumHoursOfDay = sumHoursOfDay+ (int)timeSheetItems.get(i).countDifferencBetweenDate().toHours();
            }
        }


        result = name + LocalDate.of(year,month,1) + sumHoursOfDay +"\n";
        for (ReportLine rpl: calculateProjectByNameYearMonth(name,year,month)) {
            if(rpl.getTime() != 0L) {
                result = result + rpl.getProject().getName()+"\t"+rpl.getTime()+"\n";
            }
        }

        return result;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<TimeSheetItem> getTimeSheetItems() {
        return timeSheetItems;
    }
}
