package main.java.main;

import com.google.common.util.concurrent.AtomicDouble;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * handles the sales
 */
class SaleProcessorCallable implements Callable<List<Pair<String, Integer>>> {

    /**
     * The sale to be handled
     */
    private List<Pair<Product, Integer>> sale;
    /**
     * The queue of all the bills where the result is added
     */
    private final BlockingQueue<List<Pair<String, Integer>>> bills;
    /**
     * The central money where the result of the sale is added
     */
    private AtomicDouble money;
    /**
     * The locks for the central bills queue
     */
    private ReadWriteLock billsLock;

    /**
     * The sale handler as a callable requires as input:
     *
     * @param sale      a list of pairs of products and quantities to be sold
     * @param bills     the queue of bills where sales are centralized
     * @param money     the AtomicDouble where the money form sales is stored
     * @param billsLock the lock for the bills so that sales halt while checks are performed
     */
    SaleProcessorCallable(List<Pair<Product, Integer>> sale, BlockingQueue<List<Pair<String, Integer>>> bills, AtomicDouble money, ReadWriteLock billsLock) {
        this.sale = sale;
        this.bills = bills;
        this.money = money;
        this.billsLock = billsLock;
    }

    /**
     * The call is the sale handler itself
     *
     * @return the bill generated as a list of pairs of string(the name f the product and integer quantity
     */
    public List<Pair<String, Integer>> call() {

        ArrayList<Pair<String, Integer>> result = new ArrayList<>();

        if (Main.BILL_LOCKING)
            billsLock.readLock().lock();

        sale.forEach(p ->
                {
                    Product product = p.getValue0();
                    Integer number = p.getValue1();

                    try {
                        product.sale(number);
                        result.add(Pair.with(p.getValue0().name, p.getValue1()));
                        money.addAndGet(product.price * number);
                    } catch (Exception e) {
//                                System.out.println("Not enough "+ product.name+ " !");
                    }
//                    finally {
//                                System.out.println("Sold "+p.getValue1()+" "+p.getValue0().name);
//                    }


                }
        );

        this.bills.add(result);

        if (Main.BILL_LOCKING)
            billsLock.readLock().unlock();

        return null;

    }

}
