/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karlmarxindustries.flooring.service;

import com.karlmarxindustries.flooring.dao.FilePersistenceException;
import com.karlmarxindustries.flooring.dto.Order;
import java.util.List;
import com.karlmarxindustries.flooring.dao.FlooringAuditDao;
import com.karlmarxindustries.flooring.dao.FlooringOrderDao;
import com.karlmarxindustries.flooring.dao.FlooringProductDao;
import com.karlmarxindustries.flooring.dao.FlooringTaxDao;
import com.karlmarxindustries.flooring.dao.TestingModeException;
import com.karlmarxindustries.flooring.dto.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author karlmarx
 */
public class FlooringServiceLayerImpl implements FlooringServiceLayer {

    FlooringTaxDao tDao;
    FlooringOrderDao oDao;
    FlooringProductDao pDao;

    public FlooringServiceLayerImpl(FlooringTaxDao tDao, FlooringOrderDao oDao, FlooringProductDao pDao) {
        this.tDao = tDao;
        this.oDao = oDao;
        this.pDao = pDao;
    }
    @Override
    public void initialLoadProductTaxInfo() throws FilePersistenceException {
         tDao.loadRateList();
         pDao.loadProductInfo();
    }
    
    @Override
    public List<String> loadValidStates() throws FilePersistenceException {
      
        return tDao.getAllStates();
    }

    public List<Product> loadProducts() throws FilePersistenceException {
        return pDao.getAllProducts();
    }

    public Order calculateAndOrderNumber(Order order) throws
            FlooringDuplicateIdException,
            FlooringDataValidationException,
            FilePersistenceException {
        this.calculateCostsTaxesTotal(order);
        order.setOrderNumber(newOrderNumber());
        return order;
    }

//    public void validateStudentData(Order student) throws
//            FlooringDataValidationException {
//
//        if (student.getFirstName() == null
//                || student.getFirstName().trim().length() == 0
//                || student.getLastName() == null
//                || student.getLastName().trim().length() == 0
//                || student.getCohort() == null
//                || student.getCohort().trim().length() == 0) {
//
//            throw new FlooringDataValidationException(
//                    "ERROR: All fields [First Name, Last Name, Cohort] are required.");
//        }
//    }
//
//    @Override
//    public List<Order> getAllStudents() throws FilePersistenceException {
//        return dao.getAllStudents();
//    }
//
//    @Override
//    public Order getStudent(String studentId) throws FilePersistenceException {
//        return dao.getStudent(studentId);
//    }
//
//    @Override
//    public Order removeStudent(String studentId) throws FilePersistenceException {
//        Order removedStudent = dao.removeStudent(studentId);
//        // Write to audit log
//        auditDao.writeAuditEntry("Student " + studentId + " REMOVED.");
//        return removedStudent;
//    }

    private FlooringAuditDao auditDao;

    private int newOrderNumber() throws FilePersistenceException {
        List<Order> allOrders = oDao.getAllOrders();
        List<Integer> orderNumbers = new ArrayList<>();
        orderNumbers.add(0);
        for (Order each : allOrders) {
            orderNumbers.add(each.getOrderNumber());
        }
        int newOrderNumber = Collections.max(orderNumbers) + 1;
        return newOrderNumber;
    }

//    public void addOrder(Order toAdd) {
//        oDao.createOrder(toAdd);
//    }

    @Override
    public List<Order> getAllOrders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate searchDate) throws NoOrdersOnDateException, FilePersistenceException {
        List<Order> results = oDao.getAllOrdersForDate(searchDate);
        if (results.size() == 0) {
            throw new NoOrdersOnDateException("No orders were found for that date.  Please try again.");
        }
        return results;
    }

    @Override
    public Order getMatchingOrderForDate(LocalDate searchDate, int searchOrderNumber) throws FilePersistenceException, NoMatchingOrdersException {
        Order foundOrder = oDao.getOrder(searchOrderNumber);
        if (foundOrder.getDate().compareTo(searchDate) == 0 ) {
            return foundOrder;
        } else {
            throw new NoMatchingOrdersException("There were no matching orders for that date.");
        }
    }

    @Override
    public void removeOrder(Order toRemove) throws FilePersistenceException {
        oDao.removeOrder(toRemove.getOrderNumber());
    }

    @Override
    public void saveWorks()  throws FilePersistenceException, TestingModeException  {
        oDao.saveAllOrders();
    }

    @Override
    public Order calculateCostsTaxesTotal(Order order)  throws
            FlooringDuplicateIdException,
            FlooringDataValidationException,
            FilePersistenceException{
       
         Product productChosen = pDao.getProduct(order.getProductType().toUpperCase());
        BigDecimal taxRate = tDao.getTax(order.getState().toUpperCase()).getTaxRate();
        order.setTaxRate(taxRate);
        order.setCostPerSquareFoot(productChosen.getCostPerSquareFoot().setScale(2));
        order.setLaborCostPerSquareFoot(productChosen.getLaborCostPerSquareFoot().setScale(2));
        order.setMaterialCost(order.getArea().multiply(order.getCostPerSquareFoot()).setScale(2));
        order.setLaborCost(order.getArea().multiply(order.getLaborCostPerSquareFoot()).setScale(2));
        order.setTax((order.getMaterialCost().add(order.getLaborCost())).multiply(taxRate).setScale(2));
        order.setTotal(order.getTax().add(order.getLaborCost()).add(order.getMaterialCost()).setScale(2));
        return order;
    }

    @Override
    public void editOrder(Order editedAndCalculated) throws FilePersistenceException {
        oDao.editOrder(editedAndCalculated.getOrderNumber(), editedAndCalculated);
    }

    @Override
    public void loadOrderData() throws FilePersistenceException {
        oDao.loadOrders();
    }
    @Override 
    public String firstLetterCapRestLower (String string) {
    return (string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase());
}

    @Override
    public Order addOrder(Order toAdd) throws FilePersistenceException {
        Order newOrder = oDao.createOrder(toAdd.getOrderNumber(), toAdd);
        return newOrder;
    }

}
