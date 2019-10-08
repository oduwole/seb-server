/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gui.widget;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.ethz.seb.sebserver.gbl.Constants;
import ch.ethz.seb.sebserver.gui.service.i18n.I18nSupport;
import ch.ethz.seb.sebserver.gui.service.i18n.LocTextKey;

public class FileUploadSelection extends Composite {

    private static final long serialVersionUID = 5800153475027387363L;

    private static final LocTextKey PLEASE_SELECT_TEXT =
            new LocTextKey("sebserver.overall.upload");

    private final I18nSupport i18nSupport;
    private final List<String> supportedFileExtensions = new ArrayList<>();

    private final boolean readonly;
    private final FileUpload fileUpload;
    private final Label fileName;

    private Consumer<String> errorHandler;
    private InputStream inputStream;

    public FileUploadSelection(
            final Composite parent,
            final I18nSupport i18nSupport,
            final boolean readonly) {

        super(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        super.setLayout(gridLayout);

        this.i18nSupport = i18nSupport;
        this.readonly = readonly;

        if (readonly) {
            this.fileName = new Label(this, SWT.NONE);
            this.fileName.setText(i18nSupport.getText(PLEASE_SELECT_TEXT));
            this.fileName.setLayoutData(new GridData());
            this.fileUpload = null;
        } else {
            this.fileUpload = new FileUpload(this, SWT.NONE);
            this.fileUpload.setImage(WidgetFactory.ImageIcon.IMPORT.getImage(parent.getDisplay()));
            this.fileUpload.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            this.fileUpload.setToolTipText(this.i18nSupport.getText(PLEASE_SELECT_TEXT));
            final FileUploadHandler uploadHandler = new FileUploadHandler(new InputReceiver());

            this.fileName = new Label(this, SWT.NONE);
            this.fileName.setText(i18nSupport.getText(PLEASE_SELECT_TEXT));
            this.fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            this.fileUpload.addListener(SWT.Selection, event -> {
                final String fileName = FileUploadSelection.this.fileUpload.getFileName();
                if (fileName == null || !fileSupported(fileName)) {
                    if (FileUploadSelection.this.errorHandler != null) {
                        final String text = i18nSupport.getText(new LocTextKey(
                                "sebserver.overall.upload.unsupported.file",
                                this.supportedFileExtensions.toString()),
                                "Unsupported image file type selected");
                        FileUploadSelection.this.errorHandler.accept(text);
                    }
                    return;
                }
                FileUploadSelection.this.fileUpload.submit(uploadHandler.getUploadUrl());
                FileUploadSelection.this.fileName.setText(fileName);
                FileUploadSelection.this.errorHandler.accept(null);
            });

        }
    }

    public String getFileName() {
        if (this.fileName != null) {
            return this.fileName.getText();
        }

        return Constants.EMPTY_NOTE;
    }

    public void setFileName(final String fileName) {
        if (this.fileName != null && fileName != null) {
            this.fileName.setText(fileName);
        }
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public void update() {
        if (this.inputStream != null) {
            this.fileName.setText(this.i18nSupport.getText(PLEASE_SELECT_TEXT));
        }
        if (!this.readonly) {
            this.fileUpload.setToolTipText(this.i18nSupport.getText(PLEASE_SELECT_TEXT));
        }
    }

    public FileUploadSelection setErrorHandler(final Consumer<String> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public FileUploadSelection withSupportFor(final String fileExtension) {
        this.supportedFileExtensions.add(fileExtension);
        return this;
    }

    private boolean fileSupported(final String fileName) {
        return this.supportedFileExtensions
                .stream()
                .filter(fileType -> fileName.toUpperCase(Locale.ROOT)
                        .endsWith(fileType.toUpperCase(Locale.ROOT)))
                .findFirst()
                .isPresent();
    }

    private final class InputReceiver extends FileUploadReceiver {
        @Override
        public void receive(final InputStream stream, final FileDetails details) throws IOException {
            final PipedInputStream pIn = new PipedInputStream();
            final PipedOutputStream pOut = new PipedOutputStream(pIn);

            FileUploadSelection.this.inputStream = pIn;

            try {
                IOUtils.copyLarge(stream, pOut);
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(pOut);
            }
        }
    }

}
