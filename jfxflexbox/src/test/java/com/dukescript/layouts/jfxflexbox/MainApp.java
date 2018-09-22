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

import com.dukescript.layouts.flexbox.FlexboxLayout;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        FlexBoxPane flex = new FlexBoxPane();

        ObservableList<JustifyItem> justifyChoices = FXCollections.observableArrayList();
        justifyChoices.add(JustifyItem.JUSTIFY_CONTENT_CENTER);
        justifyChoices.add(JustifyItem.JUSTIFY_CONTENT_FLEX_END);
        justifyChoices.add(JustifyItem.JUSTIFY_CONTENT_FLEX_START);
        justifyChoices.add(JustifyItem.JUSTIFY_CONTENT_SPACE_AROUND);
        justifyChoices.add(JustifyItem.JUSTIFY_CONTENT_SPACE_BETWEEN);
        ChoiceBox<JustifyItem> justifyContent = new ChoiceBox<>(justifyChoices);
        justifyContent.setOnAction(e -> {
            JustifyItem selectedItem = justifyContent.getSelectionModel().getSelectedItem();
            flex.setJustifyContent(selectedItem.ordinal());
         
        });
        justifyContent.getSelectionModel().select(JustifyItem.JUSTIFY_CONTENT_FLEX_START);
        ObservableList<FlexWrap> flexWrapChoices = FXCollections.observableArrayList();
        flexWrapChoices.add(FlexWrap.FLEX_WRAP_NOWRAP);
        flexWrapChoices.add(FlexWrap.FLEX_WRAP_WRAP);
        flexWrapChoices.add(FlexWrap.FLEX_WRAP_WRAP_REVERSE);

        ChoiceBox<FlexWrap> flexWrap = new ChoiceBox<>(flexWrapChoices);
        flexWrap.setOnAction(e -> {
            FlexWrap selectedItem = flexWrap.getSelectionModel().getSelectedItem();
            flex.setFlexWrap(selectedItem.ordinal());
        });
        flexWrap.getSelectionModel().select(FlexWrap.FLEX_WRAP_WRAP);
        ObservableList<FlexDirection> flexDirectionChoices = FXCollections.observableArrayList();
        flexDirectionChoices.add(FlexDirection.FLEX_DIRECTION_COLUMN);
        flexDirectionChoices.add(FlexDirection.FLEX_DIRECTION_COLUMN_REVERSE);
        flexDirectionChoices.add(FlexDirection.FLEX_DIRECTION_ROW);
        flexDirectionChoices.add(FlexDirection.FLEX_DIRECTION_ROW_REVERSE);

        ChoiceBox<FlexDirection> flexDirection = new ChoiceBox<>(flexDirectionChoices);
        flexDirection.setOnAction(e -> {
            FlexDirection selectedItem = flexDirection.getSelectionModel().getSelectedItem();
            flex.setFlexDirection(selectedItem.ordinal());
        });
        flexDirection.getSelectionModel().select(FlexDirection.FLEX_DIRECTION_ROW);

        ObservableList<FlexAlignItems> flexAlignItemsChoices = FXCollections.observableArrayList();
        flexAlignItemsChoices.add(FlexAlignItems.ALIGN_ITEMS_BASELINE);
        flexAlignItemsChoices.add(FlexAlignItems.ALIGN_ITEMS_CENTER);
        flexAlignItemsChoices.add(FlexAlignItems.ALIGN_ITEMS_FLEX_END);
        flexAlignItemsChoices.add(FlexAlignItems.ALIGN_ITEMS_FLEX_START);
        flexAlignItemsChoices.add(FlexAlignItems.ALIGN_ITEMS_STRETCH);

        ChoiceBox<FlexAlignItems> flexAlignItems = new ChoiceBox<>(flexAlignItemsChoices);
        flexAlignItems.setOnAction(e -> {
            FlexAlignItems selectedItem = flexAlignItems.getSelectionModel().getSelectedItem();
            flex.setAlignItems(selectedItem.ordinal());
        });
        flexAlignItems.getSelectionModel().select(FlexAlignItems.ALIGN_ITEMS_FLEX_START);
        ObservableList<FlexAlignContent> flexAlignContentChoices = FXCollections.observableArrayList();
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_CENTER);
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_FLEX_END);
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_FLEX_START);
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_SPACE_AROUND);
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_SPACE_BETWEEN);
        flexAlignContentChoices.add(FlexAlignContent.ALIGN_CONTENT_STRETCH);

        ChoiceBox<FlexAlignContent> flexAlignContent = new ChoiceBox<>(flexAlignContentChoices);
        flexAlignContent.setOnAction(e -> {
            FlexAlignContent selectedItem = flexAlignContent.getSelectionModel().getSelectedItem();
            flex.setAlignContent(selectedItem.ordinal());
        });
        flexAlignContent.getSelectionModel().select(FlexAlignContent.ALIGN_CONTENT_CENTER);

        VBox toolbar = new VBox(flexWrap, flexDirection, justifyContent, flexAlignContent, flexAlignItems);
        toolbar.setAlignment(Pos.CENTER);
        Scene sceneTools = new Scene(toolbar, 300, 300);
        Stage stage1 = new Stage();
        stage1.setScene(sceneTools);
        stage1.show();
        HBox h;
        root.setCenter(flex);
        for (int i = 0; i < 5; i++) {
            Label l = new Label("Flex Item " + i);
            l.setPrefHeight(50 + (i * 5));
            l.setMinWidth(20);
            FlexBoxPane.setGrow(l, 1.0f);

            l.getStyleClass().add("flex-item");
            l.getStyleClass().add("flex-item-" + i);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setAlignment(Pos.CENTER);
            FlexBoxPane.setMargin(l, new Insets(5));
            FlexBoxPane.setFlexBasisPercent(l, 10f);
//            FlexBoxPane.setGrow(l, 1);
            flex.getChildren().add(l);
        }
        FlexBoxPane.setShrink(flex.getChildren().get(1), 3.0f);

        Scene scene = new Scene(root, 200, 200);
        scene.getStylesheets().add("/com/dukescript/native4j/jfxflexbox/styles.css");

        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private enum JustifyItem {
        JUSTIFY_CONTENT_FLEX_START, JUSTIFY_CONTENT_FLEX_END, JUSTIFY_CONTENT_CENTER,
        JUSTIFY_CONTENT_SPACE_BETWEEN, JUSTIFY_CONTENT_SPACE_AROUND;
    }

    private enum FlexWrap {
        FLEX_WRAP_NOWRAP, FLEX_WRAP_WRAP, FLEX_WRAP_WRAP_REVERSE;
    }

    private enum FlexDirection {
        FLEX_DIRECTION_ROW, FLEX_DIRECTION_ROW_REVERSE, FLEX_DIRECTION_COLUMN, FLEX_DIRECTION_COLUMN_REVERSE;
    }

    private enum FlexAlignItems {
        ALIGN_ITEMS_FLEX_START,
        ALIGN_ITEMS_FLEX_END, ALIGN_ITEMS_CENTER, ALIGN_ITEMS_BASELINE, ALIGN_ITEMS_STRETCH;
    }

    private enum FlexAlignContent {
        ALIGN_CONTENT_FLEX_START, ALIGN_CONTENT_FLEX_END, ALIGN_CONTENT_CENTER, ALIGN_CONTENT_SPACE_BETWEEN,
        ALIGN_CONTENT_SPACE_AROUND, ALIGN_CONTENT_STRETCH;
    }

}
