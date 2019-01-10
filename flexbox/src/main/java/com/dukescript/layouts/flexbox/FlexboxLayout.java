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
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexItem.AlignSelf;
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
 * The implementation tries to follow the W3C spec as closely as possible. For
 * more information check here:
 *
 * https://www.w3.org/TR/css-flexbox-1/
 *
 *
 * @author antonepple
 */
public final class FlexboxLayout {

    private static final Logger LOG = Logger.getLogger(FlexboxLayout.class.getName());

    public enum FlexDirection {
        /**
         * Horizontal, left to right
         */
        ROW,
        /**
         * Horizontal, right to left
         */
        ROW_REVERSE,
        /**
         * Vertical, top to bottom
         */
        COLUMN,
        /**
         * Vertical, bottom to top
         */
        COLUMN_REVERSE
    };

    public enum FlexWrap {
        /**
         * Single line which may cause the container to overflow
         */
        NOWRAP,
        /**
         * multi-lines, direction defined by flexDirection
         */
        WRAP,
        /**
         * multi-lines, direction opposite of flexDirection
         */
        WRAP_REVERSE
    };

    public enum JustifyContent {
        /**
         * items are packed toward the start line (main direction)
         */
        FLEX_START,
        /**
         * items are packed toward the end line (main direction)
         */
        FLEX_END,
        /**
         * items are centered around center (main direction)
         */
        CENTER,
        /**
         * one item at the start, one at the end, extra space is distributed
         * between the items (main direction)
         */
        SPACE_BETWEEN,
        /**
         * extra space is distributed around the items (main direction)
         */
        SPACE_AROUND,
    };

    public enum AlignItems {
        /**
         * items are packed toward the start line (cross direction)
         */
        FLEX_START,
        /**
         * items are packed toward the end line (cross direction)
         */
        FLEX_END,
        /**
         * items are centered around center (main direction)
         */
        CENTER,
        /**
         * Items are positioned at the baseline of the container
         */
        BASELINE,
        /**
         * stretch items to fit the container
         */
        STRETCH
    };

    public enum AlignContent {
        /**
         * lines packed to the start of the container (cross direction)
         */
        FLEX_START,
        /**
         * lines packed to the end of the container* (cross direction)
         */
        FLEX_END,
        /**
         * lines packed around center (cross direction)
         */
        CENTER,
        /**
         * one item at the start, one at the end, extra space is distributed
         * between the items (cross direction)
         */
        SPACE_BETWEEN,
        /**
         * extra space is distributed equally around the items
         */
        SPACE_AROUND,
        /**
         * lines are stretched in cross direction to fill container
         */
        STRETCH
    };

