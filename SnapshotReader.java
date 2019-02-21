/**
 * Created by Cekis on 2/20/2017.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import swg.WSFile;

import java.io.IOException;

public class SnapshotReader extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("SWG Snapshot Viewer");

        TabPane tabView = new TabPane();
        Tab tab = new Tab();
        tab.closableProperty().setValue(false);
        tab.setText("lok.ws");
        TableView dataTable = new TableView();

        ObservableList cols = dataTable.getColumns();

        cols.add(makeColumn("Object ID", "id",75));
        cols.add(makeColumn("Parent ID", "parentId", 75));
        cols.add(makeColumn("Template", "template", dataTable, 550d));
        cols.add(makeColumn("Index", "nodeIndex", 50));
        cols.add(makeColumn("X", "x", 50));
        cols.add(makeColumn("Y", "y", 50));
        cols.add(makeColumn("Z", "z", 50));
        cols.add(makeColumn("QW", "objW",50));
        cols.add(makeColumn("QX", "objX",50));
        cols.add(makeColumn("QY", "objY",50));
        cols.add(makeColumn("QZ", "objZ",50));

        tab.setContent(dataTable);
        tabView.getTabs().add(tab);

        WSFile wsFile = new WSFile();
        wsFile.readFile("snapshot/lok.ws");
        tab.setText(wsFile.getAreaName() + ".ws");

        ObservableList data = FXCollections.observableArrayList(wsFile.getAllNodes().toArray());
        dataTable.setItems(data);

        Scene scene = new Scene(tabView, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private TableColumn makeColumn(String name, String property, double width){
        TableColumn newCol = new TableColumn();
        newCol.setPrefWidth(width);
        newCol.setText(name);
        newCol.setCellValueFactory(new PropertyValueFactory(property));
        return newCol;
    }
    private TableColumn makeColumn(String name, String property, TableView dt, double diff){
        TableColumn newCol = new TableColumn();
        newCol.prefWidthProperty().bind(dt.widthProperty().subtract(diff));
        newCol.setText(name);
        newCol.setCellValueFactory(new PropertyValueFactory(property));
        return newCol;
    }
}
