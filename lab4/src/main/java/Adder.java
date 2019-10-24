import org.javatuples.Pair;
import org.thavam.util.concurrent.blockingMap.BlockingHashMap;

import java.util.List;
import java.util.concurrent.Callable;

public class Adder implements Callable<Object> {
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m1;
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m2;
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m_result;
    private List<Pair<Integer,Integer>> positions;

    Adder(BlockingHashMap<Pair<Integer,Integer>,Integer> m1, BlockingHashMap<Pair<Integer,Integer>,Integer> m2, BlockingHashMap<Pair<Integer,Integer>,Integer> m_result, List<Pair<Integer, Integer>> positions) {
        this.m1 = m1;
        this.m2 = m2;
        this.m_result = m_result;
        this.positions = positions;
    }

    @Override
    public Object call() throws Exception {
        m1.get(Pair.with(positions.get(0).getValue0(),positions.get(0).getValue1()));
        positions.forEach(position -> m_result.put(Pair.with(position.getValue0(),position.getValue1()),m1.get(Pair.with(position.getValue0(),position.getValue1())) + m2.get(Pair.with(position.getValue0(),position.getValue1()))));
        return null;
    }
}
