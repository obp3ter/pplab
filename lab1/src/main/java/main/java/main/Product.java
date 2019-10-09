package main.java.main;

import java.security.InvalidParameterException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//public class Product {
//    public String name;
//    public Integer quantity;
//    public  final Lock mutex = new ReentrantLock(true);
//
//    public Double price;
//
//    public Product(String name, Double price, Integer quantity) {
//        this.name = name;
//        this.price = price;
//        this.quantity = quantity;
//    }
//
//    public Product(Product p) {
//        this.name = p.name;
//        this.price = p.price;
//        this.quantity = p.quantity;
//    }
//
//    public void sale(Integer nr) throws InvalidParameterException{
//        mutex.lock();
//        if(nr>quantity) {
//            mutex.unlock();
//            throw new InvalidParameterException("Not enough Items!");
//
//        }
//        quantity-=nr;
//        mutex.unlock();
//    }
//
//    public Product() {
//    }
//}

/**
 * This class handles the inventory part of products
 */
class Product {
    /**
     * The name of the product
     */
    String name;
    /**
     * The number of products available of the product
     */
    private AtomicInteger quantity;

    /**
     * The price of the product
     */
    Double price;

    Product(String name, Double price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = new AtomicInteger(quantity);
    }


    /**
     * Copy-constructor of the class
     *
     * @param p the product to be copied
     * @return a copy of the product given as a parameter
     */
    Product(Product p) {
        this.name = p.name;
        this.price = p.price;
        this.quantity = p.quantity;
    }

    /**
     * Sells a The specified number of product if available otherwise throws an exception
     *
     * @param nr the number of products to be sold
     * @throws InvalidParameterException if the requested amount is unavailable
     */
    synchronized void sale(Integer nr) throws InvalidParameterException {
        if (nr < quantity.get()) {
            quantity.addAndGet(-nr);
        } else
            throw new InvalidParameterException("Not enough Items!");
    }
}

