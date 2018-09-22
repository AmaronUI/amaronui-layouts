package com.dukescript.layouts.iosflexbox;

/*-
 * #%L
 * iosflexbox - a library from the "DukeScript Layouts" project.
 * %%
 * Copyright (C) 2018 Dukehoff GmbH
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

import com.dukescript.layouts.flexbox.Bounds;
import com.dukescript.layouts.flexbox.FlexboxLayout;
import java.util.logging.Logger;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIView;

/**
 *
 * @author antonepple
 */
public class FlexboxView extends UIView {

    private FlexboxLayout flexboxLayout = new FlexboxLayout();

    public FlexboxView(CGRect context) {
        super(context);
        setBackgroundColor(UIColor.blue()); 
        setUserInteractionEnabled(true);
    }
    
    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        UIInterfaceOrientation statusBarOrientation = UIApplication.getSharedApplication().getStatusBarOrientation();
        NSArray<UIView> subviews = getSubviews();

        flexboxLayout.clearItems();
        for (UIView subview : subviews) {
            flexboxLayout.add(new FlexItemAdapter(subview));
        }
        boolean isHorizontal = statusBarOrientation == UIInterfaceOrientation.LandscapeLeft 
                || statusBarOrientation == UIInterfaceOrientation.LandscapeRight;
        double width =  isHorizontal ? getBounds().getWidth(): getBounds().getHeight();
        double height =  isHorizontal ?getBounds().getHeight(): getBounds().getWidth();
        System.out.println("horizontal layout "+isHorizontal+ "width: "+width+" height "+height);
        
        
        flexboxLayout.layoutSubViews(flexboxLayout.isHorizontal() ?  width : height,
                flexboxLayout.isHorizontal() ? height : width);

        
    }

    
    
    
    public void setFlexDirection(int flexDirection) {
        flexboxLayout.setFlexDirection(flexDirection);
    }

    public void setFlexWrap(int flexWrap) {
        flexboxLayout.setFlexWrap(flexWrap);
    }

    public void setJustifyContent(int justifyContent) {
        flexboxLayout.setJustifyContent(justifyContent);
    }

    public void setAlignItems(int alignItems) {
        flexboxLayout.setAlignItems(alignItems);
    }

    public void setAlignContent(int alignContent) {
        flexboxLayout.setAlignContent(alignContent);
    }

    /**
     * Introduced to allow generic implementation of FlexBox Algorithm
     */
    private static class FlexItemAdapter extends FlexboxLayout.FlexItem {
        Logger LOG = Logger.getLogger(FlexItemAdapter.class.getName());
        private final UIView delegate;
        private FlexBoxlayoutParams.LayoutParams params;
        private double height, width = 0;

        public FlexItemAdapter(UIView subview) {
            this.delegate = subview;
            NSArray<NSLayoutConstraint> constraints = this.delegate.getConstraints();
            for (NSLayoutConstraint constraint : constraints) {
                if (constraint instanceof FlexBoxlayoutParams) {
                    LOG.info("Found FlexBoxlayoutParams");
                    this.params = ((FlexBoxlayoutParams) constraint).getLayoutParams();
                    break;
                }
            }
            if (this.params == null) {
                 LOG.info("Found no FlexBoxlayoutParams, creating empty one");
                this.params = new FlexBoxlayoutParams.LayoutParams();
            }
        }

        @Override
        public float getFlexBasisPercent() {
            return params.getFlexBasisPercent();
        }

        @Override
        public float getFlexGrow() {
            return params.getFlexGrow();
        }

        @Override
        public float getFlexShrink() {
            return params.getFlexShrink();
        }

        @Override
        public double getHeight() {
            return 30;
        }

        @Override
        public double getMarginBottom() {
            //TODO this value should be in params
            return 0;
        }

        @Override
        public double getMarginLeft() {
            //TODO this value should be in params
            return 0;
        }

        @Override
        public double getMarginRight() {
            //TODO this value should be in params
            return 0;
        }

        @Override
        public double getMarginTop() {
            //TODO this value should be in params
            return 0;
        }

        @Override
        public double getMaxHeight() {
            return params.getMaxHeight();
        }

        @Override
        public double getMaxWidth() {
            return params.getMaxWidth();
        }

        @Override
        public double getMinHeight() {
            return params.getMinHeight();
        }

        @Override
        public double getMinWidth() {
            return params.getMinWidth();
        }

        @Override
        public int getOrder() {
            return params.getOrder();
        }

        @Override
        public double getWidth() {
            return 100;
        }

        @Override
        public boolean isWrapBefore() {
            return params.isWrapBefore();
        }

        @Override
        public int getFlexAlignSelf() {
            return params.getAlignSelf();
        }

        @Override
        protected void adjustBounds(Bounds bounds) {
            
    
            delegate.setFrame(new CGRect(bounds.getX(), bounds.getY(), bounds.getW(), bounds.getH()));
            delegate.setBounds(new CGRect(0, 0, bounds.getW(), bounds.getH()));
        }

    }

}
