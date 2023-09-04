package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
public class ProjectBrowse extends StandardLookup<Project> {
    @Autowired
    private DataManager dataManager;

    @Autowired
    private CollectionLoader<Project> projectsDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private Button editBtn;

    @Subscribe("projectsTable.generateData")
    public void onProjectsTableGenerateData(final Action.ActionPerformedEvent event) {
        SaveContext saveContext = new SaveContext();
        for (int i = 0; i < 100; i++) {
            Project project = dataManager.create(Project.class);
            final User user = (User) currentAuthentication.getUser();
            project.setManager(user);
            project.setName("Generated " + i);
            saveContext.saving(project);
        }

        dataManager.save(saveContext);
        projectsDl.load();
    }
}