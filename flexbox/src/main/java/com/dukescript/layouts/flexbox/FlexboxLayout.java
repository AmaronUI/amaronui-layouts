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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * A reusable, platform independent implementation of the FlexBox Layout
 * Algorithm. To reuse this for your own ui toolkit implement the Container and
 * extend FlexBoxItem.
 * 
 * The implementation tries to follow the W3C spec as closely as possible. For more
 * information check here:
 * 
 * https://www.w3.org/TR/css-flexbox-1/
 * 
 *
 * @author antonepple
 */
public final class FlexboxLayout {

    private static Logger LOG = Logger.getLogger(FlexboxLayout.class.getName());

    /** Horizontal, left to right */
    public static final int FLEX_DIRECTION_ROW = 0;
    /** Horizontal, right to left */
    public static final int FLEX_DIRECTION_ROW_REVERSE = 1;
    /** Vertical, top to bottom */
    public static final int FLEX_DIRECTION_COLUMN = 2;
    /** Vertical, bottom to top */
    public static final int FLEX_DIRECTION_COLUMN_REVERSE = 3;
    private int flexDirection;
    /** Single line which may cause the container to overflow */
    public static final int FLEX_WRAP_NOWRAP = 0;
    /** multi-lines, direction defined by flexDirection */
    public static final int FLEX_WRAP_WRAP = 1;
    /** multi-lines, direction opposite of flexDirection */
    public static final int FLEX_WRAP_WRAP_REVERSE = 2;
    private int flexWrap;
    /** items are packed toward the start line (main direction) */
    public static final int JUSTIFY_CONTENT_FLEX_START = 0;
    /** items are packed toward the end line (main direction)*/
    public static final int JUSTIFY_CONTENT_FLEX_END = 1;
    /** items are centered around center (main direction)*/
    public static final int JUSTIFY_CONTENT_CENTER = 2;
    /** one item at the start, one at the end, extra space is distributed between the items (main direction)*/
    public static final int JUSTIFY_CONTENT_SPACE_BETWEEN = 3;
    /** extra space is distributed around the items (main direction)*/
    public static final int JUSTIFY_CONTENT_SPACE_AROUND = 4;
    private int justifyContent;

    /** items are packed toward the start line (cross direction) */
    public static final int ALIGN_ITEMS_FLEX_START = 0;
    /** items are packed toward the end line (cross direction)*/
    public static final int ALIGN_ITEMS_FLEX_END = 1;
    /** items are centered around center (main direction)*/
    public static final int ALIGN_ITEMS_CENTER = 2;
    /** Items are positioned at the baseline of the container */
    public static final int ALIGN_ITEMS_BASELINE = 3;
    /** stretch items to  fit the container */
    public static final int ALIGN_ITEMS_STRETCH = 4;
    private int alignItems;
    
    /** lines packed to the start of the container (cross direction)*/
    public static final int ALIGN_CONTENT_FLEX_START = 0;
    /** lines packed to the end of the container* (cross direction)*/
    public static final int ALIGN_CONTENT_FLEX_END = 1;
    /** lines packed around center (cross direction)*/
    public static final int ALIGN_CONTENT_CENTER = 2;
    /** one item at the start, one at the end, extra space is distributed between the items (cross direction) */
    public static final int ALIGN_CONTENT_SPACE_BETWEEN = 3;
    /** extra space is distributed equally around the items*/
    public static final int ALIGN_CONTENT_SPACE_AROUND = 4;
    /**lines are stretched in cross direction to fill container*/
    public static final int ALIGN_CONTENT_STRETCH = 5;
    private int alignContent;

//    public static final int SHOW_DIVIDER_NONE = 0;
//    public static final int SHOW_DIVIDER_BEGINNING = 1;
//    public static final int SHOW_DIVIDER_MIDDLE = 2;
//    public static final int SHOW_DIVIDER_END = 4;

    final List<FlexLine> flexLines = new ArrayList<>();
    private final List<FlexItem> originalItems = new ArrayList<>();
    private List<FlexItem> items;
    private double minMainSize = Double.MIN_VALUE;

    /**
     * Gets the minimum main size. The main size is the width or height of a
     * flex container or flex item, whichever is in the main dimension, is that
     * box’s main size. Its main size property is thus either its width or
     * height property, whichever is in the main dimension.
     *
     * @return minMainSize
     */
    public double getMinMainSize() {
        return minMainSize;
    }

