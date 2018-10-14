package com.dukescript.layouts.jfxflexbox;

/*-
 * #%L
 * jfxflexbox - a library from the "DukeScript Layouts" project.
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
import com.dukescript.layouts.flexbox.FlexboxLayout.AlignContent;
import com.dukescript.layouts.flexbox.FlexboxLayout.AlignItems;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexDirection;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexWrap;
import com.dukescript.layouts.flexbox.FlexboxLayout.JustifyContent;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * FlexBoxPane is a JavaFX Layout Pane.
 *
 * Use it like this:
 *
 * <pre>
 * {@code
 * FlexBoxPane flex = new FlexBoxPane();
 * Label flexItem = new Label("Flex Item ");
 * flex.getChildren().add(flexItem);
 * }
 * </pre>
 *
 * You can adjust the layout by setting properties on the FlexBoxPane:
 * <pre>
 * {@code
 * flex.setJustifyContent(FlexboxLayout.JUSTIFY_CONTENT_CENTER);
 * flex.setFlexWrap(FlexboxLayout.FLEX_WRAP_NOWRAP);
 * }
 * </pre>
 *
 * Setting constraints is done in the typical JavaFX way via static methods on
 * the defining Layout Pane:
 * <pre>
 * {@code
 * FlexBoxPane.setMargin(flexItem, new Insets(5));
 * FlexBoxPane.setFlexBasisPercent(flexItem, 10f);
 * }
 * </pre>
 *
 * For more info about the Layout algorithm itself and the meaning of the
 * properties please checkout the specification:
 * https://www.w3.org/TR/css-flexbox-1/ We tried to follow the spec as closely
 * as possible where applicable (but obviously this isn't css).
 *
 * @author antonepple
 */
public class FlexBoxPane extends Pane {

    private static String MARGIN_CONSTRAINT = "MARGIN_CONSTRAINT";
    private static String ORDER_CONSTRAINT = "ORDER_CONSTRAINT";
    private static final String WRAP_BEFORE = "WRAP_BEFORE";
    private static final String FLEX_GROW = "FLEX_GROW";
    private static final String FLEX_SHRINK = "FLEX_SHRINK";
    private static final String FLEX_BASIS_PERCENT = "FLEX_BASIS_PERCENT";
    private static final String FLEX_ALIGN_SELF = "FLEX_ALIGN_SELF";
    private static final String FLEX_ORDER = "FLEX_ORDER";

    private static Insets DEFAULT_MARGIN = new Insets(0);

    private final FlexboxLayout layout = new FlexboxLayout();
    private ListChangeListener childwatch;

    public FlexBoxPane() {
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setFlexWrap(FlexWrap.WRAP_REVERSE);
        layout.setJustifyContent(JustifyContent.CENTER);
    }

    /**
     * Set the "Margin" layout contraint for this child.
     *
     * @param child
     * @param insets
     */
    public static void setMargin(Node child, Insets insets) {
        setConstraint(child, MARGIN_CONSTRAINT, insets);
    }

    /**
     * Set the "Order" layout contraint for this child. Use to reorder the items
     * directly. -1 places the item at the line start.
     *
     * @param child
     * @param order
     */
    public static void setOrder(Node child, int order) {
        setConstraint(child, ORDER_CONSTRAINT, order);
    }

    /**
     * Set the "flex-grow" layout contraint for this child. It defines how space
     * is distributed among the items. By default all items have a value of 1.
     * If you were to give one of the children a value of 2, that child would
     * take up twice as much space as the others.
     *
     * @param child
     * @param grow
     */
    public static void setGrow(Node child, float grow) {
        setConstraint(child, FLEX_GROW, grow);
    }

    /**
     * Set the "flex-shrink" layout contraint for this child. It defines how
     * much the items will shrink if there's not enough space.
     *
     * @param child
     * @param shrink
     */
    public static void setShrink(Node child, float shrink) {
        setConstraint(child, FLEX_SHRINK, shrink);
    }

    /**
     * Set the "flex-basis-percent" layout contraint for this child.
     *
     * @param child
     * @param f
     */
    public static void setFlexBasisPercent(Node child, float f) {
        setConstraint(child, FLEX_BASIS_PERCENT, f);
    }

    /**
     * Set the "flex-basis-percent" layout contraint for this child.
     *
     * @param child
     * @param f
     */
    public static void setFlexBasisPercent(Node child, int f) {
        setConstraint(child, FLEX_ALIGN_SELF, f);
    }

    static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    static Object getConstraint(Node node, Object key, Object defaultVal) {
        if (node.hasProperties()) {
            Object value = node.getProperties().get(key);
            if (value != null) {
                return value;
            } else {
                setConstraint(node, key, defaultVal);
            }
        }
        return defaultVal;
    }

