/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.modes.mindmapmode.url;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ListIterator;

import javax.swing.JOptionPane;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.map.MapModel;
import org.freeplane.core.map.NodeModel;
import org.freeplane.core.ui.FreeplaneAction;
import org.freeplane.core.url.UrlManager;
import org.freeplane.map.clipboard.ClipboardController;
import org.freeplane.map.clipboard.mindmapmode.MClipboardController;
import org.freeplane.map.link.NodeLinks;
import org.freeplane.modes.mindmapmode.MMapController;
import org.freeplane.modes.mindmapmode.MModeController;

/**
 * This is exactly the opposite of exportBranch.
 */
class ImportLinkedBranchWithoutRootAction extends FreeplaneAction {
	public ImportLinkedBranchWithoutRootAction() {
		super("import_linked_branch_without_root");
	}

	public void actionPerformed(final ActionEvent e) {
		final MapModel map = Controller.getController().getMap();
		final NodeModel selected = getModeController().getSelectedNode();
		if (selected == null || NodeLinks.getLink(selected) == null) {
			JOptionPane.showMessageDialog(getModeController().getMapView().getComponent(),
			    getModeController().getText("import_linked_branch_no_link"));
			return;
		}
		URL absolute = null;
		try {
			final String relative = NodeLinks.getLink(selected);
			absolute = UrlManager.isAbsolutePath(relative) ? UrlManager
			    .fileToUrl(new File(relative)) : new URL(UrlManager.fileToUrl(map.getFile()),
			    relative);
		}
		catch (final MalformedURLException ex) {
			JOptionPane.showMessageDialog(getModeController().getMapView().getComponent(),
			    "Couldn't create valid URL.");
			return;
		}
		try {
			final NodeModel node = ((MMapController) MModeController.getMModeController()
			    .getMapController()).loadTree(map, new File(absolute.getFile()));
			for (final ListIterator i = node.getModeController().getMapController()
			    .childrenUnfolded(node); i.hasNext();) {
				final NodeModel importNode = (NodeModel) i.next();
				((MClipboardController) ClipboardController.getController(MModeController
				    .getMModeController())).paste(importNode, selected);
			}
		}
		catch (final Exception ex) {
			UrlManager.getController(getModeController()).handleLoadingException(ex);
		}
	}
}
