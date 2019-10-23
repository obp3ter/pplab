import org.javatuples.Pair;

import java.util.List;
import java.util.concurrent.Callable;

public class Adder implements Callable<Object> {
    private Integer[][] m1;
    private Integer[][] m2;
    private Integer[][] m_result;
    private List<Pair<Integer,Integer>> positions;

    Adder(Integer[][] m1, Integer[][] m2, Integer[][] m_result, List<Pair<Integer, Integer>> positions) {
        this.m1 = m1;
        this.m2 = m2;
        this.m_result = m_result;
        this.positions = positions;
    }

    @Override
    public Object call() throws Exception {
        positions.forEach(position -> m_result[position.getValue0()][position.getValue1()]= m1[position.getValue0()][position.getValue1()]+m2[position.getValue0()][position.getValue1()]);
        return null;
    }
}
