package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.Task;
import com.company.jmixpm.entity.User;
import io.jmix.core.*;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.ui.Actions;
import io.jmix.ui.Facets;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiScreenProperties;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.entitypicker.EntityClearAction;
import io.jmix.ui.action.entitypicker.EntityLookupAction;
import io.jmix.ui.action.entitypicker.EntityOpenAction;
import io.jmix.ui.action.list.CreateAction;
import io.jmix.ui.action.list.EditAction;
import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.table.ContainerTableItems;
import io.jmix.ui.component.data.value.ContainerValueSource;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.*;
import io.jmix.ui.screen.*;
import io.jmix.ui.settings.facet.ScreenSettingsFacet;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;

//@PrimaryEditorScreen(Project.class)
@DialogMode(width = "800px", height = "600px")
@UiController("ProgrammaticProjectEdit")
public class ProgrammaticProjectEdit extends StandardEditor<Project> {

    @Autowired
    private Actions actions;
    @Autowired
    private Messages messages;
    @Autowired
    private MessageTools messageTools;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Facets facets;
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private DataComponents dataComponents;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private UiScreenProperties uiScreenProperties;

    private InstanceContainer<Project> projectDc;
    private CollectionPropertyContainer<Task> tasksDc;

    private Form form;
    private Table<Task> taskTable;

    @Subscribe
    public void onInit(InitEvent event) {
        getWindow().setCaption(messageBundle.getMessage("programmaticProjectEdit.caption"));

        initData();
        initFacets();
        initActions();
        initLayout();

        getWindow().setFocusComponent(form.getId());
    }

    private void initData() {
        DataContext dataContext = dataComponents.createDataContext();
        getScreenData().setDataContext(dataContext);

        projectDc = dataComponents.createInstanceContainer(Project.class);
        getScreenData().registerContainer("projectDc", projectDc);

        InstanceLoader<Project> projectDl = dataComponents.createInstanceLoader();
        projectDl.setContainer(projectDc);
        projectDl.setDataContext(dataContext);
        projectDl.setFetchPlan(createProjectFetchPlan());
        getScreenData().registerLoader("projectDl", projectDl);

        tasksDc = dataComponents.createCollectionContainer(Task.class, projectDc, "tasks");
        getScreenData().registerContainer("tasksDc", tasksDc);
    }