    /**
     * Gets the minimum cross size. The axis perpendicular to the main axis is
     * called the cross axis. It extends in the cross dimension.
     *
     * @return minCrossSize
     */
    public double getMinCrossSize() {
        return minCrossSize;
    }
    private double minCrossSize = Double.MIN_VALUE;

    /**
     * Call this to do the actual layout when you implement a layout container.
     *
     * @param mainSize available width or height of the component, depending on
     * main axis
     * @param crossSize available width or height of the component, depending on
     * cross axis
     */
    public void layoutSubViews(double mainSize, double crossSize) {
        resetChildren();
        sortChildren();
        calculateFlexLines(mainSize);
        alignContent(crossSize);
        layoutFlexLines(mainSize);
        applyLayout();
    }

    /**
     *
     * @return true if the layout is in flex direction "row"
     */
    public boolean isHorizontal() {
        return flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE;
    }

    void sortChildren() {
        items = new ArrayList<>(originalItems);

        Collections.sort(items, new Comparator<FlexItem>() {
            @Override
            public int compare(FlexItem o1, FlexItem o2) {

                return ((Integer) o1.getOrder()).compareTo(o2.getOrder());
            }
        });
        final boolean descending = flexDirection == FLEX_DIRECTION_ROW_REVERSE || flexDirection == FLEX_DIRECTION_COLUMN_REVERSE;
        if (descending) {
            Collections.reverse(items);
        }
    }

    void alignContent(double crossSize) {
        minCrossSize = 0;
        if (flexLines.size() <= 1) {
            if (flexLines.size() == 1) {
                minCrossSize = flexLines.get(0).getMinCrossSize();
            }
            return; // nothing to do according to spec
        }

        for (FlexLine flexLine : flexLines) {
            minCrossSize += flexLine.getMinCrossSize();
        }
        double crossStartPos = 0;
        switch (alignContent) {

            case (ALIGN_CONTENT_FLEX_START):

                for (FlexLine flexLine : flexLines) {
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize;
                }
                minCrossSize = crossStartPos;
                break;
            case (ALIGN_CONTENT_FLEX_END):
                crossStartPos = Math.max(0, crossSize - minCrossSize);
                for (FlexLine flexLine : flexLines) {
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize;
                }
                break;
            case (ALIGN_CONTENT_CENTER):
                crossStartPos = Math.max(0, (crossSize - minCrossSize) / 2);
                for (FlexLine flexLine : flexLines) {
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize;
                }
                break;
            case (ALIGN_CONTENT_SPACE_BETWEEN):
                double extra = (crossSize - minCrossSize) / (flexLines.size() - 1);
                for (FlexLine flexLine : flexLines) {
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize + extra;
                }
                break;
            case (ALIGN_CONTENT_STRETCH): {
                double extraSpace = ((crossSize - minCrossSize) / flexLines.size());
                for (FlexLine flexLine : flexLines) {
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize + extraSpace;
//                    List<FlexItem> flexItems = flexLine.getFlexItems();
//                    for (FlexItem flexItem : flexItems) {
//                        flexItem.setCrossTargetSize(flexLine.minCrossSize + extraSpace);
//                    }
                    flexLine.setMinCrossSize(flexLine.minCrossSize + extraSpace);
                }
                break;
            }
            case (ALIGN_CONTENT_SPACE_AROUND):
                double extraSpace = ((crossSize - minCrossSize) / flexLines.size()) / 2;
                for (FlexLine flexLine : flexLines) {
                    crossStartPos += extraSpace;
                    flexLine.crossStartPos = crossStartPos;
                    crossStartPos += flexLine.minCrossSize + extraSpace;
                }
                break;
        }
    }

    void layoutFlexLines(double mainSize) {
        boolean horizontal = flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE;

        for (FlexLine flexLine : flexLines) {
            distributeMainLineSpace(flexLine, horizontal, mainSize);
            applyJustifyContent(flexLine, horizontal, mainSize);
            applyAlignItems(flexLine, horizontal);
        }
    }

