package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import io.jmix.core.*;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.ui.Actions;
import io.jmix.ui.Facets;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiScreenProperties;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.list.CreateAction;
import io.jmix.ui.action.list.EditAction;
import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.LookupComponent;
import io.jmix.ui.component.data.table.ContainerGroupTableItems;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataComponents;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.impl.NoopDataContext;
import io.jmix.ui.screen.*;
import io.jmix.ui.settings.facet.ScreenSettingsFacet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@DialogMode(width = "800px", height = "600px")
@UiController("ProgrammaticProjectBrowse")
public class ProgrammaticProjectBrowse extends StandardLookup<Project> {

    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private DataComponents dataComponents;
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private Facets facets;
    @Autowired
    private Messages messages;
    @Autowired
    private UiScreenProperties uiScreenProperties;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Actions actions;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private MessageTools messageTools;

    private CollectionContainer<Project> projectsDc;
    private CollectionLoader<Project> projectsDl;
    private GroupTable<Project> projectsTable;

    @Subscribe
    public void onInit(final InitEvent event) {
        getWindow().setCaption(messageBundle.getMessage("programmaticProjectBrowse.caption"));

        initDate();
        initFacets();
        initActions();
        initLayout();

        getWindow().setFocusComponent(projectsTable.getId());
    }

    private void initDate() {
//        DataContext dataContext = dataComponents.createDataContext();
        DataContext dataContext = new NoopDataContext(getApplicationContext());

        getScreenData().setDataContext(dataContext);

        projectsDc = dataComponents.createCollectionContainer(Project.class);
        getScreenData().registerContainer("projectsDc", projectsDc);

        projectsDl = dataComponents.createCollectionLoader();
        projectsDl.setContainer(projectsDc);
        projectsDl.setDataContext(dataContext);
        projectsDl.setQuery("select e from Project e");
        projectsDl.setFetchPlan(createProjectsFetchPlan());
        getScreenData().registerLoader("projectsDl", projectsDl);
    }

    private FetchPlan createProjectsFetchPlan() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.BASE)
                .add("manager", fetchPlanBuilder ->
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
        BaseAction lookupSelectAction = new BaseAction(LOOKUP_SELECT_ACTION_ID)
                .withCaption(messages.getMessage("actions.Select"))
                .withIcon(JmixIcon.LOOKUP_OK.source())
                .withPrimary(true)
                .withShortcut(uiScreenProperties.getCommitShortcut())
                .withHandler(this::select);
        getWindow().addAction(lookupSelectAction);

        BaseAction lookupCancelAction = new BaseAction(LOOKUP_CANCEL_ACTION_ID)
                .withCaption(messages.getMessage("actions.Cancel"))
                .withIcon(JmixIcon.LOOKUP_CANCEL.source())
                .withHandler(this::cancel);
        getWindow().addAction(lookupCancelAction);
    }

    private void initLayout() {
        initFilter();
        initTable();
        initWindowButtonsPanel();

        getWindow().setSpacing(true);
        getWindow().expand(projectsTable);
    }

    private void initFilter() {
        Filter filter = uiComponents.create(Filter.class);
        filter.setId("filter");
        filter.setDataLoader(projectsDl);
        filter.loadConfigurationsAndApplyDefault();

        getWindow().add(filter);
    }

    private void initTable() {
        projectsTable = uiComponents.create(GroupTable.of(Project.class));
        projectsTable.setId("projectsTable");
        projectsTable.setWidthFull();

        initTableActions();
        initTableColumns();
        initTableButtonsPanel();
        initTablePagination();

        projectsTable.setItems(new ContainerGroupTableItems<>(projectsDc));

        getWindow().add(projectsTable);
    }

    private void initWindowButtonsPanel() {
        HBoxLayout buttonsPanel = uiComponents.create(HBoxLayout.class);
        buttonsPanel.setId("lookupActions");
        buttonsPanel.setSpacing(true);
        buttonsPanel.setVisible(false);
        getWindow().add(buttonsPanel);

        Button selectButton = uiComponents.create(Button.class);
        selectButton.setAction(getWindow().getActionNN(LOOKUP_SELECT_ACTION_ID));
        buttonsPanel.add(selectButton);

        Button cancelButton = uiComponents.create(Button.class);
        cancelButton.setAction(getWindow().getActionNN(LOOKUP_CANCEL_ACTION_ID));
        buttonsPanel.add(cancelButton);
    }

    @SuppressWarnings("unchecked")
    private void initTableActions() {
        CreateAction<Project> createAction = ((CreateAction<Project>) actions.create(CreateAction.ID));
        projectsTable.addAction(createAction);

        EditAction<Project> editAction = ((EditAction<Project>) actions.create(EditAction.ID));
        projectsTable.addAction(editAction);

        RemoveAction<Project> removeAction = ((RemoveAction<Project>) actions.create(RemoveAction.ID));
        projectsTable.addAction(removeAction);
    }

    private void initTableColumns() {
        addColumn("name");
        addColumn("startDate");
        addColumn("endDate");
        addColumn("manager");
    }

    private void addColumn(String property) {
        MetaClass metaClass = projectsDc.getEntityMetaClass();
        MetaPropertyPath metaPropertyPath = metadataTools.resolveMetaPropertyPath(metaClass, property);

        Table.Column<Project> column = projectsTable.addColumn(metaPropertyPath);
        column.setCaption(messageTools.getPropertyCaption(metaClass, property));
    }

    private void initTableButtonsPanel() {
        ButtonsPanel buttonsPanel = uiComponents.create(ButtonsPanel.class);

        Collection<Action> tableActions = projectsTable.getActions();
        for (Action action : tableActions) {
            Button button = uiComponents.create(Button.class);
            button.setId(action.getId() + "Btn");
            button.setAction(action);

            buttonsPanel.add(button);
        }

        projectsTable.setButtonsPanel(buttonsPanel);
    }

    private void initTablePagination() {
        SimplePagination simplePagination = uiComponents.create(SimplePagination.class);
        projectsTable.setPagination(simplePagination);
    }

    @Override
    protected LookupComponent<Project> getLookupComponent() {
        return projectsTable;
    }
}