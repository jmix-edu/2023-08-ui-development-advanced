package com.company.jmixpm.app;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.Task;
import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private final DataManager dataManager;
    private final CurrentAuthentication currentAuthentication;

    public TaskService(DataManager dataManager, CurrentAuthentication currentAuthentication) {
        this.dataManager = dataManager;
        this.currentAuthentication = currentAuthentication;
    }

    public User findLeastBusyUser() {
        return dataManager.loadValues("select u, count(t.id) " +
                        "from User u left outer join Task_ t " +
                        "on u = t.assignee " +
                        "group by u order by count(t.id)")
                .properties("user", "tasks")
                .list().stream()
                .map(e -> e.<User>getValue("user"))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public Task createTask(Project project, String name, LocalDateTime startDate) {
        Task task = dataManager.create(Task.class);
        task.setProject(project);
        task.setName(name);
        task.setStartDate(startDate);
        task.setAssignee(((User) currentAuthentication.getUser()));

        return dataManager.save(task);
    }
}