/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gui.service.page.content;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sebserver.gbl.api.EntityType;
import ch.ethz.seb.sebserver.gbl.authorization.PrivilegeType;
import ch.ethz.seb.sebserver.gbl.model.Domain;
import ch.ethz.seb.sebserver.gbl.model.institution.Institution;
import ch.ethz.seb.sebserver.gbl.profile.GuiProfile;
import ch.ethz.seb.sebserver.gui.service.i18n.LocTextKey;
import ch.ethz.seb.sebserver.gui.service.page.PageContext;
import ch.ethz.seb.sebserver.gui.service.page.TemplateComposer;
import ch.ethz.seb.sebserver.gui.service.page.action.ActionDefinition;
import ch.ethz.seb.sebserver.gui.service.page.action.InstitutionActions;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.RestService;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.institution.GetInstitutions;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.auth.CurrentUser;
import ch.ethz.seb.sebserver.gui.service.table.ColumnDefinition;
import ch.ethz.seb.sebserver.gui.service.table.EntityTable;
import ch.ethz.seb.sebserver.gui.service.widget.WidgetFactory;

@Lazy
@Component
@GuiProfile
public class InstitutionList implements TemplateComposer {

    private static final Logger log = LoggerFactory.getLogger(InstitutionList.class);

    private final WidgetFactory widgetFactory;
    private final RestService restService;
    private final CurrentUser currentUser;

    protected InstitutionList(
            final WidgetFactory widgetFactory,
            final RestService restService,
            final CurrentUser currentUser) {

        this.widgetFactory = widgetFactory;
        this.restService = restService;
        this.currentUser = currentUser;
    }

    @Override
    public void compose(final PageContext pageContext) {

        if (log.isDebugEnabled()) {
            log.debug("Compose Institutoion list within PageContext: {}", pageContext);
        }

        final Composite content = this.widgetFactory.defaultPageLayout(
                pageContext.getParent(),
                new LocTextKey("sebserver.institution.list.title"));

        // table
        final EntityTable<Institution> table =
                this.widgetFactory.entityTableBuilder(this.restService.getRestCall(GetInstitutions.class))
                        .withPaging(3)
                        .withColumn(new ColumnDefinition<>(
                                Domain.INSTITUTION.ATTR_NAME,
                                new LocTextKey("sebserver.institution.list.column.name"),
                                entity -> entity.name,
                                true))
                        .withColumn(new ColumnDefinition<>(
                                Domain.INSTITUTION.ATTR_URL_SUFFIX,
                                new LocTextKey("sebserver.institution.list.column.urlSuffix"),
                                entity -> entity.urlSuffix,
                                true))
                        .withColumn(new ColumnDefinition<>(
                                Domain.INSTITUTION.ATTR_ACTIVE,
                                new LocTextKey("sebserver.institution.list.column.active"),
                                entity -> entity.active,
                                true))
                        .compose(content);

        // propagate content actions to action-pane
        pageContext.createAction(ActionDefinition.INSTITUTION_NEW)
                .withExec(InstitutionActions::newInstitution)
                .publishIf(() -> this.currentUser.hasPrivilege(PrivilegeType.WRITE, EntityType.INSTITUTION))
                .createAction(ActionDefinition.INSTITUTION_VIEW_FROM_LIST)
                .withSelectionSupplier(table::getSelection)
                .withExec(InstitutionActions::viewInstitution)
                .publish()
                .createAction(ActionDefinition.INSTITUTION_MODIFY_FROM__LIST)
                .withSelectionSupplier(table::getSelection)
                .withExec(InstitutionActions::editInstitutionFromList)
                .publishIf(() -> this.currentUser.hasPrivilege(PrivilegeType.MODIFY, EntityType.INSTITUTION));

    }

}