    private FetchPlan createProjectFetchPlan() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.BASE)
                .add("manager", fetchPlanBuilder ->
                        fetchPlanBuilder.addFetchPlan(FetchPlan.BASE))
                .add("tasks", fetchPlanBuilder ->
                        fetchPlanBuilder.addFetchPlan(FetchPlan.BASE))
                .build();
    }

    private void initFacets() {
        ScreenSettingsFacet screenSettingsFacet = facets.create(ScreenSettingsFacet.class);
        getWindow().addFacet(screenSettingsFacet);
        screenSettingsFacet.setAuto(true);

        DataLoadCoordinator dataLoadCoordinator = facets.create(DataLoadCoordinator.class);
        getWindow().addFacet(dataLoadCoordinator);
        dataLoadCoordinator.configureAutomatically();
    }

    private void initActions() {
        BaseAction windowCommitAndClose = new BaseAction(WINDOW_COMMIT_AND_CLOSE)
                .withCaption(messages.getMessage("actions.Ok"))
                .withIcon(JmixIcon.EDITOR_OK.source())
                .withPrimary(true)
                .withShortcut(uiScreenProperties.getCommitShortcut())
                .withHandler(this::commitAndClose);
        getWindow().addAction(windowCommitAndClose);

        BaseAction windowClose = new BaseAction(WINDOW_CLOSE)
                .withCaption(messages.getMessage("actions.Cancel"))
                .withIcon(JmixIcon.EDITOR_CANCEL.source())
                .withHandler(this::cancel);
        getWindow().addAction(windowClose);
    }

    private void initLayout() {
        initForm();
        initTable();
        initWindowButtonsPanel();

        getWindow().setSpacing(true);
    }

    private void initForm() {
        form = uiComponents.create(Form.class);
        form.setId("form");
        getWindow().add(form);

        TextField<String> nameField = uiComponents.create(TextField.TYPE_STRING);
        initField(nameField, "name");
        form.add(nameField);

        DateField<LocalDate> startDateField = uiComponents.create(DateField.TYPE_LOCALDATE);
        initField(startDateField, "startDate");
        form.add(startDateField);

        DateField<LocalDate> endDateField = uiComponents.create(DateField.TYPE_LOCALDATE);
        initField(endDateField, "endDate");
        form.add(endDateField);

        EntityPicker<User> managerField = uiComponents.create(EntityPicker.of(User.class));
        initField(managerField, "manager");
        managerField.addAction(actions.create(EntityLookupAction.ID));
        managerField.addAction(actions.create(EntityClearAction.ID));
        managerField.addAction(actions.create(EntityOpenAction.ID));
        form.add(managerField);
    }

    private void initField(Field<?> field, String property) {
        field.setValueSource(new ContainerValueSource<>(projectDc, property));
        field.setCaption(messageTools.getPropertyCaption(projectDc.getEntityMetaClass(), property));
        field.setWidth("350px");
    }

    private void initTable() {
        taskTable = uiComponents.create(Table.of(Task.class));
        taskTable.setId("tasksTable");
        taskTable.setWidthFull();
        taskTable.setHeight("200px");
        taskTable.setItems(new ContainerTableItems<>(tasksDc));

        initTableActions();
        initTableColumns();
        initTableButtonsPanel();
        initTablePagination();

        GroupBoxLayout tasksBox = uiComponents.create(GroupBoxLayout.class);
        tasksBox.setCaption(messageTools.getPropertyCaption(projectDc.getEntityMetaClass(), "tasks"));
        tasksBox.add(taskTable);

        getWindow().add(tasksBox);
    }

    @SuppressWarnings("unchecked")
    private void initTableActions() {
        CreateAction<Project> createAction = actions.create(CreateAction.class);
        taskTable.addAction(createAction);

        EditAction<Project> editAction = actions.create(EditAction.class);
        taskTable.addAction(editAction);

        RemoveAction<Project> removeAction = actions.create(RemoveAction.class);
        taskTable.addAction(removeAction);
    }

    private void initTableColumns() {
        addColumn("name");
        addColumn("startDate");
        addColumn("estimatedEfforts");
    }

    private void addColumn(String id) {
        MetaClass metaClass = tasksDc.getEntityMetaClass();
        MetaPropertyPath metaPropertyPath = metadataTools.resolveMetaPropertyPath(metaClass, id);

        Table.Column<Task> column = taskTable.addColumn(metaPropertyPath);

        column.setCaption(messageTools.getPropertyCaption(metaClass, id));
    }

    private void initTableButtonsPanel() {
        ButtonsPanel buttonsPanel = uiComponents.create(ButtonsPanel.class);

        Collection<Action> tableActions = taskTable.getActions();
        for (Action action : tableActions) {
            Button button = uiComponents.create(Button.class);
            button.setId(action.getId() + "Btn");
            button.setAction(action);

            buttonsPanel.add(button);
        }

        taskTable.setButtonsPanel(buttonsPanel);
    }

    private void initTablePagination() {
        SimplePagination simplePagination = uiComponents.create(SimplePagination.class);
        taskTable.setPagination(simplePagination);
    }

    private void initWindowButtonsPanel() {
        HBoxLayout buttonsPanel = uiComponents.create(HBoxLayout.class);
        buttonsPanel.setId("editActions");
        buttonsPanel.setSpacing(true);

        Button commitAndCloseButton = uiComponents.create(Button.class);
        commitAndCloseButton.setAction(getWindow().getActionNN("windowCommitAndClose"));
        buttonsPanel.add(commitAndCloseButton);

        Button closeButton = uiComponents.create(Button.class);
        closeButton.setAction(getWindow().getActionNN("windowClose"));
        buttonsPanel.add(closeButton);

        getWindow().add(buttonsPanel);
        getWindow().expand(buttonsPanel);
    }

    @Override
    protected InstanceContainer<Project> getEditedEntityContainer() {
        return projectDc;
    }
}