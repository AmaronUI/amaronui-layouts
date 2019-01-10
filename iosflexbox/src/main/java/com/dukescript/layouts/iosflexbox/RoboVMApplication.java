package com.dukescript.layouts.iosflexbox;

/*-
 * #%L
 * iosflexbox - a library from the "DukeScript Layouts" project.
 * %%
 * Copyright (C) 2018 - 2019 Dukehoff GmbH
 * %%
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Dukehoff GmbH designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Dukehoff GmbH in the LICENSE file that accompanied this code.
 * 
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 * 
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 *  Please contact Dukehoff GmbH, Heimeranstr. 68, 80339 Muenchen, Germany
 *  or visit www.dukescript.com if you need additional information or have any
 *  questions.
 * #L%
 */

import com.dukescript.layouts.flexbox.FlexboxLayout.AlignContent;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexDirection;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexWrap;
import java.util.concurrent.CountDownLatch;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;

public final class RoboVMApplication extends UIApplicationDelegateAdapter {

    private static UIWindow window;

    private static String page;
    private static CountDownLatch waitFor;

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        application.setStatusBarHidden(false);
        final CGRect bounds = UIScreen.getMainScreen().getBounds();
        CGRect whole = UIScreen.getMainScreen().getBounds();
        window = new UIWindow(whole);
        window.setRootViewController(new UIViewController() {
            @Override
            public boolean prefersStatusBarHidden() {
                return true;
            }

            @Override
            public boolean shouldAutorotate() {
                return true;
            }

            @Override
            public boolean shouldAutomaticallyForwardRotationMethods() {
                return false;
            }

            @Override
            public void didRotate(UIInterfaceOrientation uiio) {
            }
        });

        FlexboxView flexBox = new FlexboxView(bounds);
        flexBox.setFlexWrap(FlexWrap.WRAP);
        flexBox.setFlexDirection(FlexDirection.COLUMN);
        flexBox.setAlignContent(AlignContent.SPACE_AROUND);
        for (int i = 0; i < 60; i++) {
            UILabel uiLabel = new UILabel();
            uiLabel.setText("Label " + i);
            FlexBoxlayoutParams.LayoutParams layoutParams = new FlexBoxlayoutParams.LayoutParams();
            layoutParams.setFlexGrow(1);
            layoutParams.setMinWidth(40);
            layoutParams.setMinHeight(30);
            uiLabel.addConstraint(new FlexBoxlayoutParams(layoutParams));
            flexBox.addSubview(uiLabel);
        }
        flexBox.setFrame(whole);
//        flexBox.setBackgroundColor(UIColor.cyan());
        window.setRootViewController(new FlexBoxViewController());
        window.getRootViewController().setView(flexBox);
        window.setBackgroundColor(UIColor.white());
        window.addSubview(flexBox);
        window.makeKeyAndVisible();

        return true;
    }

    public static void runOnUiThread(Runnable w) {
        NSOperationQueue mq = NSOperationQueue.getMainQueue();
        mq.addOperation(w);
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, RoboVMApplication.class);

        }
    }

}