    void applyAlignSelf(FlexLine line, FlexItem flexItem, boolean horizontal) {
        double lineCrossSize = line.getMinCrossSize();
        int flexAlignSelf = flexItem.getFlexAlignSelf();
        switch (flexAlignSelf) {
            case (FlexItem.ALIGN_SELF_AUTO):
                break; // shouldn't happen
            case (FlexItem.ALIGN_SELF_BASELINE):
                break; // not implemented, 
            case (FlexItem.ALIGN_SELF_CENTER):
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                break;
            case (FlexItem.ALIGN_SELF_FLEX_START):
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                break;
            case (FlexItem.ALIGN_SELF_FLEX_END):
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + lineCrossSize - flexItem.getCrossTargetSize());
                break;
            case (FlexItem.ALIGN_SELF_STRETCH):
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                flexItem.setCrossTargetSize(lineCrossSize - flexItem.getCrossMarginStart(horizontal) - flexItem.getCrossMarginEnd(horizontal));
                break;
        }
    }

    void applyAlignItems(FlexLine line, boolean horizontal) {
        List<FlexItem> flexItems = line.getFlexItems();
        double lineCrossSize = line.minCrossSize;

        switch (alignItems) {
            case (FlexboxLayout.ALIGN_ITEMS_FLEX_START):
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                    }
                }
                break;
            case (FlexboxLayout.ALIGN_ITEMS_FLEX_END):
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + lineCrossSize - flexItem.getCrossTargetSize());
                    }
                }
                break;
            case (FlexboxLayout.ALIGN_ITEMS_STRETCH):
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                        flexItem.setCrossTargetSize(lineCrossSize);
                    }
                }
                break;
            case (FlexboxLayout.ALIGN_ITEMS_CENTER):
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                    }
                }
                break;
            case (FlexboxLayout.ALIGN_ITEMS_BASELINE):
                // TODO  find a way to calc baseline
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                    }
                }
                break;
        }
    }

    void applyJustifyContent(FlexLine line, boolean horizontal, double mainSize) {
        List<FlexItem> flexItems = line.getFlexItems();

        double rest = mainSize - line.minMainSize;
        double startMain = 0;
        switch (justifyContent) {
            case (FlexboxLayout.JUSTIFY_CONTENT_FLEX_START):
                for (FlexItem flexItem : flexItems) {
                    flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
                    startMain += flexItem.getMainTargetSize();
                }
                break;
            case (FlexboxLayout.JUSTIFY_CONTENT_FLEX_END):
                startMain = Math.max(0, rest);
                for (FlexItem flexItem : flexItems) {
                    flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
                    startMain += flexItem.getMainTargetSize();
                }
                break;
            case (FlexboxLayout.JUSTIFY_CONTENT_CENTER):
                startMain = Math.max(rest / 2, 0);
                for (FlexItem flexItem : flexItems) {
                    flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
                    startMain += flexItem.getMainTargetSize();
                }
                break;
            case (FlexboxLayout.JUSTIFY_CONTENT_SPACE_AROUND):
                double extraSpacePerItem = Math.max((rest / line.flexItems.size()) / 2, 0);

                for (FlexItem flexItem : flexItems) {
                    startMain += extraSpacePerItem;
                    flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
                    startMain += extraSpacePerItem + flexItem.getMainTargetSize();
                }
                break;
            case (FlexboxLayout.JUSTIFY_CONTENT_SPACE_BETWEEN):
                if (line.flexItems.size() == 1) {
                    break;
                }
                double extraSpacebetweenItems = Math.max(0, rest / (line.flexItems.size() - 1));
                for (FlexItem flexItem : flexItems) {
                    flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
                    startMain += extraSpacebetweenItems + flexItem.getMainTargetSize();
                }
                break;
        }

    }

    void distributeMainLineSpace(FlexLine line, boolean horizontal, double mainSize) {
        List<FlexItem> flexItems = line.flexItems;
        double freeSpace = mainSize - line.minMainSize;
        if (freeSpace > 0) {
            double growUnit = 0;
            if (line.getGrow() > 0) {
                HashSet<FlexItem> frozen = new HashSet<>();
                float totalGrow = line.getGrow();
                growUnit = freeSpace / line.getGrow();
                while (totalGrow > 0 && growUnit > 0) { // while we have space left
                    double rest = 0;
                    for (FlexItem flexItem : flexItems) {
                        if (frozen.contains(flexItem)) {
                            continue;
                        }
                        if (flexItem.getFlexGrow() == 0) {
                            frozen.add(flexItem);
                            continue;
                        }
                        double grow = flexItem.getFlexGrow() * growUnit;
                        double grownSize = grow + flexItem.getMainTargetSize();
                        double clampedSize = horizontal ? flexItem.getMaxWidth() : flexItem.getMaxHeight();
                        if (grownSize > clampedSize) { // clamp to maxSize
                            rest += grownSize - clampedSize;
                            grownSize = clampedSize;
                            // use this value to setTargetMainSize
                            frozen.add(flexItem);
                            totalGrow -= flexItem.getFlexGrow(); // rest is split up between the others
                        }
                        double distributed = grownSize - flexItem.getMainTargetSize();
                        line.minMainSize += distributed;
                        flexItem.setMainTargetSize(grownSize);
                    }
                    if (rest > 0) { // there's extra space to distribute
                        if (totalGrow <= 0) { // nobody is interested
                            growUnit = 0;
                        } else { // calculate distribution factor for next round
                            growUnit = rest / totalGrow;
                        }
                    } else {
                        break;
                    }
                }
            }
        } else if (freeSpace < 0) {
            double growUnit = 0;
            if (line.getShrink() > 0) {
                HashSet<FlexItem> frozen = new HashSet<>();
                float totalGrow = line.getShrink();
                growUnit = freeSpace / line.getShrink();
                while (totalGrow > 0 && growUnit < 0) { // while we have space left
                    double rest = 0;
                    for (FlexItem flexItem : flexItems) {
                        if (frozen.contains(flexItem)) {
                            continue;
                        }
                        if (flexItem.getFlexShrink() == 1f) {
                            frozen.add(flexItem);
                            continue;
                        }
                        double grow = flexItem.getFlexShrink() * growUnit;
                        double grownSize = grow + flexItem.getMainTargetSize();
                        double clampedSize = horizontal ? flexItem.getMinWidth() : flexItem.getMinHeight();
                        if (grownSize < clampedSize) { // clamp to minSize
                            rest += grownSize - clampedSize;
                            grownSize = clampedSize;
                            // use this value to setTargetMainSize
                            frozen.add(flexItem);
                            totalGrow -= flexItem.getFlexShrink(); // rest is split up between the others
                        }
                        double distributed = grownSize - flexItem.getMainTargetSize();
                        line.minMainSize += distributed;
                        flexItem.setMainTargetSize(grownSize);
                    }
                    if (rest > 0) { // there's extra space to distribute
                        if (totalGrow <= 0) { // nobody is interested
                            growUnit = 0;
                        } else { // calculate distribution factor for next round
                            growUnit = rest / totalGrow;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    void calculateFlexLines(double mainSize) {
        LOG.finest("calculateFlexLines");
        boolean horizontal = flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE;
        minMainSize = 0;
        flexLines.clear();
        FlexLine line = new FlexLine();
        flexLines.add(line);
        if (flexWrap == FLEX_WRAP_NOWRAP) {
            LOG.finest("flex-wrap is FLEX_WRAP_NOWRAP");
            line.flexItems.addAll(items);
            for (FlexItem item : items) {
                double hypotheticalMainSize = (horizontal ? item.getHypotheticalMainWidth(mainSize) : item.getHypotheticalMainHeight(mainSize));
                item.setMainTargetSize(hypotheticalMainSize);
                double hypotheticalCrossSize = (!horizontal ? item.getHypotheticalMainWidth(mainSize) : item.getHypotheticalMainHeight(mainSize));
                item.setCrossTargetSize(hypotheticalCrossSize);
                line.setMinMainSize(line.getMinMainSize() + hypotheticalMainSize);
                line.adjustCrossSize(horizontal ? item.getHypotheticalMainHeight(mainSize) : item.getHypotheticalMainWidth(mainSize));
                if (item.getFlexGrow() != FlexItem.FLEX_GROW_DEFAULT) {
                    line.setGrow(line.getGrow() + item.getFlexGrow());
                }
                if (item.getFlexShrink() != FlexItem.FLEX_SHRINK_DEFAULT) {
                    line.setShrink(line.getShrink() + item.getFlexShrink());
                }
            }
            minMainSize = line.getMinMainSize();

            return;
        }
        LOG.finest("flex-wrap is not FLEX_WRAP_NOWRAP");
        double minSize = 0;

        for (FlexItem item : items) {
            LOG.finest("analyze item " + item);
            double hypotheticalMainSize = (horizontal ? item.getHypotheticalMainWidth(mainSize) : item.getHypotheticalMainHeight(mainSize));
            item.setMainTargetSize(hypotheticalMainSize);
            if (item.getMainTargetSize() > minMainSize) {
                minMainSize = item.getMainTargetSize();
            }
            LOG.finest("hypotheticalMainSize = " + hypotheticalMainSize);
            double hypotheticalCrossSize = (!horizontal ? item.getHypotheticalMainWidth(mainSize) : item.getHypotheticalMainHeight(mainSize));
            item.setCrossTargetSize(hypotheticalCrossSize);
            LOG.finest("hypotheticalCrossSize = " + hypotheticalCrossSize);

            minSize += hypotheticalMainSize;
            if (minSize > mainSize) {
                if (!line.flexItems.isEmpty()) {
                    line = new FlexLine();
                    flexLines.add(line);
                    minSize = hypotheticalMainSize;
                }
            }
            line.adjustCrossSize(horizontal ? item.getHypotheticalMainHeight(mainSize) : item.getHypotheticalMainWidth(mainSize));
            line.addFlexItem(item);
            line.setMinMainSize(minSize);

            if (item.getFlexGrow() != FlexItem.FLEX_GROW_DEFAULT) {
                line.setGrow(line.getGrow() + item.getFlexGrow());
            }
            if (item.getFlexShrink() != FlexItem.FLEX_SHRINK_DEFAULT) {
                line.setShrink(line.getShrink() + item.getFlexShrink());
            }
        }
        if (flexWrap == FLEX_WRAP_WRAP_REVERSE) {
            Collections.reverse(flexLines);
        }
    }

    /**
     * getter for flexDirection, which specifies the direction of the flexitems.
     *
     * @return flexDirection
     */
    public int getFlexDirection() {
        return flexDirection;
    }

    /**
     * setter for flexDirection, which specifies the direction of the flexitems.
     * 
     * @param flexDirection
     */
    public void setFlexDirection(int flexDirection) {
        this.flexDirection = flexDirection;
    }

    /**
     * Getter for flexWrap. flexWrap specifies if and how the flex items should be wrapped.
     * @return 
     */
    public int getFlexWrap() {
        return flexWrap;
    }

    /**
     * Setter for flexWrap. flexWrap specifies if and how the flex items should be wrapped.
     * @param flexWrap 
     */
    public void setFlexWrap(int flexWrap) {
        this.flexWrap = flexWrap;
    }

    /**
     * Getter for justifyContent. The justifyContent Property defines the 
     * alignment along the main axis and the distribution of extra space.
     * @return justifyContent
     */
    public int getJustifyContent() {
        return justifyContent;
    }

    /**
     * Setter for justifyContent. The justifyContent Property defines the 
     * alignment along the main axis and the distribution of extra space.
     * @param justifyContent
     */
    public void setJustifyContent(int justifyContent) {
        this.justifyContent = justifyContent;
    }

    /**
     * Getter for alignItems. It specifies the default alignment for items inside the flexible
     * container.
     *
     * @return alignItems;
     */
    public int getAlignItems() {
        return alignItems;
    }

    /**
     * Setter for alignItems.alignItems specifies the default alignment for items inside the flexible
     * container.
     *
     * @param alignItems
     */
    public void setAlignItems(int alignItems) {
        this.alignItems = alignItems;
    }

    /**
     * getter for alignContent. alignContent modifies the behavior of the
     * flexWrap property. It is similar to align-items, but instead of aligning
     * flex items, it aligns flex lines.
     *
     * @return alignContent
     */
    public int getAlignContent() {
        return alignContent;
    }

    /**
     * setter for alignContent. alignContent modifies the behavior of the
     * flexWrap property. It is similar to align-items, but instead of aligning
     * flex items, it aligns flex lines.
     *
     * @param alignContent
     */
    public void setAlignContent(int alignContent) {
        this.alignContent = alignContent;
    }

    /**
     * Add a FlexItem to be layed out.
     *
     * @param flexItem
     */
    public void add(FlexItem flexItem) {
        originalItems.add(flexItem);
    }

    /**
     * clears all FlexItems from the layout.
     */
    public void clearItems() {
        originalItems.clear();
    }

    private void applyLayout() {
        boolean horizontal = flexDirection == FLEX_DIRECTION_ROW || flexDirection == FLEX_DIRECTION_ROW_REVERSE;
        double crossStartPos = 0;
        for (FlexLine flexLine : flexLines) {
            crossStartPos = flexLine.crossStartPos;
            List<FlexItem> flexItems = flexLine.getFlexItems();
            for (FlexItem flexItem : flexItems) {
                double mainMargin = flexItem.getMainMarginStart(horizontal) + flexItem.getMainMarginEnd(horizontal);
                double crossMargin = flexItem.getCrossMarginStart(horizontal) + flexItem.getCrossMarginEnd(horizontal);
                flexItem.setBounds(new Bounds(horizontal ? flexItem.mainStartPos : crossStartPos + flexItem.crossStartPos,
                        horizontal ? crossStartPos + flexItem.crossStartPos : flexItem.mainStartPos,
                        horizontal ? flexItem.mainTargetSize - mainMargin : flexItem.crossTargetSize - crossMargin,
                        horizontal ? flexItem.crossTargetSize - crossMargin : flexItem.mainTargetSize - mainMargin));

            }
        }
    }

    private void resetChildren() {
        for (FlexItem item : originalItems) {
            item.setCrossStartPos(-1);

            item.setCrossTargetSize(-1);
            item.setMainStartPos(-1);
            item.setMainTargetSize(-1);
        }
    }

    public static class FlexLine {

        private List<FlexItem> flexItems = new ArrayList<>();
        private double minMainSize = 0, minCrossSize = 0;
        private float shrink = 0, grow = 0;
        private double crossStartPos = 0;

        double getCrossStartPos() {
            return crossStartPos;
        }

        void setCrossStartPos(double crossStartPos) {
            this.crossStartPos = crossStartPos;
        }

        public float getShrink() {
            return shrink;
        }

        public void setShrink(float shrink) {
            this.shrink = shrink;
        }

        public float getGrow() {
            return grow;
        }

        public void setGrow(float grow) {
            this.grow = grow;
        }

        public void addFlexItem(FlexItem item) {
            flexItems.add(item);
        }

        public List<FlexItem> getFlexItems() {
            return flexItems;
        }

        public double getMinMainSize() {
            return minMainSize;
        }

        void setMinMainSize(double minMainSize) {
            this.minMainSize = minMainSize;
        }

        public double getMinCrossSize() {
            return minCrossSize;
        }

        void setMinCrossSize(double minCrossSize) {
            this.minCrossSize = minCrossSize;
        }

        private void adjustCrossSize(double d) {
            if (d > this.minCrossSize) {
                this.minCrossSize = d;
            }
        }

    }

    /**
     *
     * @author antonepple
     */
    public static class DefaultFlexItem extends FlexItemBase {

        double width = -1, height = -1, minWidth = -1, minHeight = -1, maxWidth = Double.MAX_VALUE, maxHeight = Double.MAX_VALUE,
                marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;

        public DefaultFlexItem() {
        }

        public static class Builder {

            private boolean wrapBefore = false;
            private double width = -1;
            private double height = -1;
            private double minWidth = -1;
            private double minHeight = -1;
            private double maxWidth = Double.MAX_VALUE;
            private double maxHeight = Double.MAX_VALUE;
            private double marginLeft = 0;
            private double marginTop = 0;
            private double marginRight = 0;
            private double marginBottom = 0;
            private float flexGrow = FlexboxLayout.FlexItemBase.FLEX_GROW_DEFAULT;
            private float flexShrink = FlexboxLayout.FlexItemBase.FLEX_SHRINK_DEFAULT;
            private float flexBasisPercent = FlexboxLayout.FlexItemBase.FLEX_BASIS_PERCENT_DEFAULT;
            private int flexAlignSelf = FlexboxLayout.FlexItemBase.FLEX_ALIGN_SELF_DEFAULT;
            private int order = 0;

            private Builder() {
            }

            public Builder wrapBefore(final boolean value) {
                this.wrapBefore = value;
                return this;
            }

            public Builder width(final double value) {
                this.width = value;
                return this;
            }

            public Builder height(final double value) {
                this.height = value;
                return this;
            }

            public Builder minWidth(final double value) {
                this.minWidth = value;
                return this;
            }

            public Builder minHeight(final double value) {
                this.minHeight = value;
                return this;
            }

            public Builder maxWidth(final double value) {
                this.maxWidth = value;
                return this;
            }

            public Builder maxHeight(final double value) {
                this.maxHeight = value;
                return this;
            }

            public Builder marginLeft(final double value) {
                this.marginLeft = value;
                return this;
            }

            public Builder marginTop(final double value) {
                this.marginTop = value;
                return this;
            }

            public Builder marginRight(final double value) {
                this.marginRight = value;
                return this;
            }

            public Builder marginBottom(final double value) {
                this.marginBottom = value;
                return this;
            }

            public Builder flexGrow(final float value) {
                this.flexGrow = value;
                return this;
            }

            public Builder flexAlignSelf(final int value) {
                this.flexAlignSelf = value;
                return this;
            }

            public Builder flexShrink(final float value) {
                this.flexShrink = value;
                return this;
            }

            public Builder flexBasisPercent(final float value) {
                this.flexBasisPercent = value;
                return this;
            }

            public Builder order(final int value) {
                this.order = value;
                return this;
            }

            public DefaultFlexItem build() {
                return new DefaultFlexItem(wrapBefore, width, height, minWidth, minHeight, maxWidth, maxHeight, marginLeft, marginTop, marginRight, marginBottom, flexGrow, flexShrink, flexBasisPercent, order, flexAlignSelf);
            }
        }

        public static DefaultFlexItem.Builder builder() {
            return new DefaultFlexItem.Builder();
        }

        private DefaultFlexItem(final boolean wrapBefore, final double width, final double height, final double minWidth, final double minHeight, final double maxWidth, final double maxHeight, final double marginLeft, final double marginTop, final double marginRight, final double marginBottom, final float flexGrow, final float flexShrink, final float flexBasisPercent, final int order, final int alignSelf) {
            super.setWrapBefore(wrapBefore);
            this.width = width;
            this.height = height;
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.marginLeft = marginLeft;
            this.marginTop = marginTop;
            this.marginRight = marginRight;
            this.marginBottom = marginBottom;
            super.setFlexGrow(flexGrow);
            super.setFlexShrink(flexShrink);
            super.setFlexBasisPercent(flexBasisPercent);
            super.setOrder(order);
            super.setFlexAlignSelf(alignSelf);

        }

        @Override
        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        @Override
        public double getMinWidth() {
            return minWidth;
        }

        public void setMinWidth(double minWidth) {
            this.minWidth = minWidth;
        }

        @Override
        public double getMinHeight() {
            return minHeight;
        }

        public void setMinHeight(double minHeight) {
            this.minHeight = minHeight;
        }

        @Override
        public double getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
        }

        @Override
        public double getMaxHeight() {
            return maxHeight;
        }

        public void setMaxHeight(double maxHeight) {
            this.maxHeight = maxHeight;
        }

        @Override
        public double getMarginLeft() {
            return marginLeft;
        }

        public void setMarginLeft(double marginLeft) {
            this.marginLeft = marginLeft;
        }

        @Override
        public double getMarginTop() {
            return marginTop;
        }

        public void setMarginTop(double marginTop) {
            this.marginTop = marginTop;
        }

        @Override
        public double getMarginRight() {
            return marginRight;
        }

        public void setMarginRight(double marginRight) {
            this.marginRight = marginRight;
        }

        @Override
        public double getMarginBottom() {
            return marginBottom;
        }

        public void setMarginBottom(double marginBottom) {
            this.marginBottom = marginBottom;
        }

    }

    public static abstract class FlexItem {

        public static final int ALIGN_SELF_FLEX_START = FlexboxLayout.ALIGN_ITEMS_FLEX_START;
        public static final int ALIGN_SELF_FLEX_END = FlexboxLayout.ALIGN_ITEMS_FLEX_END;
        static final int ALIGN_SELF_CENTER = FlexboxLayout.ALIGN_ITEMS_CENTER;
        public static final int ALIGN_SELF_BASELINE = FlexboxLayout.ALIGN_ITEMS_BASELINE;
        public static final int ALIGN_SELF_STRETCH = FlexboxLayout.ALIGN_ITEMS_STRETCH;
        public static final int ALIGN_SELF_AUTO = 5;
        public static final float FLEX_GROW_DEFAULT = 0;
        public static final float FLEX_SHRINK_DEFAULT = 1;
        public static final int FLEX_BASIS_PERCENT_DEFAULT = -1;
        public static final int FLEX_ALIGN_SELF_DEFAULT = ALIGN_SELF_AUTO;

        Bounds bounds;
        protected double mainTargetSize = 0;
        protected double crossTargetSize = 0;
        protected double mainStartPos = 0;
        protected double crossStartPos = 0;

        /**
         * The actual Bounds set by the FlexBoxLayout in the containers
         * Coordinate System. If you want to reuse this algorithm for your UI
         * Technology, you can either apply the layout in setBounds (e.g. by
         * setting the bounds on a wrapped widget), or call getBounds after the
         * layout has been performed and apply it.
         *
         * @return the bounds of this Item according to the FlexBoxlayout
         */
        public final Bounds getBounds() {
            return this.bounds;
        }

        private void setBounds(Bounds bounds) {
            this.bounds = bounds;
            adjustBounds(bounds);
        }

        public boolean isSelfAligned() {
            return getFlexAlignSelf() != ALIGN_SELF_AUTO;
        }

        /**
         * This method is called by the Layout as the final step. You can
         * override it savely to directly apply the Bounds to your widgets, in
         * case you can apply the bounds directly. (e.g. JavaFX and RoboVM could
         * do that).
         *
         * @param bounds the bounds of this Item according to the FlexBoxlayout
         */
        protected void adjustBounds(Bounds bounds) {
        }

        public abstract boolean isWrapBefore();

        public abstract double getWidth();

        public abstract double getHeight();

        public abstract double getMinWidth();

        public abstract double getMinHeight();

        public abstract double getMaxWidth();

        public abstract double getMaxHeight();

        public abstract double getMarginLeft();

        public abstract double getMarginTop();

        public abstract double getMarginRight();

        public abstract double getMarginBottom();

        public abstract float getFlexGrow();

        public abstract float getFlexShrink();

        public abstract float getFlexBasisPercent();

        public abstract int getFlexAlignSelf();

        public abstract int getOrder();

        final double getMainStartPos() {
            return mainStartPos;
        }

        public double getMainMarginStart(boolean horizontal) {
            return horizontal ? getMarginLeft() : getMarginTop();
        }

        public double getMainMarginEnd(boolean horizontal) {
            return horizontal ? getMarginRight() : getMarginBottom();
        }

        public double getCrossMarginStart(boolean horizontal) {
            return horizontal ? getMarginTop() : getMarginLeft();
        }

        public double getCrossMarginEnd(boolean horizontal) {
            return horizontal ? getMarginBottom() : getMarginRight();
        }

        private void setMainStartPos(double mainStartPos) {
            this.mainStartPos = mainStartPos;
        }

        private double getCrossStartPos() {
            return crossStartPos;
        }

        private void setCrossStartPos(double crossStartPos) {
            this.crossStartPos = crossStartPos;
        }

        final double getMainTargetSize() {
            return mainTargetSize;
        }

        final void setMainTargetSize(double mainTargetSize) {
            this.mainTargetSize = mainTargetSize;
        }

        private double getCrossTargetSize() {
            return crossTargetSize;
        }

        private void setCrossTargetSize(double crossTargetSize) {
            this.crossTargetSize = crossTargetSize;
        }

        private double getHypotheticalMainWidth(double mainSize) {
            double flexBasisPercentWidth = (mainSize * getFlexBasisPercent()) / 100;
            flexBasisPercentWidth = Math.min(flexBasisPercentWidth, getMaxWidth());
            double margin = getMarginLeft() + getMarginRight();
            return getWidth() >= 0 ? margin + getWidth() : margin + Math.max(getMinWidth(), flexBasisPercentWidth);
        }

        private double getHypotheticalMainHeight(double mainSize) {
            double flexBasisPercentHeight = (mainSize * getFlexBasisPercent()) / 100;
            flexBasisPercentHeight = Math.min(flexBasisPercentHeight, getMaxHeight());
            double margin = getMarginTop() + getMarginBottom();
            return getHeight() >= 0 ? margin + getHeight() : margin + Math.max(getMinHeight(), flexBasisPercentHeight);
        }

    }

    /**
     *
     * @author antonepple
     */
    public static abstract class FlexItemBase extends FlexItem {

        boolean wrapBefore;
        float flexGrow = FLEX_GROW_DEFAULT;
        float flexShrink = FLEX_SHRINK_DEFAULT;
        float flexBasisPercent = FLEX_BASIS_PERCENT_DEFAULT;
        int flexAlignSelf = FLEX_ALIGN_SELF_DEFAULT;
        int order;

        public FlexItemBase() {
            super();
        }

        @Override
        public final boolean isWrapBefore() {
            return wrapBefore;
        }

        public final void setWrapBefore(boolean wrapBefore) {
            this.wrapBefore = wrapBefore;
        }

        @Override
        public int getFlexAlignSelf() {
            return this.flexAlignSelf;
        }

        public final void setFlexAlignSelf(int alignSelf) {
            this.flexAlignSelf = alignSelf;
        }

        @Override
        public float getFlexGrow() {
            return this.flexGrow;
        }

        public final void setFlexGrow(float flexGrow) {
            this.flexGrow = flexGrow;
        }

        @Override
        public final float getFlexShrink() {
            return flexShrink;
        }

        public final void setFlexShrink(float flexShrink) {
            this.flexShrink = flexShrink;
        }

        @Override
        public final float getFlexBasisPercent() {
            return flexBasisPercent;
        }

        public final void setFlexBasisPercent(float flexBasisPercent) {
            this.flexBasisPercent = flexBasisPercent;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        public final void setOrder(int order) {
            this.order = order;
        }

    }

}
