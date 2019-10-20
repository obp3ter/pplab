package main.java.main;

import com.google.common.util.concurrent.AtomicDouble;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    /**
     * The number of sales to be generated
     */
    static final int SALES = 4000000;
    /**
     * If the bills queue and the money to be blocking when checking
     */
    static final boolean BILL_LOCKING = true;
    /**
     * Minimum number of each item to be sold
     */
    private static final int MIN_SALE_QUANTITY = 1;
    /**
     * Maximum number of each number to be sold
     */
    private static final int MAX_SALE_QUANTITY = 20;
    /**
     * The factor to which the initial stock is generated after the formula: SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY
     */
    private static final double PRODUCT_FACTOR = 0.5;

    /**
     * The frequency with which the checks are performed during runtime
     */
    private static final int CHECK_FREQUENCY = 3000;

    /**
     * The main generates random sales set, then creates callables for each of them, also schedules checks periodically, executes the sales concurrently and does the final check and prints the elapsed time
     */
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(5);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        ReadWriteLock billsLock = new ReentrantReadWriteLock();

        ArrayList<Product> products = new ArrayList<>();
        ArrayBlockingQueue<List<Pair<String, Integer>>> bills = new ArrayBlockingQueue<>(SALES);

        AtomicDouble money = new AtomicDouble(0);

        products.add(new Product("apple", 2.1, (int) (SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY)));
        products.add(new Product("beer", 5.0, (int) (SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY)));
        products.add(new Product("mango", 1.5, (int) (SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY)));
        products.add(new Product("bread", 2.2, (int) (SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY)));
        products.add(new Product("soap", 2.1, (int) (SALES * PRODUCT_FACTOR * MAX_SALE_QUANTITY)));

        ArrayList<SaleProcessorCallable> callables = new ArrayList<>();


//        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("D:\\Downloads\\MOCK_DATA.xlsx"));
//        XSSFSheet sheet = wb.getSheetAt(0);
//        Integer rows = sheet.getPhysicalNumberOfRows();
//
//        sheet.forEach(row ->{
//            if(row.getRowNum()==0)
//                return;
//
//            List<Pair<Product,Integer>> sale = new ArrayList<>();
//
//            for (int i = 0; i < 5; i++) {
//                sale.add(Pair.with(products.get(i),Integer.valueOf(String.format("%d",Math.round(row.getCell(i).getNumericCellValue())))));
//            }
//
//            SaleProcessorCallable saleProcessor = new SaleProcessorCallable(sale, bills, money, billsLock);
//            callables.add(saleProcessor);
//
//        });

/**
 * Random generation of sales
 */
        for (int i = 0; i < SALES; i++) {

            List<Pair<Product, Integer>> sale = new ArrayList<>();

            for (Product product : products) {
                sale.add(Pair.with(product, (int) (Math.random() * (MAX_SALE_QUANTITY - MIN_SALE_QUANTITY) + MIN_SALE_QUANTITY)));
            }
            SaleProcessorCallable saleProcessor = new SaleProcessorCallable(sale, bills, money, billsLock);
            callables.add(saleProcessor);
        }


        long start2 = System.nanoTime();

        BillCheckRunnable billCheck = new BillCheckRunnable(products, bills, money, billsLock);
        scheduledExecutorService.scheduleAtFixedRate(billCheck, 0, CHECK_FREQUENCY, TimeUnit.MILLISECONDS);

        executor.invokeAll(callables);

        System.out.println("DONE!");
        executor.submit(billCheck);

        scheduledExecutorService.shutdown();


        System.out.println("Completed in:" + ((System.nanoTime() - start2) / 1000000));

    }
}