    private FlexDirection flexDirection;
    private FlexWrap flexWrap;
    private JustifyContent justifyContent = JustifyContent.FLEX_START;
    private AlignItems alignItems = AlignItems.STRETCH;
    private AlignContent alignContent = AlignContent.FLEX_START;
    final List<FlexLine> flexLines = new ArrayList<>();
    private final List<FlexItem> originalItems = new ArrayList<>();
    private List<FlexItem> items;
    private double minMainSize = Double.MIN_VALUE;
    private double minCrossSize = Double.MIN_VALUE;
    
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
        return flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE;
    }

    void sortChildren() {
        items = new ArrayList<>(originalItems);

        Collections.sort(items, new Comparator<FlexItem>() {
            @Override
            public int compare(FlexItem o1, FlexItem o2) {

                return ((Integer) o1.getOrder()).compareTo(o2.getOrder());
            }
        });
        final boolean descending = flexDirection == FlexDirection.ROW_REVERSE || flexDirection == FlexDirection.COLUMN_REVERSE;
        if (descending) {
            Collections.reverse(items);
        }
    }

    void alignContent(double crossSize) {
        minCrossSize = 0;
        if (flexLines.size()==0)return;
//        if (flexLines.size() <= 1) {
//            if (flexLines.size() == 1) {
//                minCrossSize = flexLines.get(0).getMinCrossSize();
//            }
//            return; // nothing to do according to spec
//        }

        for (FlexLine flexLine : flexLines) {
            minCrossSize += flexLine.getMinCrossSize();
        }
        double crossStartPos = 0;
        switch (alignContent) {

            case FLEX_START:
                for (FlexLine flexLine : flexLines) {
                    crossStartPos = getNewStartpossByAlignmentType(flexLine, crossStartPos);
                }
                minCrossSize = crossStartPos;
                break;
            case FLEX_END:
                crossStartPos = Math.max(0, crossSize - minCrossSize);
                for (FlexLine flexLine : flexLines) {
                    crossStartPos = getNewStartpossByAlignmentType(flexLine, crossStartPos);
                }
                break;
            case CENTER:
                crossStartPos = Math.max(0, (crossSize - minCrossSize) / 2);
                for (FlexLine flexLine : flexLines) {
                    crossStartPos = getNewStartpossByAlignmentType(flexLine, crossStartPos);
                }
                break;
            case SPACE_BETWEEN:
                double extra = (crossSize - minCrossSize) / (flexLines.size() - 1);
                for (FlexLine flexLine : flexLines) {
                    crossStartPos = getNewStartPosWithExtra(flexLine, crossStartPos, extra);
                }
                break;
            case STRETCH: {
                double extraSpace = ((crossSize - minCrossSize) / flexLines.size());
                for (FlexLine flexLine : flexLines) {
                    crossStartPos = getNewStartPosWithExtra(flexLine, crossStartPos, extraSpace);
//                    List<FlexItem> flexItems = flexLine.getFlexItems();
//                    for (FlexItem flexItem : flexItems) {
//                        flexItem.setCrossTargetSize(flexLine.minCrossSize + extraSpace);
//                    }
                    flexLine.setMinCrossSize(flexLine.minCrossSize + extraSpace);
                }
                break;
            }
            case SPACE_AROUND:
                double extraSpace = ((crossSize - minCrossSize) / flexLines.size()) / 2;
                for (FlexLine flexLine : flexLines) {
                    crossStartPos += extraSpace;
                    crossStartPos = getNewStartPosWithExtra(flexLine, crossStartPos, extraSpace);
                }
                break;
        }
    }

    private double getNewStartPosWithExtra(FlexLine flexLine, double crossStartPos, double extra) {
        flexLine.crossStartPos = crossStartPos;
        return crossStartPos += flexLine.minCrossSize + extra;
    }

    private double getNewStartpossByAlignmentType(FlexLine flexLine, double crossStartPos) {
        flexLine.crossStartPos = crossStartPos;
        crossStartPos += flexLine.minCrossSize;
        return crossStartPos;
    }

    void layoutFlexLines(double mainSize) {
        boolean horizontal = flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE;

        for (FlexLine flexLine : flexLines) {
            distributeMainLineSpace(flexLine, horizontal, mainSize);
            applyJustifyContent(flexLine, horizontal, mainSize);
            applyAlignItems(flexLine, horizontal);
        }
    }

    void applyAlignSelf(FlexLine line, FlexItem flexItem, boolean horizontal) {
        double lineCrossSize = line.getMinCrossSize();
        AlignSelf flexAlignSelf = flexItem.getFlexAlignSelf();
        switch (flexAlignSelf) {
            case AUTO:
                break; // shouldn't happen
            case BASELINE:
                break; // TODO: not implemented, 
            case CENTER:
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                break;
            case FLEX_START:
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                break;
            case FLEX_END:
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal) + lineCrossSize - flexItem.getCrossTargetSize());
                break;
            case STRETCH:
                flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                flexItem.setCrossTargetSize(lineCrossSize - flexItem.getCrossMarginStart(horizontal) - flexItem.getCrossMarginEnd(horizontal));
                break;
        }
    }

    void applyAlignItems(FlexLine line, boolean horizontal) {
        List<FlexItem> flexItems = line.getFlexItems();
        double lineCrossSize = line.minCrossSize;

        switch (alignItems) {
            case FLEX_START:
                for (FlexItem flexItem : flexItems) {
                    applyCrossStartPostOnItem(flexItem, line, horizontal, flexItem.getCrossMarginStart(horizontal));
                }
                break;
            case FLEX_END:
                for (FlexItem flexItem : flexItems) {
                    applyCrossStartPostOnItem(flexItem, line, horizontal, flexItem.getCrossMarginStart(horizontal) + lineCrossSize - flexItem.getCrossTargetSize());
                }
                break;
            case STRETCH:
                for (FlexItem flexItem : flexItems) {
                    if (flexItem.isSelfAligned()) {
                        applyAlignSelf(line, flexItem, horizontal);
                    } else {
                        flexItem.setCrossStartPos(flexItem.getCrossMarginStart(horizontal));
                        flexItem.setCrossTargetSize(lineCrossSize);
                    }
                }
                break;
            case CENTER:
                for (FlexItem flexItem : flexItems) {
                    applyCrossStartPostOnItem(flexItem, line, horizontal, flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                }
                break;
            case BASELINE:
                // TODO:  find a way to calc baseline
                for (FlexItem flexItem : flexItems) {
                    applyCrossStartPostOnItem(flexItem, line, horizontal, flexItem.getCrossMarginStart(horizontal) + (lineCrossSize - flexItem.getCrossTargetSize()) / 2);
                }
                break;
        }
    }

    private void applyCrossStartPostOnItem(FlexItem flexItem, FlexLine line, boolean horizontal, double startPos) {
        if (flexItem.isSelfAligned()) {
            applyAlignSelf(line, flexItem, horizontal);
        } else {
            flexItem.setCrossStartPos(startPos);
        }
    }

    void applyJustifyContent(FlexLine line, boolean horizontal, double mainSize) {
        List<FlexItem> flexItems = line.getFlexItems();

        double rest = mainSize - line.minMainSize;
        double startMain = 0;
        switch (justifyContent) {
            case FLEX_START:
                loopFlexItemsForStartMain(flexItems, startMain, horizontal);
                break;
            case FLEX_END:
                startMain = Math.max(0, rest);
                loopFlexItemsForStartMain(flexItems, startMain, horizontal);
                break;
            case CENTER:
                startMain = Math.max(rest / 2, 0);
                loopFlexItemsForStartMain(flexItems, startMain, horizontal);
                break;
            case SPACE_AROUND:
                double extraSpacePerItem = Math.max((rest / line.flexItems.size()) / 2, 0);

                for (FlexItem flexItem : flexItems) {
                    startMain += extraSpacePerItem;
                    startMain = getNewStartMainWithExtraSpace(flexItem, horizontal, startMain, extraSpacePerItem);
                }
                break;
            case SPACE_BETWEEN:
                if (line.flexItems.size() == 1) {
                    break;
                }
                double extraSpacebetweenItems = Math.max(0, rest / (line.flexItems.size() - 1));
                for (FlexItem flexItem : flexItems) {
                    startMain = getNewStartMainWithExtraSpace(flexItem, horizontal, startMain, extraSpacebetweenItems);
                }
                break;
        }

    }

    private void loopFlexItemsForStartMain(List<FlexItem> flexItems, double startMain, boolean horizontal) {
        for (FlexItem flexItem : flexItems) {
            startMain = getNewStartMain(flexItem, horizontal, startMain);
        }
    }

    private double getNewStartMain(FlexItem flexItem, boolean horizontal, double startMain) {
        flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
        return startMain += flexItem.getMainTargetSize();
    }

    private double getNewStartMainWithExtraSpace(FlexItem flexItem, boolean horizontal, double startMain, double extraSpacePerItem) {
        flexItem.setMainStartPos(flexItem.getMainMarginStart(horizontal) + startMain);
        return startMain += extraSpacePerItem + flexItem.getMainTargetSize();
    }

    void distributeMainLineSpace(FlexLine line, boolean horizontal, double mainSize) {
        List<FlexItem> flexItems = line.flexItems;
        double freeSpace = mainSize - line.minMainSize;
        double growUnit = 0;
        if (freeSpace > 0) {
            if (line.getGrow() > 0) {
                HashSet<FlexItem> frozen = new HashSet<>();
                float totalGrow = line.getGrow();
                growUnit = freeSpace / line.getGrow();
                while (totalGrow > 0 && growUnit > 0) { // while we have space left
                    double rest = 0;
                    for (FlexItem flexItem : flexItems) {
                        if (checkSkipItem(frozen, flexItem, flexItem.getFlexGrow(), 0f)) {
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
                        flexItem.setMainTargetSize(grownSize);
                        line.minMainSize += distributed;
                    }
                    if (rest > 0) {
                        growUnit = getNewGrowUnit(totalGrow, rest);
                    } else {
                        break;
                    }
                }
            }
        } else if (freeSpace < 0) {
            if (line.getShrink() > 0) {
                HashSet<FlexItem> frozen = new HashSet<>();
                float totalGrow = line.getShrink();
                growUnit = freeSpace / line.getShrink();
                while (totalGrow > 0 && growUnit < 0) { // while we have space left
                    double rest = 0;
                    for (FlexItem flexItem : flexItems) {
                        if (checkSkipItem(frozen, flexItem, totalGrow, 1f)) {
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
                        flexItem.setMainTargetSize(grownSize);
                        line.minMainSize += distributed;
                    }
                    if (rest > 0) { // there's extra space to distribute
                        growUnit = getNewGrowUnit(totalGrow, rest);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private double getNewGrowUnit(float totalGrow, double rest) {
        double growUnit;
        // there's extra space to distribute
        if (totalGrow <= 0) { // nobody is interested
            growUnit = 0;
        } else { // calculate distribution factor for next round
            growUnit = rest / totalGrow;
        }
        return growUnit;
    }

    private boolean checkSkipItem(HashSet<FlexItem> frozen, FlexItem flexItem, float valueToCheck, float valueToCheckAgainst) {
        if (frozen.contains(flexItem)) {
            return true;
        }
        if (valueToCheck == valueToCheckAgainst) {
            frozen.add(flexItem);
            return true;
        }
        return false;
    }

    void calculateFlexLines(double mainSize) {
        LOG.finest("calculateFlexLines");
        boolean horizontal = flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE;
        minMainSize = 0;
        flexLines.clear();
        FlexLine line = new FlexLine();
        flexLines.add(line);
        if (flexWrap == FlexWrap.NOWRAP) {
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
        else LOG.finest("flex-wrap is not FLEX_WRAP_NOWRAP");
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
        if (flexWrap == FlexWrap.WRAP_REVERSE) {
            Collections.reverse(flexLines);
        }
    }

    /**
     * getter for flexDirection, which specifies the direction of the flexitems.
     *
     * @return flexDirection
     */
    public FlexDirection getFlexDirection() {
        return flexDirection;
    }

    /**
     * setter for flexDirection, which specifies the direction of the flexitems.
     *
     * @param flexDirection
     */
    public void setFlexDirection(FlexDirection flexDirection) {
        this.flexDirection = flexDirection;
    }

    /**
     * Getter for flexWrap. flexWrap specifies if and how the flex items should
     * be wrapped.
     *
     * @return
     */
    public FlexWrap getFlexWrap() {
        return flexWrap;
    }

    /**
     * Setter for flexWrap. flexWrap specifies if and how the flex items should
     * be wrapped.
     *
     * @param flexWrap
     */
    public void setFlexWrap(FlexWrap flexWrap) {
        this.flexWrap = flexWrap;
    }

    /**
     * Getter for justifyContent. The justifyContent Property defines the
     * alignment along the main axis and the distribution of extra space.
     *
     * @return justifyContent
     */
    public JustifyContent getJustifyContent() {
        return justifyContent;
    }

    /**
     * Setter for justifyContent. The justifyContent Property defines the
     * alignment along the main axis and the distribution of extra space.
     *
     * @param justifyContent
     */
    public void setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
    }

    /**
     * Getter for alignItems. It specifies the default alignment for items
     * inside the flexible container.
     *
     * @return alignItems;
     */
    public AlignItems getAlignItems() {
        return alignItems;
    }

    /**
     * Setter for alignItems.alignItems specifies the default alignment for
     * items inside the flexible container.
     *
     * @param alignItems
     */
    public void setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
    }

    /**
     * getter for alignContent. alignContent modifies the behavior of the
     * flexWrap property. It is similar to align-items, but instead of aligning
     * flex items, it aligns flex lines.
     *
     * @return alignContent
     */
    public AlignContent getAlignContent() {
        return alignContent;
    }

    /**
     * setter for alignContent. alignContent modifies the behavior of the
     * flexWrap property. It is similar to align-items, but instead of aligning
     * flex items, it aligns flex lines.
     *
     * @param alignContent
     */
    public void setAlignContent(AlignContent alignContent) {
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
        boolean horizontal = flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE;
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

    static class FlexLine {

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

        public static class DefaultFlexItemBuilder {

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
            private AlignSelf flexAlignSelf = FlexboxLayout.FlexItemBase.FLEX_ALIGN_SELF_DEFAULT;
            private int order = 0;

            private DefaultFlexItemBuilder() {
            }

            public DefaultFlexItemBuilder wrapBefore(final boolean value) {
                this.wrapBefore = value;
                return this;
            }

            public DefaultFlexItemBuilder width(final double value) {
                this.width = value;
                return this;
            }

            public DefaultFlexItemBuilder height(final double value) {
                this.height = value;
                return this;
            }

            public DefaultFlexItemBuilder minWidth(final double value) {
                this.minWidth = value;
                return this;
            }

            public DefaultFlexItemBuilder minHeight(final double value) {
                this.minHeight = value;
                return this;
            }

            public DefaultFlexItemBuilder maxWidth(final double value) {
                this.maxWidth = value;
                return this;
            }

            public DefaultFlexItemBuilder maxHeight(final double value) {
                this.maxHeight = value;
                return this;
            }

            public DefaultFlexItemBuilder marginLeft(final double value) {
                this.marginLeft = value;
                return this;
            }

            public DefaultFlexItemBuilder marginTop(final double value) {
                this.marginTop = value;
                return this;
            }

            public DefaultFlexItemBuilder marginRight(final double value) {
                this.marginRight = value;
                return this;
            }

            public DefaultFlexItemBuilder marginBottom(final double value) {
                this.marginBottom = value;
                return this;
            }

            public DefaultFlexItemBuilder flexGrow(final float value) {
                this.flexGrow = value;
                return this;
            }

            public DefaultFlexItemBuilder flexAlignSelf(final AlignSelf value) {
                this.flexAlignSelf = value;
                return this;
            }

            public DefaultFlexItemBuilder flexShrink(final float value) {
                this.flexShrink = value;
                return this;
            }

            public DefaultFlexItemBuilder flexBasisPercent(final float value) {
                this.flexBasisPercent = value;
                return this;
            }

            public DefaultFlexItemBuilder order(final int value) {
                this.order = value;
                return this;
            }

            public DefaultFlexItem build() {
                return new DefaultFlexItem(wrapBefore, width, height, minWidth, minHeight, maxWidth, maxHeight, marginLeft, marginTop, marginRight, marginBottom, flexGrow, flexShrink, flexBasisPercent, order, flexAlignSelf);
            }
        }

        public static DefaultFlexItemBuilder builder() {
            return new DefaultFlexItemBuilder();
        }

        private DefaultFlexItem(final boolean wrapBefore, final double width, final double height, final double minWidth, final double minHeight, final double maxWidth, final double maxHeight, final double marginLeft, final double marginTop, final double marginRight, final double marginBottom, final float flexGrow, final float flexShrink, final float flexBasisPercent, final int order, final AlignSelf alignSelf) {
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

        public enum AlignSelf {
            FLEX_START,
            FLEX_END,
            CENTER,
            BASELINE,
            STRETCH,
            AUTO
        }

        public static final float FLEX_GROW_DEFAULT = 0;
        public static final float FLEX_SHRINK_DEFAULT = 1;
        public static final int FLEX_BASIS_PERCENT_DEFAULT = -1;
        public static final AlignSelf FLEX_ALIGN_SELF_DEFAULT = AlignSelf.AUTO;

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
            return getFlexAlignSelf() != AlignSelf.AUTO;
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

        public abstract AlignSelf getFlexAlignSelf();

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
        AlignSelf flexAlignSelf = FLEX_ALIGN_SELF_DEFAULT;
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
        public AlignSelf getFlexAlignSelf() {
            return this.flexAlignSelf;
        }

        public final void setFlexAlignSelf(AlignSelf alignSelf) {
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
