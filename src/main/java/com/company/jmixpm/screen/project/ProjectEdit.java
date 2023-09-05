package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.Task;
import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.TabSheet;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.InstanceLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Project.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
public class ProjectEdit extends StandardEditor<Project> {

    //    @Autowired
    private Table<Task> tasksTable;

    @Autowired
    private InstanceLoader<Project> projectDl;
    @Autowired
    private CollectionLoader<Task> tasksDl;

    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private ScreenBuilders screenBuilders;

    /*@Subscribe
    public void onInitEntity(final InitEntityEvent<Project> event) {
        tasksTable.setEnabled(false);
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    public void onPostCommit(final DataContext.PostCommitEvent event) {
        tasksTable.setEnabled(true);
    }*/

    /*@Subscribe("generateDataBtn")
    public void onGenerateDataBtnClick(final Button.ClickEvent event) {
        SaveContext saveContext = new SaveContext();
        for (int i = 0; i < 200; i++) {
            Task task = dataManager.create(Task.class);
            task.setProject(getEditedEntity());
            task.setName("Generated Task #" + i);
            task.setAssignee((User) currentAuthentication.getUser());

            saveContext.saving(task);
        }

        dataManager.save(saveContext);
        projectDl.load();
    }*/

    @Subscribe("tabSheet")
    public void onTabSheetSelectedTabChange(final TabSheet.SelectedTabChangeEvent event) {
        if ("tasksTab".equals(event.getSelectedTab().getName())) {
            initTasks();
        }
    }

    private void initTasks() {
        if (tasksTable != null) {
            // It means that we've already opened this tab and initialized table
            return;
        }

        tasksDl.setParameter("project", getEditedEntity());
        tasksDl.load();

        //noinspection unchecked
        tasksTable = ((Table<Task>) getWindow().getComponentNN("tasksTable"));
        ((BaseAction) tasksTable.getActionNN("create"))
                .addActionPerformedListener(this::onTasksTableCreate);
        ((BaseAction) tasksTable.getActionNN("edit"))
                .addActionPerformedListener(this::onTasksTableEdit);
    }

    //    @Subscribe("tasksTable.create")
    public void onTasksTableCreate(final Action.ActionPerformedEvent event) {
        Task newTask = dataManager.create(Task.class);
        newTask.setProject(getEditedEntity());

        screenBuilders.editor(tasksTable)
                .newEntity(newTask)
                .withParentDataContext(getScreenData().getDataContext())
                .show();
    }

    //    @Subscribe("tasksTable.edit")
    public void onTasksTableEdit(final Action.ActionPerformedEvent event) {
        screenBuilders.editor(tasksTable)
                .withParentDataContext(getScreenData().getDataContext())
                .show();
    }

    /*@Install(to = "tasksTable.create", subject = "initializer")
    private void tasksTableCreateInitializer(final Task task) {
        task.setProject(getEditedEntity());
    }*/


}