/*******************************************************************************
 * Copyright (c) 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jonas Hungershausen - initial API and implementation
 *******************************************************************************/
package org.eclipse.dartboard.test.preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.test.util.DefaultPreferences;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferenceDialog;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class DartPreferencePageTest {
	/** Dart SDK location is operating system specific. Here catering for Linuz and Windows */
	private static String DART_SDK_LOC;

	private PreferenceDialog preferenceDialog;
	private DartPreferencePage preferencePage;

	@Before
	public void setup() {
		DART_SDK_LOC = Platform.getOS().equals(Platform.OS_WIN32) ? "C:\\Program Files\\Dart\\dart-sdk" : "/usr/lib/dart";
		preferenceDialog = new WorkbenchPreferenceDialog();
		preferencePage = new DartPreferencePage(preferenceDialog);
		preferenceDialog.open();
		preferenceDialog.select(preferencePage);
	}

	@After
	public void tearDown() {
		DefaultPreferences.resetPreferences();
		if (preferenceDialog.isOpen()) {
			preferenceDialog.cancel();
		}

	}

	@Test
	public void dartPreferencePage__DefaultPreferences__CorrectDefaultsAreDisplayed() throws Exception {
		assertTrue("Auto pub synchronization not selected", preferencePage.isAutoPubSynchronization());
		assertFalse("Use offline pub is selected", preferencePage.isUseOfflinePub());
		preferencePage.setPluginMode("Dart");

		assertEquals(DART_SDK_LOC, preferencePage.getSDKLocation());
	}

	@Test
	public void dartPreferencePage__InvalidSDKLocation__PageIsNotValid() throws Exception {
		preferencePage.setPluginMode("Dart");
		preferencePage.setSDKLocation("some-random-test-location/path-segment");
		assertTrue(preferencePage.isShowingSDKInvalidError());
	}

	public class DartPreferencePage extends PreferencePage {

		public DartPreferencePage(ReferencedComposite referencedComposite) {
			super(referencedComposite, "Dart and Flutter");
		}

		public DartPreferencePage setSDKLocation(String text) {
			new LabeledText("Dart SDK Location:").setText(text);
			return this;
		}

		public String getSDKLocation() {
			return new LabeledText("Dart SDK Location:").getText();
		}

		public DartPreferencePage setAutoPubSynchronization(boolean value) {
			new CheckBox("Automatic Pub dependency synchronization").toggle(value);
			return this;
		}

		public boolean isAutoPubSynchronization() {
			return new CheckBox("Automatic Pub dependency synchronization").isChecked();
		}

		public DartPreferencePage setUseOfflinePub(boolean value) {
			new CheckBox("Use cached packages (--offline flag)").toggle(value);
			return this;
		}

		public DartPreferencePage setPluginMode(String value) {
			new RadioButton(value).click();
			return this;
		}

		public boolean isFlutterMode() {
			return new RadioButton("Flutter").isSelected();
		}

		public boolean isUseOfflinePub() {
			return new CheckBox("Use cached packages (--offline flag)").isChecked();
		}

		public boolean isShowingSDKInvalidError() {
			try {
				new DefaultText("Not a valid SDK location");
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
