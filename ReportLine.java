package timesheets;

public class ReportLine {
    private Project project;
    private Long time;

    public ReportLine(Project project, Long time) {
        this.project = project;
        this.time = time;
    }

    public Project getProject() {
        return project;
    }

    public Long getTime() {
        return time;
    }

    public void addTime(Long l) {
        time = time + l;
    }
}
