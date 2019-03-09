package com.dukescript.layouts.flexbox;

/*-
 * #%L
 * flexbox - a library from the "DukeScript Layouts" project.
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
import com.dukescript.layouts.flexbox.FlexboxLayout.AlignItems;
import com.dukescript.layouts.flexbox.FlexboxLayout.DefaultFlexItem;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexDirection;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexItem.AlignSelf;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexItemBase;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexWrap;
import com.dukescript.layouts.flexbox.FlexboxLayout.JustifyContent;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antonepple
 */
public class FlexBoxLayoutTest {

    @Test
    public void testCalculateFlexLinesNumLines() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.NOWRAP);

        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(50);
            flexItemImpl.setMinHeight(50);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(1, flexboxLayout.flexLines.size());
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
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

    /**
     * Bug report from user
     */
    @Test
    public void testSingleRowHeight() {
//        Logger rootLogger = LogManager.getLogManager().getLogger("");
//        rootLogger.setLevel(Level.FINEST);
//        for (Handler h : rootLogger.getHandlers()) {
//            h.setLevel(Level.FINEST);
//        }
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        flexboxLayout.setJustifyContent(JustifyContent.FLEX_START);
        flexboxLayout.setAlignItems(AlignItems.STRETCH);
        flexboxLayout.setAlignContent(AlignContent.STRETCH);

        FlexItemBase flexItemImpl1 = DefaultFlexItem.builder().minWidth(50).height(50).build();
        flexboxLayout.add(flexItemImpl1);
        FlexItemBase flexItemImpl2 = DefaultFlexItem.builder().minWidth(50).height(50).build();
        flexboxLayout.add(flexItemImpl2);

//        flexboxLayout.layoutSubViews(80, 400);
//        Assert.assertEquals(200, flexItemImpl1.bounds.getH(), 0.001);
        flexboxLayout.layoutSubViews(100, 400);
        Assert.assertEquals(400, flexItemImpl1.bounds.getH(), 0.001);

    }

    @Test
    public void testOrder() {
        FlexboxLayout flexboxLayout = new FlexboxLayout();
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
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
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);

        for (int i = 0; i < 3; i++) {
            DefaultFlexItem flexItemImpl = new DefaultFlexItem();
            flexItemImpl.setMinWidth(50);
            flexboxLayout.add(flexItemImpl);
        }
        flexboxLayout.sortChildren();
        flexboxLayout.calculateFlexLines(100);
        Assert.assertEquals(2, flexboxLayout.flexLines.get(0).getFlexItems().size());
        flexboxLayout.setFlexWrap(FlexWrap.WRAP_REVERSE);
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
        flexboxLayout.setJustifyContent(JustifyContent.FLEX_END);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(50, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(JustifyContent.FLEX_START);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(0, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(JustifyContent.SPACE_AROUND);
        flexboxLayout.applyJustifyContent(flexLine, true, 200);
        Assert.assertEquals(8.3333, flexLine.getFlexItems().get(0).getMainStartPos(), 0.01);
        Assert.assertEquals(75, flexLine.getFlexItems().get(1).getMainStartPos(), 0.01);
        flexboxLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
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
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        flexboxLayout.setAlignContent(AlignContent.FLEX_START);
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
        flexboxLayout.setAlignContent(AlignContent.FLEX_END);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(480, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(AlignContent.CENTER);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(260, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(AlignContent.SPACE_AROUND);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(406.6666666666666, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
        flexboxLayout.setAlignContent(AlignContent.SPACE_BETWEEN);
        flexboxLayout.calculateFlexLines(100);
        flexboxLayout.alignContent(500);
        Assert.assertEquals(480, flexboxLayout.flexLines.get(2).getCrossStartPos(), 0.01);
    }

    @Test
    public void testAlignItems() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setAlignItems(AlignItems.FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(10).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(50).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(0, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);

        layout.setAlignItems(AlignItems.FLEX_END);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(40, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(AlignItems.CENTER);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(20, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(AlignItems.CENTER);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(20, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
        layout.setAlignItems(AlignItems.STRETCH);
        layout.layoutSubViews(250, 200);
        Assert.assertEquals(0, flexItem.crossStartPos, 0.001);
        Assert.assertEquals(50, flexItem.crossTargetSize, 0.001);
        Assert.assertEquals(0, flexItem2.crossStartPos, 0.001);
    }

    @Test
    public void testAlignSelf() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setAlignItems(AlignItems.FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(10).flexAlignSelf(AlignSelf.FLEX_END).build();
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
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setAlignItems(AlignItems.STRETCH);
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
        Assert.assertEquals(5, flexItem.bounds.getX(), 0.001);
        Assert.assertEquals(50, flexItem.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem.bounds.getW(), 0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals(115, flexItem2.bounds.getX(), 0.001);
        Assert.assertEquals(5, flexItem2.bounds.getY(), 0.001);
        Assert.assertEquals(50, flexItem2.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem2.bounds.getW(), 0.001);

    }

    @Test
    public void testMargin() {
        FlexboxLayout layout = new FlexboxLayout();
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setAlignContent(AlignContent.FLEX_START);
        DefaultFlexItem flexItem = DefaultFlexItem.builder().minWidth(100).minHeight(20)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        DefaultFlexItem flexItem2 = DefaultFlexItem.builder().minWidth(100).minHeight(20)
                .marginTop(5).marginBottom(5).marginLeft(5).marginRight(5).build();
        layout.add(flexItem);
        layout.add(flexItem2);
        layout.layoutSubViews(220, 400);
        Assert.assertEquals(110, flexItem.getMainTargetSize(), 0.001);
        Assert.assertEquals(5, flexItem.bounds.getX(), 0.001);
        Assert.assertEquals(20, flexItem.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem.bounds.getW(), 0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals(115, flexItem2.bounds.getX(), 0.001);
        Assert.assertEquals(5, flexItem2.bounds.getY(), 0.001);
        Assert.assertEquals(20, flexItem2.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem2.bounds.getW(), 0.001);
        layout.layoutSubViews(120, 400);
        Assert.assertEquals(110, flexItem.getMainTargetSize(), 0.001);
        Assert.assertEquals(5, flexItem.bounds.getX(), 0.001);
        Assert.assertEquals(20, flexItem.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem.bounds.getW(), 0.001);
        Assert.assertEquals(110, flexItem2.getMainTargetSize(), 0.001);
        Assert.assertEquals(5, flexItem2.bounds.getX(), 0.001);
        Assert.assertEquals(35, flexItem2.bounds.getY(), 0.001);
        Assert.assertEquals(20, flexItem2.bounds.getH(), 0.001);
        Assert.assertEquals(100, flexItem2.bounds.getW(), 0.001);
    }

}
