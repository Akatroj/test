package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map;

import agh.cs.lab.AnimalSimulator.Configuration.Config;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

public class MapRegionPreparer {
    private final Region canvasInnerContainer;
    private final Region canvasOuterContainer;
    private final Canvas canvas;

    public MapRegionPreparer(Region canvasOuterContainer, Region canvasInnerContainer) {
        this.canvasOuterContainer = canvasOuterContainer;
        this.canvasInnerContainer = canvasInnerContainer;
        setSizeForCanvasContainer();
        canvas = prepareMapCanvas();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    //black magic
    private void setSizeForCanvasContainer() {

        /*
         * calculates the maximum width/height for canvasInnerContainer by rounding canvasOuterContainer width/height to the
         * nearest multiple of map width/height
         * needs to be contained in Binding
         */


        double mapHeight = Config.getInstance().getMapHeight();
        double mapWidth = Config.getInstance().getMapWidth();
        double combo = mapWidth/mapHeight;

        DoubleExpression temp = canvasOuterContainer.heightProperty();
        DoubleExpression temp2 = canvasOuterContainer.widthProperty();

        /*
         * if mapHeight > mapWidth, sets canvasInnerContainer height to max allowed and scales width appropriately
         * so that proportions are preserved
         * else, sets canvasInnerContainer width to max allowed width and scales height appropriately
         */

        if (combo <= 1) {
            DoubleExpression size = doubleExpressionFloorDivision(temp, mapHeight);
            canvasInnerContainer.maxHeightProperty().bind(size);
            canvasInnerContainer.maxWidthProperty().bind(size.multiply(combo));
        } else {
            DoubleExpression size = doubleExpressionFloorDivision(temp2, mapWidth);
            canvasInnerContainer.maxHeightProperty().bind(size.divide(combo));
            canvasInnerContainer.maxWidthProperty().bind(size);
        }
    }

    private Canvas prepareMapCanvas() {
        javafx.scene.canvas.Canvas canvas = new Canvas();
        canvas.heightProperty().bind(canvasInnerContainer.heightProperty());
        canvas.widthProperty().bind(canvasInnerContainer.widthProperty());

        return canvas;
    }

    //utility
    private DoubleExpression doubleExpressionFloorDivision(DoubleExpression doubleExpression, double divider) {
        return Bindings.createDoubleBinding(
                () -> doubleExpression.getValue().intValue() / ((int) divider) * divider,
                doubleExpression
        );
    }


}
