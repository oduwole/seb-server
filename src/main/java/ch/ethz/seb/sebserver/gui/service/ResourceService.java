/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gui.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sebserver.gbl.Constants;
import ch.ethz.seb.sebserver.gbl.model.Entity;
import ch.ethz.seb.sebserver.gbl.model.exam.Exam.ExamType;
import ch.ethz.seb.sebserver.gbl.model.exam.Indicator.IndicatorType;
import ch.ethz.seb.sebserver.gbl.model.institution.LmsSetup.LmsType;
import ch.ethz.seb.sebserver.gbl.model.user.UserInfo;
import ch.ethz.seb.sebserver.gbl.model.user.UserRole;
import ch.ethz.seb.sebserver.gbl.profile.GuiProfile;
import ch.ethz.seb.sebserver.gbl.util.Tuple;
import ch.ethz.seb.sebserver.gui.service.i18n.I18nSupport;
import ch.ethz.seb.sebserver.gui.service.i18n.LocTextKey;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.RestService;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.institution.GetInstitutionNames;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.lmssetup.GetLmsSetupNames;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.api.useraccount.GetUserAccountNames;
import ch.ethz.seb.sebserver.gui.service.remote.webservice.auth.CurrentUser;

@Lazy
@Service
@GuiProfile
/** Defines functionality to get resources or functions of resources to feed e.g. selection or
 * combo-box content. */
public class ResourceService {

    public static final LocTextKey ACTIVE_TEXT_KEY = new LocTextKey("sebserver.overall.status.active");
    public static final LocTextKey INACTIVE_TEXT_KEY = new LocTextKey("sebserver.overall.status.inactive");

    private final I18nSupport i18nSupport;
    private final RestService restService;
    private final CurrentUser currentUser;

    protected ResourceService(
            final I18nSupport i18nSupport,
            final RestService restService,
            final CurrentUser currentUser) {

        this.i18nSupport = i18nSupport;
        this.restService = restService;
        this.currentUser = currentUser;
    }

    public I18nSupport getI18nSupport() {
        return this.i18nSupport;
    }

    public RestService getRestService() {
        return this.restService;
    }

    public CurrentUser getCurrentUser() {
        return this.currentUser;
    }

