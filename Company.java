package timesheets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            Path file = Path.of("employees.txt");
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
            Path file = Path.of("projects.txt");
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
        timeSheetItems.add(new TimeSheetItem(this.validator,employee,project,beginDate,endDate));
    }

    public List<ReportLine> calculateProjectByNameYearMonth(String employeeName, int year, Month month) {
        List<ReportLine> reportLineList = this.emptyReportListWithProjects();
        for (TimeSheetItem actualTimeSheetItem: this.timeSheetItems) {
            if(this.equalNameToTimesheetEmployeeName(actualTimeSheetItem,employeeName) && this.equelMonthToTimesheetMonth(actualTimeSheetItem,month)) {
                addTimeSheetHoursToReportLineList(reportLineList, actualTimeSheetItem);
            }
        }
        return reportLineList;
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

    public String prepareReport(String employeeName, int year, Month month) {

        if (this.validator.notInEmployeeList(this.employees,employeeName)) {
            throw new IllegalArgumentException("Argument not in employees list "+ employeeName );
        }

        return employeeName + "\t" + LocalDate.of(year,month,1) + "\t" + sumHoursOfEmployeeByMonth(employeeName,month) +"\n" +
                calculateProjectListWithSumHoursByEmployeeMonth(employeeName,year,month);
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

    private void addTimeSheetHoursToReportLineList(List<ReportLine> reportLineList, TimeSheetItem timeSheetItem) {
        for (int i= 0; i<reportLineList.size(); i++) {
            ReportLine actualReportLine = reportLineList.get(i);
            if(reportLineList.get(i).getProject().getName().equals(timeSheetItem.getProject().getName())) {
                reportLineList.get(i).addTime((Long)timeSheetItem.countDifferencBetweenDate().toHours());
            }
        }

    }

    private int sumHoursOfEmployeeByMonth(String name, Month month) {
        int sumHours = 0;
        for (int i=0; i<timeSheetItems.size(); i++) {
            if (this.equalNameToTimesheetEmployeeName(timeSheetItems.get(i),name) && this.equelMonthToTimesheetMonth(timeSheetItems.get(i),month)) {
                sumHours = sumHours+ (int)timeSheetItems.get(i).countDifferencBetweenDate().toHours();
            }
        }
        return sumHours;
    }

    private boolean equalNameToTimesheetEmployeeName(TimeSheetItem timeSheetItem, String employeeName) {
        return timeSheetItem.getEmployee().getFullName().equals(employeeName);
    }

    private boolean equelMonthToTimesheetMonth(TimeSheetItem timeSheetItem, Month month) {
        return timeSheetItem.getBeginDate().getMonth().equals(month);
    }

    private String calculateProjectListWithSumHoursByEmployeeMonth(String employeeNamem, int year, Month month) {
        String result = "";
        for (ReportLine actualReportLine: calculateProjectByNameYearMonth(employeeNamem,year,month)) {
            if(actualReportLine.getTime() != 0L) {
                result = result + actualReportLine.getProject().getName()+"\t"+actualReportLine.getTime()+"\n";
            }
        }
        return result;
    }

}