    double minMainSize;
    double minCrossSize;

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        layout.layoutSubViews(getMainSize(), getCrossSize());

        minMainSize = layout.getMinMainSize();
        minCrossSize = layout.getMinCrossSize();
        getChildren().stream().filter(e-> !e.isManaged()).forEach(
                e-> e.resizeRelocate(e.getLayoutX(), e.getLayoutY(),e.prefWidth(-1),e.prefHeight(-1))
        );
    }

    @Override
    public ObservableList<Node> getChildren() {
        final ObservableList<Node> children = super.getChildren();
        if (childwatch == null) {
            childwatch = new ListChangeListener<Node>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Node> c) {
                    // TODO could be faster, if you deal with individual changes instead of replacing the full list
                    layout.clearItems();
                    for (Node child : children) {
                        if (child.isManaged()) {
                            layout.add(new FlexItemImpl(child));
                        }
                    }
                }
            };
            children.addListener(childwatch);
        }
        return children;
    }

    private double getCrossSize() {
        return layout.getFlexDirection() == FlexDirection.ROW
                || layout.getFlexDirection() == FlexDirection.ROW_REVERSE
                ? getHeight() : getWidth();
    }

    private double getMainSize() {
        return layout.getFlexDirection() == FlexDirection.COLUMN
                || layout.getFlexDirection() == FlexDirection.COLUMN_REVERSE
                ? getHeight() : getWidth();
    }

    public void setJustifyContent(JustifyContent ordinal) {
        layout.setJustifyContent(ordinal);
        requestLayout();
    }

    public void setFlexWrap(FlexWrap ordinal) {
        layout.setFlexWrap(ordinal);
        requestLayout();
    }

    public void setFlexDirection(FlexDirection ordinal) {
        layout.setFlexDirection(ordinal);
        requestLayout();
    }

    public void setAlignItems(AlignItems ordinal) {
        layout.setAlignItems(ordinal);
        requestLayout();
    }

    public void setAlignContent(AlignContent ordinal) {
        layout.setAlignContent(ordinal);
        requestLayout();
    }

    private static class FlexItemImpl extends FlexboxLayout.FlexItem {

        private final Node delegate;

        private FlexItemImpl(Node delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void adjustBounds(Bounds bounds) {
            delegate.resizeRelocate(bounds.getX(), bounds.getY(), bounds.getW(), bounds.getH());
        }

        @Override
        public double getWidth() {
            return delegate.prefWidth(-1);
        }

        @Override
        public double getHeight() {
            return delegate.prefHeight(-1);
        }

        @Override
        public double getMinWidth() {
            return delegate.minWidth(-1);
        }

        @Override
        public double getMinHeight() {
            return delegate.minHeight(-1);
        }

        @Override
        public double getMaxWidth() {
            return delegate.maxWidth(-1);
        }

        @Override
        public double getMaxHeight() {
            return delegate.maxHeight(-1);
        }

        @Override
        public double getMarginLeft() {
            Insets insets = (Insets) getConstraint(delegate, MARGIN_CONSTRAINT, DEFAULT_MARGIN);
            return insets.getLeft();
        }

        @Override
        public double getMarginTop() {
            Insets insets = (Insets) getConstraint(delegate, MARGIN_CONSTRAINT, DEFAULT_MARGIN);
            return insets.getTop();
        }

        @Override
        public double getMarginRight() {
            Insets insets = (Insets) getConstraint(delegate, MARGIN_CONSTRAINT, DEFAULT_MARGIN);
            return insets.getRight();
        }

        @Override
        public double getMarginBottom() {
            Insets insets = (Insets) getConstraint(delegate, MARGIN_CONSTRAINT, DEFAULT_MARGIN);
            return insets.getBottom();
        }

        @Override
        public boolean isWrapBefore() {
            return (boolean) getConstraint(delegate, WRAP_BEFORE, false);
        }

        @Override
        public float getFlexGrow() {
            return (float) getConstraint(delegate, FLEX_GROW, 0f);
        }

        @Override
        public float getFlexShrink() {
            return (float) getConstraint(delegate, FLEX_SHRINK, 1f);
        }

        @Override
        public float getFlexBasisPercent() {
            return (float) getConstraint(delegate, FLEX_BASIS_PERCENT, -1.0f);
        }

        @Override
        public int getOrder() {
            return (int) getConstraint(delegate, FLEX_ORDER, 0);
        }

        @Override
        public AlignSelf getFlexAlignSelf() {
            return (AlignSelf) getConstraint(delegate, FLEX_ALIGN_SELF, AlignSelf.AUTO);
        }

    }

}
