A FlexBox Layout Pane for JavaFX

[![Alt text](https://img.youtube.com/vi/MvQlvCnqSRA/0.jpg)](https://www.youtube.com/watch?v=MvQlvCnqSRA)

The FlexBoxPane is available from Maven Central:

    <dependency>
      <groupId>com.dukescript.amaronui.layouts</groupId>
      <artifactId>jfxflexbox</artifactId>
      <version>0.2</version>
    </dependency>

FlexBoxPane is based on [this Java implementation of Flexbox](https://github.com/AmaronUI/amaronui-layouts/tree/master/flexbox).
You can use it like any other JavaFX Layout Pane:

```java
        FlexBoxPane flex = new FlexBoxPane();
        
        flex.setAlignContent(AlignContent.SPACE_AROUND);
        flex.setFlexDirection(FlexDirection.ROW);
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
            flex.getChildren().add(l);
        }
```

## Release Info


### Current Development Version 1.0-SNAPSHOT

changed setShrink access to public
Support for unmanaged Nodes

### Version 0.2 (04.10.2018)

Incompatible change: Replaced int constants with Enums in FlexBoxLayout and FlexItem

### Version 0.1 (22.09.2018)

initial release


