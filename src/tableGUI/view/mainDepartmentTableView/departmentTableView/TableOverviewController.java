package tableGUI.view.mainDepartmentTableView.departmentTableView;

import dataTable.departments.CalendarDep;
import dataTable.departments.Department;
import dataTable.departments.ElevationTable;
import dataTable.departments.ProductionCalendarTable;
import dataTable.employees.Employee;
import dataTable.employees.EmployeeTable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import manager.Manager;
import tableGUI.model.DepartmentModel;
import tableGUI.util.Util;

public class TableOverviewController {
    private Department dep;
    private int monthId;
    private ObservableList<Pair<Employee, CalendarDep>> personData;
    private DepartmentModel departmentModel;
    //public static ArrayList<TableOverviewController> instances = new ArrayList<>();

    @FXML
    private TableView<Pair<Employee, CalendarDep>> personTable;

    @FXML
    private TableColumn<Pair<Employee, CalendarDep>, String> nameID;

    @FXML
    private TableColumn<Pair<Employee, CalendarDep>, String> positionID;

    @FXML
    private TableColumn<Pair<Employee, CalendarDep>, Integer> idID;

    @FXML
    private void initialize(){
        //instances.add(this);
        //int i = 0;
        personTable.editableProperty().setValue(true);

        nameID.setCellValueFactory(cellData -> cellData.getValue().getKey().getFstNameProperty().concat(" ").concat(cellData.getValue().getKey().getScnNameProperty()));
        positionID.setCellValueFactory(cellData -> cellData.getValue().getKey().getPositionProperty());
        idID.setCellValueFactory(cellData -> cellData.getValue().getKey().getIdProperty().asObject());
        //nameID.setStyle("-fx-background-color: #F19C75 ;");
        /*for (Integer i = 0; i < 31; ++i){
            i++;
            TableColumn<Pair<Employee, CalendarDep>, String> tableColumn = new TableColumn(i.toString());
            tableColumn.setMaxWidth(30);
            i--;
            final int num = i;
            tableColumn.setCellValueFactory(cellData -> {
                final int j = num;
                return  new SimpleStringProperty(cellData.getValue().getValue().getMonth(monthId).get(j).getValue().toString());
            });
            personTable.getColumns().add(tableColumn);
        }*/
    }

    public void setCurrentTab(String month) {
        if (dep == null) return;
        monthId = -1;
        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        switch (month){
            case ("January"):{
                monthId = 0;
                break;
            }
            case("February"):{
                monthId = 1;
                break;
            }
            case("March"):{
                monthId = 2;
                break;
            }
            case ("April"):{
                monthId = 3;
                break;
            }
            case ("May"):{
                monthId = 4;
                break;
            }
            case ("June"):{
                monthId = 5;
                break;
            }
            case ("July"):{
                monthId = 6;
                break;
            }
            case ("August"):{
                monthId = 7;
                break;
            }
            case ("September"):{
                monthId = 8;
                break;
            }
            case ("October"):{
                monthId = 9;
                break;
            }
            case ("November"): {
                monthId = 10;
                break;
            }
            case ("December"):{
                monthId = 11;
                break;
            }
        }
        if (monthDays[monthId] > (personTable.getColumns().size() - 3)){
            int n = personTable.getColumns().size() - 3;
            for (Integer i =  n + 1; i <= monthDays[monthId]; ++i){
                String type = "";
                try {
                    if (!ProductionCalendarTable.getInstance().getProductionCalendar().getPair(monthId, i - 1).getKey().equals(" ")){
                        type = "(" + ProductionCalendarTable.getInstance().getProductionCalendar().getPair(monthId, i - 1).getKey() + ")";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                TableColumn<Pair<Employee, CalendarDep>, String> a = new TableColumn(String.valueOf(i).concat(type) );

                a.setMaxWidth(45);
                a.setMinWidth(45);
                personTable.getColumns().add(a);
            }
        }else{
            personTable.getColumns().remove(monthDays[monthId] + 3, (personTable.getColumns().size()));
        }
        for (int i = 3; i < personTable.getColumns().size(); ++i){
            String type = setTitle(i-2);
            personTable.getColumns().get(i).setText(String.valueOf(i-2).concat(type));
            final int num = i;
            if (Manager.getInstance().getAccess() == 0 || Manager.getInstance().getAccess() == 3){
                ((TableColumn<Pair<Employee, CalendarDep>, String>) personTable.getColumns().get(i)).setCellFactory(col -> new TextFieldTableCell<>(new StringConverter<String>() {
                    @Override
                    public String toString(String object) {
                        try {
                            if (ElevationTable.getInstance().containsOf(object)){
                                return object;
                            }else{
                                return "";
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public String fromString(String string) {
                        return string;
                    }
                }));
                ((TableColumn<Pair<Employee, CalendarDep>, String>) personTable.getColumns().get(i)).editableProperty().setValue(true);
                ((TableColumn<Pair<Employee, CalendarDep>, String>) personTable.getColumns().get(i)).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pair<Employee, CalendarDep>, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Pair<Employee, CalendarDep>, String> event) {
                        try {
                            if (ElevationTable.getInstance().containsOf(event.getNewValue())) {
                                dep.updateEmployeesCalendar(event.getTableView().getItems().get(event.getTablePosition().getRow()).getKey(),
                                        event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue().setValue(monthId, num - 3, event.getNewValue()));
                            }else{
                                DepartmentModel departmentModel = new DepartmentModel(dep);

                                ObservableList observableList = FXCollections.observableArrayList(departmentModel.getTableLine());
                                event.getTableView().setItems(observableList);
                                //event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue().setValue(monthId, num - 3, event.getOldValue());
                                Util.viewAlertWindow("Wrong elevation.");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            ((TableColumn<Pair<Employee, CalendarDep>, String>) personTable.getColumns().get(i)).setCellValueFactory(cellData -> {
                final int j = num;
                return  new SimpleStringProperty(cellData.getValue().getValue().getMonth(monthId).get(j-3).getKey());
            });
        }
    }

    public void setDepartment(Department dep) {
        this.dep = dep;
        try {
            DepartmentModel departmentModel = new DepartmentModel(dep);
            personData = FXCollections.observableArrayList(departmentModel.getTableLine());
            personTable.setItems(personData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String setTitle(int i){
        String type = "";
        try {
            if (!ProductionCalendarTable.getInstance().getProductionCalendar().getPair(monthId, i - 1).getKey().equals(" ")){
                type = "(" + ProductionCalendarTable.getInstance().getProductionCalendar().getPair(monthId, i - 1).getKey() + ")";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return type;
    }
}
