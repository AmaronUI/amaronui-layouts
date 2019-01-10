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

import com.dukescript.layouts.flexbox.FlexboxLayout;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexItem.AlignSelf;
import org.robovm.apple.uikit.NSLayoutConstraint;

/**
 * A hacky way to get the LayoutParams for FlexBox from a view Might have side
 * effects
 *
 * @author antonepple
 */
public class FlexBoxlayoutParams extends NSLayoutConstraint {

    final LayoutParams params;

    public FlexBoxlayoutParams(LayoutParams params) {
        this.params = params;
    }

    public LayoutParams getLayoutParams() {
        return params;
    }

    public static class LayoutParams {

        private int order = 0;
        private float flexGrow = 0;
        private float flexShrink = 1;
        private AlignSelf alignSelf = AlignSelf.AUTO;
        private float flexBasisPercent = -1f;
        private double minWidth = 10;
        private double minHeight = 10;
        private double maxWidth = Double.MAX_VALUE;
        private double maxHeight = Double.MAX_VALUE;
        private boolean wrapBefore = false;

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public float getFlexGrow() {
            return flexGrow;
        }

        public void setFlexGrow(float flexGrow) {
            this.flexGrow = flexGrow;
        }

        public float getFlexShrink() {
            return flexShrink;
        }

        public void setFlexShrink(float flexShrink) {
            this.flexShrink = flexShrink;
        }

        public AlignSelf getAlignSelf() {
            return alignSelf;
        }

        public void setAlignSelf(AlignSelf alignSelf) {
            this.alignSelf = alignSelf;
        }

        public float getFlexBasisPercent() {
            return flexBasisPercent;
        }

        public void setFlexBasisPercent(float flexBasisPercent) {
            this.flexBasisPercent = flexBasisPercent;
        }

        public double getMinWidth() {
            return minWidth;
        }

        public void setMinWidth(double minWidth) {
            this.minWidth = minWidth;
        }

        public double getMinHeight() {
            return minHeight;
        }

        public void setMinHeight(double minHeight) {
            this.minHeight = minHeight;
        }

        public double getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
        }

        public double getMaxHeight() {
            return maxHeight;
        }

        public void setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
        }

        public boolean isWrapBefore() {
            return wrapBefore;
        }

        public void setWrapBefore(boolean wrapBefore) {
            this.wrapBefore = wrapBefore;
        }

        public static final int WRAP_CONTENT = -2;

    }

}
