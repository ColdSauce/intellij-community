/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.vcs.changes.shelf;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

/**
 * @author yole
 */
public class RenameShelvedChangeListAction extends AnAction {
  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
    final List<ShelvedChangeList> changelists = ShelvedChangesViewManager.getShelvedLists(e.getDataContext());
    final ShelvedChangeList changeList = ObjectUtils.assertNotNull(ContainerUtil.getFirstItem(changelists));
    String newName = Messages.showInputDialog(project, VcsBundle.message("shelve.changes.rename.prompt"),
                                              VcsBundle.message("shelve.changes.rename.title"),
                                              Messages.getQuestionIcon(), changeList.DESCRIPTION,
                                              new InputValidator() {
                                                public boolean checkInput(final String inputString) {
                                                  if (inputString.length() == 0) {
                                                    return false;
                                                  }
                                                  final List<ShelvedChangeList> list =
                                                    ShelveChangesManager.getInstance(project).getShelvedChangeLists();
                                                  for (ShelvedChangeList oldList : list) {
                                                    if (oldList != changeList && oldList.DESCRIPTION.equals(inputString)) {
                                                      return false;
                                                    }
                                                  }
                                                  return true;
                                                }

                                                public boolean canClose(final String inputString) {
                                                  return checkInput(inputString);
                                                }
                                              });
    if (newName != null && !newName.equals(changeList.DESCRIPTION)) {
      ShelveChangesManager.getInstance(project).renameChangeList(changeList, newName);
    }
  }

  public void update(final AnActionEvent e) {
    e.getPresentation().setEnabled(getEventProject(e) != null && ShelvedChangesViewManager.getShelvedLists(e.getDataContext()).size() == 1);
  }
}
