/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.popup;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityDataIOEditorWidgetViewImplTest {

    @Mock
    ActivityDataIOEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @Mock
    private TableCellElement nameth;

    @Mock
    private TableCellElement datatypeth;

    @GwtMock
    private ListWidget<AssignmentRow, AssignmentListItemWidgetViewImpl> assignments;

    private ActivityDataIOEditorWidgetViewImpl view;

    @Captor
    private ArgumentCaptor<List<AssignmentRow>> captor;

    @Captor
    private ArgumentCaptor<NotificationEvent> eventCaptor;

    private List<AssignmentRow> rows;

    protected Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        view = GWT.create(ActivityDataIOEditorWidgetViewImpl.class);
        view.assignments = assignments;
        view.addVarButton = button;
        view.nameth = nameth;
        view.datatypeth = datatypeth;
        view.notification = notification;

        doCallRealMethod().when(view).setAssignmentRows(any(List.class));
        doCallRealMethod().when(view).init(any(ActivityDataIOEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).handleAddVarButton(any(ClickEvent.class));
        doCallRealMethod().when(view).showOnlySingleEntryAllowed();
        doCallRealMethod().when(view).getAssignmentRows();
        doCallRealMethod().when(view).getAssignmentWidget(anyInt());
        doCallRealMethod().when(view).getAssignmentsCount();

        rows = new ArrayList<AssignmentRow>();
        rows.add(new AssignmentRow("varName",
                                   null,
                                   null,
                                   null,
                                   "varName",
                                   null));
        rows.add(new AssignmentRow("varName2",
                                   null,
                                   null,
                                   null,
                                   "varName2",
                                   null));
    }

    @Test
    public void testInit() {
        view.init(presenter);
        verify(button,
               times(1)).setText(DesignerEditorConstants.INSTANCE.Add());
        verify(button,
               times(1)).setIcon(IconType.PLUS);
    }

    @Test
    public void testHandleAddVarButton() {
        view.init(presenter);
        view.handleAddVarButton(mock(ClickEvent.class));
        verify(presenter,
               times(1)).handleAddClick();
    }

    @Test
    public void testAssignmentsRowsSameSourceAndTarget() {
        view.setAssignmentRows(rows);
        verify(assignments,
               times(1)).setValue(captor.capture());
        assertEquals(2,
                     captor.getValue().size());
        assertEquals("varName",
                     captor.getValue().get(0).getName());
        assertEquals("varName",
                     captor.getValue().get(0).getProcessVar());
        assertEquals("varName2",
                     captor.getValue().get(1).getName());
        assertEquals("varName2",
                     captor.getValue().get(1).getProcessVar());
    }

    @Test
    public void testOnlySingleEntryAllowed() {
        view.showOnlySingleEntryAllowed();
        verify(notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.Only_single_entry_allowed(),
                     eventCaptor.getValue().getNotification());
    }

    @Test
    public void testGetAssignmentRows() {
        when(assignments.getValue()).thenReturn(rows);
        assertEquals(rows,
                     view.getAssignmentRows());
    }

    @Test
    public void testGetAssignmentsCountEmpty() {
        when(assignments.getValue()).thenReturn(new ArrayList<AssignmentRow>());
        assertEquals(0,
                     view.getAssignmentsCount());
    }

    @Test
    public void testGetAssignmentsCount() {
        when(assignments.getValue()).thenReturn(rows);
        assertEquals(2,
                     view.getAssignmentsCount());
    }

    @Test
    public void testGetAssignmentWidget() {
        view.getAssignmentWidget(0);
        verify(assignments).getComponent(0);
    }

    @Test
    public void testGetAssignmentWidgetMoreComplex() {
        view.getAssignmentWidget(123);
        verify(assignments).getComponent(123);
    }
}