    public Supplier<List<Tuple<String>>> localizedResourceSupplier(final List<Tuple<String>> source) {
        return () -> source.stream()
                .map(tuple -> new Tuple<>(tuple._1, this.i18nSupport.getText(tuple._2)))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> lmsTypeResources() {
        return Arrays.asList(LmsType.values())
                .stream()
                .map(lmsType -> new Tuple<>(
                        lmsType.name(),
                        this.i18nSupport.getText("sebserver.lmssetup.type." + lmsType.name())))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> indicatorTypeResources() {
        return Arrays.asList(IndicatorType.values())
                .stream()
                .map(type -> new Tuple<>(
                        type.name(),
                        this.i18nSupport.getText("sebserver.exam.indicator.type." + type.name())))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> userRoleResources() {
        return UserRole.publicRolesForUser(this.currentUser.get())
                .stream()
                .map(ur -> new Tuple<>(
                        ur.name(),
                        this.i18nSupport.getText("sebserver.useraccount.role." + ur.name())))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> institutionResource() {
        return this.restService.getBuilder(GetInstitutionNames.class)
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .map(entityName -> new Tuple<>(entityName.modelId, entityName.name))
                .collect(Collectors.toList());
    }

    public Function<String, String> getInstitutionNameFunction() {

        final Map<String, String> idNameMap = this.restService.getBuilder(GetInstitutionNames.class)
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(e -> e.modelId, e -> e.name));

        return id -> {
            if (!idNameMap.containsKey(id)) {
                return Constants.EMPTY_NOTE;
            }
            return idNameMap.get(id);
        };
    }

    public List<Tuple<String>> lmsSetupResource() {
        final boolean isSEBAdmin = this.currentUser.get().hasRole(UserRole.SEB_SERVER_ADMIN);
        final String institutionId = (isSEBAdmin) ? "" : String.valueOf(this.currentUser.get().institutionId);
        return this.restService.getBuilder(GetLmsSetupNames.class)
                .withQueryParam(Entity.FILTER_ATTR_INSTITUTION, institutionId)
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .map(entityName -> new Tuple<>(entityName.modelId, entityName.name))
                .collect(Collectors.toList());
    }

    public Function<String, String> getLmsSetupNameFunction() {
        final boolean isSEBAdmin = this.currentUser.get().hasRole(UserRole.SEB_SERVER_ADMIN);
        final String institutionId = (isSEBAdmin) ? "" : String.valueOf(this.currentUser.get().institutionId);
        final Map<String, String> idNameMap = this.restService.getBuilder(GetLmsSetupNames.class)
                .withQueryParam(Entity.FILTER_ATTR_INSTITUTION, institutionId)
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(e -> e.modelId, e -> e.name));

        return id -> {
            if (!idNameMap.containsKey(id)) {
                return Constants.EMPTY_NOTE;
            }
            return idNameMap.get(id);
        };
    }

    /** Get a list of language key/name tuples for all supported languages in the
     * language of the current users locale.
     *
     * @param i18nSupport I18nSupport to get the actual current users locale
     * @return list of language key/name tuples for all supported languages in the language of the current users
     *         locale */
    public List<Tuple<String>> languageResources() {
        final Locale currentLocale = this.i18nSupport.getCurrentLocale();
        return this.i18nSupport.supportedLanguages()
                .stream()
                .map(locale -> new Tuple<>(locale.toLanguageTag(), locale.getDisplayLanguage(currentLocale)))
                .filter(tuple -> StringUtils.isNoneBlank(tuple._2))
                .sorted((t1, t2) -> t1._2.compareTo(t2._2))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> timeZoneResources() {
        final Locale currentLocale = this.i18nSupport.getCurrentLocale();
        return DateTimeZone
                .getAvailableIDs()
                .stream()
                .map(id -> new Tuple<>(id, DateTimeZone.forID(id).getName(0, currentLocale) + " (" + id + ")"))
                .sorted((t1, t2) -> t1._2.compareTo(t2._2))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> examTypeResources() {
        return Arrays.asList(ExamType.values())
                .stream()
                .map(type -> new Tuple<>(
                        type.name(),
                        this.i18nSupport.getText("sebserver.exam.type." + type.name())))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> examSupporterResources() {
        final UserInfo userInfo = this.currentUser.get();
        return this.restService.getBuilder(GetUserAccountNames.class)
                .withQueryParam(Entity.FILTER_ATTR_INSTITUTION, String.valueOf(userInfo.institutionId))
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .withQueryParam(UserInfo.FILTER_ATTR_ROLE, UserRole.EXAM_SUPPORTER.name())
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .map(entityName -> new Tuple<>(entityName.modelId, entityName.name))
                .collect(Collectors.toList());
    }

    public List<Tuple<String>> userResources() {
        final UserInfo userInfo = this.currentUser.get();
        return this.restService.getBuilder(GetUserAccountNames.class)
                .withQueryParam(Entity.FILTER_ATTR_INSTITUTION, String.valueOf(userInfo.institutionId))
                .withQueryParam(Entity.FILTER_ATTR_ACTIVE, Constants.TRUE_STRING)
                .call()
                .getOr(Collections.emptyList())
                .stream()
                .map(entityName -> new Tuple<>(entityName.modelId, entityName.name))
                .collect(Collectors.toList());
    }

    public Function<Boolean, String> localizedActivityResource() {
        return activity -> activity
                ? this.i18nSupport.getText(ACTIVE_TEXT_KEY)
                : this.i18nSupport.getText(INACTIVE_TEXT_KEY);
    }

}
