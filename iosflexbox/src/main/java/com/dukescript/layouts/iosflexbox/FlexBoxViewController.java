
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

import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIViewController;

/**
 *
 * @author antonepple
 */
public class FlexBoxViewController extends UIViewController {

    public FlexBoxViewController() {
        
    }

    @Override
    public void willRotate(UIInterfaceOrientation toInterfaceOrientation, double duration) {
        super.willRotate(toInterfaceOrientation, duration); //To change body of generated methods, choose Tools | Templates.
        System.out.println("willRotate");
        getView().setNeedsLayout();
    }


    

    
    
}
