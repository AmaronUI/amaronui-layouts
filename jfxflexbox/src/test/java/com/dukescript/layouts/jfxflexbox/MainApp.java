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
import com.dukescript.layouts.flexbox.FlexboxLayout.AlignContent;
import com.dukescript.layouts.flexbox.FlexboxLayout.AlignItems;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexDirection;
import com.dukescript.layouts.flexbox.FlexboxLayout.FlexWrap;
import com.dukescript.layouts.flexbox.FlexboxLayout.JustifyContent;
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

        ObservableList<JustifyContent> justifyChoices = FXCollections.observableArrayList();
        justifyChoices.add(JustifyContent.CENTER);
        justifyChoices.add(JustifyContent.FLEX_END);
        justifyChoices.add(JustifyContent.FLEX_START);
        justifyChoices.add(JustifyContent.SPACE_AROUND);
        justifyChoices.add(JustifyContent.SPACE_BETWEEN);
        ChoiceBox<JustifyContent> justifyContent = new ChoiceBox<>(justifyChoices);
        justifyContent.setOnAction(e -> {
            JustifyContent selectedItem = justifyContent.getSelectionModel().getSelectedItem();
            flex.setJustifyContent(selectedItem);       
        });
        justifyContent.getSelectionModel().select(JustifyContent.FLEX_START);
        ObservableList<FlexWrap> flexWrapChoices = FXCollections.observableArrayList();
        flexWrapChoices.add(FlexWrap.NOWRAP);
        flexWrapChoices.add(FlexWrap.WRAP);
        flexWrapChoices.add(FlexWrap.WRAP_REVERSE);

        ChoiceBox<FlexWrap> flexWrap = new ChoiceBox<>(flexWrapChoices);
        flexWrap.setOnAction(e -> {
            FlexWrap selectedItem = flexWrap.getSelectionModel().getSelectedItem();
            flex.setFlexWrap(selectedItem);
        });
        flexWrap.getSelectionModel().select(FlexWrap.WRAP);
        ObservableList<FlexDirection> flexDirectionChoices = FXCollections.observableArrayList();
        flexDirectionChoices.add(FlexDirection.COLUMN);
        flexDirectionChoices.add(FlexDirection.COLUMN_REVERSE);
        flexDirectionChoices.add(FlexDirection.ROW);
        flexDirectionChoices.add(FlexDirection.ROW_REVERSE);

        ChoiceBox<FlexDirection> flexDirection = new ChoiceBox<>(flexDirectionChoices);
        flexDirection.setOnAction(e -> {
            FlexDirection selectedItem = flexDirection.getSelectionModel().getSelectedItem();
            flex.setFlexDirection(selectedItem);
        });
        flexDirection.getSelectionModel().select(FlexDirection.ROW);

        ObservableList<AlignItems> flexAlignItemsChoices = FXCollections.observableArrayList();
        flexAlignItemsChoices.add(AlignItems.BASELINE);
        flexAlignItemsChoices.add(AlignItems.CENTER);
        flexAlignItemsChoices.add(AlignItems.FLEX_END);
        flexAlignItemsChoices.add(AlignItems.FLEX_START);
        flexAlignItemsChoices.add(AlignItems.STRETCH);

        ChoiceBox<AlignItems> flexAlignItems = new ChoiceBox<>(flexAlignItemsChoices);
        flexAlignItems.setOnAction(e -> {
            AlignItems selectedItem = flexAlignItems.getSelectionModel().getSelectedItem();
            flex.setAlignItems(selectedItem);
        });
        flexAlignItems.getSelectionModel().select(AlignItems.FLEX_START);
        ObservableList<AlignContent> flexAlignContentChoices = FXCollections.observableArrayList();
        flexAlignContentChoices.add(FlexboxLayout.AlignContent.CENTER);
        flexAlignContentChoices.add(AlignContent.FLEX_END);
        flexAlignContentChoices.add(AlignContent.FLEX_START);
        flexAlignContentChoices.add(AlignContent.SPACE_AROUND);
        flexAlignContentChoices.add(AlignContent.SPACE_BETWEEN);
        flexAlignContentChoices.add(AlignContent.STRETCH);

        ChoiceBox<AlignContent> flexAlignContent = new ChoiceBox<>(flexAlignContentChoices);
        flexAlignContent.setOnAction(e -> {
            AlignContent selectedItem = flexAlignContent.getSelectionModel().getSelectedItem();
            flex.setAlignContent(selectedItem);
        });
        flexAlignContent.getSelectionModel().select(AlignContent.CENTER);

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


}
