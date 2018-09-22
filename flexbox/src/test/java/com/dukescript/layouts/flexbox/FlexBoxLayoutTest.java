package com.dukescript.layouts.flexbox;

/*-
 * #%L
 * flexbox - a library from the "DukeScript Layouts" project.
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

import com.dukescript.layouts.flexbox.FlexboxLayout.DefaultFlexItem;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexItemBase;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author antonepple
 */
public class FlexBoxLayoutTest {

    public FlexBoxLayoutTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testCalculateFlexLinesNumLines() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_NOWRAP);

        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(50);
            flexItemImpl.setMinHeight(50);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(1, flexboxLayout.flexLines.size());
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(2, flexboxLayout.flexLines.size());
        Assert.assertEquals(100, flexboxLayout.flexLines.get(0).getMinMainSize(), 0.0001);
        Assert.assertEquals(50, flexboxLayout.flexLines.get(0).getMinCrossSize(), 0.0001);
        flexboxLayout.clearItems();
        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(51);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();

        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(3, flexboxLayout.flexLines.size());
        Assert.assertEquals(51, flexboxLayout.flexLines.get(0).getMinMainSize(), 0.0001);
        flexboxLayout.clearItems();
        DefaultFlexItem flexItemImpl = new DefaultFlexItem();
        flexItemImpl.setMinWidth(151);
        flexboxLayout.add(flexItemImpl);
        flexboxLayout.sortChildren();

        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(1, flexboxLayout.flexLines.size());
        Assert.assertEquals(151, flexboxLayout.flexLines.get(0).getMinMainSize(), 0.0001);
    }

    @Test
    public void testOrder() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);

        FlexItemBase flexItemImpl1 = DefaultFlexItem.builder().minWidth(50).build();
        flexboxLayout.add(flexItemImpl1);
        DefaultFlexItem flexItemImpl2 = DefaultFlexItem.builder().minWidth(50).build();
        flexboxLayout.add(flexItemImpl2);
        DefaultFlexItem flexItemImpl3 = DefaultFlexItem.builder().minWidth(51).build();
        flexboxLayout.sortChildren();
        flexboxLayout.add(flexItemImpl3);
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(2, flexboxLayout.flexLines.get(0).getFlexItems().size());
        flexItemImpl3.setOrder(-1);
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(1, flexboxLayout.flexLines.get(0).getFlexItems().size());
    }

    @Test
    public void testCalculateFlexLinesReversion() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);

        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(50);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(2, flexboxLayout.flexLines.get(0).getFlexItems().size());
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP_REVERSE);
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(1, flexboxLayout.flexLines.get(0).getFlexItems().size());
    }

    @Test
    public void testJustifyContent() {
        FlexboxLayout.FlexLine flexLine = new FlexboxLayout.FlexLine();
        flexLine.setMinMainSize(150);
        DefaultFlexItem flexItemImpl = DefaultFlexItem.builder()
                .width(50).height(50).build();
        flexItemImpl.setMainTargetSize(50);
        DefaultFlexItem flexItemImpl2 = DefaultFlexItem.builder()
                .width(50).height(50).build();
        flexItemImpl2.setMainTargetSize(50);
        DefaultFlexItem flexItemImpl3 = DefaultFlexItem.builder()
                .width(50).height(50).build();
        flexItemImpl3.setMainTargetSize(50);
        flexLine.addFlexItem(flexItemImpl);
        flexLine.addFlexItem(flexItemImpl2);
        flexLine.addFlexItem(flexItemImpl3);
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setJustifyContent(FlexboxLayout.JUSTIFY_CONTENT_FLEX_END);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(50, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(FlexboxLayout.JUSTIFY_CONTENT_FLEX_START);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(0, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(8.3333, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        Assert.assertEquals(75, flexLine.getFlexItems().get(1).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(0, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        Assert.assertEquals(75, flexLine.getFlexItems().get(1).getMainStartPos(), 0.01);
    }

    @Test
    public void testDistributeMainLineSpace() {
        FlexboxLayout.FlexLine flexLine = new FlexboxLayout.FlexLine();
        flexLine.setMinMainSize(100);
        flexLine.setGrow(2);
        DefaultFlexItem flexItemImpl = DefaultFlexItem.builder()
                .width(50).height(50).flexGrow(1).maxWidth(60).build();
        flexItemImpl.setMainTargetSize(50);
        DefaultFlexItem flexItemImpl2 = DefaultFlexItem.builder()
                .width(50).height(50).flexGrow(1).build();
        flexItemImpl2.setMainTargetSize(50);
        flexLine.addFlexItem(flexItemImpl);
        flexLine.addFlexItem(flexItemImpl2);
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.distributeMainLineSpace(flexLine, true, 200);
        Assert.assertEquals(140, flexItemImpl2.getMainTargetSize(), 0.01);
        Assert.assertEquals(60, flexItemImpl.getMainTargetSize(), 0.01);
        flexboxLayout.clearItems();
    }

    @Test
    public void testAlignContent() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        flexboxLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        flexboxLayout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_FLEX_START);
        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(51);
            flexItemImpl.setMinHeight(20);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(60, flexboxLayout.getMinCrossSize(), 0.001);
        List<FlexboxLayout.FlexLine> flexLines = flexboxLayout.flexLines;
        Assert.assertEquals(40, flexLines.get(2).getCrossStartPos(), 0.001);
        flexboxLayout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_FLEX_END);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(480, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_CENTER);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(260, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_SPACE_AROUND);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(406.6666666666666, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_SPACE_BETWEEN);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(480, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
    }

    @Test
    public void testAlignItems() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        layout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(10).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(50).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(0, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);

        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_FLEX_END);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(40, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_CENTER);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(20, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_CENTER);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(20, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_STRETCH);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(0, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(50, flexItem.crossTargetSize, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
    }

    @Test
    public void testAlignSelf() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        layout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(10).flexAlignSelf(FlexboxLayout.FlexItem.ALIGN_SELF_FLEX_END).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(50).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(40, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
    }

    @Test
    public void testMarginAlignItems() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        layout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        layout.setAlignItems(FlexboxLayout.ALIGN_ITEMS_STRETCH);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(10)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(50)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(5, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(5, flexItem2.crossStartPos, 0.001);
        Assert.assertEquals(110, flexItem.getMainTargetSize(), 0.001);
        Assert.assertEquals( 5, flexItem.bounds.x,0.001);
        Assert.assertEquals( 50, flexItem.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem.bounds.w,0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals( 115, flexItem2.bounds.x,0.001);
        Assert.assertEquals( 5, flexItem2.bounds.y,0.001);
        Assert.assertEquals( 50, flexItem2.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem2.bounds.w,0.001);
        
    }
    
    @Test
    public void testMargin() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        layout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        layout.setAlignContent(FlexboxLayout.ALIGN_CONTENT_FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(20)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(20)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(220, 400);
        Assert.assertEquals(110, flexItem.getMainTargetSize(), 0.001);
        Assert.assertEquals( 5, flexItem.bounds.x,0.001);
        Assert.assertEquals( 20, flexItem.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem.bounds.w,0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals( 115, flexItem2.bounds.x,0.001);
        Assert.assertEquals( 5, flexItem2.bounds.y,0.001);
        Assert.assertEquals( 20, flexItem2.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem2.bounds.w,0.001);
        layout.layoutSubViews(120, 400);
        Assert.assertEquals(110, flexItem.getMainTargetSize(), 0.001);
        Assert.assertEquals( 5, flexItem.bounds.x,0.001);
        Assert.assertEquals( 20, flexItem.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem.bounds.w,0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals( 5, flexItem2.bounds.x,0.001);
        Assert.assertEquals( 35, flexItem2.bounds.y,0.001);
        Assert.assertEquals( 20, flexItem2.bounds.h,0.001);
        Assert.assertEquals( 100, flexItem2.bounds.w,0.001);
    }

}
